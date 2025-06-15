-- DROP database triplog;
CREATE database triplog2;

use triplog2;


show tables;


-- 모든 테이블 생성

CREATE TABLE `my_plan` (
                           `no` BIGINT NOT NULL AUTO_INCREMENT,
                           `title` VARCHAR(255) NOT NULL,
                           `description` TEXT NULL,
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


-- 샘플 데이터
INSERT INTO attraction_types (name) VALUES
                                        ('역사'),
                                        ('자연'),
                                        ('전시관');



INSERT INTO attractions
(title, overview, map_level, latitude, longitude, tel, address, address_detail, homepage, api_id, thumbnail, ratingSum, reviewCount, attraction_type_name)
VALUES
    ('경복궁', '조선 시대의 대표 궁궐', 6, 37.579617, 126.977041, '02-3700-3900', '서울 종로구 사직로 161', '', '', 101, '', 45.0, 10, '역사'),
    ('남산타워', '서울의 야경 명소', 6, 37.551169, 126.988227, '02-3455-9277', '서울 용산구 남산공원길 105', '', '', 102, '', 38.7, 8, '자연'),
    ('부산타워', '부산을 한눈에 볼 수 있는 전망대', 6, 35.101398, 129.032753, '051-245-5011', '부산 중구 용두산길 37-55', '', '', 103, '', 29.2, 6, '자연'),
    ('전주한옥마을', '전통이 살아 숨 쉬는 마을', 6, 35.815059, 127.152529, '063-281-2891', '전북 전주시 완산구 기린대로 99', '', '', 104, '', 41.3, 12, '역사'),
    ('청계천', '서울 중심을 흐르는 도심 하천', 6, 37.5696, 126.9771, '02-2290-7111', '서울 종로구 청계천로', '', '', 105, '', 32.1, 5, '자연'),
    ('국립중앙박물관', '다양한 유물을 전시하는 박물관', 6, 37.5230, 126.9800, '02-2077-9000', '서울 용산구 서빙고로 137', '', '', 106, '', 50.0, 20, '전시관'),
    ('독립기념관', '한국의 독립운동을 기념하는 박물관', 6, 36.7765, 127.2225, '041-560-0114', '충남 천안시 동남구 목천읍 독립기념관로 1', '', '', 107, '', 35.0, 7, '역사'),
    ('에버랜드', '국내 최대 테마파크', 6, 37.2940, 127.2021, '031-320-5000', '경기 용인시 처인구 포곡읍 에버랜드로 199', '', '', 108, '', 44.2, 14, '자연'),
    ('롯데월드타워', '서울의 랜드마크 초고층 빌딩', 6, 37.5131, 127.1025, '1661-2000', '서울 송파구 올림픽로 300', '', '', 109, '', 42.5, 11, '전시관'),
    ('한라산', '제주의 대표 산', 6, 33.3617, 126.5292, '064-713-9950', '제주특별자치도 제주시', '', '', 110, '', 48.3, 9, '자연');

UPDATE attractions SET thumbnail = 'https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U';


INSERT INTO attraction_image (image_url, review_image_order, attraction_no, attraction_review_no)
VALUES
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 1, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 2, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 3, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 4, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 5, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 6, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 7, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 8, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 9, NULL),
    ('https://lh4.googleusercontent.com/proxy/2-1aYixQlQBuadB9TrGNwxHcvCNdsip88b075l_sQnpMe-iwTEwic8rCXp5jRLckxW05xhC9RrSlNWXgMAe1oxn7awKOwH4Llhug_rr4GvXZaoFbqLBMpPJ-mMdMGQ1rvttPpKKOu3ng1s_1H4Kl24HzMar6oYG4InEwdm7RxCrLQLQpXXqZfXpESWmtYF4D-YU6gFL-LPMu-0GDCHCxmyreQZCeUFMq7ChkkM2E1rSPOEXqKYZeTPwvo6U', 1, 10, NULL);

