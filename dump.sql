-- users
INSERT INTO users (email, password, social_type, social_id, nickname, profile_url, role, phone, address, address_detail, follow_count, follower_count, name)
VALUES
    ('user1@example.com', 'encrypted_pw1', 'local', 'local_id_1', 'user1nick', NULL, 'ROLE_USER', '010-1234-5678', '서울시 강남구', '역삼동 123', 2, 3, '홍길동'),
    ('user2@example.com', 'encrypted_pw2', 'local', 'local_id_2', 'user2nick', NULL, 'ROLE_USER', '010-8765-4321', '부산시 해운대구', '좌동 456', 1, 0, '김영희');

-- attraction_types
INSERT INTO attraction_types (name) VALUES
                                        ('역사'), ('테마파크'), ('자연');

-- attractions
INSERT INTO attractions (title, overview, latitude, longitude, tel, address, address_detail, homepage, api_id, thumbnail, ratingSum, reviewCount, attraction_type_name)
VALUES
    ('경복궁', '조선시대 궁궐', 37.579617, 126.977041, '02-3700-3900', '서울 종로구 사직로 161', '광화문 앞', 'http://gogung.go.kr', 101, NULL, 4.5, 2, '역사'),
    ('롯데월드', '서울 잠실에 위치한 대형 테마파크', 37.5110, 127.0980, '02-411-2000', '서울 송파구 올림픽로 240', '잠실동', 'http://lotteworld.com', 102, NULL, 4.2, 3, '테마파크');

-- attraction_review
INSERT INTO attraction_review (user_nickname, rating, content, attraction_no, user_no)
VALUES
    ('user1nick', 5, '정말 멋진 곳이었어요!', 1, 1),
    ('user2nick', 4, '역사 공부에 도움이 되었어요.', 1, 2);

-- plan_post
INSERT INTO plan_post (user_nickname, title, description, user_no, fork_count, view_count, liked_count, thumbnail)
VALUES
    ('user1nick', '서울 역사 여행', '경복궁 중심의 역사 여행 루트', 1, 0, 100, 20, NULL);

-- plan_post_tag
INSERT INTO plan_post_tag (plan_post_no, name)
VALUES
    (1, '서울'), (1, '역사'), (1, '여행');

-- bookmark_types
INSERT INTO bookmark_types (name, user_no)
VALUES
    ('가고 싶은 곳', 1);

-- bookmark
INSERT INTO bookmark (booknark_type_no, attraction_no, `order`)
VALUES
    (1, 2, 1);

-- like_post
INSERT INTO like_post (user_no, plan_post_no)
VALUES
    (2, 1);

-- follow_info
INSERT INTO follow_info (follow, follower)
VALUES
    (2, 1);

-- my_plan
INSERT INTO my_plan (title, description, user_no)
VALUES
    ('서울 여행 플랜', '서울 시내 주요 관광지 방문', 1);

-- my_daily_plan
INSERT INTO my_daily_plan (visited_date, start_time, end_time, move_time, attraction_title, attraction_thumbnail, attraction_latitude, attraction_longitude, attraction_rating, memo, attraction_no, my_plan_no)
VALUES
    ('2025-05-21', '09:00:00', '11:00:00', 30, '경복궁', NULL, 37.579617, 126.977041, 4.5, '날씨 좋음', 1, 1);

-- plan_attraction_detail
INSERT INTO plan_attraction_detail (visite_date, start_time, end_time, move_time, attraction_title, attraction_thumbnail, attraction_rating, writer_rating, plan_post_no, attraction_no, review_no)
VALUES
    ('2025-05-21', '09:00:00', '11:00:00', 30, '경복궁', NULL, 4.5, 5, 1, 1, 1);

-- plan_comment
INSERT INTO plan_comment (content, user_nickname, level, path, child_count, plan_post_no, user_no)
VALUES
    ('좋은 계획이네요!', 'user2nick', 0, '00001', 0, 1, 2);

-- attraction_image
INSERT INTO attraction_image (image_url, review_image_order, attraction_no, attraction_review_no)
VALUES
    ('http://image.com/gyeongbokgung1.jpg', 1, 1, 1);
