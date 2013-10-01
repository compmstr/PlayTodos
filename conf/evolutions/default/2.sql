#Tasks schema
#Adds user schema

# --- !Ups


ALTER TABLE task ADD COLUMN complete integer(1) not null default 0;
ALTER TABLE task ADD COLUMN uid integer not null;
			
CREATE SEQUENCE user_id_seq;
CREATE TABLE users (
			 uid integer NOT NULL DEFAULT nextval('user_id_seq'),
			 username varchar(255),
			 pass varchar(255)
);
INSERT INTO users (username, pass) values ('corey', 'pass');
INSERT INTO users (username, pass) values ('guest', 'guestPass');

# --- !Downs
ALTER TABLE task DROP COLUMN complete;
ALTER TABLE task DROP COLUMN uid;

DROP TABLE users;
