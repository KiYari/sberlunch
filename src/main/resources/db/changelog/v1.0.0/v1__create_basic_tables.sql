-- liquibase formatted sql
-- changeset Kim Arthur:1

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(64),
    real_name VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role SMALLINT
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(16)
);

INSERT INTO roles (id, role_name)
VALUES (1, 'USER'), (2, 'ADMIN');