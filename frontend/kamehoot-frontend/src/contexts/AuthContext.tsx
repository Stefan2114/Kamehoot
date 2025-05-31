import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import { AuthService } from "../utils/auth";
import { ApiService } from "../utils/api";
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
      console.log("Sending login request:", credentials);
      const response = await ApiService.post<AuthResponse>(
        "/auth/login",
        credentials
      );
      console.log("Login response:", response);

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

  const register = async (credentials: LoginRequest) => {
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
    try {
      return await ApiService.post<TwoFaSetupResponse>("/auth/setup-2fa");
    } catch (error) {
      console.error("2FA setup failed:", error);
      throw error;
    }
  };

  const verify2FA = async (totpCode: number): Promise<void> => {
    try {
      await ApiService.post("/auth/verify-2fa", {
        username: "", // You'd need to store this
        password: "", // You'd need to store this
        totpCode,
      });
    } catch (error) {
      console.error("2FA verification failed:", error);
      throw error;
    }
  };
  const disable2FA = async (totpCode: number): Promise<void> => {
    try {
      await ApiService.post("/auth/disable-2fa", { totpCode });
    } catch (error) {
      console.error("2FA disable failed:", error);
      throw error;
    }
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
