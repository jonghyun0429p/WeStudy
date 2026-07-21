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
                                    modified_at DATETIME,
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
                                    address VARCHAR(255) NULL,
                                    latitude DOUBLE NULL,
                                    longitude DOUBLE NULL,
                                    create_at DATETIME DEFAULT  CURRENT_TIMESTAMP,
                                    modified_at DATETIME,
                                    delete_at DATETIME,

                                    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 6. postContent 테이블 생성
CREATE TABLE IF NOT EXISTS post_content (
                                    post_id BIGINT NOT NULL UNIQUE,
                                    content VARCHAR(1000) NOT NULL,
                                    modified_at DATETIME,

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
                                    deadline DATETIME NULL,
                                    state varchar(20) DEFAULT  'RECRUITING',
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    modified_at DATETIME,
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
                                   modified_at DATETIME,
                                   status VARCHAR(20) DEFAULT 'WAITING', -- WAITING, APPROVED, REJECTED, CANCELLED 등

                                   UNIQUE KEY unique_participant (study_id, user_id), -- 중복 신청 방지
                                   CONSTRAINT fk_participant_study FOREIGN KEY (study_id) REFERENCES study(id),
                                   CONSTRAINT fk_participant_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS comment (
                                    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    user_id BIGINT NOT NULL,
                                    post_id BIGINT NOT NULL,
                                    content VARCHAR(100) NOT NULL,
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                    modified_at DATETIME,
                                    delete_at DATETIME,

                                    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user(id),
                                    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TABLE IF NOT EXISTS likes (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     post_id BIGINT NULL,
                                     user_id BIGINT NOT NULL,
                                     comment_id BIGINT NULL,
                                     like_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_likes_post FOREIGN KEY (post_id) REFERENCES post(id),
                                     CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES user(id),
                                     CONSTRAINT fk_likes_comment FOREIGN KEY (comment_id) REFERENCES comment(id),

                                     UNIQUE KEY ux_likes_post_user (post_id, user_id),
                                     UNIQUE KEY ux_likes_comment_user (comment_id, user_id)
);

CREATE TABLE IF NOT EXISTS like_count (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        post_id BIGINT NULL,
                                        comment_id BIGINT NULL,
                                        like_count BIGINT NOT NULL DEFAULT 0,

                                        CONSTRAINT fk_like_count_post FOREIGN KEY (post_id) REFERENCES post(id),
                                        CONSTRAINT fk_like_count_comment FOREIGN KEY (comment_id) REFERENCES comment(id),

                                        UNIQUE KEY ux_cnt_post (post_id),
                                        UNIQUE KEY ux_cnt_comment (comment_id)
);

CREATE TABLE IF NOT EXISTS bookmark (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    post_id BIGINT NULL,
                                    user_id BIGINT NOT NULL,

                                    CONSTRAINT fk_bookmark_post FOREIGN KEY (post_id) REFERENCES post(id),
                                    CONSTRAINT fk_bookmark_user FOREIGN KEY (user_id) REFERENCES user(id),

                                    UNIQUE KEY uk_bookmark_post_user (post_id, user_id),
                                    KEY uk_bookmark_post (post_id)
);

-- 10. 채팅 테이블 생성
CREATE TABLE IF NOT EXISTS chat_message (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    study_id BIGINT NOT NULL,
                                    sender_id BIGINT NOT NULL,
                                    message VARCHAR(500) NOT NULL,
                                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_chat_message_study FOREIGN KEY (study_id) REFERENCES study(id),
                                    CONSTRAINT fk_chat_message_user FOREIGN KEY (sender_id) REFERENCES user(id)
);

-- 알람 테이블
CREATE TABLE IF NOT EXISTS `alarm` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `receiver_id` BIGINT NOT NULL COMMENT '수신자 ID',
  `sender_id` BIGINT NOT NULL COMMENT '발신자 ID',
  `type` VARCHAR(50) NOT NULL COMMENT '알람 타입 (COMMENT, STUDY_APPROVE, CHAT_MESSAGE 등)',
  `content` TEXT NOT NULL COMMENT '알람 내용',
  `target_url` VARCHAR(255) COMMENT '클릭 시 이동할 URL',
  `is_read` BOOLEAN DEFAULT FALSE COMMENT '읽음 여부',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_receiver_id` (`receiver_id`),
  FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. 스터디 일지(로그) 테이블 생성
CREATE TABLE IF NOT EXISTS `study_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `study_id` BIGINT NOT NULL COMMENT '스터디 ID',
  `user_id` BIGINT NOT NULL COMMENT '작성자 ID',
  `title` VARCHAR(100) NOT NULL COMMENT '일지 제목',
  `content` TEXT NOT NULL COMMENT '일지 내용',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
  `modified_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
  INDEX `idx_study_log_study_id` (`study_id`),
  CONSTRAINT `fk_study_log_study` FOREIGN KEY (`study_id`) REFERENCES `study` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_study_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
