export interface AuthResponse {
  token: string;
  expiresInSeconds: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}