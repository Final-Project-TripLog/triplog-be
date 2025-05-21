-- DROP TABLES (FK 의존성 역순)
-- 외래키 체크 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- DROP 테이블들 실행
DROP TABLE IF EXISTS plan_comment;

DROP TABLE IF EXISTS plan_attraction_detail;

DROP TABLE IF EXISTS like_post;

DROP TABLE IF EXISTS attraction_image;

DROP TABLE IF EXISTS plan_post_tag;

DROP TABLE IF EXISTS bookmark;

DROP TABLE IF EXISTS attraction_review;

DROP TABLE IF EXISTS attractions;

DROP TABLE IF EXISTS attraction_types;

DROP TABLE IF EXISTS follow_info;

DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS bookmark_types;

DROP TABLE IF EXISTS my_daily_plan;

DROP TABLE IF EXISTS plan_post;

DROP TABLE IF EXISTS my_plan;

-- 외래키 체크 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;

-- CREATE TABLES
CREATE TABLE plan_post (
                           no BIGINT NOT NULL PRIMARY KEY,
                           user_nickname VARCHAR(100) NOT NULL,
                           title VARCHAR(255) NOT NULL,
                           totalMember INT NULL,
                           description TEXT NULL,
                           created_at DATETIME NOT NULL,
                           updated_at DATETIME NOT NULL,
                           user_no BIGINT NOT NULL,
                           fork_count INT NULL,
                           view_count INT NULL,
                           liked_count INT NULL,
                           thumbnail VARCHAR(1000) NULL,
                           startDay DATETIME NULL,
                           endDay DATETIME NULL
);

CREATE TABLE my_daily_plan (
                               no BIGINT NOT NULL PRIMARY KEY,
                               visited_date DATE NOT NULL,
                               start_time TIME NOT NULL,
                               end_time TIME NOT NULL,
                               move_time BIGINT NULL COMMENT '분 단위로 저장',
                               attraction_title VARCHAR(255) NOT NULL,
                               attraction_thumbnail VARCHAR(1000) NULL,
                               attraction_latitude DECIMAL(20, 17) NULL,
                               attraction_longitude DECIMAL(20, 17) NULL,
                               attraction_rating DECIMAL(20, 17) NULL,
                               memo VARCHAR(255) NULL,
                               attraction_no BIGINT NOT NULL,
                               my_plan_no BIGINT NOT NULL
);

CREATE TABLE bookmark_types (
                                no BIGINT NOT NULL PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                attraction_count INT NULL,
                                user_no BIGINT NOT NULL
);

CREATE TABLE users (
                       no BIGINT NOT NULL PRIMARY KEY,
                       email VARCHAR(255) NULL COMMENT 'UNIQUE',
                       password VARCHAR(255) NULL,
                       social_type VARCHAR(20) NULL,
                       social_id VARCHAR(100) NULL,
                       nickname VARCHAR(30) NOT NULL COMMENT 'UNIQUE',
                       profile_url VARCHAR(1000) NULL,
                       created_at DATETIME NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       phone VARCHAR(20) NULL COMMENT '010-0000-0000형식',
                       address VARCHAR(255) NULL,
                       address_detail VARCHAR(255) NULL,
                       follow_count INT NULL,
                       follower_count INT NULL
);

CREATE TABLE follow_info (
                             no BIGINT NOT NULL PRIMARY KEY,
                             follow BIGINT NOT NULL,
                             follower BIGINT NOT NULL,
                             create_at DATETIME NULL
);

CREATE TABLE attraction_types (
                                  name VARCHAR(50) NOT NULL PRIMARY KEY
);

CREATE TABLE attraction_review (
                                   no BIGINT NOT NULL PRIMARY KEY,
                                   user_nickname VARCHAR(100) NOT NULL,
                                   rating INT NOT NULL,
                                   content TEXT NULL,
                                   create_at DATETIME NOT NULL,
                                   update_at DATETIME NOT NULL,
                                   attraction_no BIGINT NOT NULL,
                                   user_no BIGINT NOT NULL
);

CREATE TABLE attractions (
                             no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             title VARCHAR(255) NOT NULL,
                             overview VARCHAR(10000) NULL,
                             map_level INT NULL,
                             latitude DECIMAL(20, 17) NOT NULL,
                             longitude DECIMAL(20, 17) NOT NULL,
                             tel VARCHAR(20) NULL,
                             address VARCHAR(100) NOT NULL,
                             address_detail VARCHAR(100) NULL,
                             homepage VARCHAR(1000) NULL,
                             api_id INT NULL,
                             thumbnail VARCHAR(1000) NULL,
                             ratingSum DECIMAL(20, 17) NULL,
                             reviewCount INT NULL,
                             attraction_type_no VARCHAR(50) NULL
);

CREATE TABLE bookmark (
                          bookmark_type_no BIGINT NOT NULL,
                          attraction_no BIGINT NOT NULL,
                          `order` INT NOT NULL,
                          PRIMARY KEY (
                                       bookmark_type_no,
                                       attraction_no
                              )
);

CREATE TABLE plan_post_tag (
                               no BIGINT NOT NULL PRIMARY KEY,
                               plan_post_no BIGINT NOT NULL,
                               name VARCHAR(255) NOT NULL
);

CREATE TABLE attraction_image (
                                  no BIGINT NOT NULL PRIMARY KEY,
                                  image_url VARCHAR(1000) NOT NULL,
                                  review_image_order INT NULL,
                                  attraction_no BIGINT NOT NULL,
                                  attraction_review_no BIGINT NULL
);

