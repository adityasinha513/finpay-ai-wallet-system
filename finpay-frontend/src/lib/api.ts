import axios from "axios";

export const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
});

api.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = localStorage.getItem("token");
    const url = config.url ?? "";
    const isAuthRoute =
      url.includes("/auth/login") ||
      url.includes("/auth/register");

    config.headers = config.headers ?? {};

    if (token && !isAuthRoute) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }

  return config;
});