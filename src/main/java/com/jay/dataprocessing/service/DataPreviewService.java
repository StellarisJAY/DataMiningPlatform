package com.jay.dataprocessing.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataPreviewService {
    public List<String> kmeansPreview(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line = "";
        List<String> preview = new ArrayList<>();
        int lineNum = 0;
        while((line = reader.readLine()) != null){
            if(lineNum  != 0){
                preview.add(line);
            }
            lineNum++;
        }
        return preview;
    }
}
