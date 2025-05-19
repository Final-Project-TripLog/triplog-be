-- 테이블 생성 순서를 조정해 외래키 참조 문제를 해결합니다
-- 먼저 독립적인 테이블들 생성

-- 사용자 테이블
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `no` BIGINT NOT NULL AUTO_INCREMENT,
                         `email` VARCHAR(255) NULL COMMENT 'UNIQUE',
                         `password` VARCHAR(255) NULL,
                         `social_type` VARCHAR(20) NULL,
                         `social_id` VARCHAR(100) NULL,
                         `nickname` VARCHAR(30) NOT NULL COMMENT 'UNIQUE',
                         `name` VARCHAR(100) NULL,
                         `profile_url` VARCHAR(1000) NULL,
                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `role` VARCHAR(20) NOT NULL,
                         `phone` VARCHAR(20) NULL COMMENT '010-0000-0000형식',
                         `address` VARCHAR(255) NULL,
                         `address_detail` VARCHAR(255) NULL,
                         `follow_count` INT NULL DEFAULT 0,
                         `follower_count` INT NULL DEFAULT 0,
                         PRIMARY KEY (`no`)
);

-- 시도 테이블
DROP TABLE IF EXISTS `sidos`;
CREATE TABLE `sidos` (
                         `no` INT NOT NULL,
                         `name` VARCHAR(20) NOT NULL,
                         PRIMARY KEY (`no`)
);

-- 구군 테이블
DROP TABLE IF EXISTS `guguns`;
CREATE TABLE `guguns` (
                          `gugun_no` INT NOT NULL,
                          `sido_no` INT NOT NULL,
                          `name` VARCHAR(20) NOT NULL,
                          PRIMARY KEY (`gugun_no`, `sido_no`),
                          FOREIGN KEY (`sido_no`) REFERENCES `sidos` (`no`)
);

-- 관광지 타입 테이블
DROP TABLE IF EXISTS `attraction_types`;
CREATE TABLE `attraction_types` (
                                    `no` INT NOT NULL,
                                    `name` VARCHAR(50) NOT NULL,
                                    PRIMARY KEY (`no`)
);

-- 관광지 테이블
DROP TABLE IF EXISTS `attractions`;
CREATE TABLE `attractions` (
                               `no` BIGINT NOT NULL AUTO_INCREMENT,
                               `title` VARCHAR(255) NOT NULL,
                               `overview` VARCHAR(10000) NULL,
                               `map_level` INT NULL DEFAULT 10,
                               `latitude` DECIMAL(20,17) NOT NULL,
                               `longitude` DECIMAL(20,17) NOT NULL,
                               `tel` VARCHAR(20) NULL,
                               `address` VARCHAR(100) NOT NULL,
                               `address_detail` VARCHAR(100) NULL,
                               `homepage` VARCHAR(1000) NULL,
                               `api_id` INT NULL,
                               `thumbnail` VARCHAR(1000) NULL,
                               `rating` DECIMAL(20,17) NULL,
                               `gugun_no` INT NULL,
                               `sido_no` INT NULL,
                               `attraction_type_no` INT NULL,
                               PRIMARY KEY (`no`),
                               FOREIGN KEY (`gugun_no`, `sido_no`) REFERENCES `guguns` (`gugun_no`, `sido_no`),
                               FOREIGN KEY (`sido_no`) REFERENCES `sidos` (`no`),
                               FOREIGN KEY (`attraction_type_no`) REFERENCES `attraction_types` (`no`)
);

-- 관광지 리뷰 테이블
DROP TABLE IF EXISTS `attraction_review`;
CREATE TABLE `attraction_review` (
                                     `no` BIGINT NOT NULL AUTO_INCREMENT,
                                     `user_nickname` VARCHAR(100) NOT NULL,
                                     `rating` INT NOT NULL,
                                     `content` TEXT NULL,
                                     `create_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `attraction_no` BIGINT NOT NULL,
                                     `user_no` BIGINT NOT NULL,
                                     PRIMARY KEY (`no`),
                                     FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`),
                                     FOREIGN KEY (`user_no`) REFERENCES `users` (`no`)
);

