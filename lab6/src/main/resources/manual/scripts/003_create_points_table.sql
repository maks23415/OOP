CREATE TABLE IF NOT EXISTS points (
    id BIGSERIAL PRIMARY KEY,
    f_id BIGINT NOT NULL,
    x_value DOUBLE PRECISION NOT NULL,
    y_value DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_points_function
        FOREIGN KEY (f_id)
        REFERENCES functions(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT unique_point_per_function
        UNIQUE (f_id, x_value)
);

COMMENT ON TABLE points IS 'Таблица точек математических функций';
COMMENT ON COLUMN points.id IS 'Уникальный идентификатор точки';
COMMENT ON COLUMN points.f_id IS 'Идентификатор функции (FK)';
COMMENT ON COLUMN points.x_value IS 'Значение координаты X';
COMMENT ON COLUMN points.y_value IS 'Значение координаты Y (результат функции)';
COMMENT ON COLUMN points.created_at IS 'Дата и время создания записи';

CREATE INDEX IF NOT EXISTS idx_points_f_id ON points(f_id);
CREATE INDEX IF NOT EXISTS idx_points_x_value ON points(x_value);
CREATE INDEX IF NOT EXISTS idx_points_y_value ON points(y_value);
CREATE INDEX IF NOT EXISTS idx_points_xy_values ON points(x_value, y_value);