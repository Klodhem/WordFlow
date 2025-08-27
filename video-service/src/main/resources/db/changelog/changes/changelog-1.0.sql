--liquibase formatted sql

--changeset klodhem:1
CREATE TABLE videos
(
    video_id                 UUID         NOT NULL PRIMARY KEY,
    original_text            TEXT,
    proposals                JSONB,
    status                   VARCHAR(255),
    subtitles_original_path  VARCHAR(255),
    subtitles_translate_path VARCHAR(255),
    title                    VARCHAR(255) NOT NULL,
    translate_text           TEXT,
    user_id                  BIGINT,
    video_path               VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT videos_status_check
        CHECK ( (status)::text = ANY
                ((ARRAY ['OK'::character varying, 'ERROR'::character varying, 'PROCESSING'::character varying])::text[]))
);

--changeset klodhem:2
CREATE TABLE questions
(
    question_id           BIGINT PRIMARY KEY,
    count_correct_answers INTEGER,
    text                  VARCHAR(255),
    type                  VARCHAR(255),
    video_id              UUID NOT NULL,
    constraint questions_type_check
        CHECK ((type)::text = ANY ((ARRAY ['SINGLE'::character varying, 'MULTIPLE'::character varying])::text[])),
    CONSTRAINT fk_questions_video_id FOREIGN KEY (video_id) REFERENCES videos (video_id)
);

CREATE SEQUENCE questions_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;

CREATE TABLE answers
(
    answer_id   BIGINT PRIMARY KEY,
    is_correct  BOOLEAN,
    text        VARCHAR(255),
    question_id BIGINT NOT NULL,
    CONSTRAINT fk_answers_question_id FOREIGN KEY (question_id) REFERENCES questions (question_id)
);

CREATE SEQUENCE answers_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 5;
