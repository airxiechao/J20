package com.airxiechao.j20.detection.job.manager;

import com.airxiechao.j20.detection.api.pojo.config.JobConfig;
import com.airxiechao.j20.detection.api.pojo.constant.ConstJobStatus;
import com.airxiechao.j20.detection.api.pojo.datasource.KafkaDataSource;
import com.airxiechao.j20.detection.api.pojo.job.TaskJobMetric;
import com.airxiechao.j20.detection.api.pojo.task.Task;
import com.airxiechao.j20.detection.api.service.IDataSourceService;
import com.airxiechao.j20.detection.config.DetectionConfig;
import com.airxiechao.j20.detection.config.FlinkConfig;
import com.airxiechao.j20.detection.config.KafkaConfig;
import com.airxiechao.j20.detection.config.MinioConfig;
import com.airxiechao.j20.detection.flink.FlinkConfigFactory;
import com.airxiechao.j20.detection.job.DetectionConfigFactory;
import com.airxiechao.j20.detection.kafka.KafkaConfigFactory;
import com.airxiechao.j20.detection.minio.MinioConfigFactory;
import com.airxiechao.j20.detection.minio.MinioManager;
import com.airxiechao.j20.detection.service.DataSourceService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nextbreakpoint.flinkclient.api.ApiException;
import com.nextbreakpoint.flinkclient.api.FlinkApi;
import com.nextbreakpoint.flinkclient.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行管理器
 * 负责传递任务描述、任务参数等到Flink任务，启动Flink任务，查询Flink任务状态。
 */
@Slf4j
public class TaskJobManager {
    @Getter
    private static final TaskJobManager instance = new TaskJobManager();

    private FlinkConfig flinkConfig = FlinkConfigFactory.getInstance().get();
    private KafkaConfig kafkaConfig = KafkaConfigFactory.getInstance().get();
    private MinioConfig minioConfig = MinioConfigFactory.getInstance().get();
    private DetectionConfig detectionConfig = DetectionConfigFactory.getInstance().get();

    private IDataSourceService dataSourceService = new DataSourceService();

    private FlinkApi flinkApi;

