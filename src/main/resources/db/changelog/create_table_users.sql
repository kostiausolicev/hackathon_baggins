CREATE TABLE users
(
    uuid       UUID NOT NULL DEFAULT gen_random_uuid(),
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    role_uuid  UUID,
    CONSTRAINT pk_users PRIMARY KEY (uuid)
);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE_UUID FOREIGN KEY (role_uuid) REFERENCES capabilities (uuid);