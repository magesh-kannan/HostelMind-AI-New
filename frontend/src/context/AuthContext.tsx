import React, { createContext, useContext, useEffect, useState } from 'react';
import { UserDto, AuthRequest, RegisterRequest } from '../types/auth';
import { authApi } from '../api/authApi';

interface AuthContextType {
  user: UserDto | null;
  loading: boolean;
  login: (data: AuthRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
  toggleTheme: () => void;
  mode: 'dark' | 'light';
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [mode, setMode] = useState<'dark' | 'light'>('dark');

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('access_token');
      if (token) {
        try {
          const currentUser = await authApi.getCurrentUser();
          setUser(currentUser);
        } catch {
          localStorage.removeItem('access_token');
          localStorage.removeItem('refresh_token');
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (data: AuthRequest) => {
    const res = await authApi.login(data);
    localStorage.setItem('access_token', res.accessToken);
    localStorage.setItem('refresh_token', res.refreshToken);
    setUser(res.user);
  };

  const register = async (data: RegisterRequest) => {
    const res = await authApi.register(data);
    localStorage.setItem('access_token', res.accessToken);
    localStorage.setItem('refresh_token', res.refreshToken);
    setUser(res.user);
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } catch {
      // ignore
    } finally {
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      setUser(null);
    }
  };

  const toggleTheme = () => {
    setMode((prev) => (prev === 'dark' ? 'light' : 'dark'));
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, toggleTheme, mode }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
