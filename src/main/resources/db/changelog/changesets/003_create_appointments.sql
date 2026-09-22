--liquibase formatted sql

--changeset julian:003
CREATE TABLE appointments (
                              id BIGSERIAL PRIMARY KEY,
                              date_time TIMESTAMP NOT NULL,
                              plan VARCHAR(255),
                              comment VARCHAR(255),
                              type VARCHAR(20) NOT NULL CHECK (type IN ('TRAINING', 'PLAN')),
                              client_id BIGINT REFERENCES clients(id),
                              user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_appointments_user_id ON appointments(user_id);
CREATE INDEX idx_appointments_client_id ON appointments(client_id);
CREATE INDEX idx_appointments_user_date ON appointments(user_id, date_time);