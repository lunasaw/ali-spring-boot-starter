package com.luna.ali.config;

import com.aliyun.oss.OSSClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import javax.annotation.PostConstruct;

/**
 * @author Luna@win10
 * @date 2020/5/6 21:09
 */
@ConfigurationProperties(prefix = "luna.ali")
public class AliOssConfigProperties {
    private String  accessKey;

    private String  secretKey;

    private String  bucketName;

    /**
     * endpoint
     */
    private String  endpoint;
    /**
     * 自定义域名
     */
    private String  domain;

    /**
     * 回调路径
     */
    private String  serverUrl;

    /**
     * 是否开启自定义域名
     */
    private Boolean enableCname;

    /**
     * 创建OSSClient实例
     */
    private OSS client;

    @PostConstruct
    public OSS getInstanceClient() {
        if (null == enableCname) {
            return  getInstanceClient(false);
        }
        return getInstanceClient(true);
    }

    public OSS getInstanceClient(Boolean isCname) {
        if (isCname && StringUtils.isNotEmpty(domain)) {
            ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
            // 设置是否支持CNAME。CNAME是指将自定义域名绑定到存储空间上。
            conf.setSupportCname(true);
            return new OSSClientBuilder().build(domain, accessKey, secretKey, conf);
        }
        if (client == null) {
            return new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        }
        return client;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public Boolean getEnableCname() {
        return enableCname;
    }

    public void setEnableCname(Boolean enableCname) {
        this.enableCname = enableCname;
    }

    public OSS getClient() {
        return client;
    }

    public void setClient(OSS client) {
        this.client = client;
    }
}
