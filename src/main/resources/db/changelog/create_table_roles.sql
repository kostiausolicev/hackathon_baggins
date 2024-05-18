CREATE TABLE role_paths
(
    role_uuid UUID NOT NULL,
    path      VARCHAR(255)
);

CREATE TABLE roles
(
    uuid  UUID NOT NULL DEFAULT gen_random_uuid(),
    title VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (uuid)
);

ALTER TABLE role_paths
    ADD CONSTRAINT fk_role_paths_on_role_entity FOREIGN KEY (role_uuid) REFERENCES roles (uuid);