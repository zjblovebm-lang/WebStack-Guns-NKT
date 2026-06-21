import cn.hutool.json.JSONUtil;
import com.nikati.manage.WebstackGunsApplication;
import com.nikati.manage.modular.nyunpass.controller.NyunpassController;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebstackGunsApplication.class)
@Slf4j
public class NyunpassControllerTest {

    @Autowired
    private NyunpassController nyunpassController;

    @Test
    public void testCreateRule() throws Exception {
        log.info(nyunpassController.createRules());
    }

}
