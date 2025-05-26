

-- 외래 키 제약조건 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 테이블 전체 삭제
DROP TABLE IF EXISTS `users`,
    `plan_post_tag`,
    `follow_info`,
    `attraction_review`,
    `my_plan`,
    `my_daily_plan`,
    `attraction_image`,
    `bookmark`,
    `like_post`,
    `plan_attraction_detail`,
    `plan_comment`,
    `attractions`,
    `attraction_types`,
    `plan_post`,
    `bookmark_types`;

-- 모든 테이블 생성

CREATE TABLE `users` (
                         `no` BIGINT NOT NULL AUTO_INCREMENT,
                         `email` VARCHAR(255) NULL COMMENT 'UNIQUE',
                         `password` VARCHAR(255) NULL,
                         `social_type` VARCHAR(20) NULL,
                         `social_id` VARCHAR(100) NULL,
                         `nickname` VARCHAR(30) NOT NULL COMMENT 'UNIQUE',
                         `profile_url` VARCHAR(1000) NULL,
                         `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `role` VARCHAR(20) NOT NULL,
                         `phone` VARCHAR(20) NULL COMMENT '010-0000-0000형식',
                         `address` VARCHAR(255) NULL,
                         `address_detail` VARCHAR(255) NULL,
                         `follow_count` INT NULL,
                         `follower_count` INT NULL,
                         `name` VARCHAR(20) NULL,
                         PRIMARY KEY (`no`)
);

CREATE TABLE `plan_post` (
                             `no` BIGINT NOT NULL AUTO_INCREMENT,
                             `user_nickname` VARCHAR(100) NOT NULL,
                             `title` VARCHAR(255) NOT NULL,
                             `description` TEXT NULL,
                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `user_no` BIGINT NOT NULL,
                             `fork_count` INT NULL,
                             `view_count` INT NULL,
                             `liked_count` INT NULL,
                             `thumbnail` VARCHAR(1000) NULL,
                             `start_day` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                             `end_day` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                             `total_member` BIGINT NULL DEFAULT 0,
                             PRIMARY KEY (`no`)
);

CREATE TABLE `plan_post_tag` (
                                 `no` BIGINT NOT NULL AUTO_INCREMENT,
                                 `plan_post_no` BIGINT NOT NULL,
                                 `name` VARCHAR(255) NOT NULL,
                                 PRIMARY KEY (`no`)
);

CREATE TABLE `follow_info` (
                               `no` BIGINT NOT NULL AUTO_INCREMENT,
                               `follow` BIGINT NOT NULL,
                               `follower` BIGINT NOT NULL,
                               `create_at` DATETIME NULL,
                               PRIMARY KEY (`no`)
);

CREATE TABLE `attraction_review` (
                                     `no` BIGINT NOT NULL AUTO_INCREMENT,
                                     `user_nickname` VARCHAR(100) NOT NULL,
                                     `rating` INT NOT NULL,
                                     `content` TEXT NULL,
                                     `create_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `update_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `attraction_no` BIGINT NOT NULL,
                                     `user_no` BIGINT NOT NULL,
                                     PRIMARY KEY (`no`)
);

CREATE TABLE `my_plan` (
                           `no` BIGINT NOT NULL AUTO_INCREMENT,
                           `title` VARCHAR(255) NOT NULL,
                           `description` TEXT NULL,
                           `user_no` BIGINT NOT NULL,
                           `start_day` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                           `end_day` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                           `total_member` BIGINT NULL DEFAULT 0,
                           PRIMARY KEY (`no`)
);

CREATE TABLE `my_daily_plan` (
                                 `no` BIGINT NOT NULL AUTO_INCREMENT,
                                 `visited_date` DATE NOT NULL,
                                 `start_time` TIME NOT NULL,
                                 `end_time` TIME NOT NULL,
                                 `move_time` BIGINT NULL,
                                 `attraction_title` VARCHAR(255) NOT NULL,
                                 `attraction_thumbnail` VARCHAR(1000) NULL,
                                 `attraction_latitude` DECIMAL(20, 17) NULL,
                                 `attraction_longitude` DECIMAL(20, 17) NULL,
                                 `attraction_rating` DECIMAL(20, 17) NULL,
                                 `memo` VARCHAR(255) NULL,
                                 `attraction_no` BIGINT NOT NULL,
                                 `my_plan_no` BIGINT NOT NULL,
                                 PRIMARY KEY (`no`)
);

CREATE TABLE `attraction_image` (
                                    `no` BIGINT NOT NULL AUTO_INCREMENT,
                                    `image_url` VARCHAR(1000) NOT NULL,
                                    `review_image_order` INT NULL,
                                    `attraction_no` BIGINT NOT NULL,
                                    `attraction_review_no` BIGINT NULL,
                                    PRIMARY KEY (`no`)
);

CREATE TABLE `bookmark` (
                            `bookmark_type_no` BIGINT NOT NULL,
                            `attraction_no` BIGINT NOT NULL,
                            `order` INT NOT NULL,
                            PRIMARY KEY (
                                         `bookmark_type_no`,
                                         `attraction_no`
                                )
);

CREATE TABLE `like_post` (
                             `user_no` BIGINT NOT NULL,
                             `plan_post_no` BIGINT NOT NULL,
                             `liked_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`user_no`, `plan_post_no`)
);

