-- 외래키 검사 비활성화
SET FOREIGN_KEY_CHECKS = 0;

-- 더미 데이터 삽입

-- users
INSERT INTO
    users (
    no,
    email,
    password,
    social_type,
    social_id,
    nickname,
    profile_url,
    created_at,
    role,
    phone,
    address,
    address_detail,
    follow_count,
    follower_count
)
VALUES (
           1,
           'user1@example.com',
           'pass1',
           'kakao',
           'sid1',
           'user1',
           NULL,
           NOW(),
           'USER',
           '010-1111-1111',
           'Seoul',
           'Gangnam',
           0,
           0
       ),
       (
           2,
           'user2@example.com',
           'pass2',
           'google',
           'sid2',
           'user2',
           NULL,
           NOW(),
           'USER',
           '010-2222-2222',
           'Busan',
           'Haeundae',
           0,
           0
       ),
       (
           3,
           'user3@example.com',
           'pass3',
           'naver',
           'sid3',
           'user3',
           NULL,
           NOW(),
           'USER',
           '010-3333-3333',
           'Incheon',
           'Yeonsu',
           0,
           0
       ),
       (
           4,
           'user4@example.com',
           'pass4',
           'kakao',
           'sid4',
           'user4',
           NULL,
           NOW(),
           'USER',
           '010-4444-4444',
           'Daegu',
           'Suseong',
           0,
           0
       ),
       (
           5,
           'user5@example.com',
           'pass5',
           'google',
           'sid5',
           'user5',
           NULL,
           NOW(),
           'ADMIN',
           '010-5555-5555',
           'Daejeon',
           'Yuseong',
           0,
           0
       );

-- attraction_types
INSERT INTO
    attraction_types (name)
VALUES ('Museum'),
       ('Park'),
       ('Beach'),
       ('Temple'),
       ('Landmark');

-- attractions
INSERT INTO
    attractions (
    no,
    title,
    overview,
    map_level,
    latitude,
    longitude,
    tel,
    address,
    address_detail,
    homepage,
    api_id,
    thumbnail,
    ratingSum,
    reviewCount,
    attraction_type_no
)
VALUES (
           1,
           'Seoul Tower',
           'Nice view',
           3,
           37.551169,
           126.988227,
           '02-1234-5678',
           'Seoul',
           'Namsan',
           NULL,
           NULL,
           NULL,
           4.5,
           10,
           'Landmark'
       ),
       (
           2,
           'Haeundae Beach',
           'Popular beach',
           3,
           35.158698,
           129.160384,
           '051-9876-5432',
           'Busan',
           'Haeundae',
           NULL,
           NULL,
           NULL,
           4.7,
           20,
           'Beach'
       ),
       (
           3,
           'Gyeongbok Palace',
           'Historic place',
           3,
           37.579617,
           126.977041,
           '02-1111-2222',
           'Seoul',
           'Jongno',
           NULL,
           NULL,
           NULL,
           4.8,
           15,
           'Temple'
       ),
       (
           4,
           'Jeju Museum',
           'Good exhibits',
           3,
           33.3617,
           126.5292,
           '064-123-4567',
           'Jeju',
           'City',
           NULL,
           NULL,
           NULL,
           4.3,
           5,
           'Museum'
       ),
       (
           5,
           'Yeouido Park',
           'Relaxing area',
           3,
           37.526,
           126.924,
           '02-3333-4444',
           'Seoul',
           'Yeouido',
           NULL,
           NULL,
           NULL,
           4.1,
           8,
           'Park'
       );

-- plan_post
INSERT INTO
    plan_post (
    no,
    user_nickname,
    title,
    totalMember,
    description,
    created_at,
    updated_at,
    user_no,
    fork_count,
    view_count,
    liked_count,
    thumbnail,
    startDay,
    endDay
)
VALUES (
           1,
           'user1',
           'Trip to Seoul',
           2,
           '2 day trip',
           NOW(),
           NOW(),
           1,
           0,
           5,
           2,
           NULL,
           NOW(),
           NOW()
       ),
       (
           2,
           'user2',
           'Busan Beaches',
           3,
           'Beach tour',
           NOW(),
           NOW(),
           2,
           1,
           8,
           5,
           NULL,
           NOW(),
           NOW()
       ),
       (
           3,
           'user3',
           'History Route',
           4,
           'Palace and temple',
           NOW(),
           NOW(),
           3,
           0,
           3,
           1,
           NULL,
           NOW(),
           NOW()
       ),
       (
           4,
           'user4',
           'Jeju Adventure',
           2,
           'Museum and trekking',
           NOW(),
           NOW(),
           4,
           2,
           12,
           6,
           NULL,
           NOW(),
           NOW()
       ),
       (
           5,
           'user5',
           'Chill in Park',
           1,
           'Parks of Seoul',
           NOW(),
           NOW(),
           5,
           0,
           2,
           0,
           NULL,
           NOW(),
           NOW()
       );

-- bookmark_types
INSERT INTO
    bookmark_types (
    no,
    name,
    attraction_count,
    user_no
)
VALUES (1, 'Favorites', 2, 1),
       (2, 'Want to Go', 3, 2),
       (3, 'Hidden Gems', 1, 3),
       (4, 'Weekend Trip', 4, 4),
       (5, 'Top Picks', 5, 5);

-- my_plan
INSERT INTO
    my_plan (
    no,
    title,
    description,
    user_no
)
VALUES (
           1,
           'Spring Seoul Trip',
           'Flowers and walks',
           1
       ),
       (
           2,
           'Busan Summer',
           'Sun and beach',
           2
       ),
       (
           3,
           'Temple Journey',
           'Peaceful trip',
           3
       ),
       (
           4,
           'Jeju Tour',
           'Island life',
           4
       ),
       (
           5,
           'Seoul Chill',
           'Relaxing parks',
           5
       );

-- 외래키 검사 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;