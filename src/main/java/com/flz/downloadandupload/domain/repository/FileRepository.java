package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.File;

import java.util.List;

public interface FileRepository<T extends File> {
    void saveAll(List<T> files);
}
