package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.event.FileChunkDamageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileChunkDamageEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void listen(FileChunkDamageEvent event) {
        //todo 删除不存在的chunk的表数据
        //todo 删除损坏的chunk文件
    }
}
