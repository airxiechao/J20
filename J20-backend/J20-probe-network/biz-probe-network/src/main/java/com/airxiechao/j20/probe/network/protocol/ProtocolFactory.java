package com.airxiechao.j20.probe.network.protocol;

import com.airxiechao.j20.probe.common.api.pojo.constant.ConstProtocolType;
import com.airxiechao.j20.probe.network.api.pojo.protocol.Protocol;
import com.airxiechao.j20.probe.network.api.pojo.tree.ReverseTreeNode;
import com.airxiechao.j20.probe.network.api.pojo.tree.TreeNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.*;

import static org.reflections.scanners.Scanners.SubTypes;
import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * 协议解析器工厂
 */
@Slf4j
public class ProtocolFactory {
    @Getter
    private static final ProtocolFactory instance = new ProtocolFactory();

    /**
     * 协议树
     */
    @Getter
    private TreeNode<Class<?>> protocolTree;

    /**
     * 反向协议树
     */
    private Map<String, ReverseTreeNode<Class<?>>> reverseProtocolNodes = new HashMap<>();

    private ProtocolFactory() {
        registerProtocol();
    }

    /**
     * 获取协议路径
     * @param protocol 协议名
     * @return 协议路径列表
     */
    public List<String> getProtocolPath(String protocol){
        List<String> path = new ArrayList<>();

        if(!reverseProtocolNodes.containsKey(protocol)){
            return path;
        }

        ReverseTreeNode<Class<?>> node = reverseProtocolNodes.get(protocol);
        path.add(node.getName());

        while (true){
            ReverseTreeNode<Class<?>> parent = node.getParent();
            if(null == parent){
                break;
            }

            path.add(parent.getName());

            node = parent;
        }

        return path;
    }

    /**
     * 注册协议解析器
     */
    private void registerProtocol(){
        Map<String, TreeNode<Class<?>>> nodes = new HashMap<>();
        Reflections reflections = new Reflections(this.getClass().getPackageName());
        Set<Class<?>> set = reflections.get(SubTypes.of(TypesAnnotated.with(Protocol.class)).asClass());
        for (Class<?> cls : set) {
            if (IProtocol.class.isAssignableFrom(cls)){
                Protocol protocol = cls.getAnnotation(Protocol.class);
                log.info("注册协议：{}", protocol.name());

                TreeNode<Class<?>> node = new TreeNode<>();
                node.setName(protocol.name());
                node.setNode(cls);
                nodes.put(protocol.name(), node);

                ReverseTreeNode<Class<?>> reverseNode = new ReverseTreeNode<>();
                reverseNode.setName(protocol.name());
                reverseNode.setNode(cls);
                reverseProtocolNodes.put(protocol.name(), reverseNode);
            }
        }

        // 填充节点的 children
        for (TreeNode<Class<?>> node : nodes.values()) {
            Class<?> cls = node.getNode();
            Protocol protocol = cls.getAnnotation(Protocol.class);
            if(ConstProtocolType.RAW.equals(protocol.name())){
                protocolTree = node;
            }

            if(StringUtils.isNotBlank(protocol.outer()) && nodes.containsKey(protocol.outer())) {
                TreeNode<Class<?>> parent = nodes.get(protocol.outer());
                parent.getChildren().add(node);
            }
        }

        // 填充节点的 parent
        for (ReverseTreeNode<Class<?>> reverseNode : reverseProtocolNodes.values()) {
            Class<?> cls = reverseNode.getNode();
            Protocol protocol = cls.getAnnotation(Protocol.class);
            if(StringUtils.isNotBlank(protocol.outer()) && reverseProtocolNodes.containsKey(protocol.outer())) {
                ReverseTreeNode<Class<?>> parent = reverseProtocolNodes.get(protocol.outer());
                reverseNode.setParent(parent);
            }
        }
    }
}
