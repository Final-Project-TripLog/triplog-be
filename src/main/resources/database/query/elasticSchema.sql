CREATE TABLE plan_post_search_index (
                                        id BIGINT PRIMARY KEY,
                                        title VARCHAR(255),
                                        description TEXT,
                                        user_nickname VARCHAR(100),
                                        tags TEXT,
                                        attraction_titles TEXT,
                                        address TEXT,
                                        created_at DATETIME
);

TRUNCATE TABLE plan_post_search_index;

INSERT INTO
    plan_post_search_index (
    id,
    title,
    description,
    user_nickname,
    tags,
    attraction_titles,
    address,
    created_at
)
SELECT
    pp.no AS id,
    pp.title,
    pp.description,
    pp.user_nickname,
    GROUP_CONCAT(
            DISTINCT ppt.name SEPARATOR ','
    ) AS tags,
    GROUP_CONCAT(
            DISTINCT pad.attraction_title SEPARATOR ','
    ) AS attraction_titles,
    GROUP_CONCAT(
            DISTINCT a.address SEPARATOR ','
    ) AS address,
    pp.created_at
FROM
    plan_post pp
        LEFT JOIN plan_post_tag ppt ON pp.no = ppt.plan_post_no
        LEFT JOIN plan_attraction_detail pad ON pp.no = pad.plan_post_no
        LEFT JOIN attractions a ON pad.attraction_no = a.no
GROUP BY
    pp.no;

SELECT * FROM plan_post_search_index;

CREATE TABLE plan_post_search_index_nomal (
                                              no BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                              id BIGINT,
                                              title VARCHAR(255),
                                              description TEXT,
                                              user_nickname VARCHAR(100),
                                              tags TEXT,
                                              attraction_titles TEXT,
                                              address TEXT,
                                              created_at DATETIME
);

INSERT INTO
    plan_post_search_index_nomal (
    id,
    title,
    description,
    user_nickname,
    tags,
    attraction_titles,
    address,
    created_at
)
SELECT
    pp.no AS id,
    pp.title,
    pp.description,
    pp.user_nickname,
    ppt.name AS tags,
    pad.attraction_title AS attraction_titles,
    a.address AS address,
    pp.created_at
FROM
    plan_post pp
        LEFT JOIN plan_post_tag ppt ON pp.no = ppt.plan_post_no
        LEFT JOIN plan_attraction_detail pad ON pp.no = pad.plan_post_no
        LEFT JOIN attractions a ON pad.attraction_no = a.no;

SELECT * FROM plan_post_search_index_nomal;