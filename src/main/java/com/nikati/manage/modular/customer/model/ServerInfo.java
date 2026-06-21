package com.nikati.manage.modular.customer.model;

import lombok.Data;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:15
 * @version：V1.0
 */
@Data
public class ServerInfo {

    //国家
    private String country;

    //落地机器ip
    private String ip;

    //用户名
    private String username;

    //密码
    private String password;

    //ssh端口
    private Integer port;

    //客户名称
    private String customerName;

    //客户邮箱
    private String customerEmail;

    //过期时间
    private String customerExpireDate;

    //线路类型
    private String routeType;

}