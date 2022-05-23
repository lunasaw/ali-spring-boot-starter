package com.luna.ali.oss;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.luna.ali.config.AliOssConfigProperties;
import com.luna.common.constant.StrPoolConstant;
import com.luna.common.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author luna
 * 2022/5/22
 */
public class AliOssWebApi {

    @Autowired
    private AliOssConfigProperties aliOssConfigProperties;

    public AliOssWebApi(OSS ossClient) {
        this.ossClient = ossClient;
    }

    private OSS                 ossClient;

    private static final Logger log = LoggerFactory.getLogger(AliOssUploadApi.class);

    public Map<String, Object> getPolicy(Long expireTime, String dir, String objectName, String jasonCallback) {
        log.info("getPolicy::expireTime = {}, dir = {}, objectName = {}", expireTime, dir, objectName);
        if (StringUtils.isEmpty(dir)) {
            dir += DateUtils.datePath() + "/";
        }

        if (!dir.endsWith(StrPoolConstant.SLASH)) {
            dir += "/";
        }

        Map<String, Object> policy = getPolicy(expireTime, dir);

        // https://bucketname.endpoint
        String host = "https://" + aliOssConfigProperties.getBucketName() + StrPoolConstant.DOT + aliOssConfigProperties.getEndpoint();
        policy.put("host", host);
        policy.put("objectName", objectName);
        policy.put("dir", DateUtils.datePath());
        policy.put("accessKey", aliOssConfigProperties.getAccessKey());
        policy.put("callbackUrl", aliOssConfigProperties.getCallbackUrl());
        policy.put("enableCname", aliOssConfigProperties.getEnableCname());
        policy.put("domain", aliOssConfigProperties.getDomain());

        if (StringUtils.isNotEmpty(jasonCallback)) {
            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.getBytes());
            policy.put("callback", base64CallbackBody);
        }

        log.info("getPolicy::expireTime = {}, dir = {}, policy = {}", expireTime, dir, JSON.toJSONString(policy));
        return policy;
    }

    /**
     * 签名
     * 
     * @param expireTime 分钟
     * @param dir 目录
     * @return
     */
    public Map<String, Object> getPolicy(Long expireTime, String dir) {
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = ossClient.calculatePostSignature(postPolicy);

        Map<String, Object> respMap = new LinkedHashMap<>();
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        respMap.put("expire", String.valueOf(expireEndTime / 1000));
        return respMap;
    }
}
