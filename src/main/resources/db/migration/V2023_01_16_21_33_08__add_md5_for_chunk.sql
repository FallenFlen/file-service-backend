alter table `file_chunk`
    add column md5 varchar(32) not null after `path` comment '分块md5';