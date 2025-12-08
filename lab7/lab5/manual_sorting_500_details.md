# Детальные результаты сортировки (500 записей)

## Время выполнения операций (мс)

| Data Size | Operation | Field | Direction | Time (ms) |
|-----------|-----------|-------|-----------|-----------|
| 500 | User Sorting | login | ASC | 1,128 |
| 500 | User Sorting | role | ASC | 1,335 |
| 500 | User Sorting | login | DESC | 1,743 |
| 500 | User Sorting | role+login | MULTI | 1,478 |
| 500 | Function Sorting | name | ASC | 0,923 |
| 500 | Function Sorting | user_id | ASC | 0,517 |
| 500 | Point Sorting | x_value | ASC | 1,311 |
| 500 | Point Sorting | y_value | DESC | 1,909 |


## Статистика

- **Размер данных**: 500 записей
- **Самая быстрая операция**: 1,311 мс
- **Самая медленная операция**: 1,478 мс
- **Среднее время**: 1,293 мс
