CREATE TABLE users (
                       no BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       social_type VARCHAR(20),
                       social_id VARCHAR(255),
                       nickname VARCHAR(100),
                       name VARCHAR(100),
                       profile_url VARCHAR(255),
                       created_at DATETIME,
                       role VARCHAR(20),
                       phone VARCHAR(20),
                       address VARCHAR(255),
                       address_detail VARCHAR(255),
                       follow_count INT DEFAULT 0,
                       follower_count INT DEFAULT 0
);