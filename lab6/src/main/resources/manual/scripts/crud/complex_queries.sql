SELECT
    u.id,
    u.login,
    u.role,
    COUNT(DISTINCT f.id) as function_count,
    COUNT(DISTINCT p.id) as point_count
FROM users u
LEFT JOIN functions f ON u.id = f.u_id
LEFT JOIN points p ON f.id = p.f_id
GROUP BY u.id, u.login, u.role
ORDER BY point_count DESC;

SELECT
    f.id,
    f.name,
    f.signature,
    u.login as author,
    COUNT(p.id) as total_points,
    AVG(p.y_value) as avg_y,
    MIN(p.x_value) as min_x,
    MAX(p.x_value) as max_x
FROM functions f
JOIN users u ON f.u_id = u.id
LEFT JOIN points p ON f.id = p.f_id
GROUP BY f.id, f.name, f.signature, u.login;

SELECT
    p.*,
    f.name as function_name,
    (p.y_value - avg_stats.avg_y) / avg_stats.std_y as z_score
FROM points p
JOIN functions f ON p.f_id = f.id
CROSS JOIN (
    SELECT
        AVG(y_value) as avg_y,
        STDDEV(y_value) as std_y
    FROM points
    WHERE f_id = p.f_id
) as avg_stats
WHERE ABS((p.y_value - avg_stats.avg_y) / avg_stats.std_y) > 2;

SELECT
    f1.name as function1,
    f2.name as function2,
    p1.x_value,
    p1.y_value as y1,
    p2.y_value as y2,
    ABS(p1.y_value - p2.y_value) as difference
FROM points p1
JOIN points p2 ON p1.x_value = p2.x_value
JOIN functions f1 ON p1.f_id = f1.id
JOIN functions f2 ON p2.f_id = f2.id
WHERE f1.id < f2.id
AND ABS(p1.y_value - p2.y_value) < 0.1
ORDER BY difference ASC;