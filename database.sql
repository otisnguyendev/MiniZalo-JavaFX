CREATE DATABASE mini_zalo;

USE mini_zalo;

CREATE TABLE client (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

select  * from client;
	    
-- username: testuser1, password: 123456789
-- username: testuser2, password: 123456789

INSERT INTO client (username, email, password)
VALUES ('testuser2', 'test2@gmail.com', '123456789');
