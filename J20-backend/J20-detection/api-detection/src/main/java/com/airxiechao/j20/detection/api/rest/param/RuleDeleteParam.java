package com.airxiechao.j20.detection.api.rest.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RuleDeleteParam {
    @NotBlank
    private String id;
}
