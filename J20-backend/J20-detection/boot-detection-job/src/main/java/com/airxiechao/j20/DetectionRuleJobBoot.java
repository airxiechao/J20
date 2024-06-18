package com.airxiechao.j20;

import com.airxiechao.j20.common.api.pojo.log.Log;
import com.airxiechao.j20.common.minio.MinioHelper;
import com.airxiechao.j20.common.util.LogUtil;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.job.RuleJobRunner;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 规则测试任务入口
 * 负责接收规则及参数，创建规则测试任务执行器，执行规则测试任务。
 */
@Slf4j
public class DetectionRuleJobBoot {
    public static void main(String[] args) {

        // 解析参数
        ParameterTool param = ParameterTool.fromArgs(args);
        String configParam = param.get("config");

        if(StringUtils.isBlank(configParam)){
            log.error("未配置全部启动参数: --config [任务参数json/urlencoded]");
            return;
        }

        Rule rule;
        JobConfig config;
        List<Log> input = new ArrayList<>();
        try {
            config = JSON.parseObject(URLDecoder.decode(configParam, "UTF-8"), JobConfig.class);
            String workingDir = config.getJobFilePath();

            log.info("args: criteria=[{}], job=[{}]", workingDir, config);

            MinioHelper minioHelper = new MinioHelper(
                    config.getMinioEndpoint(),
                    config.getMinioAccessKey(),
                    config.getMinioSecretKey(),
                    config.getMinioBucket()
            );

            // 从 minio 下载 rule 文件
            Path ruleSavePath = Paths.get(System.getProperty("java.io.tmpdir"), workingDir, "rule");
            ruleSavePath.getParent().toFile().mkdirs();
            String rulePath = String.format("%s/rule", workingDir);
            minioHelper.download(rulePath, ruleSavePath.toString());
            String jsonRule = Files.readString(ruleSavePath);
            rule = JSON.parseObject(jsonRule, Rule.class);

            // 从 minio 下载 input 文件
            Path inputSavePath = Paths.get(System.getProperty("java.io.tmpdir"), workingDir, "input");
            inputSavePath.getParent().toFile().mkdirs();
            String inputPath = String.format("%s/input", workingDir);
            minioHelper.download(inputPath, inputSavePath.toString());
            String inputContent = new String(Files.readAllBytes(inputSavePath), StandardCharsets.UTF_8);
            if(StringUtils.isNotBlank(inputContent)){
                for (String line : inputContent.split("\n")) {
                    Log log = LogUtil.parseLog(line, System.currentTimeMillis());
                    input.add(log);
                }
            }

        }catch (Exception e){
            log.error("启动参数解析发生错误", e);
            return;
        }

        // 创建执行任务
        RuleJobRunner ruleJobRunner = new RuleJobRunner(rule, config, input);

        // 运行规则测试器
        try {
            ruleJobRunner.run();
        } catch (Exception e) {
            log.error("规则测试发生错误", e);
        }

        log.info("规则[{}#{}]测试完成", rule.getId(), rule.getName());
    }
}
