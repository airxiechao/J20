package com.airxiechao.j20.common.util;

import com.airxiechao.j20.common.api.pojo.constant.ConstFieldOperator;

/**
 * 字段操作符辅助类实现
 */
public class FieldOperatorUtil {

    /**
     * 字符串操作
     * @param str 左值
     * @param operator 操作符
     * @param value 右值
     * @return 操作结果
     */
    public static boolean strOperate(String str, String operator, String value){
        switch (operator.toUpperCase()){
            case ConstFieldOperator.CONTAINS:
                return null != str && str.contains(value);
            case ConstFieldOperator.STARTS_WITH:
                return null != str && str.startsWith(value);
            case ConstFieldOperator.ENDS_WITH:
                return null != str && str.endsWith(value);
            case ConstFieldOperator.EQUALS:
                return null != str && str.equals(value);
            case ConstFieldOperator.NOT_EQUALS:
                return null != str && !str.equals(value);
            case ConstFieldOperator.EXISTS:
                return null != str && !str.isEmpty();
            case ConstFieldOperator.REGEX_MATCH:
                return null != str && RegexFactory.getInstance().get(value).matcher(str).matches();
            case ConstFieldOperator.IN:
                return null != str && StringSetFactory.getInstance().get(value).contains(str);
            case ConstFieldOperator.NOT_IN:
                return null != str && !StringSetFactory.getInstance().get(value).contains(str);
            default:
                return false;
        }
    }

    /**
     * 浮点数操作
     * @param v 左值
     * @param operator 操作符
     * @param value 右值
     * @return 操作结果
     */
    public static boolean doubleOperate(Double v, String operator, Double value){
        switch (operator.toUpperCase()){
            case ConstFieldOperator.EQUALS:
                return null != v && v.equals(value);
            case ConstFieldOperator.NOT_EQUALS:
                return null != v && !v.equals(value);
            case ConstFieldOperator.EXISTS:
                return null != v;
            case ConstFieldOperator.GREATER_THAN:
                return null != v && v > value;
            case ConstFieldOperator.GREATER_THAN_OR_EQUALS:
                return null != v && v >= value;
            case ConstFieldOperator.LESS_THAN:
                return null != v && v < value;
            case ConstFieldOperator.LESS_THAN_OR_EQUALS:
                return null != v && v <= value;
            default:
                return false;
        }
    }

    /**
     * 整数操作
     * @param v 左值
     * @param operator 操作符
     * @param value 右值
     * @return 操作结果
     */
    public static boolean intOperate(Integer v, String operator, Integer value){
        switch (operator.toUpperCase()){
            case ConstFieldOperator.EQUALS:
                return null != v && v.equals(value);
            case ConstFieldOperator.NOT_EQUALS:
                return null != v && !v.equals(value);
            case ConstFieldOperator.EXISTS:
                return null != v;
            case ConstFieldOperator.GREATER_THAN:
                return null != v && v > value;
            case ConstFieldOperator.GREATER_THAN_OR_EQUALS:
                return null != v && v >= value;
            case ConstFieldOperator.LESS_THAN:
                return null != v && v < value;
            case ConstFieldOperator.LESS_THAN_OR_EQUALS:
                return null != v && v <= value;
            default:
                return false;
        }
    }
}
