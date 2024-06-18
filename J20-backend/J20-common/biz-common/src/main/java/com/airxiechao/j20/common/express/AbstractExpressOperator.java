package com.airxiechao.j20.common.express;

import com.ql.util.express.*;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;

import java.util.Date;

/**
 * 表达式操作符的抽象类
 */
public abstract class AbstractExpressOperator extends OperatorBase {

    /**
     * 操作符的执行
     * @param parent 上下文
     * @param list 参数
     * @return 执行结果
     * @throws Exception 执行异常
     */
    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Object[] parameters = new Object[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list.get(i) == null && QLExpressRunStrategy.isAvoidNullPointer()) {
                parameters[i] = null;
            } else {
                parameters[i] = list.get(i).getObject(parent);
            }
        }
        Object result = this.executeInner(parent, parameters);
        if (result != null && result.getClass().equals(OperateData.class)) {
            throw new QLException("操作符号定义的返回类型错误：" + this.getAliasName());
        }
        if (result == null) {
            return OperateDataCacheManager.fetchOperateData(null, null);
        } else {
            return OperateDataCacheManager.fetchOperateData(result, ExpressUtil.getSimpleDataType(result.getClass()));
        }
    }

    /**
     * 操作符需要实现的抽象执行函数
     * @param parent 上下文
     * @param list 参数
     * @return 执行结果
     * @throws Exception 执行异常
     */
    public abstract Object executeInner(InstructionSetContext parent, Object[] list) throws Exception;

    /**
     * 进行对象是否相等的比较
     *
     * @param op1 对象1
     * @param op2 对象2
     * @return 比较结果
     */
    public static boolean objectEquals(Object op1, Object op2) {
        if (op1 == null && op2 == null) {
            return true;
        }
        if (op1 == null || op2 == null) {
            return false;
        }

        //Character的值比较
        if (op1 instanceof Character || op2 instanceof Character) {
            int compareResult;
            if (op1 instanceof Character && op2 instanceof Character) {
                return op1.equals(op2);
            } else if (op1 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((Number)op1, (int)(Character)op2);
                return compareResult == 0;
            } else if (op2 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((int)(Character)op1, (Number)op2);
                return compareResult == 0;
            }
        }
        //数值的值比较
        if (op1 instanceof Number && op2 instanceof Number) {
            //数字比较
            int compareResult = OperatorOfNumber.compareNumber((Number)op1, (Number)op2);
            return compareResult == 0;
        }
        //调用原始Object的比较
        return op1.equals(op2);
    }

    /**
     * 进行对象比较
     *
     * @param op1 对象1
     * @param op2 对象2
     * @return 0 等于 ，负数 小于 , 正数 大于
     * @throws Exception 比较异常
     */
    public static int compareData(Object op1, Object op2) throws Exception {
        if (op1 == op2) {
            return 0;
        }

        int compareResult;

        if (op1 instanceof String) {
            compareResult = ((String)op1).compareTo(op2.toString());
        } else if (op2 instanceof String) {
            compareResult = op1.toString().compareTo((String)op2);
        } else if (op1 instanceof Character || op2 instanceof Character) {
            if (op1 instanceof Character && op2 instanceof Character) {
                compareResult = ((Character)op1).compareTo((Character)op2);
            } else if (op1 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((Number)op1, (int)(Character)op2);
            } else if (op2 instanceof Number) {
                compareResult = OperatorOfNumber.compareNumber((int)(Character)op1, (Number)op2);
            } else {
                throw new QLException(op1 + "和" + op2 + "不能执行compare 操作");
            }
        } else if (op1 instanceof Number && op2 instanceof Number) {
            //数字比较
            compareResult = OperatorOfNumber.compareNumber((Number)op1, (Number)op2);
        } else if ((op1 instanceof Boolean) && (op2 instanceof Boolean)) {
            if (((Boolean)op1).booleanValue() == ((Boolean)op2).booleanValue()) {
                compareResult = 0;
            } else {
                compareResult = -1;
            }
        } else if ((op1 instanceof Date) && (op2 instanceof Date)) {
            compareResult = ((Date)op1).compareTo((Date)op2);
        } else {
            throw new QLException(op1 + "和" + op2 + "不能执行compare 操作");
        }
        return compareResult;
    }
}