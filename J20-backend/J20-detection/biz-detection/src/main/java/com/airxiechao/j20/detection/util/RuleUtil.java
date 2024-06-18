package com.airxiechao.j20.detection.util;

import com.airxiechao.j20.detection.api.pojo.rule.RuleCriteriaCondition;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleCriteriaType;
import com.alibaba.fastjson2.TypeReference;

/**
 * 规则辅助类实现
 */
public class RuleUtil {

    /**
     * 规范化规则条件
     * @param criteria 规则
     * @param type 类型
     * @return 规范化后的规则
     * @throws Exception 处理异常
     */
    public static JSONObject normalizeRuleCriteria(JSONObject criteria, String type) throws Exception {
        JSONObject normalized;
        switch(type){
            case ConstRuleCriteriaType.CONDITION:
                RuleCriteriaCondition criteriaCondition = JSON.parseObject(criteria.toJSONString(), new TypeReference<>() {});
                normalized = (JSONObject) JSON.toJSON(criteriaCondition);
                break;
            default:
                throw new Exception(String.format("规则类型[%s]不支持", type));
        }

        return normalized;
    }
}
