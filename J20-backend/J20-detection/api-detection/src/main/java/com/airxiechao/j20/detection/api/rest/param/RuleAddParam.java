package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.detection.api.pojo.rule.RuleAggregation;
import com.airxiechao.j20.detection.api.pojo.rule.RuleFrequency;
import com.airxiechao.j20.detection.api.pojo.rule.RuleOutput;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RuleAddParam {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String criteriaType;
    @NotBlank
    private String protocol;

    @NotNull
    private JSONObject criteria;
    @NotNull
    private RuleOutput output;

    private RuleFrequency frequency;

    private RuleAggregation aggregation;
}
