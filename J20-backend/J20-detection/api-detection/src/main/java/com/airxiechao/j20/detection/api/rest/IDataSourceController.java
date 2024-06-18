package com.airxiechao.j20.detection.api.rest;

import com.airxiechao.j20.common.api.pojo.rest.PageData;
import com.airxiechao.j20.common.api.pojo.rest.Resp;
import com.airxiechao.j20.detection.api.pojo.datasource.KafkaDataSource;
import com.airxiechao.j20.detection.api.rest.param.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据源请求接口
 */
public interface IDataSourceController {
    /**
     * 添加数据源
     * @param param 参数
     * @return 新增数据源
     */
    @PostMapping("/api/detection/datasource/add")
    Resp<KafkaDataSource> add(@RequestBody @Valid DataSourceAddParam param);

    /**
     * 修改数据源
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/datasource/update")
    Resp update(@RequestBody @Valid DataSourceUpdateParam param);

    /**
     * 查询数据源
     * @param param 参数
     * @return 数据源
     */
    @PostMapping("/api/detection/datasource/get")
    Resp<KafkaDataSource> get(@RequestBody @Valid DataSourceGetParam param);

    /**
     * 查询数据源列表
     * @param param 参数
     * @return 数据源列表
     */
    @PostMapping("/api/detection/datasource/list")
    Resp<PageData<KafkaDataSource>> list(@RequestBody(required = false) @Valid DataSourceListParam param);

    /**
     * 查询所有数据源
     * @param param 参数
     * @return 数据源列表
     */
    @PostMapping("/api/detection/datasource/all")
    Resp<List<KafkaDataSource>> all(@RequestBody(required = false) @Valid DataSourceListParam param);

    /**
     * 删除数据源
     * @param param 参数
     * @return 响应
     */
    @PostMapping("/api/detection/datasource/delete")
    Resp delete(@RequestBody @Valid DataSourceDeleteParam param);
}
