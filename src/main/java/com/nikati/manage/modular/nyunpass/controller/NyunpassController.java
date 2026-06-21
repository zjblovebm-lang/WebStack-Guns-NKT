package com.nikati.manage.modular.nyunpass.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.nikati.manage.modular.xui.model.ResultModel;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 16:15
 * @version：V1.0
 */
@RestController("/nyunpass")
@Api(tags = "nyunpass面板 配置")
public class NyunpassController {

    @Value("${zhuanxian.url}")
    private String suidaoUrl;

    @Value("${zhuanxian.suidaoyuming}")
    private String suidaoyuming;

    @GetMapping("createRule")
    public ResultModel createRules(){
        Map<String,Object> map = new HashMap<>();
        map.put("username","admin");
        map.put("password","yaozhen601");

        String result = HttpUtil.post(suidaoUrl.concat("auth/login"), JSONUtil.toJsonStr(map));
        String authorization = JSONUtil.parseObj(result).getStr("data");



        String str = "{\"name\":\"test3\",\"device_group_in\":5,\"device_group_out\":0,\"listen_port\":11111,\"config\":\"{\\\"dest\\\":[\\\"154.44.5.60:18272\\\"],\\\"speed_limit\\\":3750000}\"}";
        String response = HttpRequest.put(suidaoUrl.concat("user/forward")).header("Authorization", authorization)
                .body(str).execute().body();

        return ResultModel.success(response);
    }



}