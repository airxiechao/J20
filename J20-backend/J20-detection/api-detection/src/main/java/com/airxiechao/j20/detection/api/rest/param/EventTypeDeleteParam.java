package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EventTypeDeleteParam {
    @NotNull
    private String id;
}
