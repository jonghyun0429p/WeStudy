-- 1️. 데이터베이스 생성 (이미 있을 경우 skip 가능)
CREATE DATABASE IF NOT EXISTS westudy DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 2. 해당 데이터베이스 사용
USE westudy;

-- 3️. user 테이블 생성
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    username VARCHAR(50) NOT NULL UNIQUE,
                                    password VARCHAR(255) NOT NULL,
                                    email VARCHAR(100) NOT NULL UNIQUE,
                                    nickname VARCHAR(50) NOT NULL,
                                    phone_number VARCHAR(20) NOT NULL,
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    role VARCHAR(20) DEFAULT 'USER_USER'
);

-- 4. token 테이블 생성(refresh)
CREATE TABLE IF NOT EXISTS token (
                                    userid BIGINT KEY,
                                    token VARCHAR(255) NOT NULL
);