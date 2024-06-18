package com.airxiechao.j20.detection.minio;

import com.airxiechao.j20.common.minio.MinioHelper;
import lombok.Getter;

/**
 * Minio客户端管理器。
 * 负责创建Minio客户端。
 */
public class MinioManager {
    @Getter
    public static final MinioManager instance = new MinioManager();
    private MinioManager(){}

    private static String endpoint = MinioConfigFactory.getInstance().get().getEndpoint();
    private static String accessKey = MinioConfigFactory.getInstance().get().getAccessKey();
    private static String secretKey = MinioConfigFactory.getInstance().get().getSecretKey();
    private static String bucket = MinioConfigFactory.getInstance().get().getBucket();

    private MinioHelper helper;

    public MinioHelper getHelper(){
        if(null == helper){
            helper = new MinioHelper(endpoint, accessKey, secretKey, bucket);
        }

        return helper;
    }
}