    private TaskJobManager() {
        flinkApi = new FlinkApi();
        flinkApi.getApiClient().setBasePath(flinkConfig.getAddress());
        flinkApi.getApiClient().getHttpClient().setConnectTimeout(flinkConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
        flinkApi.getApiClient().getHttpClient().setWriteTimeout(flinkConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
        flinkApi.getApiClient().getHttpClient().setReadTimeout(flinkConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 启动Flink任务
     * @param task 任务
     * @return Flink任务实例ID
     * @throws Exception 启动异常
     */
    public String run(Task task) throws Exception {
        JobConfig jobConfig = new JobConfig();

        // flink checkpoint
        jobConfig.setCheckpointingEnabled(flinkConfig.getCheckpointingEnabled());
        jobConfig.setCheckpointingInterval(flinkConfig.getCheckpointingInterval());

        // kafka
        jobConfig.setSrcKafkaBootstrapServers(kafkaConfig.getBootstrapServers());
        jobConfig.setDstKafkaBootstrapServers(kafkaConfig.getBootstrapServers());
        jobConfig.setSrcTopic(kafkaConfig.getLogTopic());
        jobConfig.setDstTopic(kafkaConfig.getEventTopic());

        // 更新输入数据源
        String srcDataSourceId = task.getSrcDataSourceId();
        if(StringUtils.isNotBlank(srcDataSourceId)){
            dataSourceService.createTopicIfNotExists(srcDataSourceId);

            KafkaDataSource dataSource = new DataSourceService().get(srcDataSourceId);
            jobConfig.setSrcKafkaBootstrapServers(dataSource.getBootstrapServers());
            jobConfig.setSrcTopic(dataSource.getTopic());
        }

        jobConfig.setStartingOffsetStrategy(task.getStartingOffsetStrategy());
        jobConfig.setSrcParallelism(null != flinkConfig.getParallelism() ? flinkConfig.getParallelism() : 1);
        jobConfig.setMinioEndpoint(minioConfig.getEndpoint());
        jobConfig.setMinioAccessKey(minioConfig.getAccessKey());
        jobConfig.setMinioSecretKey(minioConfig.getSecretKey());
        jobConfig.setMinioBucket(minioConfig.getBucket());
        jobConfig.setLogSortField(detectionConfig.getLogSortField());

        // 将 rule 传到 minio 存储
        String taskFilePath = buildTaskFilePath(task.getId());
        try(InputStream inputStream = new ByteArrayInputStream(JSON.toJSONString(task).getBytes(StandardCharsets.UTF_8))) {
            MinioManager.getInstance().getHelper().upload(taskFilePath, inputStream);
        }
        jobConfig.setJobFilePath(taskFilePath);

        String configParam = URLEncoder.encode(JSON.toJSONString(jobConfig), "UTF-8");

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
                    flinkConfig.getDetectionTaskJobJarEntryClass(),
                    flinkConfig.getParallelism());

            String jobId = response.getJobid();
            return jobId;
        }catch (Exception e){
            throw new Exception("创建Flink任务发生错误", e);
        }

    }

    /**
     * 停止Flink任务
     * @param jobId Flink任务实例ID
     * @throws Exception 停止异常
     */
    public void terminate(String jobId) throws Exception {
        try{
            JobDetailsInfo details = flinkApi.getJobDetails(jobId);
            if(Arrays.asList(
                    ConstJobStatus.FAILED,
                    ConstJobStatus.FINISHED,
                    ConstJobStatus.CANCELED
            ).contains(details.getState())){
                return;
            }
        }catch (ApiException e){
            if(e.getMessage().equals("Not Found")){
                return;
            }
        }

        try{
            flinkApi.terminateJob(jobId, "cancel");
        }catch (ApiException e){
            if(e.getMessage().equals("Not Found")){
                return;
            }

            throw new Exception(String.format("终止Flink任务[%s]发生错误", jobId), e);
        }
    }

    /**
     * 查询Flink任务状态
     * @param ruleId 任务ID
     * @param jobId Flink任务实例ID
     * @return 任务监测指标
     * @throws Exception 查询异常
     */
    public TaskJobMetric getMetric(String ruleId, String jobId) throws Exception {
        JobDetailsInfo details;
        try{
            details = flinkApi.getJobDetails(jobId);
        }catch (ApiException e){
            if(e.getMessage().equals("Not Found")){
                TaskJobMetric metric = new TaskJobMetric();
                metric.setTimestamp(System.currentTimeMillis());
                metric.setRuleId(ruleId);
                metric.setJobId(jobId);
                metric.setState(ConstJobStatus.CANCELED);
                metric.setNumLogIn(0L);
                metric.setNumLogInPerSecond(0);
                metric.setNumEventOut(0L);
                metric.setNumEventOutPerSecond(0);
                return metric;
            }else{
                throw new Exception(String.format("获取Flink任务状态[%s]发生错误", jobId), e);
            }
        }

        try{
            TaskJobMetric metric = extractMetric(details);
            metric.setRuleId(ruleId);
            return metric;
        }catch (Exception e){
            throw new Exception(String.format("获取Flink任务状态[%s]发生错误", jobId), e);
        }
    }

    /**
     * 从Flink任务状态查询结果中提取任务监测指标
     * @param detailsInfo Flink任务状态
     * @return 任务监测指标
     * @throws Exception 提取异常
     */
    private TaskJobMetric extractMetric(JobDetailsInfo detailsInfo) throws Exception {
        TaskJobMetric status = new TaskJobMetric();
        status.setTimestamp(detailsInfo.getNow());
        status.setState(detailsInfo.getState().toString());

        String jobId = detailsInfo.getJid();
        status.setJobId(jobId);

        String vertFirstId = detailsInfo.getVertices().get(0).getId();
        String vertLastId = detailsInfo.getVertices().get(detailsInfo.getVertices().size() - 1).getId();

        List metFirst = (List)flinkApi.getJobAggregatedSubtaskMetrics(jobId, vertFirstId, "Source__Kafka_Source.numRecordsIn,Source__Kafka_Source.numRecordsInPerSecond", null, null);
        if(metFirst.size() > 1) {
            status.setNumLogIn((Math.round((double) ((Map) metFirst.get(0)).get("sum"))));
            status.setNumLogInPerSecond((int) (Math.round((double) ((Map) metFirst.get(1)).get("sum"))));
        }else{
            status.setNumLogIn(0L);
            status.setNumLogInPerSecond(0);
        }

        List metEvent = (List)flinkApi.getJobAggregatedSubtaskMetrics(jobId, vertLastId, "Kafka_Sink__Writer.numRecordsSend,Kafka_Sink__Writer.numRecordsOutPerSecond", null, null);
        if(metEvent.size() > 1) {
            status.setNumEventOut((Math.round((double) ((Map) metEvent.get(0)).get("sum"))));
            status.setNumEventOutPerSecond((int) (Math.round((double) ((Map) metEvent.get(1)).get("sum"))));
        }else{
            status.setNumEventOut(0L);
            status.setNumEventOutPerSecond(0);
        }

        return status;
    }

    /**
     * 查询Flink运行中的任务实例
     * @param namePrefix 任务名称前缀
     * @return 运行中的任务实例列表
     * @throws Exception 查询异常
     */
    public List<String> listRunningJobs(String namePrefix) throws Exception {
        List<String> runningJobs = new ArrayList<>();
        try {
            MultipleJobsDetails jobsOverview = flinkApi.getJobsOverview();
            jobsOverview.getJobs().forEach(o -> {
                try{
                    JSONObject job = (JSONObject) JSON.toJSON(o);
                    String jobId = job.getString("jid");
                    String jobName = job.getString("name");
                    String jobStatus = job.getString("state");

                    if(null != jobName && jobName.startsWith(namePrefix) && null != jobStatus && ConstJobStatus.RUNNING.equals(jobStatus)){
                        runningJobs.add(jobId);
                    }
                }catch (Exception e){
                    log.error("解析Flink任务信息发生错误", e);
                }
            });
        } catch (ApiException e) {
            throw new Exception("获取Flink所有任务发生错误");
        }

        return runningJobs;
    }

    /**
     * 构造任务描述文件Minio路径
     * @param id 任务ID
     * @return 任务描述文件Minio路径
     */
    private String buildTaskFilePath(String id){
        return String.format("/task/%s", id);
    }
}
