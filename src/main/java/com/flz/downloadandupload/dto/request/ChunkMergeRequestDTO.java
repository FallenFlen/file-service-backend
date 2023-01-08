package com.flz.downloadandupload.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChunkMergeRequestDTO {
    @NotBlank
    private String fullFileMd5;
    @NotBlank
    private String fullFileName;
    @NotNull
    private Long totalChunkCount;
    @NotNull
    private Long fullFileSize;
}
