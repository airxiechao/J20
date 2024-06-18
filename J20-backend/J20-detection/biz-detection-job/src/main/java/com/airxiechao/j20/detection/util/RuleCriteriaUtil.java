package com.airxiechao.j20.detection.util;

import com.airxiechao.j20.common.api.pojo.constant.ConstFieldOperator;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleFilterNodeType;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleGroupOperator;
import com.airxiechao.j20.detection.api.pojo.rule.RuleFilterNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 规则辅助类
 */
public class RuleCriteriaUtil {

    /**
     * 在规则的过滤条件中添加一个条件
     *
     * @param filterNode 条件过滤节点
     * @param field      字段
     * @param value      值
     * @return 新的过滤条件
     */
    public static RuleFilterNode addFilter(RuleFilterNode filterNode, String field, String value) {
        if (StringUtils.isBlank(field) || StringUtils.isBlank(value)) {
            return filterNode;
        }

        if (!ConstRuleFilterNodeType.GROUP.equals(filterNode.getNodeType())) {
            return filterNode;
        }

        RuleFilterNode newNode = new RuleFilterNode(
                ConstRuleFilterNodeType.FIELD,
                ConstFieldOperator.EQUALS,
                field,
                value,
                null
        );

        if (ConstRuleGroupOperator.AND.equals(filterNode.getOperator())) {
            List<RuleFilterNode> children = filterNode.getChildren();
            if (null == children) {
                children = new ArrayList<>();
            }

            children.add(newNode);

            filterNode.setChildren(children);

            return filterNode;
        } else {
            List<RuleFilterNode> newChildren = new ArrayList<>();
            newChildren.add(newNode);
            newChildren.add(filterNode);

            RuleFilterNode newFilterNode = new RuleFilterNode(
                    ConstRuleFilterNodeType.GROUP,
                    ConstRuleGroupOperator.AND,
                    null, null, newChildren
            );

            return newFilterNode;
        }

    }

}
