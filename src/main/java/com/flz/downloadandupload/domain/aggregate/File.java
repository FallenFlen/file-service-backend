package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.enums.FileType;

public interface File {
    String withUniqueKey();

    String withPath();

    FileType withFileType();

    default void damage() {

    }

    default void use() {

    }
}
