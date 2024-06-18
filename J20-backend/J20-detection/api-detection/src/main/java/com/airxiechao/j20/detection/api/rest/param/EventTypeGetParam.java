package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EventTypeGetParam {
    @NotNull
    private String id;
}
