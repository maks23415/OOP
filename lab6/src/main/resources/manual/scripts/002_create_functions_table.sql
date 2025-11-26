CREATE TABLE IF NOT EXISTS functions (
    id BIGSERIAL PRIMARY KEY,
    u_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    signature TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_functions_user
        FOREIGN KEY (u_id)
        REFERENCES users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

COMMENT ON TABLE functions IS 'Таблица математических функций';
COMMENT ON COLUMN functions.id IS 'Уникальный идентификатор функции';
COMMENT ON COLUMN functions.u_id IS 'Идентификатор пользователя-владельца функции (FK)';
COMMENT ON COLUMN functions.name IS 'Название функции';
COMMENT ON COLUMN functions.signature IS 'Сигнатура функции (математическое выражение)';
COMMENT ON COLUMN functions.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN functions.updated_at IS 'Дата и время последнего обновления записи';

CREATE INDEX IF NOT EXISTS idx_functions_u_id ON functions(u_id);
CREATE INDEX IF NOT EXISTS idx_functions_name ON functions(name);

CREATE TRIGGER trigger_functions_updated_at
    BEFORE UPDATE ON functions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();