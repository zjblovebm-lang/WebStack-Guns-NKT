package com.nikati.manage.modular.xui.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nikati.manage.modular.xui.model.ResultModel;
import com.nikati.manage.modular.xui.service.impl.XuiServiceImpl;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.Socket;
import java.util.List;
import java.util.Random;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 11:50
 * @version：V1.0
 */
@RestController("/xui")
@Api(tags = "X-UI安装脚本")
public class XuiInstallController extends HrBaseController {

    @GetMapping("/install")
    public ResultModel installXui() throws Exception {
        Session session = JschUtil.getSession(
                "154.44.5.60",
                22,
                "root",
                "www..1994"
        );

        // 1️⃣ 先检查是否已安装 x-ui
        String checkCmd = "if command -v x-ui >/dev/null 2>&1; then echo INSTALLED; else echo NOT_INSTALLED; fi";

        String result = exec(session, checkCmd);
        System.out.println("检测结果：" + result);

        if (result.equals("INSTALLED")) {
            session.disconnect();
            return ResultModel.success("x-ui 已安装，跳过执行");
        }else{
            session.disconnect();
            session = JschUtil.getSession(
                    "154.44.5.60",
                    22,
                    "root",
                    "www..1994"
            );

        }

        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        // 关键：开启伪终端，否则很多交互脚本不会接收输入
        channel.setPty(true);

        String cmd =
                "curl -Ls https://raw.githubusercontent.com/yonggekkk/x-ui-yg/main/install.sh -o install.sh; " +
                        "bash install.sh";

        channel.setCommand(cmd);

        // 关键：模拟输入（按脚本顺序一行一个）
        String input =
                "1\n" +          // 是否开放端口
                        "1\n" +          // 防火墙
                        "jack1994\n" +   // 用户名
                        "www..1994\n" +  // 密码
                        "10000\n" +      // 端口
                        "abc\n";         // 路径

        OutputStream out = channel.getOutputStream();

        channel.connect();

        out.write(input.getBytes());
        out.flush();

        Thread.sleep(20000); // 等待执行完成

        channel.disconnect();
        session.disconnect();

        return ResultModel.success(null);
    }

    /**
     * 执行单条命令并返回结果
     */
    public  String exec(Session session, String cmd) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(cmd);

        java.io.InputStream in = channel.getInputStream();

        channel.connect();

        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];

        int len;
        while ((len = in.read(buf)) != -1) {
            sb.append(new String(buf, 0, len));
        }

        channel.disconnect();
        return sb.toString().trim();
    }

    @GetMapping("createVmess")
    public ResultModel createVmess() {
        String loginUrl = "http://154.44.5.60:10000/abc/login";
        String vmessListUrl = "http://154.44.5.60:10000/abc/xui/inbound/list";
        String createVmessUrl = "http://154.44.5.60:10000/abc/xui/inbound/add";

        // 1️⃣ 模拟登录
        HttpResponse loginResp = HttpRequest.post(loginUrl)
                .form("username", "jack1994")
                .form("password", "www..1994")
                .execute();

        // 输出登录返回的 JSON
        String loginResult = loginResp.body();
        System.out.println("登录返回：" + loginResult);

        //创建
        // 1️⃣ 生成 UUID（vmess id）
        String uuid = IdUtil.randomUUID(); // 36位标准UUID

        System.out.println("UUID = " + uuid);

        // 2️⃣ 日期转 expiryTime
        String dateStr = "2026-06-07";
        long expiryTime = DateUtil.parse(dateStr).getTime();
        System.out.println("expiryTime = " + expiryTime);

        // 3️⃣ 找可用端口
        int port = XuiServiceImpl.findAvailablePort(1000, 65535,"154.44.5.60");
        System.out.println("可用端口 = " + port);

        // 4️⃣ 拼 settings JSON
        String settings = "{\n" +
                "  \"clients\": [\n" +
                "    {\n" +
                "      \"id\": \"" + uuid + "\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"disableInsecureEncryption\": false\n" +
                "}";

        // 5️⃣ streamSettings
        String streamSettings = "{\n" +
                "  \"network\": \"tcp\",\n" +
                "  \"security\": \"none\",\n" +
                "  \"tcpSettings\": {\n" +
                "    \"header\": {\n" +
                "      \"type\": \"none\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // 6️⃣ sniffing
        String sniffing = "{\n" +
                "  \"enabled\": true,\n" +
                "  \"destOverride\": [\"http\", \"tls\", \"quic\"]\n" +
                "}";

        // 7️⃣ 请求创建节点
        String result = HttpRequest.post(createVmessUrl)
                .header("Cookie", "session=" + loginResp.getCookies().get(0).getValue())
                .form("up", 0)
                .form("down", 0)
                .form("total", 1*1024*1024*1024)
                .form("remark", "auto-node")
                .form("enable", true)
                .form("expiryTime", expiryTime)
                .form("listen", "")
                .form("port", port)
                .form("protocol", "vmess")
                .form("settings", settings)
                .form("streamSettings", streamSettings)
                .form("sniffing", sniffing)
                .execute()
                .body();

        System.out.println("创建结果：" + result);



        //获取所有列表
        HttpResponse statusResp = HttpRequest.post(vmessListUrl)
                .header("Cookie", "session=" + loginResp.getCookies().get(0).getValue())
                .execute();

        return ResultModel.success(statusResp.body());
    }



}