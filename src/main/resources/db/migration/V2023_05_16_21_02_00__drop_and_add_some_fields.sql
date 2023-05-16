alter table `file_chunk`
drop
column `file_upload_record_id`;

alter table `file_upload_record`
drop
column `type`;

alter table `file_chunk`
    add column `status` varchar(50) null comment '状态' after `number`;

alter table `file_upload_record`
    add column `status` varchar(50) null comment '状态' after `path`;