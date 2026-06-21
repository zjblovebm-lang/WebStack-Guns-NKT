import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.jcraft.jsch.JSchException;
import com.nikati.manage.WebstackGunsApplication;
import com.nikati.manage.modular.xui.controller.XuiInstallController;
import com.sun.org.apache.xpath.internal.axes.FilterExprWalker;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebstackGunsApplication.class)
@Slf4j
public class XuiInstallControllerTest {

    @Autowired
    private XuiInstallController xuiInstallController;

    @Test
    public void testInstall() throws Exception {
        xuiInstallController.installXui();
    }

    @Test
    public void testCreateVmess(){
        log.info(JSONUtil.toJsonPrettyStr(xuiInstallController.createVmess()));
    }
}
