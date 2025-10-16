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
    role              TINYINT  NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    profile_image_url VARCHAR(255),
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_date      DATETIME,
    created_date       DATETIME,
    modified_date      DATETIME
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Post 테이블
CREATE TABLE posts
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT  NOT NULL,
    is_viewed    BOOLEAN NOT NULL DEFAULT FALSE,
    title        VARCHAR(255),
    content      TEXT,
    status       VARCHAR(50),
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Project 테이블 (Post와 1:1, PK 공유)
CREATE TABLE projects
(
    id             BIGINT PRIMARY KEY,
    deadline_date   DATETIME,
    started_date    DATETIME,
    ended_date      DATETIME,
    hirer_type      VARCHAR(255),
    employment_type VARCHAR(255),
    salary         BIGINT,
    personnel      INT,
    skill_level     INT,
    status         VARCHAR(50),
    CONSTRAINT fk_project_post FOREIGN KEY (id) REFERENCES posts (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Application 테이블
CREATE TABLE applications
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id      BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    status       VARCHAR(255) DEFAULT 'PENDING',
    content      TEXT,
    salary       BIGINT UNSIGNED DEFAULT 0,
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
    salary BIGINT,
    period BIGINT,
    CONSTRAINT fk_freelancer_post FOREIGN KEY (id) REFERENCES posts (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Offer 테이블
CREATE TABLE offers
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id      BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    status       VARCHAR(255) DEFAULT 'ACCEPTED',
    amount       INT UNSIGNED DEFAULT 1,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_offers_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_offers_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Question 테이블
CREATE TABLE questions
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    content      TEXT         NOT NULL,
    user_id      BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_questions_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Answer 테이블
CREATE TABLE answers
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    content      VARCHAR(1000) NOT NULL,
    user_id      BIGINT,
    question_id  BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_answers_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Review 테이블
CREATE TABLE reviews
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id      BIGINT,
    user_id      BIGINT,
    rating       INT  NOT NULL,
    comment      TEXT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_review_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Report 테이블
CREATE TABLE reports
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id  BIGINT,
    target_id    BIGINT,
    post_id      BIGINT,
    review_id    BIGINT,
    comments     VARCHAR(255),
    report_type   VARCHAR(50),
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT fk_report_target FOREIGN KEY (target_id) REFERENCES users (id),
    CONSTRAINT fk_report_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_report_review FOREIGN KEY (review_id) REFERENCES reviews (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Category 테이블
CREATE TABLE categories
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    parent_id    BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Region 테이블
CREATE TABLE regions
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    parent_id    BIGINT,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT fk_regions_parent FOREIGN KEY (parent_id) REFERENCES regions (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Skill 테이블
CREATE TABLE skills
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    created_date  DATETIME,
    modified_date DATETIME
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- PostCategory 연결 테이블
CREATE TABLE post_categories
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id      BIGINT NOT NULL,
    category_id  BIGINT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_post_categories UNIQUE (post_id, category_id),
    CONSTRAINT fk_post_categories_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_categories_category FOREIGN KEY (category_id) REFERENCES categories (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- PostRegion 연결 테이블
CREATE TABLE post_regions
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id      BIGINT NOT NULL,
    region_id    BIGINT NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    CONSTRAINT uk_post_regions UNIQUE (post_id, region_id),
    CONSTRAINT fk_post_regions_post FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT fk_post_regions_region FOREIGN KEY (region_id) REFERENCES regions (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- PostSkill 연결 테이블
CREATE TABLE post_skills
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id      BIGINT NOT NULL,
    skill_id     BIGINT NOT NULL,
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
    created_date    DATETIME,
    modified_date   DATETIME,
    CONSTRAINT fk_application_file_application FOREIGN KEY (application_id) REFERENCES applications (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- ChatRoom 테이블
CREATE TABLE chat_rooms
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    created_date  DATETIME,
    modified_date DATETIME,
    INDEX        idx_chat_rooms_name (name)
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
    created_date          DATETIME,
    modified_date         DATETIME,
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
    created_date    DATETIME,
    modified_date   DATETIME,
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
    created_date     DATETIME,
    modified_date    DATETIME,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notification_offer FOREIGN KEY (offer_id) REFERENCES offers (id),
    CONSTRAINT fk_notification_chat_message FOREIGN KEY (chat_message_id) REFERENCES chat_messages (id),
    CONSTRAINT fk_notification_review FOREIGN KEY (review_id) REFERENCES reviews (id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- User 테이블 초기 데이터
INSERT INTO users (username, password, nickname, role, email, deleted, created_date, modified_date)
VALUES ('admin', 'admin123!', '관리자', 1, 'admin@pi.com', FALSE, NOW(), NOW()),
       ('system', 'system123!', '시스템', 1, 'system@pi.com', FALSE, NOW(), NOW()),
       ('user1', 'user123!', '유저1', 0, 'user1@pi.com', FALSE, NOW(), NOW()),
       ('user2', 'user123!', '유저2', 0, 'user2@pi.com', FALSE, NOW(), NOW()),
       ('user3', 'user123!', '유저3', 0, 'user3@pi.com', FALSE, NOW(), NOW());

-- Question 테이블 초기 데이터
INSERT INTO questions (title, content, user_id, created_date, modified_date)
VALUES ('사이트 이용 방법이 궁금해요.', '구인/구직 게시글을 등록하려면 어떻게 해야 하나요?', 3, NOW(), NOW()),
       ('프리랜서로 등록하려면 어떤 절차가 필요한가요?', '프리랜서 프로필 작성 및 인증 방법을 알려주세요.', 4, NOW(), NOW()),
       ('결제 및 수수료 정책은 어떻게 되나요?', '프로젝트 완료 후 결제 과정과 수수료 부과 기준이 궁금합니다.', 5, NOW(), NOW()),
       ('구직자가 프로젝트에 지원하는 방법은?', '구직자가 프로젝트에 지원할 때 필요한 정보와 절차를 설명해주세요.', 3, NOW(), NOW()),
       ('회원 탈퇴는 어떻게 하나요?', '사이트에서 회원 탈퇴 절차를 안내해주세요.', 4, NOW(), NOW());

-- Answer 테이블 초기 데이터
INSERT INTO answers (content, user_id, question_id, created_date, modified_date)
VALUES ('구인/구직 게시글은 상단 메뉴에서 등록 가능합니다. 회원가입 후 이용해 주세요.', 1, 1, NOW(), NOW()),
       ('프리랜서 등록은 프로필 작성 후 인증 절차를 거치면 완료됩니다.', 1, 2, NOW(), NOW()),
       ('프로젝트 완료 후 결제가 진행되며, 수수료는 정책에 따라 자동 부과됩니다.', 2, 3, NOW(), NOW()),
       ('구직자는 프로젝트 상세 페이지에서 지원 버튼을 눌러 정보를 입력하면 지원이 완료됩니다.', 2, 4, NOW(), NOW()),
       ('회원 탈퇴는 마이페이지에서 직접 신청하실 수 있습니다. 탈퇴 후 데이터는 복구되지 않습니다.', 1, 5, NOW(), NOW());

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

-- Post & Project 테이블 초기 데이터 (post_id 1,2,3)
INSERT INTO posts (user_id, is_viewed, title, content, status, created_date, modified_date)
VALUES (3, TRUE, '웹 개발 프로젝트', 'React 기반 웹 개발', NULL, NOW(), NOW()),
       (4, TRUE, '디자인 리뉴얼', 'Figma로 UI/UX 개선', NULL, NOW(), NOW()),
       (5, TRUE, '마케팅 캠페인', 'SNS 마케팅 및 콘텐츠 제작', NULL, NOW(), NOW());

INSERT INTO projects (id, deadline_date, started_date, ended_date, hirer_type, employment_type, salary, personnel, skill_level,
                     status)
VALUES (1, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), '개인', '정규직', 5000000, 3, 2, NULL),
       (2, DATE_ADD(NOW(), INTERVAL 15 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY), '기업', '계약직', 3000000, 2, 1, NULL),
       (3, DATE_ADD(NOW(), INTERVAL 20 DAY), NOW(), DATE_ADD(NOW(), INTERVAL 40 DAY), '개인', '프리랜서', 2000000, 1, 1,
        NULL);

-- Post & Freelancer 테이블 초기 데이터 (post_id 4,5)
INSERT INTO posts (user_id, is_viewed, title, content, status, created_date, modified_date)
VALUES (3, TRUE, '백엔드 개발 프리랜서 모집', 'Spring Boot 경험자 우대', NULL, NOW(), NOW()),
       (4, TRUE, '디자인 프리랜서 모집', 'UI/UX 디자인 경험자', NULL, NOW(), NOW());

INSERT INTO freelancers (id, salary, period)
VALUES (4, 4000000, 30),
       (5, 3500000, 20);

-- Applications 테이블 초기 데이터 (post_id 1,2,3)
INSERT INTO applications (post_id, user_id, content, salary, period, created_date, modified_date)
VALUES (1, 3, '웹 개발 지원합니다.', 5000000, 30, NOW(), NOW()),
       (2, 4, '디자인 리뉴얼 경험 있습니다.', 3000000, 20, NOW(), NOW()),
       (3, 5, '마케팅 캠페인 참여 희망합니다.', 2000000, 15, NOW(), NOW());

-- Offers 테이블 초기 데이터 (post_id 6,7)
INSERT INTO posts (user_id, is_viewed, title, content, status, created_date, modified_date)
VALUES (4, TRUE, '오퍼용 게시글1', '오퍼 테스트1', NULL, NOW(), NOW()),
       (5, TRUE, '오퍼용 게시글2', '오퍼 테스트2', NULL, NOW(), NOW());

INSERT INTO offers (post_id, user_id, amount, created_date, modified_date)
VALUES (6, 4, 1, NOW(), NOW()),
       (7, 5, 2, NOW(), NOW());
