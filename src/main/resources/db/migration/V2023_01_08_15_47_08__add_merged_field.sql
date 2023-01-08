alter table `file_chunk`
    add column `merged` tinyint(1) null default '0' comment '是否已合并' after `full_file_md5`;

alter table `file_chunk`
    add column `merge_time` datetime(3) null comment '合并时间' after `merged`;