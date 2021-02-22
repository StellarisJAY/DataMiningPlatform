package com.jay.dataprocessing.controller;

import com.jay.dataprocessing.service.AprioriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/apriori")
public class AprioriController {
    @Autowired
    private AprioriService aprioriService;

    /**
     * 获取apriori关联规则分析结果
     * @param file        上传文件
     * @param minSupport  最小支持度计数
     * @param minConfidence 最小置信度
     * @return
     */
    @PostMapping("/result")
    public Map<String, Object> getResult(MultipartFile file, int minSupport, double minConfidence){
        try {
            Map<String, Object> result = aprioriService.apriori(file.getInputStream(), minSupport, minConfidence);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取数据文件的预览
     * @param file
     * @return
     */
    @PostMapping("/preview")
    public List<List<String>> getDataFilePreview(MultipartFile file){
        try {
            List<List<String>> preview = aprioriService.readFile(file.getInputStream());
            return preview;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
