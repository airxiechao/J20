package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TaskGetParam {
    @NotBlank
    private String id;
}
