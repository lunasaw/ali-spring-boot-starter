package com.luna.ali.oss;

import java.io.*;
import java.net.URL;
import java.util.Objects;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.luna.common.date.DateUtils;
import com.luna.common.text.RandomStrUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.luna.ali.config.AliOssConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @Package: com.luna.ali.oss
 * @ClassName: AliOssUploadApi
 * @Author: luna
 * @CreateTime: 2020/8/21 22:23
 * @Description:
 */
public class AliOssUploadApi {

    public AliOssUploadApi(OSS ossClient) {
        this.ossClient = ossClient;
    }

    private OSS                 ossClient;

    private static final Logger log = LoggerFactory.getLogger(AliOssUploadApi.class);

    /**
     * 上传文件
     *
     * @param fileName 文件路径
     * @param bucketName 桶名称
     * @param folder 文件网络路径
     * @return
     */
    public PutObjectResult uploadFile(String fileName, String bucketName, String folder) {
        Assert.notNull(bucketName, "存储空间名称不能为空");
        File file = new File(fileName);
        if (StringUtils.isNotEmpty(folder) && !folder.endsWith("/")) {
            folder += "/";
        }
        String filePath = folder + DateUtils.getTodayString() + "_" + RandomStrUtil.generateNonceStrWithUUID() + "_" + file.getName();
        return uploadFile(filePath, file, bucketName, folder, null, null);
    }

    public static void main(String[] args) {
        String filePath = "" + DateUtils.getTodayString() + "_" + RandomStrUtil.generateNonceStrWithUUID() + "_" + "1231.png";
        System.out.println(filePath);
        System.out.println(filePath.length());
    }

    /**
     * 上传文件
     * 
     * @param fileName 文件路径
     * @param bucketName 桶名称
     * @param folder 文件网络路径
     * @param access 访问权限
     * @param type 存储类型
     * @return
     */
    public PutObjectResult uploadFile(String fileName, File file, String bucketName, String folder, String access, String type) {
        log.info("uploadFile::fileName = {}, bucketName = {}, folder = {}, access = {}, type = {}", fileName, bucketName, folder, access, type);

        if (StringUtils.isEmpty(type)) {
            type = StorageClass.Standard.toString();
        }

        if (StringUtils.isEmpty(access)) {
            // 默认公共读
            access = CannedAccessControlList.PublicRead.toString();
        }

        ObjectMetadata metadata = AliOssUtil.getObjectMetadata(access, type);
        PutObjectResult putObjectResult = uploadFile(fileName, file, bucketName, metadata);
        log.info("uploadFile::fileName = {}, putObjectResult = {}", fileName, JSON.toJSONString(putObjectResult));
        return putObjectResult;
    }

    /**
     * 上传文件
     *
     * @param objectName 文件名称
     * @param file 文件
     * @param bucketName 桶名称
     * @param metadata 权限
     */
    public PutObjectResult uploadFile(String objectName, File file, String bucketName, ObjectMetadata metadata) {
        log.info("uploadFile::fileName = {}, file = {}, bucketName = {}, metadata = {}", objectName,
            file.getAbsolutePath(), bucketName, metadata);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
        putObjectRequest.setMetadata(metadata);
        try {
            return ossClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 流式上传
     *
     * @param content 内容
     * @param objectName 桶名称
     * @param metadata 权限
     */
    public PutObjectResult uploadStream(String content, String objectName, String bucketName, ObjectMetadata metadata) {
        PutObjectRequest putObjectRequest =
            new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

        putObjectRequest.setMetadata(metadata);
        try {
            return ossClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 字节上传
     *
     * @param content 内容
     * @param objectName 桶名称
     * @param metadata 权限
     */
    public PutObjectResult uploadByte(byte[] content, String objectName, String bucketName, ObjectMetadata metadata) {
        PutObjectRequest putObjectRequest =
            new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content));

        putObjectRequest.setMetadata(metadata);
        try {
            return ossClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 网络流上传
     *
     * @param content 内容
     * @param objectName 桶名称
     * @param metadata 权限
     */
    public PutObjectResult uploadURL(URL content, String objectName, String bucketName, ObjectMetadata metadata) {
        try {
            InputStream inputStream = content.openStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);

            putObjectRequest.setMetadata(metadata);

            return ossClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 文件流式上传
     *
     * @param content 内容
     * @param objectName 桶名称
     * @param metadata 权限
     */
    public PutObjectResult uploadFileStream(FileInputStream content, String objectName, String bucketName, ObjectMetadata metadata) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, content);

        putObjectRequest.setMetadata(metadata);
        try {
            return ossClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
    }

}
