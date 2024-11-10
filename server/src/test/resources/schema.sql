create table users
(
    id          bigint       not null
        constraint pk_users
            primary key,
    name        varchar(32) not null
        constraint uc_users_name
            unique,
--     email       varchar(255)
--         constraint uc_users_email
--             unique,
    image_url   varchar(255),
    description varchar(255),
    sub         uuid
);

create table communities
(
    id        bigint       not null
        constraint pk_communities
            primary key,
    name      varchar(255) not null,
    owner_id bigint not null
        constraint fk_communities_on_user
            references users on delete cascade ,
    image_url varchar(255),
    base_permissions int not null
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
            references communities on delete cascade
);

create table roles
(
    id           bigint       not null
        constraint pk_roles
            primary key,
    name         varchar(64) not null,
    permission   bigint       not null,
    community_id bigint       not null
        constraint fk_roles_on_community
            references communities on delete cascade
);

create table channel_roles
(
    channel_id bigint not null
        constraint fk_channrole_on_channel
            references channels on delete cascade ,
    role_id bigint not null
        constraint fk_channrole_on_role
            references roles on delete  cascade ,
    permission bigint not null,
    constraint pk_channel_roles
        primary key (channel_id, role_id)
);

create table community_members
(
    community_id bigint not null
        constraint fk_commem_on_community
            references communities on delete cascade ,
    user_id      bigint not null
        constraint fk_commem_on_user
            references users on delete cascade,
    constraint pk_community_members
        primary key (community_id, user_id)
);

create table messages
(
    text                   text not null,
    updated_at             timestamp,
    user_id                bigint       not null
        constraint fk_messages_on_user
            references users,
    message_id             bigint       not null,
    channel_id             bigint       not null
        constraint fk_messages_on_channel
            references channels on delete cascade ,
    responds_to_message_id bigint,
    gif_link               varchar(255),
    constraint pk_messages
        primary key (message_id, channel_id),
    constraint fk_messages_on_retomeidretochid
        foreign key (responds_to_message_id, channel_id) references messages on delete cascade
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
        foreign key (message_id, channel_id) references messages on delete cascade
);

create table reactions
(
    emoji       char   not null,
    user_id     bigint not null
        constraint fk_reactions_on_user
            references users on delete cascade ,
    reaction_id bigint not null,
    channel_id  bigint not null
        constraint fk_reactions_on_channel
            references channels on delete cascade ,
    message_id  bigint,
    constraint pk_reactions
        primary key (reaction_id, channel_id),
    constraint fk_reactions_on_meidmechid
        foreign key (message_id, channel_id) references messages on delete cascade
);

create table user_roles
(
    role_id bigint not null
        constraint fk_userol_on_role
            references roles on delete cascade ,
    user_id bigint not null
        constraint fk_userol_on_user
            references users on delete cascade ,
    constraint pk_user_roles
        primary key (role_id, user_id)
);

create table user_subject
(
    user_id bigint not null
        constraint user_subject_users_id_fk
            references users on delete cascade ,
    sub     uuid   not null,
    constraint pk_user_subject
        primary key (user_id, sub)
);

create table invitations
(
  id bigint not null constraint pk_invitation primary key,
  community_id bigint not null constraint fk_invitations_on_community references communities,
  expired_at timestamp not null
);
