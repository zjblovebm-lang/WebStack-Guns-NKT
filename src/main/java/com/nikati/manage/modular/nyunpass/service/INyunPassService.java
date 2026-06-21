package com.nikati.manage.modular.nyunpass.service;

import com.nikati.manage.modular.customer.model.ServerInfo;
import com.nikati.manage.modular.customer.model.VmessInfo;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 20:58
 * @version：V1.0
 */
public interface INyunPassService {

    VmessInfo createForwardRule(ServerInfo serverInfo,VmessInfo vmessInfo);


}