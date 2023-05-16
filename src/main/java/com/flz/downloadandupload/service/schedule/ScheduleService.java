package com.flz.downloadandupload.service.schedule;

import com.flz.downloadandupload.event.FileCleanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ApplicationEventPublisher publisher;

    @Scheduled(cron = "0 0/3 * * * ?")
    public void handleStatusChangedFile() {
        publisher.publishEvent(new FileCleanEvent(this));
    }
}
