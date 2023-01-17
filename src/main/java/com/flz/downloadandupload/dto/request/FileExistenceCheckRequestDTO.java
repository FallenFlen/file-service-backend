package com.flz.downloadandupload.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileExistenceCheckRequestDTO {
    @NotBlank
    private String fullFileMd5;
}
