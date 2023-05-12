package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.enums.FileType;

public interface File {
    String withMd5();

    String withPath();

    FileType withFileType();
}
