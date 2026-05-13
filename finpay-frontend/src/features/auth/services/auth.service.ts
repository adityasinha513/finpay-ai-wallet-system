import { api } from "@/lib/api";

import {
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
  ApiResponse,
} from "../types/auth.types";

export async function registerUser(
  payload: RegisterRequest
) {

  const response =
    await api.post<
      ApiResponse<RegisterResponse>
    >(
      "/auth/register",
      payload
    );

  return response.data.data;
}

export async function loginUser(
  payload: LoginRequest
) {

  const response =
    await api.post<
      ApiResponse<LoginResponse>
    >(
      "/auth/login",
      payload
    );

  return response.data.data;
}