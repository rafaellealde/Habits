-- Schema inicial do HabitFlow
-- Executado automaticamente pelo PostgreSQL ao criar o container (docker-entrypoint-initdb.d)

CREATE TABLE IF NOT EXISTS habits (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    frequency   VARCHAR(50)  NOT NULL,
    created_at  DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS executions (
    id          BIGSERIAL  PRIMARY KEY,
    habit_id    BIGINT     NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    executed_at TIMESTAMP  NOT NULL DEFAULT NOW()
);
