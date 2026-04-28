-- 1. Criar o banco de dados
CREATE DATABASE habitflow;

-- Conecte-se ao banco habitflow e depois rode:

-- 2. Criar as tabelas
CREATE TABLE IF NOT EXISTS habits (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    frequency   VARCHAR(20)  NOT NULL,
    created_at  DATE         NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS executions (
    id          BIGSERIAL  PRIMARY KEY,
    habit_id    BIGINT     NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
    executed_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    notes       TEXT
);