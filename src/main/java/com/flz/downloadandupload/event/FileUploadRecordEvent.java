package com.flz.downloadandupload.event;

import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.event.base.BaseApplicationEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadRecordEvent extends BaseApplicationEvent {

    public FileUploadRecordEvent(FileValueObject fileValueObject) {
        super(fileValueObject);
    }
}
