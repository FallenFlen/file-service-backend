package com.flz.downloadandupload.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileExistenceResponseDTO {
    private Boolean fullFileExist;
    private List<Integer> validChunkNumbers;
}