CREATE TABLE like_post (
                           user_no BIGINT NOT NULL,
                           plan_post_no BIGINT NOT NULL,
                           liked_at DATETIME NULL,
                           PRIMARY KEY (user_no, plan_post_no)
);

CREATE TABLE plan_attraction_detail (
                                        no BIGINT NOT NULL PRIMARY KEY,
                                        visited_date DATE NOT NULL,
                                        start_time TIME NOT NULL,
                                        end_time TIME NOT NULL,
                                        move_time INT NULL COMMENT '분단위로 저장',
                                        attraction_title VARCHAR(255) NOT NULL,
                                        attraction_thumbnail VARCHAR(1000) NULL,
                                        attraction_rating DECIMAL(20, 17) NULL,
                                        writer_rating INT NULL,
                                        plan_post_no BIGINT NOT NULL,
                                        attraction_no BIGINT NOT NULL,
                                        review_no BIGINT NULL
);

CREATE TABLE plan_comment (
                              no BIGINT NOT NULL PRIMARY KEY,
                              content VARCHAR(1000) NOT NULL,
                              user_nickname VARCHAR(100) NOT NULL,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              level INT NOT NULL DEFAULT 0 COMMENT '최대 10',
                              path VARCHAR(60) NOT NULL COMMENT '00000-0000-0000...형식으로 관리 / 부모 path + 부모 자식 댓글 개수 + 1',
                              child_count INT NOT NULL DEFAULT 0 COMMENT '최대 9999개',
                              plan_post_no BIGINT NOT NULL,
                              user_no BIGINT NOT NULL,
                              parent_no BIGINT NULL
);

CREATE TABLE my_plan (
                         no BIGINT NOT NULL PRIMARY KEY,
                         title VARCHAR(255) NOT NULL,
                         description TEXT NULL,
                         user_no BIGINT NOT NULL
);

-- FOREIGN KEYS
ALTER TABLE plan_post
    ADD CONSTRAINT FK_users_TO_plan_post FOREIGN KEY (user_no) REFERENCES users (no);

ALTER TABLE my_daily_plan
    ADD CONSTRAINT FK_attractions_TO_my_daily_plan FOREIGN KEY (attraction_no) REFERENCES attractions (no),
ADD CONSTRAINT FK_my_plan_TO_my_daily_plan FOREIGN KEY (my_plan_no) REFERENCES my_plan (no);

ALTER TABLE bookmark_types
    ADD CONSTRAINT FK_users_TO_bookmark_types FOREIGN KEY (user_no) REFERENCES users (no);

ALTER TABLE follow_info
    ADD CONSTRAINT FK_users_TO_follow_info_1 FOREIGN KEY (follow) REFERENCES users (no),
ADD CONSTRAINT FK_users_TO_follow_info_2 FOREIGN KEY (follower) REFERENCES users (no);

ALTER TABLE attraction_review
    ADD CONSTRAINT FK_attractions_TO_attraction_review FOREIGN KEY (attraction_no) REFERENCES attractions (no),
ADD CONSTRAINT FK_users_TO_attraction_review FOREIGN KEY (user_no) REFERENCES users (no);

ALTER TABLE bookmark
    ADD CONSTRAINT FK_bookmark_types_TO_bookmark FOREIGN KEY (bookmark_type_no) REFERENCES bookmark_types (no),
ADD CONSTRAINT FK_attractions_TO_bookmark FOREIGN KEY (attraction_no) REFERENCES attractions (no);

ALTER TABLE attractions
    ADD CONSTRAINT FK_attraction_types_TO_attractions FOREIGN KEY (attraction_type_no) REFERENCES attraction_types (name);

ALTER TABLE plan_post_tag
    ADD CONSTRAINT FK_plan_post_TO_plan_post_tag FOREIGN KEY (plan_post_no) REFERENCES plan_post (no);

ALTER TABLE attraction_image
    ADD CONSTRAINT FK_attractions_TO_attraction_image FOREIGN KEY (attraction_no) REFERENCES attractions (no),
ADD CONSTRAINT FK_attraction_review_TO_attraction_image FOREIGN KEY (attraction_review_no) REFERENCES attraction_review (no);

ALTER TABLE like_post
    ADD CONSTRAINT FK_users_TO_like_post FOREIGN KEY (user_no) REFERENCES users (no),
ADD CONSTRAINT FK_plan_post_TO_like_post FOREIGN KEY (plan_post_no) REFERENCES plan_post (no);

ALTER TABLE plan_attraction_detail
    ADD CONSTRAINT FK_plan_post_TO_plan_attraction_detail FOREIGN KEY (plan_post_no) REFERENCES plan_post (no),
ADD CONSTRAINT FK_attractions_TO_plan_attraction_detail FOREIGN KEY (attraction_no) REFERENCES attractions (no),
ADD CONSTRAINT FK_attraction_review_TO_plan_attraction_detail FOREIGN KEY (review_no) REFERENCES attraction_review (no);

ALTER TABLE plan_comment
    ADD CONSTRAINT FK_plan_post_TO_plan_comment FOREIGN KEY (plan_post_no) REFERENCES plan_post (no),
ADD CONSTRAINT FK_users_TO_plan_comment FOREIGN KEY (user_no) REFERENCES users (no),
ADD CONSTRAINT FK_plan_comment_TO_plan_comment FOREIGN KEY (parent_no) REFERENCES plan_comment (no);

ALTER TABLE my_plan
    ADD CONSTRAINT FK_users_TO_my_plan FOREIGN KEY (user_no) REFERENCES users (no);