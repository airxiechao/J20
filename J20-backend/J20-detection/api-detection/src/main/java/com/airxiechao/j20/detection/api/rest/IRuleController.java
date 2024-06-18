package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.vo.RuleVo;
import com.airxiechao.j20.detection.api.rest.param.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 规则请求接口
 */
public interface IRuleController {
    /**
     * 添加规则
     * @param param 参数
     * @return 新增规则
     */
    @PostMapping("/api/detection/rule/add")
    Resp<Rule> add(@RequestBody @Valid RuleAddParam param);

    /**
     * 修改规则
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/rule/update")
    Resp update(@RequestBody @Valid RuleUpdateParam param);

    /**
     * 查询规则
     * @param param 参数
     * @return 规则
     */
    @PostMapping("/api/detection/rule/get")
    Resp<Rule> get(@RequestBody @Valid RuleGetParam param);

    /**
     * 查询规则列表
     * @param param 参数
     * @return 规则列表
     */
    @PostMapping("/api/detection/rule/list")
    Resp<PageData<RuleVo>> list(@RequestBody(required = false) @Valid RuleListParam param);

    /**
     * 删除规则
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/rule/delete")
    Resp delete(@RequestBody @Valid RuleDeleteParam param);

    /**
     * 测试规则
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/rule/test")
    Resp test(@RequestBody @Valid RuleTestParam param);
}
