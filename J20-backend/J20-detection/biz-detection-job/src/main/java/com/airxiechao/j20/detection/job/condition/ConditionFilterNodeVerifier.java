package com.airxiechao.j20.detection.job.condition;

import com.airxiechao.j20.common.api.pojo.constant.ConstFieldOperator;
import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.express.ExpressTemplate;
import com.airxiechao.j20.common.util.FieldOperatorUtil;
import com.airxiechao.j20.common.util.FieldUtil;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleFilterNodeType;
import com.airxiechao.j20.detection.api.pojo.constant.ConstRuleGroupOperator;
import com.airxiechao.j20.detection.api.pojo.rule.RuleFilterNode;
import com.airxiechao.j20.detection.util.ResourceFactoryUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 条件过滤器的实现
 */
public class ConditionFilterNodeVerifier {

    private RuleFilterNode node;

    public ConditionFilterNodeVerifier(RuleFilterNode node, JobConfig config) {
        this.node = node;

        // 配置资源工厂
        ResourceFactoryUtil.config(config);
    }

    /**
     * 验证
     * @param log 日志
     * @return 是否通过
     */
    public boolean verify(Log log){
        return traverse(node, log);
    }

    /**
     * 遍历节点
     * @param node 节点
     * @param log 日志
     * @return 是否通过
     */
    private boolean traverse(RuleFilterNode node, Log log){
        switch (node.getNodeType().toUpperCase()){
            case ConstRuleFilterNodeType.GROUP:
                return traverseGroup(node.getOperator(), node.getChildren(), log);
            case ConstRuleFilterNodeType.FIELD:
                return traverseField(node.getField(), node.getOperator(), node.getValue(), log);
            default:
                return false;
        }
    }

    /**
     * 遍历组节点
     * @param operator 操作符
     * @param children 子节点
     * @param log 日志
     * @return 是否通过
     */
    private boolean traverseGroup(String operator, List<RuleFilterNode> children, Log log){
        if(null == children || children.size() == 0){
            return true;
        }

        switch (operator.toUpperCase()){
            case ConstRuleGroupOperator.AND:
                for (RuleFilterNode node : children) {
                    if(!traverse(node, log)){
                        return false;
                    }
                }
                return true;
            case ConstRuleGroupOperator.OR:
                for (RuleFilterNode node : children) {
                    if(traverse(node, log)){
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 遍历条件节点
     * @param field 左字段
     * @param operator 操作符
     * @param value 右值
     * @param log 日志
     * @return 是否通过
     */
    private boolean traverseField(String field, String operator, String value, Log log){
        // 取左值
        Object objValue = FieldUtil.renderLogToField(field, log);
        if(null == objValue){
            return false;
        }

        // 取右值
        if(StringUtils.isNotBlank(value)) {
            value = FieldUtil.renderLogToValue(value, log);
        }

        // 比较值
        String strValue = objValue.toString();
        log.addFeature(String.format("%s:%s", field, strValue));
        switch (operator.toUpperCase()){
            case ConstFieldOperator.GREATER_THAN:
            case ConstFieldOperator.GREATER_THAN_OR_EQUALS:
            case ConstFieldOperator.LESS_THAN:
            case ConstFieldOperator.LESS_THAN_OR_EQUALS:
                return FieldOperatorUtil.doubleOperate(Double.valueOf(strValue), operator, Double.valueOf(value));
            case ConstFieldOperator.EXPRESS:
                try {
                    return new ExpressTemplate(value, objValue, false).execute().equals(true);
                }catch (Exception e){
                    return false;
                }
            default:
                return FieldOperatorUtil.strOperate(strValue, operator, value);
        }
    }

}