CREATE TABLE `plan_attraction_detail` (
                                          `no` BIGINT NOT NULL AUTO_INCREMENT,
                                          `visite_date` DATE NOT NULL,
                                          `start_time` TIME NOT NULL,
                                          `end_time` TIME NOT NULL,
                                          `move_time` INT NULL,
                                          `attraction_title` VARCHAR(255) NOT NULL,
                                          `attraction_thumbnail` VARCHAR(1000) NULL,
                                          `attraction_rating` DECIMAL(20, 17) NULL,
                                          `writer_rating` INT NULL,
                                          `plan_post_no` BIGINT NOT NULL,
                                          `attraction_no` BIGINT NOT NULL,
                                          `review_no` BIGINT NULL,
                                          PRIMARY KEY (`no`)
);

CREATE TABLE `plan_comment` (
                                `no` BIGINT NOT NULL AUTO_INCREMENT,
                                `content` VARCHAR(1000) NOT NULL,
                                `user_nickname` VARCHAR(100) NOT NULL,
                                `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `level` INT NOT NULL DEFAULT 0,
                                `path` VARCHAR(60) NOT NULL,
                                `child_count` INT NOT NULL DEFAULT 0,
                                `plan_post_no` BIGINT NOT NULL,
                                `user_no` BIGINT NOT NULL,
                                `parent_no` BIGINT NULL,
                                PRIMARY KEY (`no`)
);

CREATE TABLE `attractions` (
                               `no` BIGINT NOT NULL AUTO_INCREMENT,
                               `title` VARCHAR(255) NOT NULL,
                               `overview` VARCHAR(10000) NULL,
                               `map_level` INT NULL DEFAULT 10,
                               `latitude` DECIMAL(20, 17) NOT NULL,
                               `longitude` DECIMAL(20, 17) NOT NULL,
                               `tel` VARCHAR(20) NULL,
                               `address` VARCHAR(100) NOT NULL,
                               `address_detail` VARCHAR(100) NULL,
                               `homepage` VARCHAR(1000) NULL,
                               `api_id` INT NULL,
                               `thumbnail` VARCHAR(1000) NULL,
                               `ratingSum` DECIMAL(20, 17) NULL,
                               `reviewCount` INT NULL,
                               `attraction_type_name` VARCHAR(50) NULL,
                               PRIMARY KEY (`no`)
);

CREATE TABLE `attraction_types` (
                                    `name` VARCHAR(50) NOT NULL,
                                    PRIMARY KEY (`name`)
);

CREATE TABLE `bookmark_types` (
                                  `no` BIGINT NOT NULL AUTO_INCREMENT,
                                  `name` VARCHAR(255) NOT NULL,
                                  `attraction_count` INT NULL DEFAULT 0,
                                  `user_no` BIGINT NOT NULL,
                                  PRIMARY KEY (`no`)
);

-- 외래 키 제약조건 재설정
ALTER TABLE `plan_post_tag`
    ADD FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`);

ALTER TABLE `follow_info`
    ADD FOREIGN KEY (`follow`) REFERENCES `users` (`no`);

ALTER TABLE `follow_info`
    ADD FOREIGN KEY (`follower`) REFERENCES `users` (`no`);

ALTER TABLE `attraction_review`
    ADD FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`);

ALTER TABLE `attraction_review`
    ADD FOREIGN KEY (`user_no`) REFERENCES `users` (`no`);

ALTER TABLE `my_plan`
    ADD FOREIGN KEY (`user_no`) REFERENCES `users` (`no`);

ALTER TABLE `my_daily_plan`
    ADD FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`);

ALTER TABLE `my_daily_plan`
    ADD FOREIGN KEY (`my_plan_no`) REFERENCES `my_plan` (`no`);

ALTER TABLE `attraction_image`
    ADD FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`);

ALTER TABLE `attraction_image`
    ADD FOREIGN KEY (`attraction_review_no`) REFERENCES `attraction_review` (`no`);

ALTER TABLE `bookmark`
    ADD FOREIGN KEY (`bookmark_type_no`) REFERENCES `bookmark_types` (`no`);

ALTER TABLE `bookmark`
    ADD FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`);

ALTER TABLE `like_post`
    ADD FOREIGN KEY (`user_no`) REFERENCES `users` (`no`);

ALTER TABLE `like_post`
    ADD FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`);

ALTER TABLE `plan_attraction_detail`
    ADD FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`);

ALTER TABLE `plan_attraction_detail`
    ADD FOREIGN KEY (`attraction_no`) REFERENCES `attractions` (`no`);

ALTER TABLE `plan_attraction_detail`
    ADD FOREIGN KEY (`review_no`) REFERENCES `attraction_review` (`no`);

ALTER TABLE `plan_comment`
    ADD FOREIGN KEY (`plan_post_no`) REFERENCES `plan_post` (`no`);

ALTER TABLE `plan_comment`
    ADD FOREIGN KEY (`user_no`) REFERENCES `users` (`no`);

ALTER TABLE `plan_comment`
    ADD FOREIGN KEY (`parent_no`) REFERENCES `plan_comment` (`no`);

ALTER TABLE `attractions`
    ADD FOREIGN KEY (`attraction_type_name`) REFERENCES `attraction_types` (`name`);

ALTER TABLE `plan_post`
    ADD FOREIGN KEY (`user_no`) REFERENCES `users` (`no`);

ALTER TABLE `bookmark_types`
    ADD FOREIGN KEY (`user_no`) REFERENCES `users` (`no`);

-- 외래 키 제약조건 활성화
SET FOREIGN_KEY_CHECKS = 1;