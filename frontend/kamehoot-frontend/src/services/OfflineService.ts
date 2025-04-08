// src/services/OfflineService.ts

import { Question, QuestionFromBackend } from "../types/question";

// Base URL for API requests
const API_BASE_URL = "http://localhost:8081";

// Connection state enum
export enum ConnectionState {
  ONLINE = "online",
  OFFLINE = "offline",
  SERVER_DOWN = "server_down",
}

// Pending operation type
export interface PendingOperation {
  id: string;
  type: "create" | "update" | "delete";
  data: any;
  endpoint: string;
  timestamp: number;
}

// Function to convert backend question format to frontend format
const convertQuestionFromBackend = (question: QuestionFromBackend): Question => {
  return {
    ...question,
    creationDate:
      typeof question.creationDate === "string"
        ? new Date(question.creationDate)
        : question.creationDate,
  };
};

class OfflineService {
  private connectionState: ConnectionState = ConnectionState.ONLINE;
  private pendingOperations: PendingOperation[] = [];
  private connectionListeners: ((state: ConnectionState) => void)[] = [];
  private localQuestions: Map<number, Question> = new Map();
  private categories: string[] = [];
  private initialized = false;
  private nextLocalId = -1; // Negative IDs for local-only questions

  constructor() {
    // Initialize connection state
    this.updateConnectionState();

    // Set up connection monitoring
    window.addEventListener("online", () => this.updateConnectionState());
    window.addEventListener("offline", () => {
      this.connectionState = ConnectionState.OFFLINE;
      this.notifyConnectionListeners();
    });

    // Load pending operations from IndexedDB
    this.loadFromIndexedDB();

    // Setup periodic connection check
    setInterval(() => this.updateConnectionState(), 30000);
    
    // Try to sync when coming back online
    window.addEventListener("online", () => this.trySync());
  }

  // Initialize by loading data from IndexedDB
  private async loadFromIndexedDB() {
    try {
      // Load pending operations
      const operations = await this.getFromIndexedDB("pendingOperations");
      if (operations) {
        this.pendingOperations = operations;
      }

      // Load cached questions
      const questions = await this.getFromIndexedDB("questions");
      if (questions) {
        this.localQuestions = new Map(questions.map((q: Question) => [q.id, q]));
      }

      // Load cached categories
      const categories = await this.getFromIndexedDB("categories");
      if (categories) {
        this.categories = categories;
      }
      
      // Find the lowest ID to use for new local questions
      if (this.localQuestions.size > 0) {
        const minId = Math.min(...Array.from(this.localQuestions.keys()).filter(id => id < 0));
        this.nextLocalId = minId - 1;
      }

      this.initialized = true;
      console.log("OfflineService initialized from IndexedDB");
    } catch (error) {
      console.error("Error loading from IndexedDB:", error);
    }
  }

  // Save data to IndexedDB
  private async saveToIndexedDB(key: string, data: any) {
    try {
      const dbRequest = indexedDB.open("QuestionAppDB", 1);
      
      dbRequest.onupgradeneeded = (event) => {
        const db = dbRequest.result;
        if (!db.objectStoreNames.contains("appData")) {
          db.createObjectStore("appData");
        }
      };
      
      dbRequest.onsuccess = () => {
        const db = dbRequest.result;
        const transaction = db.transaction("appData", "readwrite");
        const store = transaction.objectStore("appData");
        
        store.put(data, key);
        
        transaction.oncomplete = () => {
          db.close();
        };
      };
      
      dbRequest.onerror = (event) => {
        console.error(`IndexedDB error: ${event}`);
      };
    } catch (error) {
      console.error(`Error saving to IndexedDB: ${error}`);
    }
  }

  // Get data from IndexedDB
  private getFromIndexedDB(key: string): Promise<any> {
    return new Promise((resolve, reject) => {
      try {
        const dbRequest = indexedDB.open("QuestionAppDB", 1);
        
        dbRequest.onupgradeneeded = (event) => {
          const db = dbRequest.result;
          if (!db.objectStoreNames.contains("appData")) {
            db.createObjectStore("appData");
          }
        };
        
        dbRequest.onsuccess = () => {
          const db = dbRequest.result;
          const transaction = db.transaction("appData", "readonly");
          const store = transaction.objectStore("appData");
          const request = store.get(key);
          
          request.onsuccess = () => {
            resolve(request.result);
            db.close();
          };
          
          request.onerror = (event) => {
            reject(`Error getting from IndexedDB: ${event}`);
            db.close();
          };
        };
        
        dbRequest.onerror = (event) => {
          reject(`IndexedDB error: ${event}`);
        };
      } catch (error) {
        reject(`Error accessing IndexedDB: ${error}`);
      }
    });
  }

