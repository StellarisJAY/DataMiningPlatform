package com.jay.dataprocessing.controller;

import com.jay.dataprocessing.service.DataPreviewService;
import com.jay.dataprocessing.service.KmeansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kmeans")
public class KmeansController {
    @Autowired
    private KmeansService kmeansService;
    @Autowired
    private DataPreviewService previewService;
    @PostMapping("/result")
    public Map<String, Object> getKmeansResult(MultipartFile file, int count, int indexCol){
        try {
            Map<String, Object> result = kmeansService.getKmeansResult(file.getInputStream(), count, indexCol);
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    @PostMapping("/preview")
    public List<String> dataPreview(MultipartFile file){
        try {
            List<String> preview = previewService.kmeansPreview(file);
            return preview;
        } catch (IOException e) {
            return null;
        }
    }
}
