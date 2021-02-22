package com.jay.dataprocessing.service;

import com.jay.dataprocessing.vo.AprioriRuleVO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class AprioriService {

    /**
     * 读取文件
     * @param inputStream
     * @return
     */
    public List<List<String>> readFile(InputStream inputStream){
        try {
            List<List<String>> data = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            int lineNumber = 0;
            while((line = reader.readLine()) != null){
                if(lineNumber != 0){
                    String[] contents = line.split("\t");
                    String[] fields = contents[1].split(", ");
                    data.add(new ArrayList<>());
                    for(String field : fields){
                        data.get(lineNumber - 1).add(field);
                    }
                }
                lineNumber++;
            }
            reader.close();
            inputStream.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获得项集values在数据集dataset中的计数
     * @param dataset
     * @param values
     * @return
     */
    private int countFrequent(List<List<String>> dataset, List<String> values){
        int count = 0;

        for(List<String> data : dataset){
            boolean flag = true;
            for(String value : values){
                if(!data.contains(value)){
                    flag = false;
                    break;
                }
            }
            if(flag == true){
                count ++;
            }
        }
        return count;
    }


    public Map<String, Object> apriori(InputStream inputStream, int minSupport, double minConfidence){
        Map<String, Object> result = new HashMap<>();
        result.put("rules", new ArrayList<>());


        // 获取总体的数据集
        List<List<String>> dataset = readFile(inputStream);
        // 生成候选一项集
        List<List<String>> candidate1 = getCandidate1(dataset);
        List<List<String>> nextFrequent = getSupportedItems(dataset, candidate1, minSupport);
        List<List<String>> nextCandidate = null;
        while(true){
            // 扫描，获取候选k项集，获取后剪枝
            List<List<String>> tempCandidate = getNextCandidate(nextFrequent);
            // 从候选k项集获得频繁k项集
            List<List<String>> tempFrequent = getSupportedItems(dataset, tempCandidate, minSupport);

            if(tempCandidate.size() != 0 && tempCandidate.size() != 0){
                nextCandidate = tempCandidate;
                nextFrequent = tempFrequent;
                for(List<String> frequent : nextFrequent){
                    List<AprioriRuleVO> frequentRules = getAssociationRule(dataset, frequent, minConfidence);
                    ((List<AprioriRuleVO>)result.get("rules")).addAll(frequentRules);
                }
            }
            else{
                break;
            }
        }
        result.put("maxFrequent", nextFrequent);
        result.put("dataset", dataset);
        return result;
    }

    /**
     * 获取候选一项集
     * @param dataset
     * @return
     */
    private List<List<String>> getCandidate1(List<List<String>> dataset){
        List<String> temp = new ArrayList<>();
        for(List<String> data : dataset){
            for(String field : data){
                if(!temp.contains(field)){
                    temp.add(field);
                }
            }
        }

        List<List<String>> candidate1 = new ArrayList<>();
        for(int i = 0; i < temp.size(); i++){
            candidate1.add(new ArrayList<>());
            candidate1.get(i).add(temp.get(i));
        }
        return candidate1;
    }

    /**
     * 获取当前项集的频繁项集
     * @param dataset
     * @param minSupport 最小支持度
     * @param candidate 候选项集
     * @return
     */
    private List<List<String>> getSupportedItems(List<List<String>> dataset, List<List<String>> candidate, int minSupport){
        if(minSupport <= 0 || candidate == null || candidate.isEmpty() || candidate.get(0).isEmpty()) {
            return null;
        }
        List<List<String>> supportedItems = new ArrayList<>();

        // 对每个候选项集计算支持度，如果大于最小支持度，则保留
        for(List<String> line : candidate){
            int count = countFrequent(dataset, line);
            if(count >= minSupport){
                supportedItems.add(line);
            }
        }
        return supportedItems;
    }

    /**
     * 将频繁n项集自连接，得到候选n+1项集
     * @param supportedItems
     * @return
     */
    private List<List<String>> getNextCandidate(List<List<String>> supportedItems){
        int size = supportedItems.get(0).size();
        List<List<String>> nextCandidate = new ArrayList<>();

        for(int i = 0; i < supportedItems.size(); i++){
            for(int j = i + 1; j < supportedItems.size(); j++){
                // 先判断当前的候选集中是否已经有这些元素了，如果有就不做连接
                boolean hasElement = false;
                for(List<String> candidate : nextCandidate){
                    // 如果i和j两个项集中的所有元素已经出现在候选集中
                    if(candidate.containsAll(supportedItems.get(i)) && candidate.containsAll(supportedItems.get(j))){
                        hasElement = true;
                        break;
                    }
                }
                if(hasElement == true){
                    continue;
                }

                // 两个项集相同元素个数
                int countSame = 0;
                // 连接结果集
                HashSet<String> elementSet = new HashSet<>();
                // 记录两个项集中相同元素个数
                for(int k = 0; k < supportedItems.get(i).size(); k++){
                    if(supportedItems.get(j).contains(supportedItems.get(i).get(k))){
                        countSame ++;
                        elementSet.add(supportedItems.get(i).get(k));
                    }
                }
                // 如果两个项集有n-1个项相同，则可以自连接成一项
                if(countSame == size - 1){
                    elementSet.addAll(supportedItems.get(i));
                    elementSet.addAll(supportedItems.get(j));
                    List<String> tempList = new ArrayList<>();
                    tempList.addAll(elementSet);
                    // 剪枝操作，如果n+1项集的一个子集不是频繁项集，则去除该项集
                    if(isSubSetFrequent(tempList, supportedItems) == true){
                        nextCandidate.add(tempList);
                    }
                }
            }
        }

        return nextCandidate;
    }


    /**
     * 判断一个项集的所有子集是否在当前频繁项集中
     * @param connectedList
     * @param supportedItems
     * @return
     */
    private boolean isSubSetFrequent(List<String> connectedList, List<List<String>> supportedItems){
        List<List<String>> subSets = getSubSets(connectedList, connectedList.size() - 1);

        for(List<String> subSet : subSets){
            if(!supportedItems.contains(subSet)){
                return false;
            }
        }
        return true;
    }

    /**
     * 求一个频繁项的关联规则
     * @param dataset
     * @param frequentSet
     * @param minConfidence
     * @return
     */
    private List<AprioriRuleVO> getAssociationRule(List<List<String>> dataset, List<String> frequentSet,double minConfidence){
        List<AprioriRuleVO> rules = new ArrayList<>();
        for(int i = 1; i <= frequentSet.size() - 1; i++){
            List<List<String>> subSets = getSubSets(frequentSet, i);
            for(List<String> subSet : subSets){
                List<String> temp = new ArrayList<>();
                temp.addAll(frequentSet);
                temp.removeAll(subSet);
                int countX = countFrequent(dataset, temp);
                int countXY = countFrequent(dataset, frequentSet);
                double confidence = (double)countXY / countX;
                if(confidence >= minConfidence){
                    AprioriRuleVO ruleVO = new AprioriRuleVO(temp.toString() + "=>" + subSet.toString(), confidence);
                    rules.add(ruleVO);
                }
            }
        }
        return rules;
    }

    /**
     * 求一个n项集的size项子集
     * @param set 超集
     * @param size 子集大小
     * @return
     */
    private List<List<String>> getSubSets(List<String> set, int size){
        List<List<String>> subSets = new ArrayList<>();
        int n = set.size();

        if(size >= 2){
            for(int i = 0; i < n; i++){
                for(int j = i + 1; j < n - (size - 2); j++){
                    List<String> subset = new ArrayList<>();
                    subset.add(set.get(i));
                    subset.add(set.get(j));
                    for(int k = 1; k <= size - 2 && (j + k) < n; k++){
                        subset.add(set.get(k + j));
                    }
                    subSets.add(subset);
                }
            }
        }
        else{
            for(String s : set){
                List<String> subSet = new ArrayList<>();
                subSet.add(s);
                subSets.add(subSet);
            }
        }
        return subSets;
    }

}
