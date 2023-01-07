alter table `file_upload_record`
drop
column `status`;

alter table `file_chunk`
drop
column `full_file_path`;

alter table `file_chunk`
drop
column `content`;

alter table `file_chunk`
    add column `path` varchar(2048) null comment '分块路径' after `total_chunk_count`;

alter table `file_chunk`
    add column `file_upload_record_id` varchar(32) null comment '关联的文件上传记录id' after `path`;