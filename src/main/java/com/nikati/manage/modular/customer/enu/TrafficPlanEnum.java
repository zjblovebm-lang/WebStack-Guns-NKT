package com.nikati.manage.modular.customer.enu;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 20:34
 * @version：V1.0
 */
@Getter
@AllArgsConstructor
public enum TrafficPlanEnum {

    LIVE("直播", 200*1000*1000/8l, 1024*1024*1024*1024L),
    VIDEO("视频", 30*1000*1000/8l, 500*1024*1024*1024L);

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 限速 Mbps
     */
    private final Long speedM;

    /**
     * 流量 字节
     */
    private final Long trafficG;

    public static TrafficPlanEnum fromName(String name) {
        for (TrafficPlanEnum e : values()) {
            if (e.name.equals(name)) {
                return e;
            }
        }
        return null;
    }
}