package com.jay.dataprocessing.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * 朴素贝叶斯分类
 * @author Jay
 * @version 1.0
 */
@Service
public class BayesService {
    /**
     * 读取训练数据
     * 1、从文件逐行读取数据
     * 2、拆分每行数据得到单独的属性值和已知结果值
     * 3、按照Map格式返回训练数据：key=结果值, value=该结果下训练数据属性值集合
     * @param inputStream
     * @return
     */
    public Map<String, ArrayList<String[]>> readTrainDataFromIS(InputStream inputStream, int paramBegin, int paramEnd, int resultPos){
        Map<String, ArrayList<String[]>> data = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            String line = "";
            int lineNumber = 0;
            while((line = reader.readLine()) != null){
                if(lineNumber != 0){
                    // 切分每行数据
                    String[] fields = line.split(",");
                    // 最后一位是结果值，作为数据map的key
                    String key = fields[resultPos];
                    if(data.get(key) == null){
                        data.put(key, new ArrayList<>());
                    }
                    // 只保留数据的有效部分
                    String[] value = Arrays.copyOfRange(fields, paramBegin, paramEnd + 1);
                    System.out.println(Arrays.toString(value));
                    // 将条件值数组放入map的value中
                    data.get(key).add(value);
                }
                lineNumber ++;
            }

            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    public ArrayList<String[]> readTestDataFromIS(InputStream inputStream){
        ArrayList<String[]> data = new ArrayList<>();

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";

            while((line = reader.readLine()) != null){
                // 分割读取的内容
                String[] fields = line.split(",");
                data.add(fields);
            }

            reader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    /**
     *
     * @param trainData 训练数据集
     * @param testData 测试数据集
     * @return
     */
    public Map<String, Double> getBayesProbability(Map<String, ArrayList<String[]>> trainData,String[] testData){
        Map<String, Double> result = new HashMap<>();

        Set<String> keySet = trainData.keySet();

        int total = 0;

        for(ArrayList<String[]> datas : trainData.values()){
            total += datas.size();
        }

        for(String key : keySet){
            ArrayList<String[]> knownDatas = trainData.get(key);
            double[] PXCs = new double[testData.length];
            // 记录训练数据中与测试数据各个条件相同的数量，用于计算公式中的P（Xi|C=?）
            for(String[] knownData : knownDatas){
                for(int i = 0; i < knownData.length; i++){
                    if(knownData[i].equals(testData[i])){
                        PXCs[i] += 1;
                    }
                }
            }
            System.out.println(Arrays.toString(PXCs));
            // 计算最终结果：P(C=?) * ΠP(Xi|C=?)
            System.out.println(knownDatas.size());
            double probability = (double)knownDatas.size() / total;
            // 每个数量值除以总数，得到P(Xi | C=?)的值
            for(int i = 0; i < PXCs.length; i++){
                PXCs[i] = PXCs[i] / knownDatas.size();
                // 最终结果连乘该值
                probability *= PXCs[i];
                System.out.println(probability);
            }
            System.out.println("训练数据结果为：" + key + "情景下，样本总数=" + knownDatas.size());
            System.out.println(total);
            System.out.println("训练数据 P（Xi | C=" + key + "）=" + Arrays.toString(PXCs));

            result.put(key, probability);
            System.out.println(result.get(key));
        }
        return result;
    }

    /**
     * Service接口，controller调用此方法获取贝叶斯分类结果
     * 返回map格式数据的列表，列表每一项表示一条测试数据的分类结果
     * @param trainDataIS 训练数据文件输入流
     * @param testDataIS  测试数据文件输入流
     * @param trainDataBegin 训练数据条件开始位置
     * @param trainDataEnd   训练数据条件结束位置
     * @param trainDataResultPos 训练数据结果位置
     * @return
     */
    public List<Map<String, Object>> getBayesResult(InputStream trainDataIS, InputStream testDataIS, int trainDataBegin,
                                                    int trainDataEnd, int trainDataResultPos){

        Map<String, ArrayList<String[]>> trainData = readTrainDataFromIS(trainDataIS, trainDataBegin, trainDataEnd, trainDataResultPos);
        ArrayList<String[]> testData = readTestDataFromIS(testDataIS);

        List<Map<String, Object>> results = new ArrayList<>(testData.size());

        // 逐个分析测试数据，得出每一条测试数据的分类结果
        for(String[] data : testData){
            Map<String, Double> bayesProbability = getBayesProbability(trainData, data);
            Map<String, Object> resultMap = new HashMap<>();
            String maxKey = this.max(bayesProbability);
            resultMap.put("input", data);   // 测试数据输入
            resultMap.put("prediction", maxKey);  // 分类结果
            resultMap.put("probabilities", bayesProbability);  // 各种类别下的概率
            results.add(resultMap);
        }

        return results;
    }

    /**
     * 取概率最大的作为分类结果
     * @param probabilities
     * @return
     */
    public String max(Map<String, Double> probabilities){
        Set<String> keySet = probabilities.keySet();
        double max = 0;
        String maxKey = null;
        for(String key : keySet){
            if(probabilities.get(key) > max){
                max = probabilities.get(key);
                maxKey = key;
            }
        }

        return maxKey;
    }
}
