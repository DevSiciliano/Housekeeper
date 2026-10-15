create table app_user
(
    id            uuid primary key,
    name          varchar(100)             not null,
    email         varchar(254)             not null,
    password_hash varchar(100)             not null,
    created_at    timestamp with time zone not null,
    constraint uk_app_user_email unique (email)
);