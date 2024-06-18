package com.airxiechao.j20.common.minio;

import io.minio.*;

import java.io.InputStream;

/**
 * Minio 的客户端实现
 */
public class MinioHelper {
    private String bucket;
    MinioClient client;

    /**
     * 构造客户端
     * @param endpoint 主机地址
     * @param accessKey 访问用户名
     * @param secretKey 访问密码
     * @param bucket 桶
     */
    public MinioHelper(String endpoint, String accessKey, String secretKey, String bucket) {
        this.bucket = bucket;
        this.client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 上传文件
     * @param path 目的地址
     * @param inputStream 要上传文件的输入流
     * @throws Exception 请求异常
     */
    public void upload(String path, InputStream inputStream) throws Exception{
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if(!found){
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        client.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .stream(inputStream, -1, 10485760)
                        .build()
        );
    }

    /**
     * 删除文件
     * @param path 文件地址
     * @throws Exception 请求异常
     */
    public void remove(String path) throws Exception {
        client.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build()
        );
    }

    /**
     * 下载文件
     * @param filePath 本地文件路径
     * @param savePath 目的文件路径
     * @throws Exception 请求异常
     */
    public void download(String filePath, String savePath) throws Exception{
        client.downloadObject(DownloadObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .filename(savePath)
                .build());
    }

    /**
     * 读取文件流
     * @param filePath 目的文件地址
     * @return 文件的输入流
     * @throws Exception 请求异常
     */
    public InputStream read(String filePath) throws Exception{
        return client.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build());
    }
}