  // Update connection state based on server availability
  private async updateConnectionState() {
    // If browser is offline, set offline status immediately
    if (!navigator.onLine) {
      this.connectionState = ConnectionState.OFFLINE;
      this.notifyConnectionListeners();
      return;
    }

    // Try to ping the server
    try {
      const response = await fetch(`${API_BASE_URL}/ping`, {
        method: "GET",
        headers: { "Content-Type": "application/json" },
        // Short timeout for ping
        signal: AbortSignal.timeout(5000)
      });

      if (response.ok) {
        this.connectionState = ConnectionState.ONLINE;
      } else {
        this.connectionState = ConnectionState.SERVER_DOWN;
      }
    } catch (error) {
      this.connectionState = ConnectionState.SERVER_DOWN;
    }

    this.notifyConnectionListeners();
  }

  // Notify connection state listeners
  private notifyConnectionListeners() {
    for (const listener of this.connectionListeners) {
      listener(this.connectionState);
    }
  }

  // Add a connection state listener
  public addConnectionListener(listener: (state: ConnectionState) => void) {
    this.connectionListeners.push(listener);
    return () => {
      this.connectionListeners = this.connectionListeners.filter(l => l !== listener);
    };
  }

  // Get current connection state
  public getConnectionState() {
    return this.connectionState;
  }

  // Get count of pending operations
  public getPendingOperationsCount() {
    return this.pendingOperations.length;
  }

  // Apply filters to questions
  private applyFiltersAndSort(
    questions: Question[], 
    searchTerm: string = "",
    categoryFilter: string[] = [],
    difficultyFilter: number[] = [],
    orderBy: string = "difficulty",
    orderDirection: string = "asc"
  ): Question[] {
    let filtered = [...questions];
    
    // Apply search term filter
    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(q => 
        q.questionText.toLowerCase().includes(term) || 
        q.correctAnswer.toLowerCase().includes(term) ||
        q.wrongAnswers.some(a => a.toLowerCase().includes(term))
      );
    }
    
    // Apply category filter
    if (categoryFilter.length > 0) {
      filtered = filtered.filter(q => categoryFilter.includes(q.category));
    }
    
    // Apply difficulty filter
    if (difficultyFilter.length > 0) {
      filtered = filtered.filter(q => difficultyFilter.includes(q.difficulty));
    }
    
    // Apply sorting
    filtered.sort((a, b) => {
      let result = 0;
      
      if (orderBy === "difficulty") {
        result = a.difficulty - b.difficulty;
      } else if (orderBy === "date") {
        result = a.creationDate.getTime() - b.creationDate.getTime();
      }
      
      return orderDirection === "asc" ? result : -result;
    });
    
