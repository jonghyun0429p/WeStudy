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
                                    delete_at DATETIME,
                                    role VARCHAR(20) DEFAULT 'USER_USER'
);

-- 4. token 테이블 생성(refresh)
CREATE TABLE IF NOT EXISTS token (
                                    user_id BIGINT KEY NOT NULL UNIQUE,
                                    token VARCHAR(255) NOT NULL
);

-- 5. post 테이블 생성
CREATE TABLE IF NOT EXISTS post (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    study_id BIGINT,
                                    views BIGINT NOT NULL,
                                    is_notice BOOLEAN DEFAULT FALSE,
                                    category VARCHAR(30) NOT NULL,
                                    title VARCHAR(100),
                                    summary VARCHAR(100),
                                    create_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
                                    delete_at DATETIME,

                                    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 6. postContent 테이블 생성
CREATE TABLE IF NOT EXISTS post_content (
                                    post_id BIGINT NOT NULL UNIQUE,
                                    content VARCHAR(1000) NOT NULL,
                                    modify_at DATETIME,

                                    CONSTRAINT fk_post_content_post FOREIGN KEY (post_id) REFERENCES post(id)
);

-- 7. study 테이블 생성
CREATE TABLE IF NOT EXISTS study (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    post_id BIGINT UNIQUE NULL,
                                    user_id BIGINT NOT NULL,
                                    title VARCHAR(100),
                                    location varchar(50),
                                    max_member BIGINT NOT NULL,
                                    state varchar(20) DEFAULT  'RECRUITING',
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    delete_at DATETIME,

                                    CONSTRAINT fk_study_post FOREIGN KEY (post_id) REFERENCES post(id),
                                    CONSTRAINT fk_study_user FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 8. study 참여자 테이블 생성
CREATE TABLE IF NOT EXISTS study_participant (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   study_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   status VARCHAR(20) DEFAULT 'WAITING', -- WAITING, APPROVED, REJECTED, CANCELLED 등

                                   UNIQUE KEY unique_participant (study_id, user_id), -- 중복 신청 방지
                                   CONSTRAINT fk_participant_study FOREIGN KEY (study_id) REFERENCES study(id),
                                   CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES user(id)
);
