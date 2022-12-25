package com.flz.downloadandupload.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChunkRequestDTO {
    private MultipartFile chunk;
    private String fullFileMd5;
}
