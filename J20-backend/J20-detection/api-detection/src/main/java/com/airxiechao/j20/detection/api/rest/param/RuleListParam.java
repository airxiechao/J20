package com.airxiechao.j20.detection.api.rest.param;

import com.airxiechao.j20.common.api.pojo.rest.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RuleListParam extends PageParam {
    private String name;
    private String criteriaType;
    private String outputEventTypeId;
}
