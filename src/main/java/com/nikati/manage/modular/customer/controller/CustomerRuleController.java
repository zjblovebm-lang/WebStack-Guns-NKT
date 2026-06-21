package com.nikati.manage.modular.customer.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.nikati.manage.modular.customer.email.EmailSender;
import com.nikati.manage.modular.customer.model.ProcessResult;
import com.nikati.manage.modular.customer.model.ServerInfo;
import com.nikati.manage.modular.customer.model.VmessConfig;
import com.nikati.manage.modular.customer.model.VmessInfo;
import com.nikati.manage.modular.nyunpass.service.INyunPassService;
import com.nikati.manage.modular.xui.model.ResultModel;
import com.nikati.manage.modular.xui.service.IXuiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @descripe：
 * @company：北京华如科技股份有限公司南京分公司
 * @author：zhoujinbing
 * @time：2026/6/4 17:04
 * @version：V1.0
 */
@RestController
@RequestMapping("customer")
@Api(tags = "客户批量导入API")
@RequiredArgsConstructor
public class CustomerRuleController {

    private final ExecutorService xuiExecutor;

    @Autowired
    private IXuiService xuiService;

    @Autowired
    private INyunPassService nyunPassService;

    @Autowired
    private EmailSender gmailSender;

    /**
     * 批量导入
     *
     * @param file
     * @return
     */
    @ApiOperation("通过excel导入所有用户规则")
    @PostMapping("importRulesByExcel")
    public ResultModel importCustomer(MultipartFile file) throws IOException {

        ExcelReader reader =
                ExcelUtil.getReader(file.getInputStream());

        // 中文表头 -> 实体字段
        reader.addHeaderAlias("国家", "country");
        reader.addHeaderAlias("落地机器ip", "ip");
        reader.addHeaderAlias("落地机器ssh端口", "port");
        reader.addHeaderAlias("用户名", "username");
        reader.addHeaderAlias("密码", "password");
        reader.addHeaderAlias("客人名称", "customerName");
        reader.addHeaderAlias("客人邮箱", "customerEmail");
        reader.addHeaderAlias("过期时间", "customerExpireDate");
        reader.addHeaderAlias("线路", "routeType");

        List<ServerInfo> servers =
                reader.readAll(ServerInfo.class);

        List<CompletableFuture<ProcessResult>> futures =
                servers.stream()
                        .map(server ->
                                CompletableFuture.supplyAsync(
                                        () -> process(server),
                                        xuiExecutor)).collect(Collectors.toList());

        List<ProcessResult> processResult = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return ResultModel.success(JSONUtil.toJsonPrettyStr(processResult));
    }




    private synchronized ProcessResult process(ServerInfo server) {

        try {

            //创建vpn面板
            boolean result = xuiService.xuiInstall(server);
            if (result) {
                //vpn面板创建规则
                VmessInfo vmess = xuiService.createVmess(server);

                //进行隧道进行中转
                VmessInfo vmessInfo = nyunPassService.createForwardRule(server, vmess);

                ProcessResult processResult = new ProcessResult();
                BeanUtil.copyProperties(server,processResult, CopyOptions.create().ignoreNullValue());
                VmessConfig vmessConfig = new VmessConfig();
                vmessConfig.setPs(server.getCustomerName());
                vmessConfig.setAdd(vmessInfo.getAddress());
                vmessConfig.setPort(vmessInfo.getPort().toString());
                vmessConfig.setId(vmessInfo.getUuid());

                String vmessJson = JSONUtil.toJsonStr(vmessConfig);
                String base64 = Base64.encode(vmessJson);
                processResult.setVmessStr("vmess://" + base64);
                processResult.setPort(vmessInfo.getPort());
                processResult.setAddress(vmessInfo.getAddress());

                //发送验证码
                // 1. 生成二维码
                BufferedImage qr = QrCodeUtil.generate(processResult.getVmessStr(), 300, 300);

                // 2. 加中心字母
                Graphics2D g = qr.createGraphics();
                g.setColor(Color.BLUE);
                g.setFont(new Font("Arial", Font.BOLD, 40));

                String text = "YZ";
                FontMetrics fm = g.getFontMetrics();
                int x = (300 - fm.stringWidth(text)) / 2;
                int y = (300 + fm.getAscent()) / 2;

                g.drawString(text, x, y);
                g.dispose();

                File file = new File(server.getCustomerName() + ".png");
                ImageIO.write(qr, "png", file);

                // 3. 发送邮件
                gmailSender.send(server,server.getCustomerEmail(), file);
                return processResult;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}