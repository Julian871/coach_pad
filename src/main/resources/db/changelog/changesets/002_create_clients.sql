--liquibase formatted sql

--changeset julian:002
CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         instagram VARCHAR(255),
                         telegram VARCHAR(255),
                         phone_number VARCHAR(255),
                         comment VARCHAR(255),
                         birth_date TIMESTAMP,
                         gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_clients_user_id ON clients(user_id);
CREATE INDEX idx_clients_user_deleted ON clients(user_id, deleted);