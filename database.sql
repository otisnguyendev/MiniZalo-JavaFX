CREATE DATABASE mini_zalo;

USE mini_zalo;

CREATE TABLE client (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

select  * from client;
	    
-- username: demoUser, password: 12345678 
INSERT INTO client (username, email, password)
VALUES ('demoUser', 'demo@example.com', '$2a$10$7RTP3kpZ7o.OvppqOGCHP.aXHhfuZPzayYRcoy4zUhz5oBlVtXIlO');
