package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.common.api.pojo.rest.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProtocolListParam extends PageParam {
    private String code;
}
