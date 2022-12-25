package com.flz.downloadandupload.controller;

import com.flz.downloadandupload.dto.request.ChunkRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files/advance")
public class AdvanceFileController {

    @PostMapping("/chunk")
    public void upload(ChunkRequestDTO requestDTO) {

    }
}
