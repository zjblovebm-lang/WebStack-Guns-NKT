package com.nikati.manage.modular.customer.controller;

import cn.hutool.core.codec.Base64;

import java.nio.charset.StandardCharsets;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 22:27
 * @version：V1.0
 */
public class Test {

    public static void main(String[] args) {

            String vmess = "vmess://ew0KICAidiI6ICIyIiwNCiAgInBzIjogIuWwp+eRp+enkeaKgCIsDQogICJhZGQiOiAid3d3LnhpeWFvLm5ldC5jbiIsDQogICJwb3J0IjogIjQ2MTYyIiwNCiAgImlkIjogImE5YjZlYzhkLTg2YjgtNDEzYS05ZGM3LTk5MjIxYzRkNGJmMSIsDQogICJhaWQiOiAiMCIsDQogICJzY3kiOiAiYXV0byIsDQogICJuZXQiOiAidGNwIiwNCogICJ0eXBlIjogIm5vbmUiLA0KICAiaG9zdCI6ICIiLA0KICAicGF0aCI6ICIiLA0KICAidGxzIjogIiIsDQogICJzbmkiOiAiIiwNCiAgImFscG4iOiAiIiwNCiAgImZwIjogIiINCn0=";

        String base64 = vmess.replace("vmess://", "").trim();

        // 自动修复 URL-safe + padding
        String json = Base64.decodeStr(base64, StandardCharsets.UTF_8);

        System.out.println(json);
    }

}