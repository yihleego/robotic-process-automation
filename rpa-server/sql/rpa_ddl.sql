# MySQL
create database if not exists rpa;
use rpa;

create table if not exists app
(
    id           varchar(20) primary key not null,
    name         varchar(40)             not null,
    logo         varchar(200)            not null comment 'app logo url',
    status       tinyint default 0       not null comment 'status 0:DISABLED 1:ENABLED',
    created_time datetime                not null,
    updated_time datetime                null
);
create index idx_app_name on app (name);

create table if not exists func
(
    id           bigint unsigned primary key auto_increment not null,
    app_id       varchar(20)                                not null,
    name         varchar(40)                                not null,
    remark       varchar(200)                               null,
    param        text                                       null comment 'param definition json string',
    priority     int     default 100                        not null comment 'zero is the highest priority, please see python PriorityQueue',
    status       tinyint default 0                          not null comment 'status 0:DISABLED 1:ENABLED',
    created_time datetime                                   not null,
    updated_time datetime                                   null
);
create unique index uk_func_app_id_name on func (app_id, name);

create table if not exists user
(
    id           bigint unsigned primary key auto_increment not null,
    app_id       varchar(20)                                not null,
    account      varchar(40)                                not null,
    nickname     varchar(40)                                null,
    realname     varchar(40)                                null,
    company      varchar(40)                                null,
    phone        varchar(20)                                null,
    avatar       varchar(200)                               null comment 'avatar url',
    status       tinyint default 0                          not null comment 'status 0:DISABLED 1:ENABLED',
    created_time datetime                                   not null,
    updated_time datetime                                   null
);
create unique index uk_user_account_app_id on user (account, app_id);
create index idx_user_app_id on user (app_id);
# create index idx_user_nickname on user (nickname);
# create index idx_user_realname on user (realname);
# create index idx_user_company on user (company);
# create index idx_user_phone on user (phone);

create table if not exists task
(
    id            bigint unsigned primary key auto_increment not null,
    app_id        varchar(20)                                not null,
    user_id       bigint unsigned                            not null,
    type          varchar(40)                                not null,
    priority      int     default 100                        not null,
    data          text                                       null,
    result        text                                       null,
    message       text                                       null,
    status        tinyint default 0                          not null comment 'status 0:CREATED 1:RUNNING 2:DELETED 3:CANCELLED 10:FINISHED 11:FAILED 12:TIMEOUT 13:OFFLINE 14:TERMINATED 15:UNSUPPORTED',
    created_time  datetime                                   not null,
    updated_time  datetime                                   null,
    schedule_time datetime                                   not null,
    executed_time datetime                                   null,
    finished_time datetime                                   null
);
create index idx_task_app_id on task (app_id);
create index idx_task_user_id on task (user_id);
create index idx_task_created_time on task (created_time);
create index idx_task_schedule_time on task (schedule_time);

create table if not exists dict
(
    id           bigint unsigned primary key auto_increment not null,
    `type`       varchar(40)                                not null,
    `key`        varchar(40)                                not null,
    `value`      varchar(40)                                null,
    `remark`     varchar(40)                                null,
    `order`      smallint default 0                         null,
    created_time datetime                                   not null,
    updated_time datetime                                   null
);
create index idx_dict_type_key on dict (`type`, `key`);
