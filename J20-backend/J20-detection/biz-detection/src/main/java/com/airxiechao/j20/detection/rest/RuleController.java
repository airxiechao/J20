package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.api.pojo.vo.RuleVo;
import com.airxiechao.j20.detection.api.rest.IRuleController;
import com.airxiechao.j20.detection.api.rest.param.*;
import com.airxiechao.j20.detection.api.service.IRuleService;
import com.airxiechao.j20.detection.service.RuleService;
import com.airxiechao.j20.detection.util.RuleUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestScope
public class RuleController implements IRuleController {

    private IRuleService ruleService = new RuleService();

    @Override
    public Resp<Rule> add(RuleAddParam param) {
        log.info("创建规则：{}", param);

        try {
            if(ruleService.existsByName(param.getName())){
                throw new Exception("规则名称已存在");
            }

            Rule rule = new Rule(
                    param.getName(),
                    param.getDescription(),
                    param.getProtocol(),
                    param.getCriteriaType(),
                    RuleUtil.normalizeRuleCriteria(param.getCriteria(), param.getCriteriaType()),
                    param.getOutput(),
                    param.getFrequency(),
                    param.getAggregation());

            rule = ruleService.add(rule);
            return new Resp<>(ConstRespCode.OK, null, rule);
        }catch (Exception e){
            log.error("创建规则发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp update(RuleUpdateParam param) {
        log.info("修改规则：{}", param);

        try {
            Rule old = ruleService.get(param.getId());

            if(!param.getName().equals(old.getName())) {
                if (ruleService.existsByName(param.getName())) {
                    throw new Exception("规则名称已存在");
                }
            }

            Rule rule = new Rule(
                    param.getId(),
                    param.getName(),
                    param.getDescription(),
                    param.getProtocol(),
                    param.getCriteriaType(),
                    RuleUtil.normalizeRuleCriteria(param.getCriteria(), param.getCriteriaType()),
                    param.getOutput(),
                    param.getFrequency(),
                    param.getAggregation());

            ruleService.update(rule);
            return new Resp<>(ConstRespCode.OK, null, rule);
        }catch (Exception e){
            log.error("修改规则发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<Rule> get(RuleGetParam param) {
        log.info("查询规则：{}", param);

        try {
            Rule rule = ruleService.get(param.getId());
            return new Resp<>(ConstRespCode.OK, null, rule);
        }catch (Exception e){
            log.error("查询规则发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<PageData<RuleVo>> list(RuleListParam param) {
        log.info("查询规则列表：{}", param);

        try {
            PageVo<Rule> page = ruleService.list(param.getName(), param.getCriteriaType(), param.getOutputEventTypeId(),
                    param.getCurrent(), param.getSize(), param.getOrderBy(), param.getOrderAsc());

            List<RuleVo> vos = page.getPage().stream().map(RuleVo::of).collect(Collectors.toList());
            PageData<RuleVo> data = new PageData<>(param.getCurrent(), param.getSize(), page.getTotal(), vos);
            return new Resp<>(ConstRespCode.OK, null, data);
        }catch (Exception e){
            log.error("查询规则列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp delete(RuleDeleteParam param) {
        log.info("删除规则：{}", param);

        try {
            for (String id : param.getId().split(",")) {
                if(ruleService.exists(id)){
                    ruleService.delete(id);
                }
            }

            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("删除规则发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp test(RuleTestParam param) {
        log.info("测试规则：{}", param);

        try {
            List<JSONObject> output = ruleService.test(param.getId(), param.getInput());
            return new Resp<>(ConstRespCode.OK, null, output);
        }catch (Exception e){
            log.error("测试规则发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
