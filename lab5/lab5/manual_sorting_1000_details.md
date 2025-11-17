# Детальные результаты сортировки (1000 записей)

## Время выполнения операций (мс)

| Data Size | Operation | Field | Direction | Time (ms) |
|-----------|-----------|-------|-----------|-----------|
| 1000 | User Sorting | login | ASC | 1,550 |
| 1000 | User Sorting | role | ASC | 0,846 |
| 1000 | User Sorting | login | DESC | 1,668 |
| 1000 | User Sorting | role+login | MULTI | 1,052 |
| 1000 | Function Sorting | name | ASC | 1,028 |
| 1000 | Function Sorting | user_id | ASC | 1,188 |
| 1000 | Point Sorting | x_value | ASC | 1,147 |
| 1000 | Point Sorting | y_value | DESC | 0,758 |


## Статистика

- **Размер данных**: 1000 записей
- **Самая быстрая операция**: 0,758 мс
- **Самая медленная операция**: 1,052 мс
- **Среднее время**: 1,155 мс
