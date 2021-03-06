# MySQL
create database if not exists rpa;
use rpa;

create table if not exists app
(
    id           varchar(40) primary key not null,
    name         varchar(40)             not null,
    status       tinyint default 0       not null comment 'status 0:DISABLED 1:ENABLED',
    created_time datetime                not null,
    updated_time datetime                null
);
create index idx_app_name on app (name);

create table if not exists user
(
    id           varchar(40) primary key not null,
    app_id       varchar(40)             not null,
    account      varchar(40)             not null,
    nickname     varchar(40)             null,
    realname     varchar(40)             null,
    company      varchar(40)             null,
    phone        varchar(20)             null,
    avatar       varchar(200)            null,
    status       tinyint default 0       not null comment 'status 0:DISABLED 1:ENABLED',
    created_time datetime                not null,
    updated_time datetime                null
);
create unique index uk_user_account_app_id on user (account, app_id);
create index idx_user_app_id on user (app_id);
# create index idx_user_nickname on user (nickname);
# create index idx_user_realname on user (realname);
# create index idx_user_company on user (company);
# create index idx_user_phone on user (phone);

create table if not exists task
(
    id            varchar(40) primary key not null,
    app_id        varchar(40)             not null,
    user_id       varchar(40)             not null,
    type          varchar(40)             not null,
    status        tinyint default 0       not null comment 'status 0:CREATED 1:RUNNING 2:DELETED 3:CANCELLED 10:FINISHED 11:FAILED 12:TIMEOUT 13:OFFLINE 14:TERMINATED 15:UNSUPPORTED',
    priority      int     default 100     not null,
    data          varchar(4096)           null,
    result        varchar(4096)           null,
    message       varchar(200)            null,
    created_time  datetime                not null,
    updated_time  datetime                null,
    schedule_time datetime                not null,
    executed_time datetime                null,
    finished_time datetime                null
);
create index idx_task_app_id on task (app_id);
create index idx_task_user_id on task (user_id);
create index idx_task_created_time on task (created_time);
create index idx_task_schedule_time on task (schedule_time);

create table if not exists dict
(
    `type`   varchar(40)        not null,
    `key`    varchar(40)        not null,
    `value`  varchar(40)        null,
    `remark` varchar(40)        null,
    `order`  smallint default 0 null,
    primary key (`type`, `key`)
);
