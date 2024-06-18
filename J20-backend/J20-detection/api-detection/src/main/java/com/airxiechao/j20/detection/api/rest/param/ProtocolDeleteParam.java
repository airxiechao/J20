package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProtocolDeleteParam {
    @NotBlank
    private String code;
}
