package com.airxiechao.j20.probe.network;

import com.airxiechao.j20.probe.network.capture.ProtocolCapture;
import lombok.extern.slf4j.Slf4j;

/**
 * 网络探针入口
 */
@Slf4j
public class ProbeNetworkBoot {
    public static void main(String[] args) {

        // 启动捕获
        ProtocolCapture capture = new ProtocolCapture();

        try {
            capture.start();
        } catch (Exception e) {
            log.error("启动捕获发生错误", e);
        }
    }

}
