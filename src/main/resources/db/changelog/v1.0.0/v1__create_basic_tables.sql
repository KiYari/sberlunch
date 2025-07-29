-- liquibase formatted sql
-- changeset Kim Arthur:1

CREATE TABLE users (
    ID SERIAL PRIMARY KEY,
    username VARCHAR(64),
    real_name VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role INT,
    registration_status VARCHAR(16),
    activity_status VARCHAR(16),
    team_id BIGINT,
    place_proposed VARCHAR(64)
);

CREATE TABLE roles (
    ID SERIAL PRIMARY KEY,
    role_name VARCHAR(16)
);

CREATE TABLE teams (
    ID SERIAL PRIMARY KEY
)

INSERT INTO roles (ID, role_name)
VALUES (1, 'USER'), (2, 'ADMIN');