package com.jay.dataprocessing.pojo;

import java.util.List;

public class KmeansPoint {
    private List<Double> pointArray;
    private int clusterId;
    private Double distance;

    public KmeansPoint() {
    }

    public KmeansPoint(List<Double> pointArray, int clusterId, Double distance) {
        this.pointArray = pointArray;
        this.clusterId = clusterId;
        this.distance = distance;
    }

    public List<Double> getPointArray() {
        return pointArray;
    }

    public void setPointArray(List<Double> pointArray) {
        this.pointArray = pointArray;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.valueOf(clusterId);
    }
}