-- 북마크 타입 테이블
DROP TABLE IF EXISTS `bookmark_types`;
CREATE TABLE `bookmark_types` (
                                  `no` BIGINT NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(255) NOT NULL,
                                  `attraction_count` INT NULL DEFAULT 0,
                                  `user_no` BIGINT NOT NULL,
                                  PRIMARY KEY (`no`),
                                  FOREIGN KEY (`user_no`) REFERENCES `users` (`no`)
);

-- 북마크 테이블
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE `bookmark` (
                            `bookmark_type_no` BIGINT NOT NULL,
                            `attraction_no` BIGINT NOT NULL,
                            `order` INT NOT NULL,
                            PRIMARY KEY (`bookmark_type_no`, `attraction_no`),
                            FOREIGN KEY (`bookmark_type_no`) REFERENCES `bookmark_types` (`no`),
                            FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`)
);

-- 관광지 이미지 테이블
DROP TABLE IF EXISTS `attraction_image`;
CREATE TABLE `attraction_image` (
                                    `no` BIGINT NOT NULL AUTO_INCREMENT,
                                    `image_url` VARCHAR(1000) NOT NULL,
                                    `review_image_order` INT NULL,
                                    `attraction_no` BIGINT NOT NULL,
                                    `attraction_review_no` BIGINT NULL,
                                    PRIMARY KEY (`no`),
                                    FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`),
                                    FOREIGN KEY (`attraction_review_no`) REFERENCES `attraction_review` (`no`)
);

-- 팔로우 정보 테이블
DROP TABLE IF EXISTS `follow_info`;
CREATE TABLE `follow_info` (
                               `no` BIGINT NOT NULL AUTO_INCREMENT,
                               `follow` BIGINT NOT NULL,
                               `follower` BIGINT NOT NULL,
                               `create_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (`no`),
                               FOREIGN KEY (`follow`) REFERENCES `users` (`no`),
                               FOREIGN KEY (`follower`) REFERENCES `users` (`no`)
);

-- 여행 계획 테이블
DROP TABLE IF EXISTS `my_plan`;
CREATE TABLE `my_plan` (
                           `no` BIGINT NOT NULL AUTO_INCREMENT,
                           `title` VARCHAR(255) NOT NULL,
                           `description` TEXT NULL,
                           `user_no` BIGINT NOT NULL,
                           PRIMARY KEY (`no`),
                           FOREIGN KEY (`user_no`) REFERENCES `users` (`no`)
);

-- 일별 여행 계획 테이블
DROP TABLE IF EXISTS `my_daily_plan`;
CREATE TABLE `my_daily_plan` (
                                 `no` BIGINT NOT NULL AUTO_INCREMENT,
                                 `visited_date` DATE NOT NULL,
                                 `start_time` TIME NOT NULL,
                                 `end_time` TIME NOT NULL,
                                 `move_time` BIGINT NULL COMMENT '분 단위로 저장',
                                 `attraction_title` VARCHAR(255) NOT NULL,
                                 `attraction_thumbnail` VARCHAR(1000) NULL,
                                 `attraction_latitude` DECIMAL(20,17) NULL,
                                 `attraction_longitude` DECIMAL(20,17) NULL,
                                 `attraction_rating` DECIMAL(20,17) NULL,
                                 `memo` VARCHAR(255) NULL,
                                 `attraction_no` BIGINT NOT NULL,
                                 `my_plan_no` BIGINT NOT NULL,
                                 PRIMARY KEY (`no`),
                                 FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`),
                                 FOREIGN KEY (`my_plan_no`) REFERENCES `my_plan` (`no`)
);

