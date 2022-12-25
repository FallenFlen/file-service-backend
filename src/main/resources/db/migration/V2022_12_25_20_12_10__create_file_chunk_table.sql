create table `file_chunk`
(
    `id`                varchar(32)   not null primary key,
    `number`            bigint        not null comment '文件块编号',
    `standard_size`     bigint        not null comment '分块标准大小',
    `current_size`      bigint        not null comment '当前分块大小',
    `total_chunk_count` bigint        not null comment '文件块总数',
    `content`           text          not null comment '当前分块内容',
    `full_file_name`    varchar(2048) not null comment '文件整体名称',
    `full_file_path`    varchar(2048) not null comment '文件整体路径',
    `full_file_md5`     varchar(32)   not null comment '文件整体md5',
    `create_time`       timestamp     not null,
    `update_time`       timestamp     not null,
    `deleted`           tinyint(1) not null default '0'
);