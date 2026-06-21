package com.nikati.manage.modular.nyunpass.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.nikati.manage.modular.customer.enu.TrafficPlanEnum;
import com.nikati.manage.modular.customer.model.ServerInfo;
import com.nikati.manage.modular.customer.model.VmessInfo;
import com.nikati.manage.modular.nyunpass.service.INyunPassService;
import com.nikati.manage.modular.xui.model.ResultModel;
import com.nikati.manage.modular.xui.service.impl.XuiServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 21:02
 * @version：V1.0
 */
@Service
public class NyunpassServiceImpl implements INyunPassService {
    @Value("${zhuanxian.url}")
    private String suidaoUrl;

    @Value("${zhuanxian.suidaoyuming}")
    private String suidaoyuming;

    /**
     * 进行转发
     *
     * @param serverInfo
     * @param vmessInfo
     * @return
     */
    @Override
    public VmessInfo createForwardRule(ServerInfo serverInfo, VmessInfo vmessInfo) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "admin");
        map.put("password", "yaozhen601");

        String result = HttpUtil.post(suidaoUrl.concat("auth/login"), JSONUtil.toJsonStr(map));
        String authorization = JSONUtil.parseObj(result).getStr("data");


        int port = XuiServiceImpl.findAvailablePort(10000, 65535, suidaoyuming);
        System.out.println("可用端口 = " + port);

        TrafficPlanEnum trafficPlanEnum = TrafficPlanEnum.fromName(serverInfo.getRouteType());

        String str = "{\"name\":\"" + serverInfo.getCustomerName() + "\",\"device_group_in\":5,\"device_group_out\":0,\"listen_port\":" + port + ",\"config\":\"{\\\"dest\\\":[\\\"" + vmessInfo.getAddress() + ":" + vmessInfo.getPort() + "\\\"],\\\"speed_limit\\\":"+trafficPlanEnum.getSpeedM()+"}\"}";
        String response = HttpRequest.put(suidaoUrl.concat("user/forward")).header("Authorization", authorization)
                .body(str).execute().body();

        String vmessStr = VmessInfo.buildVmess(suidaoyuming, port, vmessInfo.getUuid(), serverInfo.getCustomerName());
        vmessInfo.setAddress(suidaoyuming);
        vmessInfo.setPort(port);
        vmessInfo.setVmessStr(vmessStr);
        return vmessInfo;
    }
}