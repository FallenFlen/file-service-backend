package com.flz.downloadandupload.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadRecordResponseDTO {
    private String id;
    private String name;
    private String path;
    private Long size;
}
