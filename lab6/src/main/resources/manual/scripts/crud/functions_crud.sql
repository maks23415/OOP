SELECT * FROM functions;

SELECT * FROM functions WHERE id = 1;

SELECT * FROM functions WHERE u_id = 1;

SELECT * FROM functions WHERE name LIKE '%sin%';

SELECT f.*, u.login as user_login
FROM functions f
JOIN users u ON f.u_id = u.id;

SELECT u.login, COUNT(f.id) as function_count
FROM users u
LEFT JOIN functions f ON u.id = f.u_id
GROUP BY u.id, u.login;

SELECT * FROM functions ORDER BY created_at DESC;

INSERT INTO functions (u_id, name, signature)
VALUES (1, 'quadratic', 'f(x) = x^2');

INSERT INTO functions (u_id, name, signature) VALUES
(1, 'linear', 'f(x) = 2x + 1'),
(2, 'exponential', 'f(x) = e^x'),
(1, 'logarithmic', 'f(x) = log(x)');

UPDATE functions SET signature = 'f(x) = x^2 + 2x + 1' WHERE id = 1;

UPDATE functions SET name = 'quadratic_updated' WHERE id = 1;

UPDATE functions SET u_id = 2 WHERE id = 1;

DELETE FROM functions WHERE id = 3;

DELETE FROM functions WHERE u_id = 1;

DELETE FROM functions WHERE name LIKE '%test%';