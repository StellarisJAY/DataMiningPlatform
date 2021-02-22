package com.jay.dataprocessing.pojo;

import java.util.ArrayList;
import java.util.List;

public class KmeansCluster {
    private List<KmeansPoint> clusterPoints;
    private List<Double> center;
    private int clusterId;

    public KmeansCluster() {
    }

    public KmeansCluster(int clusterId) {
        this.clusterId = clusterId;
        clusterPoints = new ArrayList<>();
        center = new ArrayList<>();
    }

    public KmeansCluster(List<KmeansPoint> clusterPoints, List<Double> center, int clusterId) {
        this.clusterPoints = clusterPoints;
        this.center = center;
        this.clusterId = clusterId;
    }

    public List<KmeansPoint> getClusterPoints() {
        return clusterPoints;
    }

    public void setClusterPoints(List<KmeansPoint> clusterPoints) {
        this.clusterPoints = clusterPoints;
    }

    public List<Double> getCenter() {
        return center;
    }

    public void setCenter(List<Double> center) {
        this.center = center;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }
}
