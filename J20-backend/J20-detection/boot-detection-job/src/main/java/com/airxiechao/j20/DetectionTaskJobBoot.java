package com.airxiechao.j20;

import com.airxiechao.j20.common.minio.MinioHelper;
import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.job.TaskJobRunner;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;

import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 检测任务执行入口
 */
@Slf4j
public class DetectionTaskJobBoot {
    public static void main(String[] args) {

        // 解析参数
        ParameterTool param = ParameterTool.fromArgs(args);
        String configParam = param.get("config");

        if(StringUtils.isBlank(configParam)){
            log.error("未配置启动参数: --config [任务参数json/urlencoded]");
            return;
        }

        Task task;
        JobConfig config;
        try {
            config = JSON.parseObject(URLDecoder.decode(configParam, "UTF-8"), JobConfig.class);

            // 从 minio 下载任务文件
            MinioHelper minioHelper = new MinioHelper(
                    config.getMinioEndpoint(),
                    config.getMinioAccessKey(),
                    config.getMinioSecretKey(),
                    config.getMinioBucket()
            );

            String taskFilePath = config.getJobFilePath();
            Path taskSavePath = Paths.get(System.getProperty("java.io.tmpdir"), taskFilePath);
            taskSavePath.toFile().delete();
            taskSavePath.getParent().toFile().mkdirs();
            minioHelper.download(taskFilePath, taskSavePath.toString());
            String jsonTask = Files.readString(taskSavePath);
            task = JSON.parseObject(jsonTask, Task.class);
            minioHelper.remove(taskFilePath);

            log.info("args: task=[{}], config=[{}]", task, config);
        }catch (Exception e){
            log.error("启动参数解析发生错误", e);
            return;
        }

        // 创建执行任务
        TaskJobRunner ruleJobRunner = new TaskJobRunner(task, config);

        // 运行规则执行器
        try {
            ruleJobRunner.run();
        } catch (Exception e) {
            log.error("任务执行发生错误", e);
        }
    }
}
