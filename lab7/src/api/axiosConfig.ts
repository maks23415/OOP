// src/api/axiosConfig.ts в Lab 7
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

// Создаем axios инстанс с базовой авторизацией
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Добавляем интерцептор для Basic Auth
apiClient.interceptors.request.use(
  (config) => {
    // Получаем логин и пароль из localStorage или контекста
    const authData = localStorage.getItem('auth_data');

    if (authData) {
      const { username, password } = JSON.parse(authData);
      const token = btoa(`${username}:${password}`); // Base64 encode
      config.headers.Authorization = `Basic ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Интерцептор для обработки 401 ошибок (не авторизован)
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Перенаправляем на страницу логина
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;