package com.flz.downloadandupload.domain.enums;

public enum FileSizeType {
    LARGE_SIZE_FILE,
    NORMAL_SIZE_FILE;

    public static FileSizeType calculate(long size, long criticalValue) {
        return size > criticalValue ? LARGE_SIZE_FILE : NORMAL_SIZE_FILE;
    }
}
