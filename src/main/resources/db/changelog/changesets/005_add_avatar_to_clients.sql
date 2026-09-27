--liquibase formatted sql

--changeset julian:005
ALTER TABLE clients ADD COLUMN avatar_url VARCHAR(255);