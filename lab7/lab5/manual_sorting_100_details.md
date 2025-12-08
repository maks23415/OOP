# Детальные результаты сортировки (100 записей)

## Время выполнения операций (мс)

| Data Size | Operation | Field | Direction | Time (ms) |
|-----------|-----------|-------|-----------|-----------|
| 100 | User Sorting | login | ASC | 2,026 |
| 100 | User Sorting | role | ASC | 0,842 |
| 100 | User Sorting | login | DESC | 0,558 |
| 100 | User Sorting | role+login | MULTI | 0,819 |
| 100 | Function Sorting | name | ASC | 0,840 |
| 100 | Function Sorting | user_id | ASC | 1,203 |
| 100 | Point Sorting | x_value | ASC | 1,223 |
| 100 | Point Sorting | y_value | DESC | 0,990 |


## Статистика

- **Размер данных**: 100 записей
- **Самая быстрая операция**: 0,990 мс
- **Самая медленная операция**: 0,819 мс
- **Среднее время**: 1,062 мс
