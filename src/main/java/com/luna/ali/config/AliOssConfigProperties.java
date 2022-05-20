package com.luna.ali.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

/**
 * @author Luna@win10
 * @date 2020/5/6 21:09
 */
@Component
@ConfigurationProperties(prefix = "luna.ali")
@Data
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
    private OSS     ossClient;

    public OSS getOssClient() {
        if (null == enableCname) {
            return getOssClient(false);
        }
        return getOssClient(true);
    }

    public OSS getOssClient(Boolean isCname) {
        if (isCname && StringUtils.isNotEmpty(domain)) {
            ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
            // 设置是否支持CNAME。CNAME是指将自定义域名绑定到存储空间上。
            conf.setSupportCname(true);
            return new OSSClientBuilder().build(domain, accessKey, secretKey, conf);
        }
        if (ossClient == null) {
            this.ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        }
        return ossClient;
    }

}
