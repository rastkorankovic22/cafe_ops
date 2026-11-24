export type UserRole = 'OWNER' | 'WAITER';

export interface LoginResponse {
  accessToken: string;
  userId: number;
  email: string;
  role: UserRole;
  fullName: string;
}

export interface SessionUser {
  userId: number;
  email: string;
  role: UserRole;
  fullName: string;
}