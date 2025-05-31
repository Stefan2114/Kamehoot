export interface AuthResponse {
  token: string;
  expirationSeconds: number;
  requires2FA?: boolean;
  message?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
  totpCode?: number;
}

export interface TwoFaSetupResponse {
  qrCodeUrl: string;
  secretKey: string;
}

export interface TwoFaVerifyRequest {
  username: string;
  password: string;
  totpCode: number;
}