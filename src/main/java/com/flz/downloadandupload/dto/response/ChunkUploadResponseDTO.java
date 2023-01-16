package com.flz.downloadandupload.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChunkUploadResponseDTO {
    private String fullFileMd5;
    private Boolean fullFileAlreadyExisted;
    private String currentChunkMd5;
    private Boolean chunkAlreadyExisted;
}
