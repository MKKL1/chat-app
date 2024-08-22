create table communities
(
    id        bigint       not null
        constraint pk_communities
            primary key,
    name      varchar(255) not null,
    image_url varchar(255)
);

create table channels
(
    id           bigint       not null
        constraint pk_channels
            primary key,
    name         varchar(255) not null,
    type         smallint     not null,
    community_id bigint       not null
        constraint fk_channels_on_community
            references communities
);

create table roles
(
    id           bigint       not null
        constraint pk_roles
            primary key,
    name         varchar(255) not null,
    permission   bigint       not null,
    community_id bigint       not null
        constraint fk_roles_on_community
            references communities
);

create table users
(
    id          bigint       not null
        constraint pk_users
            primary key,
    name        varchar(255) not null
        constraint uc_users_name
            unique,
    email       varchar(255)
        constraint uc_users_email
            unique,
    image_url   varchar(255),
    description varchar(255),
    sub         uuid
);

create table community_members
(
    community_id bigint not null
        constraint fk_commem_on_community
            references communities,
    user_id      bigint not null
        constraint fk_commem_on_user
            references users,
    constraint pk_community_members
        primary key (community_id, user_id)
);

create table messages
(
    text                   varchar(255) not null,
    updated_at             timestamp,
    user_id                bigint       not null
        constraint fk_messages_on_user
            references users,
    message_id             bigint       not null,
    channel_id             bigint       not null
        constraint fk_messages_on_channel
            references channels,
    responds_to_message_id bigint,
    constraint pk_messages
        primary key (message_id, channel_id),
    constraint fk_messages_on_retomeidretochid
        foreign key (responds_to_message_id, channel_id) references messages
);

create table message_attachment
(
    id         bigint       not null
        constraint pk_message_attachment
            primary key,
    path       varchar(255) not null,
    size       integer      not null,
    name       varchar(255) not null,
    message_id bigint,
    channel_id bigint,
    constraint fk_message_attachment_on_meidchid
        foreign key (message_id, channel_id) references messages
);

create table reactions
(
    emoji       char   not null,
    user_id     bigint not null
        constraint fk_reactions_on_user
            references users,
    reaction_id bigint not null,
    channel_id  bigint not null
        constraint fk_reactions_on_channel
            references channels,
    message_id  bigint,
    constraint pk_reactions
        primary key (reaction_id, channel_id),
    constraint fk_reactions_on_meidmechid
        foreign key (message_id, channel_id) references messages
);

create table user_roles
(
    role_id bigint not null
        constraint fk_userol_on_role
            references roles,
    user_id bigint not null
        constraint fk_userol_on_user
            references users,
    constraint pk_user_roles
        primary key (role_id, user_id)
);

create table user_subject
(
    user_id bigint not null
        constraint user_subject_users_id_fk
            references users,
    sub     uuid   not null,
    constraint pk_user_subject
        primary key (user_id, sub)
);

