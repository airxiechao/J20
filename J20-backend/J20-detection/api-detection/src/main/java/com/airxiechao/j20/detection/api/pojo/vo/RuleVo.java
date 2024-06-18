package com.airxiechao.j20.detection.api.pojo.vo;

import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 规则显示对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RuleVo extends Rule {
    private String outputEventTypeId;
    private String outputEventTypeName;

    public static RuleVo of(Rule rule){
        RuleVo vo = JSON.parseObject(JSON.toJSONString(rule), RuleVo.class);

        if(null != rule.getOutput()) {
            vo.setOutputEventTypeId(rule.getOutput().getEventTypeId());
            vo.setOutputEventTypeName(rule.getOutput().getEventTypeName());
        }
        
        return vo;
    }
}
