package com.airxiechao.j20.common.es;

import com.airxiechao.j20.common.api.pojo.constant.ConstEsAggType;
import com.airxiechao.j20.common.api.pojo.vo.PageVo;
import com.airxiechao.j20.common.util.TimeUtil;
import com.airxiechao.j20.common.util.TrustAllManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ES客户端的实现
 */
public class EsClient implements Closeable {
    private static final Pattern HOST_PATTERN = Pattern.compile("(\\w+)://(.+):(\\d+)");

    /**
     * ES的Rest客户端
     */
    private RestClient restClient;

    /**
     * ES的版本号：6、7、8
     */
    private int esVersion;

    /**
     * 构造ES客户端
     * @param hosts 主机列表
     * @param user 用户名
     * @param password 密码
     * @param version 版本号
     * @param timeout 超时毫秒数
     */
    public EsClient(String[] hosts, String user, String password, int version, int timeout) {
        this.esVersion = version;

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                null != user ? user : "", null != password ? password : ""));

        List<HttpHost> httpHosts = new ArrayList<>();
        for(String host : hosts) {
            Matcher matcher = HOST_PATTERN.matcher(host);
            if (matcher.find()) {
                String protocol = matcher.group(1);
                String ip = matcher.group(2);
                String port = matcher.group(3);
                httpHosts.add(new HttpHost(ip, Integer.valueOf(port), protocol));
            }
        }

        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]))
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    httpAsyncClientBuilder = httpAsyncClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setKeepAliveStrategy(CustomConnectionKeepAliveStrategy.INSTANCE);

                    if(!httpHosts.isEmpty() && "https".equalsIgnoreCase(httpHosts.get(0).getSchemeName())){
                        try {
                            // 设置SSL
                            KeyManager[] keyManagers = null;
                            TrustManager[] trustManagers = new TrustManager[]{new TrustAllManager()};

                            SSLContext ssl = SSLContext.getInstance("TLS");
                            ssl.init(keyManagers, trustManagers, null);

                            httpAsyncClientBuilder.setSSLContext(ssl);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    return httpAsyncClientBuilder;
                }).setRequestConfigCallback(requestConfigCallback -> requestConfigCallback.setSocketTimeout(timeout));

        this.restClient = builder.build();
    }

    /**
     * 构造ES客户端。默认 10s 超时
     * @param hosts 主机地址
     * @param user 用户名
     * @param password 密码
     * @param version 版本号
     */
    public EsClient(String[] hosts, String user, String password, int version) {
        this(hosts, user, password, version, 10000);
    }

    /**
     * 创建索引
     * @param index 索引名称
     * @param body 请求体
     * @throws Exception 请求异常
     */
    public void createIndex(String index, Object body) throws Exception {
        Request request = new Request("PUT", String.format("/%s", index));

        if(null != body) {
            request.setJsonEntity(JSON.toJSONString(body));
        }

        Response response = restClient.performRequest(request);
    }

    /**
     * 是否有索引
     * @param index 索引名称
     * @return 是否有索引
     */
    public boolean hasIndex(String index) {
        Request request = new Request("GET", String.format("/%s", index));

        try {
            Response response = restClient.performRequest(request);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 是否有索引模板
     * @param template 索引模板名称
     * @return 是否有索引模板
     */
    public boolean hasIndexTemplate(String template) {
        Request request = new Request("GET", String.format("/_index_template/%s", template));

        try {
            Response response = restClient.performRequest(request);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 创建或更新索引模板
     * @param template 索引模板名称
     * @param body 请求体
     * @throws Exception 请求异常
     */
    public void createOrUpdateIndexTemplate(String template, Object body) throws Exception {
        Request request = new Request("PUT", String.format("/_index_template/%s", template));

        if(null != body) {
            request.setJsonEntity(JSON.toJSONString(body));
        }

        Response response = restClient.performRequest(request);
    }

    /**
     * 更新索引 Mapping 设置
     * @param index 索引名称
     * @param properties Mapping 设置的 properties
     * @throws Exception 请求异常
     */
    public void updateIndexMapping(String index, Object properties) throws Exception {
        Request request = new Request("PUT", String.format("/%s/_mapping", index));

        request.setJsonEntity(JSON.toJSONString(new HashMap(){{
            put("properties", properties);
        }}));

        Response response = restClient.performRequest(request);
    }

    /**
     * 插入文档
     * @param index 索引名称
     * @param document 文档数据
     * @param id 文档的id。如果为空，将由ES自动创建
     * @throws Exception 请求异常
     */
    public void insert(String index, Map<String, Object> document, String id) throws Exception {
        if(null == id){
            id = "";
        }

        Request request = new Request("POST", String.format("/%s/_doc/%s", index, id));

        request.setJsonEntity(JSON.toJSONString(document));

        Response response = restClient.performRequest(request);
    }

    /**
     * 批量插入文档
     * @param index 索引名称
     * @param documents 文档列表
     * @param idField 文档的id字段。如果为空，将有ES自动创建
     * @throws Exception 请求异常
     */
    public void bulkInsert(String index, List<Map<String, Object>> documents, String idField) throws Exception {
        Request request = new Request("POST", "/_bulk");

        StringBuilder sb = new StringBuilder();
        for(Map<String, Object> doc : documents){
            Object id = null;
            if(null != idField){
                id = doc.get(idField);
            }

            String strId = "";
            if(null != id){
                strId = String.format(", \"_id\": \"%s\" ", id);
            }

            if(esVersion <= 6){
                sb.append(String.format("{ \"index\": { \"_index\": \"%s\", \"_type\": \"_doc\" %s } }\n", index, strId));
            }else{
                sb.append(String.format("{ \"index\": { \"_index\": \"%s\" %s } }\n", index, strId));
            }
            sb.append(JSON.toJSONString(doc) + "\n");
        }
        request.setJsonEntity(sb.toString());

        Response response = restClient.performRequest(request);
        checkRespErrors(response);
    }

    /**
     * 批量插入文档
     * @param indexes 文档的索引列表
     * @param documents 文档列表
     * @param idField 文档的id字段。如果为空，将有ES自动创建
     * @throws Exception 请求异常
     */
    public void bulkInsert(List<String> indexes, List<Map<String, Object>> documents, String idField) throws Exception {
        Request request = new Request("POST", "/_bulk");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            Map<String, Object> doc = documents.get(i);
            String index = indexes.get(i);

            Object id = null;
            if(null != idField){
                id = doc.get(idField);
            }

            String strId = "";
            if(null != id){
                strId = String.format(", \"_id\": \"%s\" ", id);
            }

            if(esVersion <= 6){
                sb.append(String.format("{ \"index\": { \"_index\": \"%s\", \"_type\": \"_doc\" %s } }\n", index, strId));
            }else{
                sb.append(String.format("{ \"index\": { \"_index\": \"%s\" %s } }\n", index, strId));
            }
            sb.append(JSON.toJSONString(doc) + "\n");
        }
        request.setJsonEntity(sb.toString());

        Response response = restClient.performRequest(request);
        checkRespErrors(response);
    }

    /**
     * 批量分组插入文档。先将文档列表按索引分割为多个小列表，每个小列表再进行批量插入
     * @param indexes 文档索引列表
     * @param documents 文档列表
     * @param idField 文档的id字段。如果为空，将有ES自动创建
     * @throws Exception 请求异常
     */
    public void bulkGroupInsert(List<String> indexes, List<Map<String, Object>> documents, String idField) throws Exception {
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        for (int i = 0; i < documents.size(); i++) {
            Map<String, Object> doc = documents.get(i);
            String index = indexes.get(i);

            List<Map<String, Object>> list = map.get(index);
            if(null == list){
                list = new ArrayList<>();
                map.put(index, list);
            }

            list.add(doc);
        }

        for (Map.Entry<String, List<Map<String, Object>>> entry : map.entrySet()) {
            String index = entry.getKey();
            List<Map<String, Object>> list = entry.getValue();

            bulkInsert(index, list, idField);
        }
    }

    /**
     * 更新文档内容
     * @param index 索引名称
     * @param id 文档id
     * @param documentPart 更新的文档内容
     * @throws Exception 请求异常
     */
    public void update(String index, String id, Map<String, Object> documentPart) throws Exception {
        Request request;
        if(esVersion <= 6){
            request = new Request("POST", String.format("/%s/_doc/%s/_update", index, id));
        }else{
            request = new Request("POST", String.format("/%s/_update/%s", index, id));
        }

        request.setJsonEntity(JSON.toJSONString(new HashMap<String, Object>(){{
            put("doc", documentPart);
        }}));

        Response response = restClient.performRequest(request);
    }

    /**
     * 搜索文档。所有条件需要同时满足
     * @param index 索引名称
     * @param term term 条件
     * @param match match 条件
     * @param range range 条件
     * @param prefix prefix 条件
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @param cls 结果对象类
     * @return 搜索结果
     * @param <T> 结果对象类型
     * @throws Exception 请求异常
     */
    public <T> PageVo<T> search(
            String index,
            Map<String, Object> term,  Map<String, Object> match, Map<String, Object> range, Map<String, Object> prefix,
            Integer pageNo, Integer pageSize,
            String orderBy, Boolean orderAsc,
            Class<T> cls
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_search", index));
        List must = buildMust(term, match, range, prefix);

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("query", new HashMap(){{
                put("bool", new HashMap(){{
                    if(must.size() > 0) {
                        put("must", must);
                    }
                }});
            }});
        }};

        if(null != pageNo && null != pageSize){
            body.put("from", (pageNo - 1) * pageSize);
            body.put("size", pageSize);
        }

        if(null != orderBy && null != orderAsc){
            body.put("sort", new HashMap<String, Object>(){{
                put(orderBy, new HashMap<String, String>(){{
                    put("order", orderAsc ? "asc" : "desc");
                    put("unmapped_type", "long");
                }});
            }});
        }

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        return parseSearchResp(jsonResp, cls);
    }

    /**
     * 搜索文档。包含同时满足和满足其中之一两种条件
     * @param index 缩影名称
     * @param mustTerm mustTerm 条件
     * @param mustMatch mustMatch 条件
     * @param mustRange mustRange 条件
     * @param mustPrefix mustPrefix 条件
     * @param shouldTerm shouldTerm 条件
     * @param shouldMatch shouldMatch 条件
     * @param shouldRange shouldRange 条件
     * @param shouldPrefix shouldPrefix 条件
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @param cls 结果类
     * @return 搜索结果
     * @param <T> 结果类型
     * @throws Exception 请求异常
     */
    public <T> PageVo<T> search(
            String index,
            Map<String, Object> mustTerm, Map<String, Object> mustMatch, Map<String, Object> mustRange, Map<String, Object> mustPrefix,
            List<Pair<String, Object>> shouldTerm , List<Pair<String, Object>> shouldMatch, List<Pair<String, Object>> shouldRange, List<Pair<String, Object>> shouldPrefix,
            Integer pageNo, Integer pageSize,
            String orderBy, Boolean orderAsc,
            Class<T> cls
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_search", index));
        List must = buildMust(mustTerm, mustMatch, mustRange, mustPrefix);
        List should = buildShould(shouldTerm, shouldMatch, shouldRange, shouldPrefix);

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("query", new HashMap(){{
                put("bool", new HashMap(){{
                    if(must.size() > 0) {
                        put("must", must);
                    }
                    if(should.size() > 0) {
                        put("should", should);
                        put("minimum_should_match", 1);
                    }
                }});
            }});
        }};

        if(null != pageNo && null != pageSize){
            body.put("from", (pageNo - 1) * pageSize);
            body.put("size", pageSize);
        }

        if(null != orderBy && null != orderAsc){
            body.put("sort", new HashMap<String, Object>(){{
                put(orderBy, new HashMap<String, String>(){{
                    put("order", orderAsc ? "asc" : "desc");
                    put("unmapped_type", "long");
                }});
            }});
        }

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        return parseSearchResp(jsonResp, cls);
    }

    /**
     * 搜索文档。所有条件需要同时满足
     * @param index 索引名称
     * @param query 查询 lucene 语句
     * @param pageNo 页数
     * @param pageSize 页大小
     * @param orderBy 排序字段
     * @param orderAsc 是否升序
     * @param cls 结果对象类
     * @return 搜索结果
     * @param <T> 结果对象类型
     * @throws Exception 请求异常
     */
    public <T> PageVo<T> searchByQuery(
            String index,
            String query,
            Integer pageNo, Integer pageSize,
            String orderBy, Boolean orderAsc,
            Class<T> cls
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_search", index));

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("query", new HashMap(){{
                put("query_string", new HashMap(){{
                    put("query", query);
                }});
            }});
        }};

        if(null != pageNo && null != pageSize){
            body.put("from", (pageNo - 1) * pageSize);
            body.put("size", pageSize);
        }

        if(null != orderBy && null != orderAsc){
            body.put("sort", new HashMap<String, Object>(){{
                put(orderBy, new HashMap<String, String>(){{
                    put("order", orderAsc ? "asc" : "desc");
                    put("unmapped_type", "long");
                }});
            }});
        }

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        return parseSearchResp(jsonResp, cls);
    }

    /**
     * 通过id搜索文档
     * @param index 缩影名称
     * @param id 文档id
     * @param cls 结果类
     * @return 文档
     * @param <T> 结果类型
     * @throws Exception 请求异常
     */
    public <T> T searchById(String index, String id, Class<T> cls) throws Exception {
        Map<String, Object> term = new HashMap<String, Object>(){{
            put("_id", id);
        }};
        PageVo<T> page = search(index, term, null, null, null, null, null, null, null, cls);
        if(page.getTotal() == 0){
            throw new Exception(String.format("ES对象[_index:%s, _id:%s]不存在", index, id));
        }

        return page.getPage().get(0);
    }

    /**
     * 通过id获取文档
     * @param index 索引名称
     * @param id 文档id
     * @param cls 结果类
     * @return 文档
     * @param <T> 结果类型
     * @throws Exception 请求异常
     */
    public <T> T get(String index, String id, Class<T> cls) throws Exception {
        Request request = new Request("GET", String.format("/%s/_doc/%s", index, id));

        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        Boolean found = jsonResp.getBoolean("found");
        if(!found){
            throw new Exception(String.format("ES对象[索引:%s, ID:%s]不存在", index, id));
        }
        return parseGetResp(jsonResp, cls);
    }

    /**
     * 通过id判断文档是否存在
     * @param index 索引铭恒
     * @param id 文档id
     * @return 是否存在
     * @throws Exception 请求异常
     */
    public boolean exists(String index, String id) throws Exception {
        Request request = new Request("GET", String.format("/%s/_doc/%s", index, id));

        try{
            Response response = restClient.performRequest(request);
            String strResp = EntityUtils.toString(response.getEntity());
            JSONObject jsonResp = JSON.parseObject(strResp);
            Boolean found = jsonResp.getBoolean("found");
            return found;
        }catch (ResponseException e){
            return false;
        }
    }

    /**
     * 计数文档。所有条件需要同时满足
     * @param index 索引名称
     * @param term term 条件
     * @param match match 条件
     * @param range range 条件
     * @param prefix prefix 条件
     * @return 总数
     * @throws Exception 请求异常
     */
    public Long count(
            String index,
            Map<String, Object> term,  Map<String, Object> match, Map<String, Object> range, Map<String, Object> prefix
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_count", index));
        List must = buildMust(term, match, range, prefix);

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("query", new HashMap(){{
                put("bool", new HashMap(){{
                    if(must.size() > 0) {
                        put("must", must);
                    }
                }});
            }});
        }};

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        return parseCountResp(jsonResp);
    }

    /**
     * 计数文档。包含同时满足和满足其中之一两种条件
     * @param index 缩影名称
     * @param mustTerm mustTerm 条件
     * @param mustMatch mustMatch 条件
     * @param mustRange mustRange 条件
     * @param mustPrefix mustPrefix 条件
     * @param shouldTerm shouldTerm 条件
     * @param shouldMatch shouldMatch 条件
     * @param shouldRange shouldRange 条件
     * @param shouldPrefix shouldPrefix 条件
     * @return 总数
     * @throws Exception 请求异常
     */
    public Long count(
            String index,
            Map<String, Object> mustTerm, Map<String, Object> mustMatch, Map<String, Object> mustRange, Map<String, Object> mustPrefix,
            List<Pair<String, Object>> shouldTerm , List<Pair<String, Object>> shouldMatch, List<Pair<String, Object>> shouldRange, List<Pair<String, Object>> shouldPrefix
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_search", index));
        List must = buildMust(mustTerm, mustMatch, mustRange, mustPrefix);
        List should = buildShould(shouldTerm, shouldMatch, shouldRange, shouldPrefix);

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("query", new HashMap(){{
                put("bool", new HashMap(){{
                    if(must.size() > 0) {
                        put("must", must);
                    }
                    if(should.size() > 0) {
                        put("should", should);
                        put("minimum_should_match", 1);
                    }
                }});
            }});
        }};

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        return parseCountResp(jsonResp);
    }

    /**
     * 聚合文档查询
     * @param index 索引名称
     * @param term term 条件
     * @param match match 条件
     * @param range range 条件
     * @param prefix prefix 条件
     * @param aggFields 聚合字段数组
     * @return 聚合结果
     * @throws Exception 请求异常
     */
    public EsAggregation aggregate(
            String index,
            Map<String, Object> term,  Map<String, Object> match, Map<String, Object> range, Map<String, Object> prefix,
            EsAggField[] aggFields
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_search", index));
        List must = buildMust(term, match, range, prefix);
        Map aggs = buildAggs(aggFields);

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("size", 0);

            put("query", new HashMap(){{
                put("bool", new HashMap(){{
                    if(must.size() > 0) {
                        put("must", must);
                    }
                }});
            }});

            put("aggs", aggs);
        }};

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        EsAggregation aggregation = parseAggregationResp(jsonResp);

        return aggregation;
    }

    /**
     * 删除索引
     * @param index 索引名称
     * @throws Exception 请求异常
     */
    public void deleteIndex(String index) throws Exception {
        Request request = new Request("DELETE", String.format("/%s", index));
        Response response = restClient.performRequest(request);
    }

    /**
     * 查询索引列表
     * @param index 索引名称
     * @return 索引结合
     * @throws Exception 请求异常
     */
    public Set<String> listIndex(String index) throws Exception {
        Request request = new Request("GET", String.format("/%s*", index));
        Response response = restClient.performRequest(request);
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        return jsonResp.keySet();
    }

    /**
     * 删除文档
     * @param index 索引名称
     * @param id 文档id
     * @throws Exception 请求异常
     */
    public void delete(String index, String id) throws Exception {
        Request request = new Request("DELETE", String.format("/%s/_doc/%s", index, id));
        Response response = restClient.performRequest(request);
    }

    /**
     * 通过搜索删除文档
     * @param index 索引名称
     * @param mustTerm mustTerm 条件
     * @param mustMatch mustMatch 条件
     * @param mustRange mustRange 条件
     * @param mustPrefix mustPrefix 条件
     * @param shouldTerm shouldTerm 条件
     * @param shouldMatch shouldMatch 条件
     * @param shouldRange shouldRange 条件
     * @param shouldPrefix shouldPrefix 条件
     * @throws Exception 请求异常
     */
    public void delete(
            String index,
            Map<String, Object> mustTerm, Map<String, Object> mustMatch, Map<String, Object> mustRange, Map<String, Object> mustPrefix,
            List<Pair<String, Object>> shouldTerm , List<Pair<String, Object>> shouldMatch, List<Pair<String, Object>> shouldRange, List<Pair<String, Object>> shouldPrefix
    ) throws Exception {
        Request request = new Request("POST", String.format("/%s/_delete_by_query?wait_for_completion=false", index));
        List must = buildMust(mustTerm, mustMatch, mustRange, mustPrefix);
        List should = buildShould(shouldTerm, shouldMatch, shouldRange, shouldPrefix);

        Map<String, Object> body = new HashMap<String, Object>(){{
            put("query", new HashMap(){{
                put("bool", new HashMap(){{
                    if(must.size() > 0) {
                        put("must", must);
                    }
                    if(should.size() > 0) {
                        put("should", should);
                        put("minimum_should_match", 1);
                    }
                }});
            }});
        }};

        request.setJsonEntity(JSON.toJSONString(body));
        Response response = restClient.performRequest(request);
    }

    /**
     * 关闭客户端
     * @throws IOException 关闭异常
     */
    @Override
    public void close() throws IOException {
        restClient.close();
    }

    /**
     * 构造搜索的 must 条件
     * @param term term 条件
     * @param match match 条件
     * @param range range 条件
     * @param prefix prefix 条件
     * @return must 条件列表
     */
    private List buildMust(Map<String, Object> term,  Map<String, Object> match, Map<String, Object> range, Map<String, Object> prefix) {
            List must = new ArrayList();
            if(null != term && term.size() > 0){
                term.forEach((key, value) -> {
                    must.add(new HashMap<String, Object>(){{
                        put("term", new HashMap<String, Object>(){{
                            put(key, value);
                        }});
                    }});
                });
            }

            if(null != match && match.size() > 0){
                match.forEach((key, value) -> {
                    must.add(new HashMap<String, Object>(){{
                        put("match", new HashMap<String, Object>(){{
                            put(key, value);
                        }});
                    }});
                });
            }

            if(null != range && range.size() > 0){
                range.forEach((key, value) -> {
                    must.add(new HashMap<String, Object>(){{
                        put("range", new HashMap<String, Object>(){{
                            put(key, value);
                        }});
                    }});
                });
            }

            if(null != prefix && prefix.size() > 0){
                prefix.forEach((key, value) -> {
                    must.add(new HashMap<String, Object>(){{
                        put("prefix", new HashMap<String, Object>(){{
                            put(key, value);
                        }});
                    }});
                });
            }

            return must;
    }

    /**
     * 构造搜索的 should 条件
     * @param term term 条件
     * @param match match 条件
     * @param range range 条件
     * @param prefix prefix 条件
     * @return should 条件列表
     */
    private List buildShould(List<Pair<String, Object>> term ,List<Pair<String, Object>> match, List<Pair<String, Object>> range, List<Pair<String, Object>> prefix) {
        List should = new ArrayList();
        if(null != term && term.size() > 0){
            term.forEach(p -> {
                should.add(new HashMap<String, Object>(){{
                    put("term", new HashMap<String, Object>(){{
                        put(p.getLeft(), p.getRight());
                    }});
                }});
            });
        }

        if(null != match && match.size() > 0){
            match.forEach(p -> {
                should.add(new HashMap<String, Object>(){{
                    put("match", new HashMap<String, Object>(){{
                        put(p.getLeft(), p.getRight());
                    }});
                }});
            });
        }

        if(null != range && range.size() > 0){
            range.forEach(p -> {
                should.add(new HashMap<String, Object>(){{
                    put("range", new HashMap<String, Object>(){{
                        put(p.getLeft(), p.getRight());
                    }});
                }});
            });
        }

        if(null != prefix && prefix.size() > 0){
            prefix.forEach(p -> {
                should.add(new HashMap<String, Object>(){{
                    put("prefix", new HashMap<String, Object>(){{
                        put(p.getLeft(), p.getRight());
                    }});
                }});
            });
        }

        return should;
    }

    /**
     * 构造聚合搜索条件
     * @param aggFields 聚合字段数组
     * @return 聚合搜索条件
     */
    private Map buildAggs(EsAggField[] aggFields){
        Map aggs = new HashMap();

        Map parentAggs = aggs;
        for (int i = 0; i < aggFields.length; i++) {
            EsAggField f = aggFields[i];
            Map subAggs = new HashMap();

            switch (f.getAggType()){
                case ConstEsAggType.HISTOGRAM:
                    EsAggHistogramField hf = (EsAggHistogramField) f;
                    parentAggs.put(hf.getField(), new HashMap(){{
                        put("date_histogram", new HashMap(){{
                            put("field", hf.getField());
                            put("calendar_interval", hf.getHistogramInterval());
                            put("min_doc_count", 0);
                            put("offset", String.format("-%dh", TimeUtil.getTimezoneOffsetHour()));

                            if(null != hf.getHistogramBoundMin() && null != hf.getHistogramBoundMax()){
                                put("extended_bounds", new HashMap(){{
                                    put("min", hf.getHistogramBoundMin());
                                    put("max", hf.getHistogramBoundMax());
                                }});
                            }
                        }});

                        put("aggs", subAggs);
                    }});

                    break;
                case ConstEsAggType.GROUP_BY:
                    EsAggGroupByField gf = (EsAggGroupByField) f;
                    parentAggs.put(gf.getField(), new HashMap(){{
                        put("terms", new HashMap(){{
                            put("field", gf.getField());
                            put("size", gf.getSize());
                        }});

                        put("aggs", subAggs);
                    }});

                    break;
                case ConstEsAggType.SUM:
                    EsAggSumField sf = (EsAggSumField) f;
                    parentAggs.put(sf.getField(), new HashMap(){{
                        put("sum", new HashMap(){{
                            put("field", sf.getField());
                        }});
                    }});

                    break;
            }

            parentAggs = subAggs;
        }

        return aggs;
    }

    /**
     * 检查请求返回结果中 errors 字段是否为 true
     * @param response 请求返回结果
     * @throws Exception errors 字段为 true
     */
    private void checkRespErrors(Response response) throws Exception {
        String strResp = EntityUtils.toString(response.getEntity());
        JSONObject jsonResp = JSON.parseObject(strResp);
        Boolean errors = jsonResp.getBoolean("errors");
        if(null != errors && errors){
            throw new Exception(strResp);
        }
    }

    /**
     * 将搜索结果转为业务对象
     * @param jsonResp 搜索结果
     * @param cls 业务对象的类
     * @return 业务对象结果
     * @param <T> 业务对象的类型
     */
    private <T> PageVo<T> parseSearchResp(JSONObject jsonResp, Class<T> cls) {
        JSONObject hits = jsonResp.getJSONObject("hits");
        long total = hits.getJSONObject("total").getLong("value");
        List list = new ArrayList();
        hits.getJSONArray("hits").forEach(h -> {
            list.add(parseGetResp((JSONObject) h, cls));
        });

        return new PageVo<T>(total, list);
    }

    /**
     * 将单个查询结果转为业务对象
     * @param jsonResp 查询结果
     * @param cls 业务对象的类
     * @return 业务对象结果
     * @param <T> 业务对象的类型
     */
    private <T> T parseGetResp(JSONObject jsonResp, Class<T> cls) {
        String _id = jsonResp.getString("_id");
        JSONObject _source = jsonResp.getJSONObject("_source");
        _source.put("id", _id);
        return _source.toJavaObject(cls);
    }

    /**
     * 解析计数结果
     * @param jsonResp 查询结果
     * @return 总数
     */
    private Long parseCountResp(JSONObject jsonResp) {
        return jsonResp.getLong("count");
    }

    /**
     * 解析聚合查询结果
     * @param jsonResp 聚合查询结果
     * @return 聚合结果
     * @throws Exception 解析异常
     */
    private EsAggregation parseAggregationResp(JSONObject jsonResp) throws Exception {
        List<EsAggregation> list = new ArrayList<>();
        JSONObject aggregations = jsonResp.getJSONObject("aggregations");
        if(null == aggregations){
            throw new Exception("无ES聚合查询结果");
        }

        aggregations.keySet().forEach(name -> {
            JSONObject aggJson = aggregations.getJSONObject(name);
            EsAggregation agg = parseAggregation(name, aggJson);
            list.add(agg);
        });

        if(list.size() == 0){
            throw new Exception("解析ES聚合查询结果发生错误");
        }

        return list.get(0);
    }

    private EsAggregation parseAggregation(String name, JSONObject jsonObject){
        EsAggregation agg = new EsAggregation();
        agg.setName(name);

        List<EsAggregationBucket> buckets = new ArrayList<>();
        JSONArray bucketArray = jsonObject.getJSONArray("buckets");
        if(null != bucketArray) {
            for (int i = 0; i < bucketArray.size(); i++) {
                JSONObject bucketJson = bucketArray.getJSONObject(i);
                EsAggregationBucket bucket = parseAggregationBucket(bucketJson);
                buckets.add(bucket);
            }
            agg.setBuckets(buckets);
        }

        Double value = jsonObject.getDouble("value");
        if(null != value){
            agg.setValue(value);
        }

        return agg;
    }

    private EsAggregationBucket parseAggregationBucket(JSONObject jsonObject){
        EsAggregationBucket bucket = new EsAggregationBucket();

        String key = jsonObject.getString("key");
        bucket.setKey(key);

        Integer count = jsonObject.getInteger("doc_count");
        bucket.setCount(count);

        jsonObject.remove("key");
        jsonObject.remove("key_as_string");
        jsonObject.remove("doc_count");

        List<EsAggregation> list = new ArrayList<>();
        jsonObject.keySet().forEach(name -> {
            JSONObject aggJson = jsonObject.getJSONObject(name);
            EsAggregation agg = parseAggregation(name, aggJson);
            list.add(agg);
        });

        if(!list.isEmpty()){
            bucket.setAggregation(list.get(0));
        }

        return bucket;
    }
}
