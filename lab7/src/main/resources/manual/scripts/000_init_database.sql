\i 001_create_users_table.sql
\i 002_create_functions_table.sql
\i 003_create_points_table.sql

SELECT
    table_name,
    table_type
FROM information_schema.tables
WHERE table_schema = 'public'
AND table_name IN ('users', 'functions', 'points')
ORDER BY table_name;