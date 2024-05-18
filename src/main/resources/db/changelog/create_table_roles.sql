CREATE TABLE capabilities_paths
(
    capabilities_uuid UUID NOT NULL,
    path      VARCHAR(255)
);

CREATE TABLE capabilities
(
    uuid  UUID NOT NULL DEFAULT gen_random_uuid(),
    title VARCHAR(255),
    CONSTRAINT pk_roles PRIMARY KEY (uuid)
);

ALTER TABLE capabilities_paths
    ADD CONSTRAINT fk_capabilities_paths_on_capabilities_entity FOREIGN KEY (capabilities_uuid) REFERENCES capabilities (uuid);