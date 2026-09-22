--liquibase formatted sql

--changeset julian:001
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       telegram_id BIGINT NOT NULL UNIQUE,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('TRAINER', 'CLIENT', 'ADMIN')),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP
);

CREATE INDEX idx_users_telegram_id ON users(telegram_id);