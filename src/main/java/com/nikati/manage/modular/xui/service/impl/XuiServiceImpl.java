package com.nikati.manage.modular.xui.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.ssh.JschUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import com.nikati.manage.modular.customer.enu.TrafficPlanEnum;
import com.nikati.manage.modular.customer.model.ServerInfo;
import com.nikati.manage.modular.customer.model.VmessInfo;
import com.nikati.manage.modular.xui.model.ResultModel;
import com.nikati.manage.modular.xui.service.IXuiService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:35
 * @version：V1.0
 */
@Service
public class XuiServiceImpl  implements IXuiService {


    @Override
    public boolean xuiInstall(ServerInfo serverInfo) {
        try {
            Session session = JschUtil.getSession(
                    serverInfo.getIp(),
                    serverInfo.getPort(),
                    serverInfo.getUsername(),
                    serverInfo.getPassword()
            );

            // 1️⃣ 先检查是否已安装 x-ui
            String checkCmd = "if command -v x-ui >/dev/null 2>&1; then echo INSTALLED; else echo NOT_INSTALLED; fi";

            String result = exec(session, checkCmd);

            if (result.equals("INSTALLED")) {
                session.disconnect();
                return true;
            }else{
                session.disconnect();
                session = JschUtil.getSession(
                        serverInfo.getIp(),
                        serverInfo.getPort(),
                        serverInfo.getUsername(),
                        serverInfo.getPassword()
                );
            }

            ChannelShell channel = (ChannelShell) session.openChannel("shell");

            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();

            channel.setPty(true);
            channel.connect();

            // 执行安装脚本
            out.write(("curl -Ls https://raw.githubusercontent.com/yonggekkk/x-ui-yg/main/install.sh -o install.sh && bash install.sh\n").getBytes());
            out.flush();

            byte[] buffer = new byte[4096];
            StringBuilder sb = new StringBuilder();
            boolean finished = false;

            long start = System.currentTimeMillis();

            while (true) {

                while (in.available() > 0) {
                    int len = in.read(buffer);
                    String text = new String(buffer, 0, len);

                    System.out.print(text); // 调试用

                    sb.append(text);

                    // ========= 关键：按提示逐步输入 =========
                    if(text.contains("请输入数字【0-14】:"))
                    {
                        out.write("1\n".getBytes());
                        out.flush();
                    }

                    if (text.contains("是否开放端口，关闭防火墙？")) {
                        out.write("1\n".getBytes());
                        out.flush();
                    }


                    if (text.contains("设置 x-ui 登录用户名")) {
                        out.write("jack1994\n".getBytes());
                        out.flush();
                    }

                    if (text.contains("设置 x-ui 登录密码")) {
                        out.write("www..1994\n".getBytes());
                        out.flush();
                    }

                    if (text.contains("设置 x-ui 登录端口")) {
                        out.write("10000\n".getBytes());
                        out.flush();
                    }

                    if (text.contains("设置 x-ui 登录根路径")) {
                        out.write("abc\n".getBytes());
                        out.flush();
                    }
                    if(text.contains("x-ui已安装，可先选择2卸载，再安装")){
                        finished = true;
                        break;
                    }
                }

                // 超时保护（10分钟）
                if (System.currentTimeMillis() - start > 10 * 60 * 1000) {
                    System.out.println("install timeout");
                    break;
                }

                if (channel.isClosed()) {
                    break;
                }

                if (finished) {
                    break;
                }

                Thread.sleep(300);
            }

            for(int i=0;i<30;i++){

                String execBash =
                        exec(session,
                                "systemctl is-active x-ui");

                if(execBash.equals("active")){
                    channel.disconnect();
                    session.disconnect();
                    return true;
                }
                Thread.sleep(2000);
            }
            channel.disconnect();
            session.disconnect();
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public VmessInfo createVmess(ServerInfo serverInfo) {
        String loginUrl = "http://"+serverInfo.getIp()+":10000/abc/login";
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
        String dateStr = serverInfo.getCustomerExpireDate();
        long expiryTime = DateUtil.parse(dateStr).getTime();
        System.out.println("expiryTime = " + expiryTime);

        // 3️⃣ 找可用端口
        int port = findAvailablePort(10000, 65535,serverInfo.getIp());
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

        TrafficPlanEnum trafficPlanEnum = TrafficPlanEnum.fromName(serverInfo.getRouteType());


        // 7️⃣ 请求创建节点
        String result = HttpRequest.post(createVmessUrl)
                .header("Cookie", "session=" + loginResp.getCookies().get(0).getValue())
                .form("up", 0)
                .form("down", 0)
                .form("total", trafficPlanEnum.getTrafficG())
                .form("remark", serverInfo.getCustomerName())
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

        VmessInfo vmessInfo = new VmessInfo();
        vmessInfo.setUuid(uuid);
        vmessInfo.setPort(port);
        vmessInfo.setAddress(serverInfo.getIp());
        return vmessInfo;
    }

    /**
     * 端口检测（远程服务器）
     */
    public static int findAvailablePort(int min, int max,String host) {
        Random random = new Random();

        while (true) {
            int port = random.nextInt(max - min) + min;

            if (isPortAvailable(host, port)) {
                return port;
            }

            System.out.println("端口占用：" + port + " 继续尝试...");
        }
    }

    /**
     * 检测远程端口是否可用
     */
    public static boolean isPortAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), 300);
            return false; // 能连上 = 被占用
        } catch (Exception e) {
            return true; // 连不上 = 可用
        }
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
}