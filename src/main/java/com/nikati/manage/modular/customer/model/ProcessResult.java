package com.nikati.manage.modular.customer.model;

import lombok.Data;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:28
 * @version：V1.0
 */
@Data
public class ProcessResult extends ServerInfo {
    private boolean success;
    private String message;

    private String address;

    private Integer port;

    //VMESS协议字符串
    private String vmessStr;

    //根据vmess生成得二维码
    private String vmessPng;
}