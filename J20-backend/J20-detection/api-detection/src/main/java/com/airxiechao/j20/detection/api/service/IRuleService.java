package com.airxiechao.j20.detection.api.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;

/**
 * 规则服务接口
 */
public interface IRuleService {
    /**
     * 查询规则列表
     * @param name 名称
     * @param criteriaType 类型
     * @param outputEventTypeId 输出事件的类型ID
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @return 规则列表
     */
    PageVo<Rule> list(
            String name, String criteriaType, String outputEventTypeId,
            Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc);

    /**
     * 是否存在规则ID
     * @param id 规则ID
     * @return 是否存在
     */
    boolean hasId(String id);

    /**
     * 查询规则
     * @param id 规则ID
     * @return 规则
     */
    Rule get(String id) throws NotFoundException;

    /**
     * 是否存在
     * @param id 规则ID
     * @return 是否存在
     */
    boolean exists(String id);

    /**
     * 是否存在名称
     * @param name 名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 通过名称获取规则
     * @param name 名称
     * @return 规则
     */
    Rule getByName(String name) throws NotFoundException;

    /**
     * 添加规则
     * @param rule 规则
     */
    Rule add(Rule rule) throws Exception;

    /**
     * 更新规则
     * @param rule 规则
     */
    void update(Rule rule) throws Exception;

    /**
     * 山粗规则
     * @param id ID
     */
    void delete(String id) throws Exception;

    /**
     * 测试规则
     * @param id 规则ID
     * @param input 测试日志输入
     * @return 指标分类/事件识别结果
     */
    List<JSONObject> test(String id, List<JSONObject> input) throws Exception;
}
