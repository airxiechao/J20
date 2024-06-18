package com.airxiechao.j20.detection.api.rest.param;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RuleTestParam {
    @NotBlank
    private String id;
    @NotNull
    private List<JSONObject> input;
}
