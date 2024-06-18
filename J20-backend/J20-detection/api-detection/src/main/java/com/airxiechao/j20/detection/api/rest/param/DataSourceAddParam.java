package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DataSourceAddParam {
    @NotBlank
    private String name;
    @NotBlank
    private String bootstrapServers;
    @NotBlank
    private String topic;
    @NotNull
    private Integer numPartition;
}
