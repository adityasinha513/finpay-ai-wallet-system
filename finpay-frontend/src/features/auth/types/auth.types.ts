export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  message: string;
}

export interface RegisterResponse {
  id: number;
  name: string;
  email: string;
  message: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}