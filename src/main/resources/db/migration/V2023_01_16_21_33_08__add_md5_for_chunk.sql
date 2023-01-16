alter table `file_chunk`
    add column md5 varchar(32) not null comment '分块md5' after `path`;