SELECT * FROM points;

SELECT * FROM points WHERE id = 1;

SELECT * FROM points WHERE f_id = 1;

SELECT * FROM points WHERE x_value BETWEEN -10 AND 10;

SELECT * FROM points WHERE y_value > 0;

SELECT p.*, f.name as function_name, u.login as user_login
FROM points p
JOIN functions f ON p.f_id = f.id
JOIN users u ON f.u_id = u.id;

SELECT * FROM points ORDER BY x_value;

SELECT f_id, COUNT(*) as point_count,
       MIN(x_value) as min_x, MAX(x_value) as max_x,
       MIN(y_value) as min_y, MAX(y_value) as max_y
FROM points
GROUP BY f_id;

SELECT *, ABS(y_value) as abs_y, POWER(y_value, 2) as y_squared
FROM points
WHERE f_id = 1;

INSERT INTO points (f_id, x_value, y_value)
VALUES (1, 2.5, 6.25);

INSERT INTO points (f_id, x_value, y_value) VALUES
(1, -2, 4),
(1, -1, 1),
(1, 0, 0),
(1, 1, 1),
(1, 2, 4);

INSERT INTO points (f_id, x_value, y_value)
SELECT 1, x, x * x
FROM generate_series(-10, 10) as x;

UPDATE points SET y_value = 9.0 WHERE id = 1;

UPDATE points SET x_value = 3.0, y_value = 9.0 WHERE id = 1;

UPDATE points
SET y_value = x_value * x_value
WHERE f_id = 1;

DELETE FROM points WHERE id = 5;

DELETE FROM points WHERE f_id = 1;

DELETE FROM points WHERE x_value < -100 OR x_value > 100;

DELETE FROM points WHERE y_value = 0;