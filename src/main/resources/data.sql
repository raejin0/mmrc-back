INSERT INTO USER (USER_ID, EMAIL, USERNAME, PASSWORD, PHONE, ACTIVATED) VALUES (1, 'admin@miraeit.net', 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', '01012345678', 1);
INSERT INTO USER (USER_ID, EMAIL, USERNAME, PASSWORD, PHONE, ACTIVATED) VALUES (2, 'user@miraeit.net', 'user', '$2a$08$UkVvwpULis18S19S5pZFn.HXlwGiyATrzG.1w8zb3Ev1/tJQiXr1G', '01012345678', 1);
INSERT INTO USER (USER_ID, EMAIL, USERNAME, PASSWORD, PHONE, ACTIVATED) VALUES (3, 'user1@miraeit.net', '사용자1', '$2a$10$nhqvC8hAIvxIsuW0Xonq/eH/pOYqQcWUkrOtCEWFS9lyIDw/uC32K', '01012345678', 1);

INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_USER');
INSERT INTO AUTHORITY (AUTHORITY_NAME) values ('ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_NAME) values (1, 'ROLE_USER');
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_NAME) values (1, 'ROLE_ADMIN');
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_NAME) values (2, 'ROLE_USER');
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_NAME) values (3, 'ROLE_USER');

-- schedule
INSERT INTO SCHEDULE (id, email, username, 	title, start_time, end_time, is_all_day, content, CREATED_DATE,	LAST_MODIFIED_DATE)
		VALUES 		( 1, 'user1@miraeit.net', '사용자1', '사내 착수보고회', '2022-05-01 10:00:00', '2022-05-01 12:00:00', false,'중대재해예방 프로젝트 사내 착수보고회', '2022-05-01 12:00:00', '2022-05-01 12:00:00');
INSERT INTO SCHEDULE (id, email, username, 	title, start_time, end_time, is_all_day, content, CREATED_DATE,	LAST_MODIFIED_DATE)
		VALUES 		( 2, 'user2@miraeit.net', '사용자2', '면접', '2022-05-01 15:00:00', '2022-05-01 17:00:00', false,	'개발팀 면접(참석자: 부장, 차장)', '2022-05-01 12:00:00', '2022-05-01 12:00:00');
INSERT INTO SCHEDULE (id, email, username, 	title, start_time, end_time, is_all_day, content, CREATED_DATE,	LAST_MODIFIED_DATE)
		VALUES 		( 3, 'user3@miraeit.net', '사용자3', '회의', '2022-05-02 10:00:00', '2022-05-02 12:00:00', false,	'개발팀 전체 회의', '2022-05-01 12:00:00', '2022-05-01 12:00:00');