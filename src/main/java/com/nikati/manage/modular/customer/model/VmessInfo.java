package com.nikati.manage.modular.customer.model;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:46
 * @version：V1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VmessInfo {

    private String uuid;

    private Integer port;

    private String address;

    private String vmessStr;


    public static String buildVmess(
            String ip,
            Integer port,
            String uuid,
            String remark) {

        JSONObject json = new JSONObject();

        json.put("v","2");
        json.put("ps",remark);
        json.put("add",ip);
        json.put("port",port);
        json.put("id",uuid);
        json.put("aid","0");
        json.put("net","tcp");
        json.put("type","none");
        json.put("host","");
        json.put("path","");
        json.put("tls","");

        return "vmess://"+
                Base64.getEncoder()
                        .encodeToString(
                                json.toString()
                                        .getBytes(StandardCharsets.UTF_8));
    }

}