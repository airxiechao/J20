package com.airxiechao.j20.probe.network.capture;

import com.airxiechao.j20.probe.common.kafka.KafkaAdmin;
import com.airxiechao.j20.probe.common.kafka.KafkaClient;
import com.airxiechao.j20.probe.network.api.config.CaptureConfig;
import com.airxiechao.j20.probe.network.api.config.OutputConfig;
import com.airxiechao.j20.probe.network.api.config.ProbeCaptureConfig;
import com.airxiechao.j20.probe.network.api.pojo.tree.LayerNode;
import com.airxiechao.j20.probe.network.api.pojo.tree.TreeNode;
import com.airxiechao.j20.probe.network.kafka.KafkaManager;
import com.airxiechao.j20.probe.network.protocol.IProtocol;
import com.airxiechao.j20.probe.network.protocol.ProtocolFactory;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 捕获器
 */
@Slf4j
public class ProtocolCapture {
    /**
     * 捕获包长度
     */
    private static final int SNAP_LEN = 65536;

    /**
     * 捕获超时
     */
    private static final int READ_TIMEOUT = 0;

    /**
     * 捕获处理器列表
     */
    private List<PcapHandle> handles = new ArrayList<>();

    public ProtocolCapture() {

    }

    /**
     * 启动
     * @throws Exception
     */
    public void start() throws Exception {
        // 读取配置文件
        ProbeCaptureConfig config;
        try{
            config = ProbeCaptureConfigFactory.getInstance().get();
            log.info("读取配置文件：{}", JSON.toJSONString(config));
        }catch (Exception e){
            throw new Exception("读取配置文件发生错误", e);
        }

        // 启动每一个捕获
        List<CaptureConfig> captures = config.getCaptures();
        for (CaptureConfig captureConfig : captures) {
            startOneAsync(captureConfig);
        }
    }

    /**
     * 停止
     */
    public void stop(){
        log.info("停止捕获");
        for (PcapHandle handle : handles) {
            if(null != handle){
                handle.close();
            }
        }
    }

    /**
     * 异步启动单个捕获处理器
     * @param captureConfig 捕获配置
     * @throws Exception 捕获异常
     */
    private void startOneAsync(CaptureConfig captureConfig) throws Exception {
        Set<String> filterProtocols = captureConfig.getProtocols();
        String interfaceIp = captureConfig.getIp();
        OutputConfig outputConfig = captureConfig.getOutput();

        // 裁剪协议树
        Set<String> protocols = new HashSet<>();
        for (String protocol : filterProtocols) {
            List<String> path = ProtocolFactory.getInstance().getProtocolPath(protocol);
            protocols.addAll(path);
        }

        TreeNode<Class<?>> protocolTree = ProtocolFactory.getInstance().getProtocolTree();
        TreeNode<Class<?>> trimmedProtocolTree = trimProtocolTree(protocolTree, protocols);

        if(null == trimmedProtocolTree){
            throw new Exception("协议为空");
        }

        PcapNetworkInterface nif = Pcaps.getDevByAddress(InetAddress.getByName(interfaceIp));
        if(null == nif){
            throw new Exception("设备未找到");
        }

        // 检查Kafka输出队列
        KafkaClient kafkaClient;
        if(null != outputConfig && null != outputConfig.getKafka()){
            String bootstrapServers =outputConfig.getKafka().getBootstrapServers();
            String dstTopic = outputConfig.getKafka().getTopic();
            try(KafkaAdmin kafkaAdmin = KafkaManager.getInstance().getAdmin(bootstrapServers)){
                if(!kafkaAdmin.hasTopic(dstTopic)){
                    kafkaAdmin.createTopic(dstTopic, 1, 1);
                }
            }

            kafkaClient = KafkaManager.getInstance().getClient(bootstrapServers, dstTopic);
        }else{
            kafkaClient = null;
        }


        log.info("开始捕获设备：{}，协议：{}", nif, filterProtocols);

        PcapHandle handle = nif.openLive(SNAP_LEN, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, READ_TIMEOUT);
        handles.add(handle);

        // 启动线程
        Thread thread = new Thread(() -> {
            try {
                handle.loop(0, (PacketListener) packet -> {

                    // 解析
                    List<Object> outputs = null;
                    try {
                        outputs = traverseProtocolTree(new LayerNode(packet, null), trimmedProtocolTree, filterProtocols);
                    } catch (Exception e) {
                        log.error("解析包发生错误", e);
                    }

                    // 输出
                    if(null != outputs){
                        for (Object output : outputs) {
                            log.debug("捕获：{}", JSON.toJSONString(output));

                            if(null != kafkaClient) {
                                try {
                                    kafkaClient.produce(null, JSON.toJSONString(output));
                                }catch (Exception e){
                                    log.error("输出发生错误", e);
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                log.error("捕获设备[{}]发生错误", nif, e);
            }
        });
        thread.start();
    }

    /**
     * 修剪协议树
     * @param node 协议树节点
     * @param names 协议名称集合
     * @return 修建后的协议树
     */
    private TreeNode<Class<?>> trimProtocolTree(TreeNode<Class<?>> node, Set<String> names){
        if(!names.contains(node.getName())){
            return null;
        }

        List<TreeNode<Class<?>>> newChildren = new ArrayList<>();
        for (TreeNode<Class<?>> child : node.getChildren()) {
            TreeNode<Class<?>> newChild = trimProtocolTree(child, names);
            if(null != newChild){
                newChildren.add(newChild);
            }
        }

        TreeNode<Class<?>> newNode = new TreeNode<>();
        newNode.setName(node.getName());
        newNode.setNode(node.getNode());
        newNode.setChildren(newChildren);

        return newNode;
    }

    /**
     * 遍历协议树
     * @param outerPayload 外层对象
     * @param node 协议树节点
     * @param filterProtocols 协议集合
     * @return 输出数据列表
     * @throws Exception 遍历异常
     */
    private List<Object> traverseProtocolTree(LayerNode outerPayload, TreeNode<Class<?>> node, Set<String> filterProtocols) throws Exception {
        List<Object> outputs = new ArrayList<>();

        Class<?> cls = node.getNode();
        Object obj = cls.getDeclaredConstructor().newInstance();

        if(obj instanceof IProtocol){
            IProtocol protocol = (IProtocol) obj;

            // 解析
            if(!protocol.parse(outerPayload)){
                return outputs;
            }

            // 输出
            if(filterProtocols.contains(node.getName())){
                Object output = protocol.getOutput();
                if(null != output){
                    outputs.add(output);
                }
            }

            // 下一步解析
            LayerNode payload = protocol.getPayload();
            if(null == payload){
                return outputs;
            }

            for (TreeNode<Class<?>> child : node.getChildren()) {
                List<Object> childOutputs = traverseProtocolTree(payload, child, filterProtocols);
                outputs.addAll(childOutputs);
            }
        }

        return outputs;
    }
}
