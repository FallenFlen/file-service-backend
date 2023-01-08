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
    private Integer fileChunkNumber;
    private Boolean fullFileAlreadyUploaded;
    private Boolean merged;
}
