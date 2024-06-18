package com.airxiechao.j20.detection.rest;

import com.airxiechao.j20.common.api.pojo.constant.ConstRespCode;
import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.detection.api.pojo.datasource.KafkaDataSource;
import com.airxiechao.j20.detection.api.rest.IDataSourceController;
import com.airxiechao.j20.detection.api.rest.param.*;
import com.airxiechao.j20.detection.api.service.IDataSourceService;
import com.airxiechao.j20.detection.service.DataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Slf4j
@RestController
@RequestScope
public class DataSourceController implements IDataSourceController {

    private IDataSourceService dataSourceService = new DataSourceService();

    @Override
    public Resp<KafkaDataSource> add(DataSourceAddParam param) {
        log.info("创建数据源：{}", param);

        try {
            if(dataSourceService.existsByTopic(param.getTopic())){
                throw new Exception("数据源队列名已存在");
            }

            KafkaDataSource dataSource = new KafkaDataSource(param.getName(), param.getBootstrapServers(), param.getTopic(), param.getNumPartition());
            dataSource = dataSourceService.add(dataSource);
            return new Resp<>(ConstRespCode.OK, null, dataSource);
        }catch (Exception e){
            log.error("创建数据源发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp update(DataSourceUpdateParam param) {
        log.info("修改数据源：{}", param);

        try {
            if(!dataSourceService.exists(param.getId())){
                throw new Exception("数据源不存在");
            }

            KafkaDataSource old = dataSourceService.get(param.getId());
            if(!param.getTopic().equals(old.getTopic())) {
                if (dataSourceService.existsByTopic(param.getTopic())) {
                    throw new Exception("数据源队列名已存在");
                }
            }

            KafkaDataSource dataSource = new KafkaDataSource(param.getId(), param.getName(),
                    param.getBootstrapServers(), param.getTopic(), param.getNumPartition());
            dataSourceService.update(dataSource);
            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("修改数据源发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<KafkaDataSource> get(DataSourceGetParam param) {
        log.info("查询数据源：{}", param);

        try {
            if(!dataSourceService.exists(param.getId())){
                throw new Exception("数据源不存在");
            }

            KafkaDataSource dataSource = dataSourceService.get(param.getId());
            return new Resp<>(ConstRespCode.OK, null, dataSource);
        }catch (Exception e){
            log.error("查询数据源发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<PageData<KafkaDataSource>> list(DataSourceListParam param) {
        log.info("查询数据源列表：{}", param);

        try {
            if(null == param){
                param = new DataSourceListParam();
            }

            PageVo<KafkaDataSource> page = dataSourceService.list(param.getName(), param.getCurrent(), param.getSize(), param.getOrderBy(), param.getOrderAsc());
            PageData<KafkaDataSource> data = new PageData<>(param.getCurrent(), param.getSize(), page.getTotal(), page.getPage());
            return new Resp<>(ConstRespCode.OK, null, data);
        }catch (Exception e){
            log.error("查询数据源列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp<List<KafkaDataSource>> all(DataSourceListParam param) {
        log.info("查询所有数据源列表：{}", param);

        try {
            if(null == param){
                param = new DataSourceListParam();
            }

            List<KafkaDataSource> list = dataSourceService.list(param.getName());
            return new Resp<>(ConstRespCode.OK, null, list);
        }catch (Exception e){
            log.error("查询所有数据源列表发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public Resp delete(DataSourceDeleteParam param) {
        log.info("删除数据源：{}", param);

        try {
            for (String id : param.getId().split(",")) {
                if(dataSourceService.exists(id)){
                    dataSourceService.delete(id);
                }
            }

            return new Resp<>(ConstRespCode.OK, null, null);
        }catch (Exception e){
            log.error("删除数据源发生错误", e);
            return new Resp<>(ConstRespCode.ERROR, e.getMessage(), null);
        }
    }
}
