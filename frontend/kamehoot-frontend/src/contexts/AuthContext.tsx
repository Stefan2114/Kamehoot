import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import { AuthService } from "../services/authService";
import { ApiService } from "../services/apiService";
import { AuthResponse, LoginRequest, TwoFaSetupResponse } from "../types/auth";

interface AuthContextType {
  isAuthenticated: boolean;
  login: (credentials: LoginRequest) => Promise<AuthResponse>;
  register: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  loading: boolean;
  setup2FA: () => Promise<TwoFaSetupResponse>;
  verify2FA: (totpCode: number) => Promise<void>;
  disable2FA: (totpCode: number) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = AuthService.getToken();
    setIsAuthenticated(!!token);
    setLoading(false);
  }, []);

  const login = async (credentials: LoginRequest): Promise<AuthResponse> => {
    try {
      const response = await ApiService.post<AuthResponse>(
        "/auth/login",
        credentials
      );

      if (!response.requires2FA) {
        AuthService.setToken(response.token, response.expirationSeconds);
        setIsAuthenticated(true);
      }

      return response;
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  const register = async (credentials: LoginRequest): Promise<void> => {
    try {
      const response = await ApiService.post<AuthResponse>(
        "/auth/register",
        credentials
      );
      AuthService.setToken(response.token, response.expirationSeconds);
      setIsAuthenticated(true);
    } catch (error) {
      console.error("Registration failed:", error);
      throw error;
    }
  };

  const setup2FA = async (): Promise<TwoFaSetupResponse> => {
    return ApiService.post<TwoFaSetupResponse>("/auth/setup-2fa");
  };

  const verify2FA = async (totpCode: number): Promise<void> => {
    const response = await ApiService.post<AuthResponse>("/auth/verify-2fa", {
      totpCode,
    });

    AuthService.setToken(response.token, response.expirationSeconds);
    setIsAuthenticated(true);
  };

  const disable2FA = async (totpCode: number): Promise<void> => {
    await ApiService.post("/auth/disable-2fa", { totpCode });
  };

  const logout = () => {
    AuthService.clearToken();
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        login,
        register,
        logout,
        loading,
        setup2FA,
        verify2FA,
        disable2FA,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
