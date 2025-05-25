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

SELECT
    pp.no AS id,
    pp.title,
    pp.description,
    pp.user_nickname,
    GROUP_CONCAT(DISTINCT ppt.name SEPARATOR ',') AS tags,
    GROUP_CONCAT(DISTINCT pad.attraction_title SEPARATOR ',') AS attraction_titles,
    GROUP_CONCAT(DISTINCT a.address SEPARATOR ',') AS address,
    pp.created_at
FROM plan_post pp
         LEFT JOIN plan_post_tag ppt ON pp.no = ppt.plan_post_no
         LEFT JOIN plan_attraction_detail pad ON pp.no = pad.plan_post_no
         LEFT JOIN attractions a ON pad.attraction_no = a.no
GROUP BY pp.no;