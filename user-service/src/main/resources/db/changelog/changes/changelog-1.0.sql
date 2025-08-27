--liquibase formatted sql

--changeset klodhem:1
CREATE TABLE users
(
    user_id  BIGINT PRIMARY KEY,
    role     VARCHAR(31)  NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE
);

CREATE SEQUENCE users_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;

--changeset klodhem:2
CREATE TABLE groups
(
    group_id   BIGINT PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    owner_id   BIGINT       NOT NULL,
    CONSTRAINT fk_groups_owner_id FOREIGN KEY (owner_id) REFERENCES users (user_id)
);

CREATE SEQUENCE groups_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;

--changeset klodhem:3
CREATE TABLE groups_users
(
    group_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    CONSTRAINT fk_groups_users_group_id FOREIGN KEY (group_id) REFERENCES groups (group_id),
    CONSTRAINT fk_groups_users_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT uk_groups_users UNIQUE (group_id, user_id)
);


CREATE TABLE groups_videos
(
    group_id BIGINT       NOT NULL,
    video_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_groups_videos_group_id FOREIGN KEY (group_id) REFERENCES groups (group_id),
    CONSTRAINT uk_groups_videos UNIQUE (group_id, video_id)
);

--changeset klodhem:4
CREATE TABLE invites
(
    invite_id     BIGINT PRIMARY KEY,
    student_id    BIGINT NOT NULL,
    group_id      BIGINT NOT NULL,
    status_invite VARCHAR(31),
    teacher_id    BIGINT NOT NULL,
    CONSTRAINT fk_invites_student_id FOREIGN KEY (student_id) REFERENCES users (user_id),
    CONSTRAINT fk_invites_group_id FOREIGN KEY (group_id) REFERENCES groups (group_id),
    CONSTRAINT fk_invites_teacher_id FOREIGN KEY (teacher_id) REFERENCES users (user_id),
    CONSTRAINT status_invite_check CHECK (status_invite IN ('ACCEPTED', 'DECLINED', 'EXPECTED'))
);

CREATE SEQUENCE invites_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;

--changeset klodhem:5
CREATE TABLE solutions
(
    solution_id BIGINT PRIMARY KEY,
    mark        INT          NOT NULL,
    user_id     BIGINT       NOT NULL,
    video_id    VARCHAR(255) NOT NULL,
    datetime    TIMESTAMP,
    CONSTRAINT fk_solutions_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE SEQUENCE solutions_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;

--changeset klodhem:6
CREATE TABLE user_answer_sheets
(
    user_answer_sheet_id BIGINT PRIMARY KEY,
    mark                 INT    NOT NULL,
    question_id          BIGINT NOT NULL,
    solution_id          BIGINT NOT NULL,
    CONSTRAINT fk_user_answer_sheets_solution_id FOREIGN KEY (solution_id) REFERENCES solutions (solution_id)
);

CREATE SEQUENCE user_answer_sheets_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;