-- 관광지 평점 통계 테이블
DROP TABLE IF EXISTS `attraction_rating_stats`;
CREATE TABLE `attraction_rating_stats` (
                                           `attraction_no` BIGINT NOT NULL,
                                           `rating_count` INT NOT NULL DEFAULT 0,
                                           `rating_sum` BIGINT NOT NULL DEFAULT 0,
                                           PRIMARY KEY (`attraction_no`),
                                           FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`)
);

-- 여행 계획 게시글 테이블
DROP TABLE IF EXISTS `plan_post`;
CREATE TABLE `plan_post` (
                             `no` BIGINT NOT NULL AUTO_INCREMENT,
                             `user_nickname` VARCHAR(100) NOT NULL,
                             `title` VARCHAR(255) NOT NULL,
                             `description` TEXT NULL,
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `user_no` BIGINT NOT NULL,
                             `fork_count` INT NULL DEFAULT 0,
                             `view_count` INT NULL DEFAULT 0,
                             `liked_count` INT NULL DEFAULT 0,
                             `thumbnail` VARCHAR(1000) NULL,
                             PRIMARY KEY (`no`),
                             FOREIGN KEY (`user_no`) REFERENCES `users` (`no`)
);

-- 계획 관광지 상세 테이블
DROP TABLE IF EXISTS `plan_attraction_detail`;
CREATE TABLE `plan_attraction_detail` (
                                          `no` BIGINT NOT NULL AUTO_INCREMENT,
                                          `visite_date` DATE NOT NULL,
                                          `start_time` TIME NOT NULL,
                                          `end_time` TIME NOT NULL,
                                          `move_time` INT NULL COMMENT '분단위로 저장',
                                          `attraction_title` VARCHAR(255) NOT NULL,
                                          `attraction_thumbnail` VARCHAR(1000) NULL,
                                          `attraction_rating` DECIMAL(20,17) NULL,
                                          `writer_rating` INT NULL,
                                          `plan_post_no` BIGINT NOT NULL,
                                          `attraction_no` BIGINT NOT NULL,
                                          `review_no` BIGINT NULL,
                                          PRIMARY KEY (`no`),
                                          FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`),
                                          FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`),
                                          FOREIGN KEY (`review_no`) REFERENCES `attraction_review` (`no`)
);

-- 게시글 태그 테이블
DROP TABLE IF EXISTS `plan_post_tag`;
CREATE TABLE `plan_post_tag` (
                                 `no` BIGINT NOT NULL AUTO_INCREMENT,
                                 `plan_post_no` BIGINT NOT NULL,
                                 `name` VARCHAR(255) NOT NULL,
                                 PRIMARY KEY (`no`),
                                 FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`)
);

-- 좋아요 테이블
DROP TABLE IF EXISTS `like_post`;
CREATE TABLE `like_post` (
                             `user_no` BIGINT NOT NULL,
                             `plan_post_no` BIGINT NOT NULL,
                             `liked_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`user_no`, `plan_post_no`),
                             FOREIGN KEY (`user_no`) REFERENCES `users` (`no`),
                             FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`)
);

-- 계획 댓글 테이블
DROP TABLE IF EXISTS `plan_comment`;
CREATE TABLE `plan_comment` (
                                `no` BIGINT NOT NULL AUTO_INCREMENT,
                                `content` VARCHAR(1000) NOT NULL,
                                `user_nickname` VARCHAR(100) NOT NULL,
                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `level` INT NOT NULL DEFAULT 0 COMMENT '최대 10',
                                `path` VARCHAR(60) NOT NULL COMMENT '00000-0000-0000...형식으로 관리 / 부모 path + 부모 자식 댓글 개수 + 1',
                                `child_count` INT NOT NULL DEFAULT 0 COMMENT '최대 9999개',
                                `plan_post_no` BIGINT NOT NULL,
                                `user_no` BIGINT NOT NULL,
                                `parent_no` BIGINT NULL,
                                PRIMARY KEY (`no`),
                                FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`),
                                FOREIGN KEY (`user_no`) REFERENCES `users` (`no`),
                                FOREIGN KEY (`parent_no`) REFERENCES `plan_comment` (`no`)
);