alter table `file_upload_record`
    add column `type` varchar(100) null comment '类型' after `status`;

alter table `file_upload_record`
drop
column `is_large_file`;