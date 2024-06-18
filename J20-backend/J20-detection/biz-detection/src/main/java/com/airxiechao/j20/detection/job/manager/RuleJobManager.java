package com.airxiechao.j20.detection.job.manager;

import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.ConstJobStatus;
import com.airxiechao.j20.detection.api.pojo.rule.Rule;
import com.airxiechao.j20.detection.config.DetectionConfig;
import com.airxiechao.j20.detection.config.FlinkConfig;
import com.airxiechao.j20.detection.config.KafkaConfig;
import com.airxiechao.j20.detection.config.MinioConfig;
import com.airxiechao.j20.detection.flink.FlinkConfigFactory;
import com.airxiechao.j20.detection.job.DetectionConfigFactory;
import com.airxiechao.j20.detection.kafka.KafkaConfigFactory;
import com.airxiechao.j20.detection.minio.MinioConfigFactory;
import com.airxiechao.j20.detection.minio.MinioManager;
import com.alibaba.fastjson2.JSON;
import com.nextbreakpoint.flinkclient.api.ApiException;
import com.nextbreakpoint.flinkclient.api.FlinkApi;
import com.nextbreakpoint.flinkclient.model.JarFileInfo;
import com.nextbreakpoint.flinkclient.model.JarListInfo;
import com.nextbreakpoint.flinkclient.model.JarRunResponseBody;
import com.nextbreakpoint.flinkclient.model.JobDetailsInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 规则测试任务执行管理器
 * 负责传递规则描述、任务参数等到Flink任务，启动Flink任务，查询Flink任务状态。
 */
@Slf4j
public class RuleJobManager {
    @Getter
    private static final RuleJobManager instance = new RuleJobManager();

    private FlinkConfig flinkConfig = FlinkConfigFactory.getInstance().get();
    private KafkaConfig kafkaConfig = KafkaConfigFactory.getInstance().get();
    private MinioConfig minioConfig = MinioConfigFactory.getInstance().get();
    private DetectionConfig detectionConfig = DetectionConfigFactory.getInstance().get();

    private FlinkApi flinkApi;

    private RuleJobManager() {
        flinkApi = new FlinkApi();
        flinkApi.getApiClient().setBasePath(flinkConfig.getAddress());
        flinkApi.getApiClient().getHttpClient().setConnectTimeout(flinkConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
        flinkApi.getApiClient().getHttpClient().setWriteTimeout(flinkConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
        flinkApi.getApiClient().getHttpClient().setReadTimeout(flinkConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 启动规则任务的Flink任务实例ID
     * @param rule 规则
     * @param dstTopic 输出Kafka Topic
     * @param workingDir 工作路径
     * @return Flink任务实例ID
     * @throws Exception 启动异常
     */
    public String run(Rule rule, String dstTopic, String workingDir) throws Exception {
        JobConfig config = new JobConfig();
        config.setCheckpointingEnabled(false);

        // kafka server
        config.setDstKafkaBootstrapServers(kafkaConfig.getBootstrapServers());
        config.setDstTopic(dstTopic);

        config.setSrcParallelism(1);
        config.setMinioEndpoint(minioConfig.getEndpoint());
        config.setMinioAccessKey(minioConfig.getAccessKey());
        config.setMinioSecretKey(minioConfig.getSecretKey());
        config.setMinioBucket(minioConfig.getBucket());
        config.setLogSortField(detectionConfig.getLogSortField());

        // 将 rule 传到 minio 存储
        String ruleFilePath = String.format("%s/rule", workingDir);
        try(InputStream inputStream = new ByteArrayInputStream(JSON.toJSONString(rule).getBytes(StandardCharsets.UTF_8))) {
            MinioManager.getInstance().getHelper().upload(ruleFilePath, inputStream);
        }

        config.setJobFilePath(workingDir);

        String configParam = URLEncoder.encode(JSON.toJSONString(config), "UTF-8");

        String[] params = {
                "--config",
                configParam
        };

        JarListInfo jars = flinkApi.listJars();
        Optional<JarFileInfo> jarOpt = jars.getFiles().stream().filter(j -> j.getName().equals(flinkConfig.getDetectionJobJarName())).findFirst();
        if(!jarOpt.isPresent()) {
            throw new Exception(String.format("没有找到任务Jar包: %s", flinkConfig.getDetectionJobJarName()));
        }

        JarFileInfo jarFileInfo = jarOpt.get();

        try{
            JarRunResponseBody response = flinkApi.runJar(
                    jarFileInfo.getId(),
                    flinkConfig.getAllowNonRestoredState(),
                    flinkConfig.getSavePointPath(),
                    String.join(" ", params),
                    null,
                    flinkConfig.getDetectionRuleJobJarEntryClass(),
                    flinkConfig.getParallelism());

            String jobId = response.getJobid();
            return jobId;
        }catch (Exception e){
            throw new Exception("创建Flink任务发生错误", e);
        }

    }

    /**
     * 查询Flink任务实例状态
     * @param jobId Flink任务实例ID
     * @return 任务状态
     * @throws Exception 查询异常
     */
    public String getState(String jobId) throws Exception {
        JobDetailsInfo details;
        try{
            details = flinkApi.getJobDetails(jobId);
            return details.getState().toString();
        }catch (ApiException e){
            if(e.getMessage().equals("Not Found")){
                return ConstJobStatus.CANCELED;
            }else{
                throw new Exception(String.format("获取Flink任务状态[%s]发生错误", jobId), e);
            }
        }
    }

}
