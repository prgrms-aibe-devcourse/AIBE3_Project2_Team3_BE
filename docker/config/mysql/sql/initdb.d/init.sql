SET NAMES utf8mb4;
SET
CHARACTER SET utf8mb4;

-- User 테이블
CREATE TABLE users
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    username          VARCHAR(50)  NOT NULL UNIQUE,
    password          VARCHAR(100) NOT NULL,
    nickname          VARCHAR(50),
    role              VARCHAR(20)  NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    profile_image_url VARCHAR(255),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_date      DATETIME,
    created_date      DATETIME,
    modified_date     DATETIME
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Post 테이블
CREATE TABLE posts
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT  NOT NULL,
    is_viewed     BOOLEAN NOT NULL DEFAULT FALSE,
    title         VARCHAR(255),
    content       TEXT,
    view_count    INT     NOT NULL DEFAULT 0,
    like_count    INT     NOT NULL DEFAULT 0,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Project 테이블 (Post와 1:1, PK 공유)
CREATE TABLE projects
(
    id              BIGINT PRIMARY KEY,
    deadline_date   DATETIME,
    started_date    DATETIME,
    ended_date      DATETIME,
    hirer_type      VARCHAR(255),
    employment_type VARCHAR(255),
    salary          BIGINT,
    personnel       INT,
    skill_level     INT,
    CONSTRAINT fk_project_post FOREIGN KEY (id) REFERENCES posts (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Application 테이블
CREATE TABLE applications
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    status        VARCHAR(255) DEFAULT 'PENDING',
    content       TEXT,
    salary        BIGINT UNSIGNED DEFAULT 0,
    period INT UNSIGNED DEFAULT 0,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_applications UNIQUE (post_id, user_id),
    CONSTRAINT fk_applications_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_applications_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Freelancer 테이블 (Post와 1:1, PK 공유)
CREATE TABLE freelancers
(
    id     BIGINT PRIMARY KEY,
    salary BIGINT UNSIGNED DEFAULT 0,
    period INT UNSIGNED DEFAULT 0,
    CONSTRAINT fk_freelancer_post FOREIGN KEY (id) REFERENCES posts (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Offer 테이블
CREATE TABLE offers
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    status        VARCHAR(255) DEFAULT 'ACCEPTED',
    amount        INT UNSIGNED DEFAULT 1,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_offers_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_offers_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Question 테이블
CREATE TABLE questions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(50) NOT NULL,
    content       VARCHAR(1000)         NOT NULL,
    user_id       BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_questions_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Answer 테이블
CREATE TABLE answers
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment       VARCHAR(2000) NOT NULL,
    user_id       BIGINT,
    question_id   BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_answers_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Review 테이블
CREATE TABLE reviews
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT,
    user_id       BIGINT,
    rating        INT  NOT NULL,
    comment       TEXT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_review_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Category 테이블
CREATE TABLE categories
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255),
    parent_id     BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Region 테이블
CREATE TABLE regions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255),
    parent_id     BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_regions_parent FOREIGN KEY (parent_id) REFERENCES regions (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Skill 테이블
CREATE TABLE skills
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255),
    created_date  DATETIME,
    modified_date DATETIME
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- PostCategory 연결 테이블
CREATE TABLE post_categories
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    category_id   BIGINT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_post_categories UNIQUE (post_id, category_id),
    CONSTRAINT fk_post_categories_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- PostRegion 연결 테이블
CREATE TABLE post_regions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    region_id     BIGINT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_post_regions UNIQUE (post_id, region_id),
    CONSTRAINT fk_post_regions_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_regions_region FOREIGN KEY (region_id) REFERENCES regions (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- PostSkill 연결 테이블
CREATE TABLE post_skills
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    skill_id      BIGINT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_post_skills UNIQUE (post_id, skill_id),
    CONSTRAINT fk_post_skills_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ApplicationFile 테이블
CREATE TABLE application_files
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT,
    url            VARCHAR(255),
    created_date   DATETIME,
    modified_date  DATETIME,
    CONSTRAINT fk_application_file_application FOREIGN KEY (application_id) REFERENCES applications (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ChatRoom 테이블
CREATE TABLE chat_rooms
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    INDEX         idx_chat_rooms_name (name)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ChatMember 테이블
CREATE TABLE chat_members
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_room_id         BIGINT      NOT NULL,
    user_id              BIGINT      NOT NULL,
    started_date         DATETIME,
    ended_date           DATETIME,
    role                 VARCHAR(20) NOT NULL,
    last_read_message_id BIGINT,
    created_date         DATETIME,
    modified_date        DATETIME,
    CONSTRAINT uk_chat_members_room_user_active UNIQUE (chat_room_id, user_id, ended_date),
    CONSTRAINT fk_chat_member_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id),
    CONSTRAINT fk_chat_member_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX                idx_chat_members_room (chat_room_id),
    INDEX                idx_chat_members_user (user_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ChatMessage 테이블
CREATE TABLE chat_messages
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_member_id BIGINT NOT NULL,
    chat_room_id   BIGINT NOT NULL,
    content        TEXT   NOT NULL,
    message_seq    BIGINT NOT NULL,
    created_date   DATETIME,
    modified_date  DATETIME,
    CONSTRAINT fk_chat_message_member FOREIGN KEY (chat_member_id) REFERENCES chat_members (id),
    CONSTRAINT fk_chat_message_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Notification 테이블
CREATE TABLE notifications
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT,
    offer_id        BIGINT,
    chat_message_id BIGINT,
    review_id       BIGINT,
    content         VARCHAR(255),
    created_date    DATETIME,
    modified_date   DATETIME,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notification_offer FOREIGN KEY (offer_id) REFERENCES offers (id),
    CONSTRAINT fk_notification_chat_message FOREIGN KEY (chat_message_id) REFERENCES chat_messages (id),
    CONSTRAINT fk_notification_review FOREIGN KEY (review_id) REFERENCES reviews (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- FreelancerFile 테이블
CREATE TABLE freelancer_files
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    freelancer_id BIGINT       NOT NULL,
    url           VARCHAR(255) NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_freelancer_file_freelancer FOREIGN KEY (freelancer_id) REFERENCES freelancers (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE reactions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    user_id       BIGINT NOT NULL,
    reaction_type VARCHAR(50) NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_post_user UNIQUE (post_id, user_id),
    CONSTRAINT fk_reaction_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_reaction_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- User 테이블 초기 데이터
INSERT INTO users (username, password, nickname, role, email, deleted, created_date, modified_date)
VALUES ('admin', '$2y$04$4Dxsdv5u.SYeJxeH6BFeG.KIMDzE1vA/Hub7zIY85h/WdM/fpkEjW', '관리자', 'ROLE_ADMIN', 'admin@pi.com', FALSE, NOW(), NOW()),
       ('system', '$2y$04$4Dxsdv5u.SYeJxeH6BFeG.KIMDzE1vA/Hub7zIY85h/WdM/fpkEjW', '시스템', 'ROLE_ADMIN', 'system@pi.com', FALSE, NOW(), NOW()),
       ('user1', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '까망베르', 'ROLE_USER', 'jobseeker1@pi.com', FALSE, NOW(), NOW()),
       ('user2', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '샛별', 'ROLE_USER', 'codingmaster@pi.com', FALSE, NOW(), NOW()),
       ('user3', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '비둘기', 'ROLE_USER', 'newstart2025@pi.com', FALSE, NOW(), NOW()),
       ('user4', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '지각대장', 'ROLE_USER', 'smartguy@pi.com', FALSE, NOW(), NOW()),
       ('user5', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '어쩌다어른', 'ROLE_USER', 'happyworker@pi.com', FALSE, NOW(), NOW()),
       ('user6', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '기러기아빠', 'ROLE_USER', 'findyourway@pi.com', FALSE, NOW(), NOW()),
       ('user7', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '야근요정', 'ROLE_USER', 'dev_kim@pi.com', FALSE, NOW(), NOW()),
       ('user8', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '배고픈개발자', 'ROLE_USER', 'dreamer77@pi.com', FALSE, NOW(), NOW()),
       ('user9', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '걷는사람', 'ROLE_USER', 'green_tree@pi.com', FALSE, NOW(), NOW()),
       ('user10', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '푸른바다', 'ROLE_USER', 'sunrise01@pi.com', FALSE, NOW(), NOW()),
       ('user11', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '긍정의아이콘', 'ROLE_USER', 'min_j@pi.com', FALSE, NOW(), NOW()),
       ('user12', '$2y$04$z.ojhMEFDMGtPpWUGt0SIe0yfvElhbfUm2wsTAxbqqee9GhM53Z46', '비온뒤맑음', 'ROLE_USER', 'coffeeholic@pi.com', FALSE, NOW(), NOW());

-- Question 테이블 초기 데이터
INSERT INTO questions (title, content, user_id, created_date, modified_date)
VALUES ('허위/부적절 게시물 신고 절차 문의', '허위 구인 광고나 커뮤니티에 부적절한 게시물을 발견했을 때, 사이트 관리자에게 신고할 수 있는 절차와 방법을 알려주세요.', 3, NOW(), NOW()),
       ('결제 및 수수료 정책', '프로젝트 완료 후 결제 과정과 수수료 부과 기준이 궁금합니다.', 3, NOW(), NOW()),
       ('구직자가 프로젝트에 지원하는 방법', '구직자가 프로젝트에 지원할 때 필요한 정보와 절차를 설명해주세요.', 5, NOW(), NOW()),
       ('회원 탈퇴', '사이트에서 회원 탈퇴 절차를 안내해주세요.', 6, NOW(), NOW());

-- Answer 테이블 초기 데이터
INSERT INTO answers (comment, user_id, question_id, created_date, modified_date)
VALUES ('구인/구직 게시글은 상단 메뉴에서 등록 가능합니다. 회원가입 후 이용해 주세요.', 1, 1, NOW(), NOW()),
       ('프로젝트 완료 후 결제가 진행되며, 수수료는 정책에 따라 자동 부과됩니다.', 1, 2, NOW(), NOW()),
       ('구직자는 프로젝트 상세 페이지에서 지원 버튼을 눌러 정보를 입력하면 지원이 완료됩니다.', 1, 3, NOW(), NOW()),
       ('회원 탈퇴는 마이페이지에서 직접 신청하실 수 있습니다. 탈퇴 후 데이터는 복구되지 않습니다.', 1, 4, NOW(), NOW());

-- Region 테이블 초기 데이터
INSERT INTO regions (name, parent_id, created_date, modified_date)
VALUES ('서울특별시', NULL, NOW(), NOW()),
       ('강남구', 1, NOW(), NOW()),
       ('서초구', 1, NOW(), NOW()),
       ('송파구', 1, NOW(), NOW()),
       ('마포구', 1, NOW(), NOW()),
       ('부산광역시', NULL, NOW(), NOW()),
       ('해운대구', 6, NOW(), NOW()),
       ('수영구', 6, NOW(), NOW()),
       ('동래구', 6, NOW(), NOW()),
       ('대구광역시', NULL, NOW(), NOW()),
       ('중구', 10, NOW(), NOW()),
       ('동구', 10, NOW(), NOW()),
       ('수성구', 10, NOW(), NOW()),
       ('인천광역시', NULL, NOW(), NOW()),
       ('남동구', 14, NOW(), NOW()),
       ('연수구', 14, NOW(), NOW()),
       ('부평구', 14, NOW(), NOW()),
       ('광주광역시', NULL, NOW(), NOW()),
       ('동구', 18, NOW(), NOW()),
       ('서구', 18, NOW(), NOW()),
       ('광산구', 18, NOW(), NOW()),
       ('대전광역시', NULL, NOW(), NOW()),
       ('서구', 22, NOW(), NOW()),
       ('유성구', 22, NOW(), NOW()),
       ('동구', 22, NOW(), NOW()),
       ('울산광역시', NULL, NOW(), NOW()),
       ('남구', 26, NOW(), NOW()),
       ('동구', 26, NOW(), NOW()),
       ('중구', 26, NOW(), NOW()),
       ('세종특별자치시', NULL, NOW(), NOW()),
       ('세종시', 30, NOW(), NOW()),
       ('제주특별자치도', NULL, NOW(), NOW()),
       ('제주시', 32, NOW(), NOW()),
       ('서귀포시', 32, NOW(), NOW());

-- Category 테이블 초기 데이터
INSERT INTO categories (name, parent_id, created_date, modified_date)
VALUES ('IT/개발', NULL, NOW(), NOW()),
       ('웹 개발', 1, NOW(), NOW()),
       ('앱 개발', 1, NOW(), NOW()),
       ('서버/백엔드', 1, NOW(), NOW()),
       ('데이터/AI', 1, NOW(), NOW()),
       ('디자인', NULL, NOW(), NOW()),
       ('그래픽 디자인', 6, NOW(), NOW()),
       ('UI/UX 디자인', 6, NOW(), NOW()),
       ('제품/패키지 디자인', 6, NOW(), NOW()),
       ('마케팅', NULL, NOW(), NOW()),
       ('온라인 마케팅', 10, NOW(), NOW()),
       ('콘텐츠 마케팅', 10, NOW(), NOW()),
       ('브랜드 마케팅', 10, NOW(), NOW()),
       ('번역/통역', NULL, NOW(), NOW()),
       ('영어 번역', 14, NOW(), NOW()),
       ('중국어 번역', 14, NOW(), NOW()),
       ('일본어 번역', 14, NOW(), NOW()),
       ('영상/음향', NULL, NOW(), NOW()),
       ('영상 편집', 18, NOW(), NOW()),
       ('촬영', 18, NOW(), NOW()),
       ('음향/녹음', 18, NOW(), NOW()),
       ('문서/글쓰기', NULL, NOW(), NOW()),
       ('블로그/콘텐츠 작성', 22, NOW(), NOW()),
       ('기획서 작성', 22, NOW(), NOW()),
       ('교정/교열', 22, NOW(), NOW()),
       ('기타', NULL, NOW(), NOW()),
       ('비즈니스 지원', 26, NOW(), NOW()),
       ('교육/과외', 26, NOW(), NOW()),
       ('상담/코칭', 26, NOW(), NOW());

-- Skill 테이블 초기 데이터
INSERT INTO skills (name, created_date, modified_date)
VALUES ('Java', NOW(), NOW()),
       ('Spring Boot', NOW(), NOW()),
       ('Kotlin', NOW(), NOW()),
       ('React', NOW(), NOW()),
       ('Node.js', NOW(), NOW()),
       ('Python', NOW(), NOW()),
       ('Django', NOW(), NOW()),
       ('AWS', NOW(), NOW()),
       ('MySQL', NOW(), NOW()),
       ('Git', NOW(), NOW()),
       ('Photoshop', NOW(), NOW()),
       ('Illustrator', NOW(), NOW()),
       ('Figma', NOW(), NOW()),
       ('Sketch', NOW(), NOW()),
       ('XD', NOW(), NOW()),
       ('Google Analytics', NOW(), NOW()),
       ('SEO', NOW(), NOW()),
       ('콘텐츠 기획', NOW(), NOW()),
       ('SNS 마케팅', NOW(), NOW()),
       ('영어', NOW(), NOW()),
       ('중국어', NOW(), NOW()),
       ('일본어', NOW(), NOW()),
       ('Premiere Pro', NOW(), NOW()),
       ('After Effects', NOW(), NOW()),
       ('Final Cut Pro', NOW(), NOW()),
       ('Audition', NOW(), NOW()),
       ('MS Word', NOW(), NOW()),
       ('MS Excel', NOW(), NOW()),
       ('블로그 작성', NOW(), NOW()),
       ('기획서 작성', NOW(), NOW()),
       ('엑셀 자동화', NOW(), NOW()),
       ('교육/과외', NOW(), NOW()),
       ('상담/코칭', NOW(), NOW());

INSERT INTO posts (id, user_id, is_viewed, title, content, created_date, modified_date)
VALUES
 (1, 3, TRUE,  '웹 개발 프로젝트',       'React 기반 웹 개발 프로젝트(프론트 담당자 모집)', NOW(), NOW()),
 (2, 4, TRUE,  '디자인 리뉴얼 프로젝트',  'Figma 기반 UI/UX 개선 작업', NOW(), NOW()),
 (3, 5, TRUE,  '마케팅 캠페인',          'SNS 채널 운영 및 캠페인 집행', NOW(), NOW()),
 (4, 3, TRUE,  '백엔드 프리랜서 모집',    'Spring Boot 경험자 우대', NOW(), NOW()),
 (5, 4, TRUE,  '디자인 프리랜서 모집',    'UI/UX 디자이너 모집(포트폴리오 필수)', NOW(), NOW()),
 (6, 6, TRUE,  '웹 상품 상세페이지 개발', '전자상거래 상품 상세페이지 개발', NOW(), NOW()),
 (7, 7, TRUE,  '모바일 앱 유지보수',     'Android/iOS 유지보수 및 배포', NOW(), NOW()),
 (8, 8, TRUE,  '데이터 분석 프로젝트',   'Python으로 데이터 수집/분석', NOW(), NOW()),
 (9, 9, TRUE,  '프론트엔드 개발자 채용',  'React/Next 경험자 우대', NOW(), NOW()),
 (10,10, TRUE,  '콘텐츠 제작 프로젝트',   '영상 및 콘텐츠 제작(편집 포함)', NOW(), NOW());

INSERT INTO projects (id, deadline_date, started_date, ended_date, hirer_type, employment_type, salary, personnel, skill_level)
VALUES
 (1, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), '개인', '정규직', 5000000, 3, 2),
 (2, DATE_ADD(NOW(), INTERVAL 20 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 50 DAY), '기업', '계약직', 3500000, 2, 1),
 (3, DATE_ADD(NOW(), INTERVAL 25 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 55 DAY), '기업', '프리랜서', 2500000, 1, 1),
 (6, DATE_ADD(NOW(), INTERVAL 40 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 70 DAY), '기업', '계약직', 4200000, 2, 2),
 (8, DATE_ADD(NOW(), INTERVAL 45 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 75 DAY), '개인', '프리랜서', 3000000, 1, 2);

INSERT INTO freelancers (id, salary, period)
VALUES
 (4, 4000000, 30),
 (5, 3500000, 20),
 (7, 2800000, 60),
 (9, 3800000, 45),
 (10, 2600000, 25);

INSERT INTO applications (post_id, user_id, status, content, salary, period, created_date, modified_date)
VALUES
 (1, 7, 'PENDING', '웹 프론트 개발자로 지원합니다. React 경험 3년.', 5000000, 30, NOW(), NOW()),
 (2, 8, 'PENDING', 'UI/UX 디자이너 지원. Figma 주요 사용', 3500000, 20, NOW(), NOW()),
 (3, 9, 'PENDING', '마케팅 운영 경험자입니다.', 2500000, 15, NOW(), NOW()),
 (6, 11, 'PENDING', '전자상거래 상세페이지 개발 가능', 4200000, 40, NOW(), NOW()),
 (8, 12, 'PENDING', '데이터 전처리 및 분석 가능', 3000000, 30, NOW(), NOW()),
 (1, 5, 'PENDING', '프론트 협업 가능, 포트폴리오 보유', 4800000, 28, NOW(), NOW());

INSERT INTO offers (post_id, user_id, status, amount, created_date, modified_date)
VALUES
 (6, 4, 'ACCEPTED', 1, NOW(), NOW()),
 (9, 3, 'ACCEPTED', 2, NOW(), NOW()),
 (10, 5, 'PENDING', 1, NOW(), NOW());

INSERT INTO post_categories (post_id, category_id, created_date, modified_date)
VALUES
 (1, 2, NOW(), NOW()),
 (2, 7, NOW(), NOW()),
 (3, 11, NOW(), NOW()),
 (4, 3, NOW(), NOW()),
 (5, 8, NOW(), NOW()),
 (6, 2, NOW(), NOW()),
 (7, 3, NOW(), NOW()),
 (8, 5, NOW(), NOW()),
 (9, 2, NOW(), NOW()),
 (10, 19, NOW(), NOW());

INSERT INTO post_regions (post_id, region_id, created_date, modified_date)
VALUES
 (1, 1, NOW(), NOW()),
 (2, 1, NOW(), NOW()),
 (3, 6, NOW(), NOW()),
 (4, 1, NOW(), NOW()),
 (5, 1, NOW(), NOW()),
 (6, 14, NOW(), NOW()),
 (7, 10, NOW(), NOW()),
 (8, 26, NOW(), NOW()),
 (9, 1, NOW(), NOW()),
 (10, 18, NOW(), NOW());

INSERT INTO post_skills (post_id, skill_id, created_date, modified_date)
VALUES
 (1, 4, NOW(), NOW()),
 (1, 2, NOW(), NOW()),
 (2, 11, NOW(), NOW()),
 (3, 16, NOW(), NOW()),
 (4, 1, NOW(), NOW()),
 (4, 2, NOW(), NOW()),
 (6, 9, NOW(), NOW()),
 (8, 6, NOW(), NOW()),
 (9, 4, NOW(), NOW()),
 (10, 23, NOW(), NOW());

-- 추가 초기 데이터 (posts 11-20, projects 4/5/7/9/10, freelancers 11-15, applications, offers, post_categories, post_regions, post_skills)

INSERT INTO posts (id, user_id, is_viewed, title, content, created_date, modified_date)
VALUES
 (11, 11, TRUE, '데이터 시각화 프로젝트',      'Python/Chart.js를 이용한 대시보드 개발', NOW(), NOW()),
 (12, 12, TRUE, 'AI 챗봇 개발',               '대화형 AI 챗봇 개발자 모집', NOW(), NOW()),
 (13, 1,  TRUE, 'UI 컴포넌트 라이브러리',     '디자인 시스템 및 컴포넌트 개발', NOW(), NOW()),
 (14, 2,  TRUE, '서버 최적화 작업',           'Spring Boot 성능 개선 경험자', NOW(), NOW()),
 (15, 3,  TRUE, '전자상거래 백오피스 개발',    '관리자 페이지 및 주문 처리 개발', NOW(), NOW()),
 (16, 4,  TRUE, '테스트 자동화 구축',         'Selenium/Jest 기반 테스트 자동화', NOW(), NOW()),
 (17, 5,  TRUE, '모바일 게임 클라이언트',     'Unity 기반 모바일 게임 개발', NOW(), NOW()),
 (18, 6,  TRUE, '데이터 파이프라인 구성',     'ETL 및 배치 처리 경험자', NOW(), NOW()),
 (19, 7,  TRUE, '프론트엔드 성능 튜닝',       '웹 성능 최적화 경험자 우대', NOW(), NOW()),
 (20, 8,  TRUE, '브랜드 콘텐츠 제작',         '브랜디드 콘텐츠 제작 및 마케팅', NOW(), NOW());

INSERT INTO projects (id, deadline_date, started_date, ended_date, hirer_type, employment_type, salary, personnel, skill_level)
VALUES
 (4,  DATE_ADD(NOW(), INTERVAL 35 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 65 DAY), '개인', '프리랜서', 3200000, 2, 2),
 (5,  DATE_ADD(NOW(), INTERVAL 15 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), '기업', '정규직', 4500000, 4, 3),
 (7,  DATE_ADD(NOW(), INTERVAL 50 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 80 DAY), '기업', '프리랜서', 2800000, 1, 1),
 (9,  DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 40 DAY), '개인', '계약직', 2000000, 1, 1),
 (10, DATE_ADD(NOW(), INTERVAL 55 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 85 DAY), '기업', '정규직', 6000000, 5, 4);

INSERT INTO freelancers (id, salary, period)
VALUES
 (11, 3000000, 30),
 (12, 4500000, 60),
 (13, 2700000, 15),
 (14, 5000000, 90),
 (15, 3100000, 45);

INSERT INTO applications (post_id, user_id, status, content, salary, period, created_date, modified_date)
VALUES
 (11, 3, 'PENDING', '데이터 시각화 개발 지원합니다. Pandas/Matplotlib 경험', 3200000, 30, NOW(), NOW()),
 (12, 4, 'PENDING', 'AI 챗봇 개발 참여 희망, NLP 경험 보유', 4200000, 60, NOW(), NOW()),
 (13, 5, 'PENDING', '디자인 시스템 구축 경험자입니다.', 3500000, 40, NOW(), NOW()),
 (14, 6, 'PENDING', '서버 최적화 및 모니터링 경험', 3800000, 50, NOW(), NOW()),
 (15, 7, 'PENDING', '전자상거래 백오피스 개발 가능', 3300000, 35, NOW(), NOW()),
 (16, 8, 'PENDING', '테스트 자동화 구축 지원합니다. CI 경험 있음', 3000000, 25, NOW(), NOW());

INSERT INTO offers (post_id, user_id, status, amount, created_date, modified_date)
VALUES
 (11, 4, 'PENDING', 1, NOW(), NOW()),
 (12, 6, 'ACCEPTED', 2, NOW(), NOW()),
 (13, 8, 'PENDING', 1, NOW(), NOW());

INSERT INTO post_categories (post_id, category_id, created_date, modified_date)
VALUES
 (11, 4, NOW(), NOW()),
 (12, 12, NOW(), NOW()),
 (13, 7, NOW(), NOW()),
 (14, 2, NOW(), NOW()),
 (15, 14, NOW(), NOW()),
 (16, 9, NOW(), NOW()),
 (17, 18, NOW(), NOW()),
 (18, 16, NOW(), NOW()),
 (19, 4, NOW(), NOW()),
 (20, 19, NOW(), NOW());

INSERT INTO post_regions (post_id, region_id, created_date, modified_date)
VALUES
 (11, 2, NOW(), NOW()),
 (12, 3, NOW(), NOW()),
 (13, 1, NOW(), NOW()),
 (14, 14, NOW(), NOW()),
 (15, 6, NOW(), NOW()),
 (16, 10, NOW(), NOW()),
 (17, 11, NOW(), NOW()),
 (18, 26, NOW(), NOW()),
 (19, 1, NOW(), NOW()),
 (20, 18, NOW(), NOW());

INSERT INTO post_skills (post_id, skill_id, created_date, modified_date)
VALUES
 (11, 6, NOW(), NOW()),
 (12, 15, NOW(), NOW()),
 (13, 2, NOW(), NOW()),
 (14, 1, NOW(), NOW()),
 (15, 9, NOW(), NOW()),
 (16, 12, NOW(), NOW()),
 (17, 22, NOW(), NOW()),
 (18, 6, NOW(), NOW()),
 (19, 4, NOW(), NOW()),
 (20, 23, NOW(), NOW());

