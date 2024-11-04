CREATE DATABASE mini_zalo;

USE mini_zalo;

CREATE TABLE client
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- lưu nội dung tin nhắn văn bảng
CREATE TABLE message
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    sender_id   INT NOT NULL,
    receiver_id INT NOT NULL,
    content     TEXT,
    timestamp   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES client (id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES client (id) ON DELETE CASCADE
);

-- lưu hình ảnh/tệp tin
CREATE TABLE attachment
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    message_id       INT                    NOT NULL,
    file_path        VARCHAR(255)           NOT NULL,
    file_type        ENUM ('image', 'file') NOT NULL,    -- Loại tệp đính kèm (hình ảnh hoặc tệp tin)
    FOREIGN KEY (message_id) REFERENCES message (id) ON DELETE CASCADE
);



select *
from client;

-- username: testuser2,3,4,5: password: 123456789
INSERT INTO client (username, email, password)
VALUES ('testuser2', 'test2@gmail.com', '123456789');
