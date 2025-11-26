# Детальные результаты сортировки (5000 записей)

## Время выполнения операций (мс)

| Data Size | Operation | Field | Direction | Time (ms) |
|-----------|-----------|-------|-----------|-----------|
| 5000 | User Sorting | login | ASC | 4,792 |
| 5000 | User Sorting | role | ASC | 2,471 |
| 5000 | User Sorting | login | DESC | 3,933 |
| 5000 | User Sorting | role+login | MULTI | 4,258 |
| 5000 | Function Sorting | name | ASC | 3,203 |
| 5000 | Function Sorting | user_id | ASC | 1,545 |
| 5000 | Point Sorting | x_value | ASC | 1,894 |
| 5000 | Point Sorting | y_value | DESC | 2,053 |


## Статистика

- **Размер данных**: 5000 записей
- **Самая быстрая операция**: 1,894 мс
- **Самая медленная операция**: 4,258 мс
- **Среднее время**: 3,019 мс
