-- 🗃️ 1. 캐시 테이블 생성 (역정규화)
CREATE TABLE plan_post_cache (
                                 no BIGINT PRIMARY KEY,              -- 게시글 PK
                                 user_nickname VARCHAR(50),          -- 작성자 닉네임
                                 title VARCHAR(255),                 -- 제목
                                 description TEXT,                   -- 게시글 소개글
                                 created_at DATETIME,                -- 작성일시
                                 updated_at DATETIME,                -- 수정일시
                                 thumbnail VARCHAR(500),             -- 썸네일
                                 user_no BIGINT,                     -- 작성자 PK
                                 fork_count INT DEFAULT 0,           -- fork 수
                                 liked_count INT DEFAULT 0,          -- 좋아요수
                                 view_count INT DEFAULT 0,           -- 조회수
                                 start_day DATETIME,                 -- 여행 시작일
                                 end_day DATETIME,                   -- 여행 종료일
                                 total_member BIGINT,                -- 총 인원수

    -- 🔥 역정규화된 필드들 (콤마로 구분)
                                 tag_names TEXT,                     -- 태그명들: "서울,맛집,여행"
                                 attraction_titles TEXT,             -- 관광지명들: "경복궁,남산타워,한강공원"
                                 attraction_addresses TEXT,          -- 관광지 주소들: "서울시 종로구,서울시 중구,서울시 영등포구"

    -- 🔍 검색용 통합 필드 (모든 검색 대상을 하나로 합침)
                                 search_text TEXT,                   -- title + user_nickname + description + tag_names + attraction_titles + attraction_addresses

                                 INDEX idx_search_text (search_text(100)),  -- 검색용 인덱스
                                 INDEX idx_created_at (created_at)          -- 정렬용 인덱스
);

-- 🚀 2. 캐시 테이블에 데이터 삽입 (기존 데이터 기반)
INSERT INTO plan_post_cache (
    no, user_nickname, title, description, created_at, updated_at,
    thumbnail, user_no, fork_count, liked_count, view_count,
    start_day, end_day, total_member,
    tag_names, attraction_titles, attraction_addresses, search_text
)
SELECT
    pp.no,
    pp.user_nickname,
    pp.title,
    pp.description,
    pp.created_at,
    pp.updated_at,
    pp.thumbnail,
    pp.user_no,
    pp.fork_count,
    pp.liked_count,
    pp.view_count,
    pp.start_day,
    pp.end_day,
    pp.total_member,

    -- 태그명들을 콤마로 연결
    COALESCE(
            (SELECT GROUP_CONCAT(ppt.name SEPARATOR ',')
             FROM plan_post_tag ppt
             WHERE ppt.plan_post_no = pp.no),
            ''
    ) AS tag_names,

    -- 관광지명들을 콤마로 연결
    COALESCE(
            (SELECT GROUP_CONCAT(DISTINCT pad.attraction_title SEPARATOR ',')
             FROM plan_attraction_detail pad
             WHERE pad.plan_post_no = pp.no),
            ''
    ) AS attraction_titles,

    -- 관광지 주소들을 콤마로 연결
    COALESCE(
            (SELECT GROUP_CONCAT(DISTINCT a.address SEPARATOR ',')
             FROM plan_attraction_detail pad
                      JOIN attractions a ON pad.attraction_no = a.no
             WHERE pad.plan_post_no = pp.no),
            ''
    ) AS attraction_addresses,

    -- 🔍 통합 검색 텍스트 생성
    CONCAT_WS(' ',
              pp.title,
              pp.user_nickname,
              COALESCE(pp.description, ''),
              COALESCE(
                      (SELECT GROUP_CONCAT(ppt.name SEPARATOR ' ')
                       FROM plan_post_tag ppt
                       WHERE ppt.plan_post_no = pp.no),
                      ''
              ),
              COALESCE(
                      (SELECT GROUP_CONCAT(DISTINCT pad.attraction_title SEPARATOR ' ')
                       FROM plan_attraction_detail pad
                       WHERE pad.plan_post_no = pp.no),
                      ''
              ),
              COALESCE(
                      (SELECT GROUP_CONCAT(DISTINCT a.address SEPARATOR ' ')
                       FROM plan_attraction_detail pad
                                JOIN attractions a ON pad.attraction_no = a.no
                       WHERE pad.plan_post_no = pp.no),
                      ''
              )
    ) AS search_text

FROM plan_post pp
ORDER BY pp.created_at DESC;

-- 🔄 3. 캐시 테이블 동기화용 트리거 (선택사항)
-- plan_post 테이블이 변경될 때 자동으로 캐시 테이블도 업데이트

DELIMITER $$

-- INSERT 트리거
CREATE TRIGGER tr_plan_post_cache_insert
    AFTER INSERT ON plan_post
    FOR EACH ROW
BEGIN
    INSERT INTO plan_post_cache (
        no, user_nickname, title, description, created_at, updated_at,
        thumbnail, user_no, fork_count, liked_count, view_count,
        start_day, end_day, total_member, tag_names, attraction_titles,
        attraction_addresses, search_text
    ) VALUES (
                 NEW.no, NEW.user_nickname, NEW.title, NEW.description,
                 NEW.created_at, NEW.updated_at, NEW.thumbnail, NEW.user_no,
                 NEW.fork_count, NEW.liked_count, NEW.view_count,
                 NEW.start_day, NEW.end_day, NEW.total_member,
                 '', '', '', -- 태그/관광지 정보는 별도로 업데이트
                 CONCAT_WS(' ', NEW.title, NEW.user_nickname, COALESCE(NEW.description, ''))
             );
END$$

-- UPDATE 트리거
CREATE TRIGGER tr_plan_post_cache_update
    AFTER UPDATE ON plan_post
    FOR EACH ROW
BEGIN
    UPDATE plan_post_cache SET
                               user_nickname = NEW.user_nickname,
                               title = NEW.title,
                               description = NEW.description,
                               updated_at = NEW.updated_at,
                               thumbnail = NEW.thumbnail,
                               fork_count = NEW.fork_count,
                               liked_count = NEW.liked_count,
                               view_count = NEW.view_count,
                               start_day = NEW.start_day,
                               end_day = NEW.end_day,
                               total_member = NEW.total_member,
                               search_text = CONCAT_WS(' ', NEW.title, NEW.user_nickname, COALESCE(NEW.description, ''))
    WHERE no = NEW.no;
END$$

-- DELETE 트리거
CREATE TRIGGER tr_plan_post_cache_delete
    AFTER DELETE ON plan_post
    FOR EACH ROW
BEGIN
    DELETE FROM plan_post_cache WHERE no = OLD.no;
END$$

DELIMITER ;

-- 📊 4. 캐시 테이블 상태 확인
SELECT
    COUNT(*) as total_count,
    COUNT(CASE WHEN tag_names != '' THEN 1 END) as posts_with_tags,
    COUNT(CASE WHEN attraction_titles != '' THEN 1 END) as posts_with_attractions
FROM plan_post_cache;

-- 샘플 데이터 확인
SELECT
    no, title, tag_names, attraction_titles,
    LEFT(search_text, 100) as search_preview
FROM plan_post_cache
LIMIT 5;