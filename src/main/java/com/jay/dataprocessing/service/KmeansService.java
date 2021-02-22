package com.jay.dataprocessing.service;

import com.jay.dataprocessing.pojo.KmeansCluster;
import com.jay.dataprocessing.pojo.KmeansPoint;
import com.jay.dataprocessing.vo.KmeansClusterVO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class KmeansService {

    /**
     * 获取聚类结果，返回到controller
     * @param inputStream
     * @param countCluster
     * @param indexCol
     * @return
     */
    public Map<String, Object> getKmeansResult(InputStream inputStream, int countCluster, int indexCol){
        // 读取文件
        List<KmeansPoint> dataset = readDataFile(inputStream, indexCol);
        // 初始化聚类，生成初始聚类中心
        List<KmeansCluster> clusters = initClusters(dataset, countCluster);

        Map<String, Object> result = new HashMap<>();

        double jcValue = 0.0;
        int times = 0;
        // 循环进行聚类，直到收敛
        while(true){
            // 记录聚类次数
            times ++;
            // 为每一个点重新分类
            for(KmeansPoint point : dataset){
                chooseCluster(point, clusters);
            }
            // 重新计算聚类中心
            for(KmeansCluster cluster : clusters){
                cluster.setCenter(calculateCenter(cluster));
            }
            // 判断是否收敛
            double temp = Jc(clusters);
            if(temp == jcValue){
                break;
            }
            else{
                jcValue = temp;
                for(KmeansCluster cluster : clusters){
                    cluster.getClusterPoints().clear();
                }
            }
        }
        result.put("times", times);
        result.put("clusters", getClusterVOs(clusters));
        result.put("points", dataset);
        return result;
    }


    /**
     * 读取文件
     * @param inputStream
     * @param indexCol
     * @return
     */
    public List<KmeansPoint> readDataFile(InputStream inputStream, int indexCol){
        List<KmeansPoint> points = new ArrayList<>();
        try {
            BufferedReader reader =  new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int lineNum = 0;
            String line = "";
            while((line = reader.readLine()) != null){
                if(lineNum != 0){
                    String[] contents = line.split(",");
                    List<Double> pointArray = new ArrayList<>();
                    for(int i = 0; i < contents.length; i++){
                        if(i != indexCol && indexCol >= 0){
                            pointArray.add(Double.valueOf(contents[i]));
                        }
                    }
                    KmeansPoint point = new KmeansPoint();
                    point.setPointArray(pointArray);
                    points.add(point);
                }
                lineNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return points;
    }

    /**
     * 初始化聚类，生成初始聚类中心
     * @param dataset  数据集
     * @param count    聚类个数
     * @return
     */
    private List<KmeansCluster> initClusters(List<KmeansPoint> dataset, int count){
        List<KmeansCluster> clusters = new ArrayList<>(count);

        int featureSize = dataset.get(0).getPointArray().size();

        double[] maxValues = new double[featureSize];
        double[] minValues = new double[featureSize];

        /*
            生成方法：切分值域，将每一个切分点作为聚类中心
         */
        for(KmeansPoint point : dataset){
            for(int i = 0; i < featureSize; i++){
                if(maxValues[i] <= point.getPointArray().get(i)){
                    maxValues[i] = point.getPointArray().get(i);
                }
                if(minValues[i] >= point.getPointArray().get(i)){
                    minValues[i] = point.getPointArray().get(i);
                }
            }
        }
        // 计算聚类中心之间的步长
        double[] steps = new double[featureSize];
        for(int i = 0; i < featureSize; i++){
            steps[i] = (maxValues[i] - minValues[i]) / (count + 1);
        }

        // 生成初始聚类中心
        for(int j = 0; j < count; j++){
            KmeansCluster cluster = new KmeansCluster(j);
            List<Double> center = new ArrayList<>(featureSize);
            for(int k = 0; k < featureSize; k++){
                center.add(minValues[k] + steps[k] * (j + 1));
            }
            cluster.setCenter(center);
            clusters.add(cluster);
        }


        return clusters;
    }

    /**
     * 计算个案点到一个聚类中心的欧式距离
     * @param point    个案点
     * @param cluster  聚类
     * @return
     */
    private double getDistance(KmeansPoint point, KmeansCluster cluster){
        double distance = 0.0f;
        List<Double> center = cluster.getCenter();
        List<Double> pointArray = point.getPointArray();
        int featureSize = center.size();
        for(int i = 0; i < featureSize; i++){
            distance += Math.pow(center.get(i)-pointArray.get(i), 2);
        }
        return Math.sqrt(distance);
    }

    /**
     * 为点分配聚类集群，计算点到每一个聚类中心的距离，分配到最小的一个
     * @param point
     * @param clusters
     * @return
     */
    private void chooseCluster(KmeansPoint point, List<KmeansCluster> clusters){
        double minDistance = Double.MAX_VALUE;
        for(KmeansCluster cluster : clusters){
            // 计算距离，比较大小
            double distance = getDistance(point, cluster);
            if(minDistance >= distance){
                minDistance = distance;
                point.setClusterId(cluster.getClusterId());
                point.setDistance(minDistance);
            }
        }
        // 将点放入聚类的点集
        clusters.get(point.getClusterId()).getClusterPoints().add(point);
    }

    /**
     * 重新计算聚类中心，用点集的平均值作为新的聚类中心
     * @param cluster
     * @return
     */
    private List<Double> calculateCenter(KmeansCluster cluster){
        List<KmeansPoint> clusterPoints = cluster.getClusterPoints();
        // 如果该类没有点，返回（0，0，0）
        if(clusterPoints.size() == 0){
            List<Double> temp = new ArrayList<>();
            temp.add(0.0);
            temp.add(0.0);
            temp.add(0.0);
            return temp;
        }
        // 计算平均值，作为聚类中心
        int featureSize = clusterPoints.get(0).getPointArray().size();
        double[] average = new double[featureSize];
        for(KmeansPoint point : clusterPoints){
            for(int i = 0; i < featureSize; i++){
                average[i] += point.getPointArray().get(i);
            }
        }
        List<Double> center = new ArrayList<>(featureSize);
        for(int j = 0; j < featureSize; j++){
            average[j] = average[j] / clusterPoints.size();
            center.add(average[j]);
        }

        return center;
    }

    /**
     * 准则函数，用来判断是否收敛
     * @param clusters
     * @return
     */
    private double Jc(List<KmeansCluster> clusters){
        double sum = 0.0;
        for(KmeansCluster cluster : clusters){
            List<KmeansPoint> points = cluster.getClusterPoints();
            int featureSize = points.get(0).getPointArray().size();
            double[] average = new double[featureSize];

            for(KmeansPoint point : points){
                for(int i = 0; i < featureSize; i++){
                    average[i] += point.getPointArray().get(i);
                }
            }
            for(int j = 0; j < featureSize; j++){
                average[j] = average[j] / points.size();
            }

            double pointSum = 0.0;
            for(KmeansPoint point : points){
                for(int i = 0; i < featureSize; i++){
                    pointSum += Math.pow(point.getPointArray().get(i) - average[i], 2);
                }
            }
            sum += pointSum;
        }
        return sum;
    }

    /**
     * 从聚类簇列表获取一个页面对象列表
     * @param clusters
     * @return
     */
    private List<KmeansClusterVO> getClusterVOs(List<KmeansCluster> clusters){
        List<KmeansClusterVO> vos = new ArrayList<>(clusters.size());
        for(KmeansCluster cluster : clusters){
            KmeansClusterVO vo = new KmeansClusterVO(cluster.getCenter(), cluster.getClusterPoints().size());
            vos.add(vo);
        }
        return vos;
    }
}
