package com.jay.dataprocessing.controller;

import com.jay.dataprocessing.service.BayesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bayes")
@CrossOrigin
public class BayesController {
    @Autowired
    private BayesService bayesService;

    /**
     * 后端接口，传入训练数据文件、测试数据文件和训练数据的具体信息
     * 返回bayes分类结果，Map格式的列表，列表每项为每一条测试数据的结果
     * @param trainDataFile    训练数据文件
     * @param testDataFile     测试数据文件
     * @param trainDataBegin   训练数据条件值起始位置
     * @param trainDataEnd     训练数据条件之结束位置
     * @param trainDataResultPos  训练数据分类结果位置
     * @return
     * @throws IOException
     */
    @PostMapping("/result")
    public List<Map<String, Object>> getBayesResult(@RequestParam("file1") MultipartFile trainDataFile,
                                                    @RequestParam("file2") MultipartFile testDataFile,
                                                    @RequestParam("trainDataBegin") int trainDataBegin,
                                                    @RequestParam("trainDataEnd") int trainDataEnd,
                                                    @RequestParam("trainDataResultPos") int trainDataResultPos) throws IOException {
        if(trainDataFile == null || testDataFile == null) {
            return null;
        }
        InputStream trainDataIS = trainDataFile.getInputStream();
        InputStream testDataIS = testDataFile.getInputStream();

        List<Map<String, Object>> result = bayesService.getBayesResult(trainDataIS, testDataIS, trainDataBegin,
                trainDataEnd, trainDataResultPos);

        return result;
    }
}
