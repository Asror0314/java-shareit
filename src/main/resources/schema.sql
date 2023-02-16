CREATE TABLE IF NOT EXISTS PUBLIC.users (
    id BIGINT AUTO_INCREMENT,
    username CHARACTER VARYING(100) NOT NULL,
    email CHARACTER VARYING(100) NOT NULL,
    CONSTRAINT USER_PK PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS PUBLIC.request (
    id BIGINT AUTO_INCREMENT,
    description CHARACTER VARYING(200) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    requester_id BIGINT NOT NULL,
    CONSTRAINT REQUEST_PK PRIMARY KEY (id),
    CONSTRAINT REQUEST_FK FOREIGN KEY (requester_id) REFERENCES PUBLIC.users(id)
    );

CREATE TABLE IF NOT EXISTS PUBLIC.item (
    id BIGINT AUTO_INCREMENT,
    itemname CHARACTER VARYING(100) NOT NULL,
    description CHARACTER VARYING(200) NOT NULL,
    available bit,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT ITEM_PK PRIMARY KEY (id),
    CONSTRAINT ITEM_FK FOREIGN KEY (owner_id) REFERENCES PUBLIC.users(id),
    CONSTRAINT ITEM_FK2 FOREIGN KEY (request_id) REFERENCES PUBLIC.request(id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.booking (
    id BIGINT AUTO_INCREMENT,
    start_booking TIMESTAMP WITHOUT TIME ZONE,
    end_booking TIMESTAMP WITHOUT TIME ZONE,
    status CHARACTER VARYING(10) NOT NULL,
    booker_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    CONSTRAINT BOOKING_PK PRIMARY KEY (id),
    CONSTRAINT BOOKING_FK FOREIGN KEY (booker_id) REFERENCES PUBLIC.users(id),
    CONSTRAINT BOOKING_FK2 FOREIGN KEY (item_id) REFERENCES PUBLIC.item(id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.comment (
    id BIGINT AUTO_INCREMENT,
    text CHARACTER VARYING(200) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    CONSTRAINT COMMNET_PK PRIMARY KEY (id),
    CONSTRAINT COMMNET_FK FOREIGN KEY (author_id) REFERENCES PUBLIC.users(id),
    CONSTRAINT COMMNET_FK2 FOREIGN KEY (item_id) REFERENCES PUBLIC.item(id)
);

