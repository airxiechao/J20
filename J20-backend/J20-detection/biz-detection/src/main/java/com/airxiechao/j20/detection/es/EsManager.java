package com.airxiechao.j20.detection.es;

import com.airxiechao.j20.common.es.EsClient;
import lombok.Getter;

/**
 * ES客户端管理器
 */
public class EsManager {
    @Getter
    public static final EsManager instance = new EsManager();
    private EsManager(){}

    private static String[] hosts = EsConfigFactory.getInstance().get().getHosts().split(",");
    private static String user = EsConfigFactory.getInstance().get().getUser();
    private static String password = EsConfigFactory.getInstance().get().getPassword();
    private static int version = EsConfigFactory.getInstance().get().getVersion();
    private static int timeout = EsConfigFactory.getInstance().get().getTimeoutMillis();

    private EsClient client;

    public EsClient getClient(){
        if(null == client){
            client = new EsClient(hosts, user, password, version, timeout);
        }

        return client;
    }
}
