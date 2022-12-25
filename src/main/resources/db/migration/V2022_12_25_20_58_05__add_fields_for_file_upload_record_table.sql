alter table `file_upload_record`
    add column `size` bigint null default '0' comment '文件大小' after `path`;

alter table `file_upload_record`
    add column `md5` varchar(32) null comment 'md5值' after `path`;

alter table `file_upload_record`
    add column `status` varchar(32) null comment '上传状态' after `path`;

alter table `file_upload_record`
    add column `is_large_file` tinyint(1) null default '0' comment '是否为大文件' after `md5`;