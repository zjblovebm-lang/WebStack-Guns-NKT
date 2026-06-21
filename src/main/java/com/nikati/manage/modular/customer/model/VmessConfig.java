package com.nikati.manage.modular.customer.model;

import lombok.Data;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 22:35
 * @version：V1.0
 */
@Data
public class VmessConfig {

    public String v = "2";
    public String ps;//名称
    public String add;//域名地址
    public String port;//端口
    public String id;//uuid
    public String aid = "0";
    public String scy = "auto";
    public String net = "tcp";
    public String type = "none";
    public String host = "";
    public String path = "";
    public String tls = "";
    public String sni = "";
    public String alpn = "";
    public String fp = "";
}