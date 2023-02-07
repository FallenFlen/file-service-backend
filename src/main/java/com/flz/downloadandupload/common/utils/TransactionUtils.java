package com.flz.downloadandupload.common.utils;

import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionUtils {

    public void runAfterRollback(Runnable runnable) {
        runAfterByStatus(TransactionSynchronization.STATUS_ROLLED_BACK, runnable);
    }

    public void runAfterCommit(Runnable runnable) {
        runAfterByStatus(TransactionSynchronization.STATUS_COMMITTED, runnable);
    }

    private void runAfterByStatus(int currentStatus, Runnable runnable) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == currentStatus) {
                    runnable.run();
                }
            }
        });
    }
}
