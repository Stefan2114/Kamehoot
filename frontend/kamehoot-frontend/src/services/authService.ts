import { TwoFaSetupResponse } from "../types/auth";
import { ApiService } from "./apiService";

export class AuthService {
  private static TOKEN_KEY = 'auth_token';
  private static EXPIRY_KEY = 'auth_expiry';
  private static USER_KEY = 'auth_user';

  static setToken(token: string, expiresInSeconds: number): void {
    const expiryTime = Date.now() + (expiresInSeconds * 1000);
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.EXPIRY_KEY, expiryTime.toString());
  }

  static setCurrentUser(user: {username: string }): void {
  localStorage.setItem(this.USER_KEY, JSON.stringify(user));
}

static getCurrentUser(): { id: number; username: string; roles?: string[] } | null {
  const user = localStorage.getItem(this.USER_KEY);
  return user ? JSON.parse(user) : null;
}

  static getToken(): string | null {
    const token = localStorage.getItem(this.TOKEN_KEY);
    const expiry = localStorage.getItem(this.EXPIRY_KEY);
    
    if (!token || !expiry) return null;
    
    if (Date.now() > parseInt(expiry)) {
      this.clearToken();
      return null;
    }
    
    return token;
  }


  //needs to be separated
  static clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.EXPIRY_KEY);
      localStorage.removeItem(this.USER_KEY);
  }

  static isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  // 2FA API methods
  static async setup2FA(): Promise<TwoFaSetupResponse> {
    return ApiService.post<TwoFaSetupResponse>('/auth/setup-2fa');
  }

  static async verify2FA(totpCode: number): Promise<string> {
    return ApiService.post<string>('/auth/verify-2fa', { totpCode });
  }

  static async disable2FA(totpCode: number): Promise<string> {
    return ApiService.post<string>('/auth/disable-2fa', { totpCode });
  }
}