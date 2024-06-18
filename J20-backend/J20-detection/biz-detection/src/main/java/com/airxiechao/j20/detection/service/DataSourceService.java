package com.airxiechao.j20.detection.service;

import com.airxiechao.j20.common.api.pojo.exception.NotFoundException;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.kafka.KafkaAdmin;
import com.airxiechao.j20.common.util.ApplicationContextUtil;
import com.airxiechao.j20.common.util.UuidUtil;
import com.airxiechao.j20.detection.api.pojo.datasource.KafkaDataSource;
import com.airxiechao.j20.detection.api.service.IDataSourceService;
import com.airxiechao.j20.detection.db.record.KafkaDataSourceRecord;
import com.airxiechao.j20.detection.db.reposiroty.IDataSourceRepository;
import com.airxiechao.j20.detection.kafka.KafkaConfigFactory;
import com.airxiechao.j20.detection.kafka.KafkaManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DataSourceService implements IDataSourceService {

    private IDataSourceRepository dataSourceRepository = ApplicationContextUtil.getContext().getBean(IDataSourceRepository.class);
    private String logTopic = KafkaConfigFactory.getInstance().get().getLogTopic();
    private String eventTopic = KafkaConfigFactory.getInstance().get().getEventTopic();

    @Override
    public KafkaDataSource add(KafkaDataSource dataSource) throws Exception {
        checkDatasource(dataSource);

        KafkaDataSourceRecord record = buildKafkaDataSourceRecord(dataSource);
        return buildKafkaDataSource(dataSourceRepository.save(record));
    }

    @Override
    public void update(KafkaDataSource dataSource) throws Exception {
        checkDatasource(dataSource);

        KafkaDataSourceRecord record = buildKafkaDataSourceRecord(dataSource);
        dataSourceRepository.save(record);
    }

    @Override
    public boolean exists(String id) {
        return dataSourceRepository.existsById(id);
    }

    @Override
    public boolean existsByTopic(String topic) {
        return dataSourceRepository.existsByTopic(topic);
    }

    @Override
    public KafkaDataSource get(String id) throws NotFoundException {
        Optional<KafkaDataSourceRecord> opt = dataSourceRepository.findById(id);
        if(opt.isEmpty()){
            throw new NotFoundException();
        }

        return buildKafkaDataSource(opt.get());
    }

    @Override
    public PageVo<KafkaDataSource> list(String name, Integer pageNo, Integer pageSize, String orderBy, Boolean orderAsc) {
        if(StringUtils.isBlank(orderBy)){
            orderBy =  "name";
        }

        Sort sort;
        if(null != orderAsc && !orderAsc){
            sort = Sort.by(orderBy).descending();
        }else{
            sort = Sort.by(orderBy).ascending();
        }

        if(null == pageNo){
            pageNo = 1;
        }
        if(null == pageSize){
            pageSize = 20;
        }

        PageRequest pageRequest  = PageRequest.of(pageNo - 1, pageSize, sort);

        Page<KafkaDataSourceRecord> page;
        if(StringUtils.isNotBlank(name)){
            page = dataSourceRepository.findByNameContainingIgnoreCase(name, pageRequest);
        }else{
            page = dataSourceRepository.findAll(pageRequest);
        }

        List<KafkaDataSource> list = page.stream()
                .map(r -> buildKafkaDataSource(r))
                .collect(Collectors.toList());

        return new PageVo<>(page.getTotalElements(), list);
    }

    @Override
    public List<KafkaDataSource> list(String name) {
        String orderBy =  "name";
        Sort sort = Sort.by(orderBy).ascending();

        List<KafkaDataSourceRecord> records;
        if(StringUtils.isNotBlank(name)){
            records = dataSourceRepository.findByNameContainingIgnoreCase(name, sort);
        }else{
            records = dataSourceRepository.findAll(sort);
        }
        List<KafkaDataSource> list = records.stream().map(r -> buildKafkaDataSource(r)).collect(Collectors.toList());
        return list;
    }

    @Override
    public void createTopicIfNotExists(String id) throws NotFoundException {
        KafkaDataSource kafkaDataSource = get(id);

        try(KafkaAdmin kafkaAdmin = KafkaManager.getInstance().getAdmin(kafkaDataSource.getBootstrapServers())){
            if(!kafkaAdmin.hasTopic(kafkaDataSource.getTopic())){
                log.info("创建Kafka数据源[{}]的Topic：{}，分区数：{}", id, kafkaDataSource.getTopic(), kafkaDataSource.getNumPartition());
                kafkaAdmin.createTopic(kafkaDataSource.getTopic(), kafkaDataSource.getNumPartition(), 1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(String id) {
        dataSourceRepository.deleteById(id);
    }

    private void checkDatasource(KafkaDataSource dataSource) throws Exception {
        String topic = dataSource.getTopic().trim();
        if(List.of(logTopic, eventTopic).contains(topic)){
            throw new Exception("队列名和系统队列名重复");
        }
    }

    private KafkaDataSource buildKafkaDataSource(KafkaDataSourceRecord record){
        return new KafkaDataSource(record.getId(), record.getName(), record.getBootstrapServers(), record.getTopic(), record.getNumPartition());
    }

    private KafkaDataSourceRecord buildKafkaDataSourceRecord(KafkaDataSource dataSource){
        KafkaDataSourceRecord record = new KafkaDataSourceRecord();
        record.setId(null != dataSource.getId() ? dataSource.getId() : UuidUtil.random());
        record.setName(dataSource.getName());
        record.setBootstrapServers(dataSource.getBootstrapServers());
        record.setTopic(dataSource.getTopic());
        record.setNumPartition(dataSource.getNumPartition());

        return record;
    }
}
