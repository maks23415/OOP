# API Контракт для Framework (Spring Boot)

## Общие настройки
- **Базовый URL**: `/api/v1`
- **Формат**: JSON
- **Content-Type**: `application/json`

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

## Entity структуры (с учетом связей JPA)

### User Entity
```json
{
  "id": "long",
  "login": "string",
  "password": "string",
  "role": "string",
  "functions": []  // массив функций (только при явном запросе)
}

  Function Entity
  "id": "long", 
  "name": "string",
  "signature": "string",
  "user": {
    "id": "long",
    "login": "string",
    "role": "string"
  },
  "points": []  // массив точек (только при явном запросе)
}
  Point Entity
{
  "id": "long",
  "xValue": "number",
  "yValue": "number", 
  "function": {
    "id": "long",
    "name": "string",
    "signature": "string"
  }
}