package com.nikati.manage.modular.xui.service;

import com.nikati.manage.modular.customer.model.ServerInfo;
import com.nikati.manage.modular.customer.model.VmessInfo;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:33
 * @version：V1.0
 */
public interface IXuiService {

   /**
    * 创建x-ui面板
    * @param serverInfo
    * @return
    */
   boolean xuiInstall(ServerInfo serverInfo);

   /**
    * x-ui面板创建vmess协议
    * @param serverInfo
    * @return
    */
   VmessInfo createVmess(ServerInfo serverInfo);
}