CREATE DATABASE mini_zalo;

USE mini_zalo;

CREATE TABLE client (
    id VARCHAR(255) PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255)
);
