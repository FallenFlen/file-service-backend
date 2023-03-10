package com.flz.downloadandupload.domain.enums;

public enum FileType {
    LARGE_SIZE_FILE,
    NORMAL_SIZE_FILE;

    public static FileType calculate(long size, long criticalValue) {
        return size > criticalValue ? LARGE_SIZE_FILE : NORMAL_SIZE_FILE;
    }
}
