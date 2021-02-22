package com.jay.dataprocessing.vo;

import java.util.List;

public class KmeansClusterVO {
    private List<Double> center;
    private int countPoints;

    public KmeansClusterVO() {
    }

    public KmeansClusterVO(List<Double> center, int countPoints) {
        this.center = center;
        this.countPoints = countPoints;
    }

    public List<Double> getCenter() {
        return center;
    }

    public void setCenter(List<Double> center) {
        this.center = center;
    }

    public int getCountPoints() {
        return countPoints;
    }

    public void setCountPoints(int countPoints) {
        this.countPoints = countPoints;
    }
}
