package com.flz.downloadandupload.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChunkUploadRequestDTO {
    private MultipartFile chunk;
    private Integer number;
    private Long standardSize;
    private Long totalChunkCount;
    private String fullFileName;
    private String fullFileMd5;
}
