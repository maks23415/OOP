## Базовые настройки
- Базовый URL: /api/v1
- Формат: JSON

## Эндпоинты

### Пользователи (Users)
- `GET    /api/v1/users` - получить всех пользователей
- `GET    /api/v1/users/{id}` - получить пользователя по ID
- `POST   /api/v1/users` - создать пользователя
- `PUT    /api/v1/users/{id}` - обновить пользователя
- `DELETE /api/v1/users/{id}` - удалить пользователя

### Функции (Functions)
- `GET    /api/v1/functions` - получить все функции
- `GET    /api/v1/functions/{id}` - получить функцию по ID
- `GET    /api/v1/users/{userId}/functions` - получить функции пользователя
- `POST   /api/v1/functions` - создать функцию
- `PUT    /api/v1/functions/{id}` - обновить функцию
- `DELETE /api/v1/functions/{id}` - удалить функцию

### Точки (Points)
- `GET    /api/v1/points` - получить все точки
- `GET    /api/v1/points/{id}` - получить точку по ID
- `GET    /api/v1/functions/{functionId}/points` - получить точки функции
- `POST   /api/v1/points` - создать точку
- `PUT    /api/v1/points/{id}` - обновить точку
- `DELETE /api/v1/points/{id}` - удалить точку

## DTO структуры

### User DTOs
```json
// CreateUserRequest
{
  "login": "string",
  "password": "string", 
  "role": "string"
}

// UserResponse  
{
  "id": "long",
  "login": "string",
  "role": "string"
}

### Function DTOs

// CreateFunctionRequest
{
  "userId": "long",
  "name": "string",
  "signature": "string"
}

// FunctionResponse
{
  "id": "long", 
  "userId": "long",
  "name": "string",
  "signature": "string"
}

### Point DTOs

// CreatePointRequest
{
  "functionId": "long",
  "xValue": "number",
  "yValue": "number"
}

// PointResponse
{
  "id": "long",
  "functionId": "long", 
  "xValue": "number",
  "yValue": "number"
}