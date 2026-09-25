import { axiosClient } from './axiosClient';
import { AuthRequest, AuthResponse, RegisterRequest, UserDto } from '../types/auth';

export const authApi = {
  login: async (data: AuthRequest): Promise<AuthResponse> => {
    const response = await axiosClient.post<AuthResponse>('/auth/login', data);
    return response.data;
  },
  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await axiosClient.post<AuthResponse>('/auth/register', data);
    return response.data;
  },
  logout: async (): Promise<void> => {
    await axiosClient.post('/auth/logout');
  },
  getCurrentUser: async (): Promise<UserDto> => {
    const response = await axiosClient.get<UserDto>('/users/me');
    return response.data;
  },
};
