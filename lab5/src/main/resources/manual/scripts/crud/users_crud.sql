SELECT * FROM users;

SELECT * FROM users WHERE id = 1;

SELECT * FROM users WHERE login = 'admin';

SELECT * FROM users WHERE role = 'ADMIN';

SELECT * FROM users ORDER BY id LIMIT 10 OFFSET 0;

SELECT * FROM users WHERE login LIKE '%user%';

SELECT COUNT(*) as user_count FROM users;

SELECT * FROM users ORDER BY created_at DESC;

INSERT INTO users (login, role, password)
VALUES ('admin', 'ADMIN', 'hashed_password_123');

INSERT INTO users (login, role, password) VALUES
('user1', 'USER', 'pass1'),
('user2', 'MODERATOR', 'pass2'),
('user3', 'USER', 'pass3');

UPDATE users SET password = 'new_hashed_password' WHERE id = 1;

UPDATE users SET role = 'MODERATOR' WHERE login = 'user1';

UPDATE users
SET role = 'ADMIN', login = 'super_admin'
WHERE id = 1;

DELETE FROM users WHERE id = 3;

DELETE FROM users WHERE login = 'user2';

DELETE FROM users WHERE role = 'GUEST';