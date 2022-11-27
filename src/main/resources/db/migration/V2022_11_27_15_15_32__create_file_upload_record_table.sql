create table `file_upload_record`
(
    `id`          varchar(32)   not null primary key,
    `name`        varchar(512)  not null,
    `path`        varchar(2048) not null,
    `create_time` timestamp     not null,
    `update_time` timestamp     not null,
    `deleted`     tinyint(1) not null default '0'
);