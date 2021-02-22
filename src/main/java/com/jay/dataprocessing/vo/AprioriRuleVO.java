package com.jay.dataprocessing.vo;

public class AprioriRuleVO {
    private String rule;
    private Double confidence;

    public AprioriRuleVO() {
    }

    public AprioriRuleVO(String rule, Double confidence) {
        this.rule = rule;
        this.confidence = confidence;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