    return filtered;
  }

  // Force synchronization with server
  public async forceSync() {
    if (this.connectionState === ConnectionState.ONLINE && this.pendingOperations.length > 0) {
      return this.processPendingOperations();
    }
  }

  // Try to sync with server automatically
  private async trySync() {
    if (this.connectionState === ConnectionState.ONLINE && this.pendingOperations.length > 0) {
      return this.processPendingOperations();
    }
  }

  // Process pending operations
  private async processPendingOperations() {
    if (this.pendingOperations.length === 0) return;

    const operationsToProcess = [...this.pendingOperations];
    const successfulOperations: string[] = [];

    for (const operation of operationsToProcess) {
      try {
        let response;
        const { type, data, endpoint } = operation;

        switch (type) {
          case "create":
            response = await fetch(`${API_BASE_URL}${endpoint}`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(data)
            });
            
            if (response.ok) {
              // If this was a locally created question with negative ID, replace it
              if (data.id < 0) {
                const serverQuestion = await response.json();
                
                // Update the local questions map
                this.localQuestions.delete(data.id);
                this.localQuestions.set(serverQuestion.id, convertQuestionFromBackend(serverQuestion));
                
                // Save the updated questions to IndexedDB
                this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
              }
              
              successfulOperations.push(operation.id);
            }
            break;

          case "update":
            response = await fetch(`${API_BASE_URL}${endpoint}/${data.id}`, {
              method: "PUT",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify(data)
            });
            
            if (response.ok) {
              successfulOperations.push(operation.id);
            }
            break;

          case "delete":
            response = await fetch(`${API_BASE_URL}${endpoint}/${data.id}`, {
              method: "DELETE"
            });
            
            if (response.ok) {
              successfulOperations.push(operation.id);
            }
            break;
        }
      } catch (error) {
        console.error(`Error processing operation ${operation.id}:`, error);
      }
    }

    // Remove successful operations
    this.pendingOperations = this.pendingOperations.filter(
      op => !successfulOperations.includes(op.id)
    );

    // Save updated pending operations
    this.saveToIndexedDB("pendingOperations", this.pendingOperations);

    // Notify listeners
    this.notifyConnectionListeners();

    return successfulOperations.length > 0;
  }

  // Add a pending operation
  private addPendingOperation(type: "create" | "update" | "delete", data: any, endpoint: string) {
    const operation: PendingOperation = {
      id: `${Date.now()}-${Math.random().toString(36).substring(2, 9)}`,
      type,
      data,
      endpoint,
      timestamp: Date.now()
    };

    this.pendingOperations.push(operation);
    this.saveToIndexedDB("pendingOperations", this.pendingOperations);
    this.notifyConnectionListeners();
  }

  // Fetch questions with pagination and filtering
  public async fetchQuestions(params: URLSearchParams): Promise<Question[]> {
    await this.ensureInitialized();

    try {
      if (this.connectionState === ConnectionState.ONLINE) {
        // Online mode: fetch from server
        const response = await fetch(`${API_BASE_URL}/questions?${params.toString()}`);
        
        if (!response.ok) {
          throw new Error(`Server responded with ${response.status}`);
        }
        
        const data = await response.json();
        
        // Update local cache with the fetched questions
        data.forEach((question: QuestionFromBackend) => {
          const convertedQuestion = convertQuestionFromBackend(question);
          this.localQuestions.set(question.id, convertedQuestion);
        });
        
        this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
        
        return data.map(convertQuestionFromBackend);
      } else {
        // Offline mode: use cached data with client-side filtering
        // Parse filter parameters
        const categoryFilterParam = params.get("categories");
        const categoryFilter = categoryFilterParam ? categoryFilterParam.split(",") : [];
        
        const difficultyFilterParam = params.get("difficulties");
        const difficultyFilter = difficultyFilterParam 
          ? difficultyFilterParam.split(",").map(Number) 
          : [];
        
        const searchTerm = params.get("searchTerm") || "";
        const orderBy = params.get("orderBy") || "difficulty";
        const orderDirection = params.get("orderDirection") || "asc";
        const page = parseInt(params.get("page") || "1");
        const pageSize = parseInt(params.get("pageSize") || "10");
        
        // Apply filters and pagination
        const allQuestions = Array.from(this.localQuestions.values());
        const filteredQuestions = this.applyFiltersAndSort(
          allQuestions,
          searchTerm,
          categoryFilter,
          difficultyFilter,
          orderBy,
          orderDirection
        );
        
        // Apply pagination
        const startIndex = (page - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        return filteredQuestions.slice(startIndex, endIndex);
      }
    } catch (error) {
      console.error("Error fetching questions:", error);
      
      // Use cached data in case of error
      console.log("Using cached questions due to error");
      
      // Apply client-side filtering like in offline mode
      // Parse filter parameters
      const categoryFilterParam = params.get("categories");
      const categoryFilter = categoryFilterParam ? categoryFilterParam.split(",") : [];
      
      const difficultyFilterParam = params.get("difficulties");
      const difficultyFilter = difficultyFilterParam 
        ? difficultyFilterParam.split(",").map(Number) 
        : [];
      
      const searchTerm = params.get("searchTerm") || "";
      const orderBy = params.get("orderBy") || "difficulty";
      const orderDirection = params.get("orderDirection") || "asc";
      const page = parseInt(params.get("page") || "1");
      const pageSize = parseInt(params.get("pageSize") || "10");
      
      // Apply filters and pagination
      const allQuestions = Array.from(this.localQuestions.values());
      const filteredQuestions = this.applyFiltersAndSort(
        allQuestions,
        searchTerm,
        categoryFilter,
        difficultyFilter,
        orderBy,
        orderDirection
      );
      
      // Apply pagination
      const startIndex = (page - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      return filteredQuestions.slice(startIndex, endIndex);
    }
  }

  // Fetch a single question by ID
  public async fetchQuestion(id: string): Promise<Question | null> {
    await this.ensureInitialized();
    
    const numericId = parseInt(id);
    
    try {
      // First check if we have it in the local cache
      if (this.localQuestions.has(numericId)) {
        return this.localQuestions.get(numericId) || null;
      }
      
      // If not in cache and we're online, try to fetch from server
      if (this.connectionState === ConnectionState.ONLINE) {
        const response = await fetch(`${API_BASE_URL}/questions/${id}`);
        
        if (!response.ok) {
          if (response.status === 404) {
            return null;
          }
          throw new Error(`Server responded with ${response.status}`);
        }
        
        const data = await response.json();
        const convertedQuestion = convertQuestionFromBackend(data);
        
        // Update local cache
        this.localQuestions.set(convertedQuestion.id, convertedQuestion);
        this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
        
        return convertedQuestion;
      }
      
      // If we get here, we couldn't find the question
      return null;
    } catch (error) {
      console.error(`Error fetching question ${id}:`, error);
      
      // If we have it in cache, return it despite the error
      if (this.localQuestions.has(numericId)) {
        return this.localQuestions.get(numericId) || null;
      }
      
      return null;
    }
  }

  // Fetch categories
  public async fetchCategories(): Promise<string[]> {
    await this.ensureInitialized();
    
    try {
      // If online, fetch from server
      if (this.connectionState === ConnectionState.ONLINE) {
        const response = await fetch(`${API_BASE_URL}/categories`);
        
        if (!response.ok) {
          throw new Error(`Server responded with ${response.status}`);
        }
        
        const data = await response.json();
        
        // Update cache
        this.categories = data;
        this.saveToIndexedDB("categories", this.categories);
        
        return data;
      }
      
      // If offline, use cached data
      return this.categories;
    } catch (error) {
      console.error("Error fetching categories:", error);
      
      // Use cached data in case of error
      return this.categories;
    }
  }

  // Create a new question
  public async createQuestion(question: Question): Promise<Question | null> {
    try {
      // If online, send to server
      if (this.connectionState === ConnectionState.ONLINE) {
        const response = await fetch(`${API_BASE_URL}/questions`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(question)
        });
        
        if (!response.ok) {
          throw new Error(`Server responded with ${response.status}`);
        }
        
        const data = await response.json();
        const convertedQuestion = convertQuestionFromBackend(data);
        
        // Update local cache
        this.localQuestions.set(convertedQuestion.id, convertedQuestion);
        this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
        
        return convertedQuestion;
      } else {
        // If offline, create locally with temporary ID and queue for sync
        const localQuestion: Question = {
          ...question,
          id: this.nextLocalId--,
          creationDate: new Date()
        };
        
        // Add to local cache
        this.localQuestions.set(localQuestion.id, localQuestion);
        this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
        
        // Add to pending operations
        this.addPendingOperation("create", localQuestion, "/questions");
        
        return localQuestion;
      }
    } catch (error) {
      console.error("Error creating question:", error);
      
      // If error, create locally with temporary ID and queue for sync
      const localQuestion: Question = {
        ...question,
        id: this.nextLocalId--,
        creationDate: new Date()
      };
      
      // Add to local cache
      this.localQuestions.set(localQuestion.id, localQuestion);
      this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
      
      // Add to pending operations
      this.addPendingOperation("create", localQuestion, "/questions");
      
      return localQuestion;
    }
  }

  // Update a question
  public async updateQuestion(question: Question): Promise<boolean> {
    // Update local cache first
    this.localQuestions.set(question.id, question);
    this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
    
    try {
      // If online, send to server
      if (this.connectionState === ConnectionState.ONLINE) {
        const response = await fetch(`${API_BASE_URL}/questions/${question.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(question)
        });
        
        return response.ok;
      } else {
        // If offline, queue for sync
        this.addPendingOperation("update", question, "/questions");
        return true;
      }
    } catch (error) {
      console.error(`Error updating question ${question.id}:`, error);
      
      // Queue for sync
      this.addPendingOperation("update", question, "/questions");
      return true;
    }
  }

  // Delete a question
  public async deleteQuestion(id: number): Promise<boolean> {
    // Remove from local cache first
    this.localQuestions.delete(id);
    this.saveToIndexedDB("questions", Array.from(this.localQuestions.values()));
    
    try {
      // If online, send to server
      if (this.connectionState === ConnectionState.ONLINE) {
        const response = await fetch(`${API_BASE_URL}/questions/${id}`, {
          method: "DELETE"
        });
        
        return response.ok;
      } else {
        // If offline, queue for sync
        this.addPendingOperation("delete", { id }, "/questions");
        return true;
      }
    } catch (error) {
      console.error(`Error deleting question ${id}:`, error);
      
      // Queue for sync
      this.addPendingOperation("delete", { id }, "/questions");
      return true;
    }
  }

  // Ensure the service is initialized
  private async ensureInitialized(): Promise<void> {
    if (!this.initialized) {
      // Wait for initialization to complete
      let attempts = 0;
      while (!this.initialized && attempts < 10) {
        await new Promise(resolve => setTimeout(resolve, 100));
        attempts++;
      }
      
      if (!this.initialized) {
        console.warn("OfflineService initialization timed out");
      }
    }
  }
}

// Create and export a singleton instance
export const offlineService = new OfflineService();