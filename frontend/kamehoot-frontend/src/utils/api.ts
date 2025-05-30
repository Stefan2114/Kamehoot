import { AuthService } from './auth';

const API_BASE_URL = 'https://localhost:8443';

export class ApiService {
  private static async makeRequest<T>(
    url: string,
    options: RequestInit = {}
  ): Promise<any> {
    const token = AuthService.getToken();
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...(options.headers as Record<string, string>),
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...options,
      headers,
    });

    if (response.status === 401) {
      AuthService.clearToken();
      window.location.href = '/login';
      throw new Error('Unauthorized');
    }

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const contentType = response.headers.get('content-type');
    const contentLength = response.headers.get('content-length');

    if (response.status === 204 || contentLength === '0' || 
        (!contentType?.includes('application/json') && !contentLength)) {
      return null;
    }

    // Try to parse JSON, but handle empty responses gracefully
    try {
      const text = await response.text();
      return text ? JSON.parse(text) : null;
    } catch (error) {
      // If JSON parsing fails, return null for empty responses
      return null;
    }
  }

  static async get<T>(url: string): Promise<T> {
    return this.makeRequest<T>(url, { method: 'GET' });
  }

  static async post<T>(url: string, data?: any): Promise<T> {
    return this.makeRequest<T>(url, {
      method: 'POST',
      body: data ? JSON.stringify(data) : undefined,
    });
  }

  static async put<T>(url: string, data: any): Promise<T> {
    return this.makeRequest<T>(url, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static async delete<T>(url: string): Promise<T> {
    return this.makeRequest<T>(url, { method: 'DELETE' });
  }
}