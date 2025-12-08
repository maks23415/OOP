-- Enable UUID extension if needed
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(100) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create functions table
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
);

-- Create points table
CREATE TABLE IF NOT EXISTS points (
    id BIGSERIAL PRIMARY KEY,
    f_id BIGINT NOT NULL,
    x_value DOUBLE PRECISION NOT NULL,
    y_value DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_points_function
        FOREIGN KEY (f_id)
        REFERENCES functions(id)
        ON DELETE CASCADE,
    CONSTRAINT unique_point_per_function
        UNIQUE (f_id, x_value)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_login ON users(login);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_functions_user_id ON functions(u_id);
CREATE INDEX IF NOT EXISTS idx_functions_name ON functions(name);
CREATE INDEX IF NOT EXISTS idx_points_function_id ON points(f_id);
CREATE INDEX IF NOT EXISTS idx_points_x_value ON points(x_value);
CREATE INDEX IF NOT EXISTS idx_points_y_value ON points(y_value);

-- Insert default users
INSERT INTO users (login, role, password) VALUES
('admin', 'ADMIN', 'admin123'),
('user1', 'USER', 'password1'),
('user2', 'USER', 'password2')
ON CONFLICT (login) DO NOTHING;

-- Create update timestamp function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for updated_at
CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_functions_updated_at
    BEFORE UPDATE ON functions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();