export type Role = 'ROLE_ADMIN' | 'ROLE_WARDEN' | 'ROLE_STUDENT' | 'ROLE_STAFF' | 'ROLE_HIGHER_OFFICIAL';

export interface UserDto {
  id: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  active: boolean;
  roles: Role[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: UserDto;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phoneNumber?: string;
  roles: Role[];
}
