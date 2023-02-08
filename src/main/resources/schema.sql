DROP TABLE if exists PUBLIC.LIKES;

CREATE TABLE IF NOT EXISTS PUBLIC.users (
    id BIGINT AUTO_INCREMENT,
    username CHARACTER VARYING(100) NOT NULL,
    email CHARACTER VARYING(100) NOT NULL,
    CONSTRAINT USER_PK PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS PUBLIC.item (
    id BIGINT AUTO_INCREMENT,
    itemname CHARACTER VARYING(100) NOT NULL,
    description CHARACTER VARYING(200) NOT NULL,
    available integer,
    owner_id INTEGER NOT NULL,
    CONSTRAINT ITEM_PK PRIMARY KEY (id),
    CONSTRAINT ITEM_FK FOREIGN KEY (owner_id) REFERENCES PUBLIC.users(id)
    );