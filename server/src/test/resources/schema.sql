CREATE TABLE channels
(
    id           BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    type         SMALLINT     NOT NULL,
    community_id BIGINT       NOT NULL,
    CONSTRAINT pk_channels PRIMARY KEY (id)
);

CREATE TABLE communities
(
    id        BIGINT       NOT NULL,
    name      VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    CONSTRAINT pk_communities PRIMARY KEY (id)
);

CREATE TABLE community_members
(
    community_id BIGINT NOT NULL,
    user_id      BIGINT NOT NULL,
    CONSTRAINT pk_community_members PRIMARY KEY (community_id, user_id)
);

CREATE TABLE message_attachment
(
    id         BIGINT       NOT NULL,
    path       VARCHAR(255) NOT NULL,
    size       INTEGER      NOT NULL,
    name       VARCHAR(255) NOT NULL,
    message_id BIGINT,
    channel_id BIGINT,
    CONSTRAINT pk_message_attachment PRIMARY KEY (id)
);

CREATE TABLE messages
(
    text                   VARCHAR(255) NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    user_id                BIGINT       NOT NULL,
    message_id             BIGINT       NOT NULL,
    channel_id             BIGINT       NOT NULL,
    responds_to_message_id BIGINT,
    CONSTRAINT pk_messages PRIMARY KEY (message_id, channel_id)
);

CREATE TABLE reactions
(
    emoji              CHAR   NOT NULL,
    user_id            BIGINT NOT NULL,
    reaction_id        BIGINT NOT NULL,
    channel_id         BIGINT NOT NULL,
    message_id         BIGINT,
    CONSTRAINT pk_reactions PRIMARY KEY (reaction_id, channel_id)
);

CREATE TABLE roles
(
    id           BIGINT       NOT NULL,
    name         VARCHAR(255) NOT NULL,
    permission   BIGINT       NOT NULL,
    community_id BIGINT       NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id          BIGINT       NOT NULL,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    image_url   VARCHAR(255),
    description VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_name UNIQUE (name);

ALTER TABLE channels
    ADD CONSTRAINT FK_CHANNELS_ON_COMMUNITY FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_CHANNEL FOREIGN KEY (channel_id) REFERENCES channels (id);

CREATE INDEX idx_message_channel_id ON messages (channel_id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_RETOMEIDRETOCHID FOREIGN KEY (responds_to_message_id, channel_id) REFERENCES messages (message_id, channel_id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE message_attachment
    ADD CONSTRAINT FK_MESSAGE_ATTACHMENT_ON_MEIDCHID FOREIGN KEY (message_id, channel_id) REFERENCES messages (message_id, channel_id);

ALTER TABLE reactions
    ADD CONSTRAINT FK_REACTIONS_ON_CHANNEL FOREIGN KEY (channel_id) REFERENCES channels (id);

ALTER TABLE reactions
    ADD CONSTRAINT FK_REACTIONS_ON_MEIDMECHID FOREIGN KEY (message_id, channel_id) REFERENCES messages (message_id, channel_id);

ALTER TABLE reactions
    ADD CONSTRAINT FK_REACTIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE roles
    ADD CONSTRAINT FK_ROLES_ON_COMMUNITY FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE community_members
    ADD CONSTRAINT fk_commem_on_community FOREIGN KEY (community_id) REFERENCES communities (id);

ALTER TABLE community_members
    ADD CONSTRAINT fk_commem_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);