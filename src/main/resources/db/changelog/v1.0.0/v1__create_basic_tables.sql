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
    team_id INT,
    place_proposed VARCHAR(255),
    room_id BIGINT
);

CREATE TABLE roles (
    ID SERIAL PRIMARY KEY,
    role_name VARCHAR(16)
);

CREATE TABLE rooms (
    ID SERIAL PRIMARY KEY,
    admin_id BIGINT,
    team_amount INT
);

INSERT INTO roles (ID, role_name)
VALUES (1, 'USER'), (2, 'ADMIN');

-- changeset Kim Arthur:2
INSERT INTO rooms(ID, admin_id, team_amount)
VALUES (1, null, 2)