package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TaskUpdateParam {
    @NotBlank
    private String id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String srcDataSourceId;
    @NotBlank
    private String startingOffsetStrategy;
    @NotNull
    private List<Rule> rules;
}
