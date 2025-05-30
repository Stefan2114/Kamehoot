export interface Question {
  id: string; // Changed to string (UUID)
  creationDate: Date;
  questionText: string;
  category: string;
  correctAnswer: string;
  wrongAnswers: string[];
  difficulty: number;
}

export interface QuestionFromBackend {
  id: string;
  creationDate: string;
  questionText: string;
  category: string;
  correctAnswer: string;
  wrongAnswers: string[];
  difficulty: number;
}

export interface QuestionDTO {
  id?: string;
  creationDate: string;
  questionText: string;
  category: string;
  correctAnswer: string;
  wrongAnswers: string[];
  difficulty: number;
}