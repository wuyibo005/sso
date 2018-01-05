package win.scolia.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import win.scolia.sso.autoconfigure.SSOProperties;

/**
 * Created by scolia on 2017/11/27
 *
 * spring boot 入口程序
 */

@SpringBootApplication
@MapperScan("win.scolia.sso.dao")
@EnableTransactionManagement
@ServletComponentScan
@EnableConfigurationProperties(SSOProperties.class)
public class SSOApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSOApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SSOApplication.class, args);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("spring boot started....");
        }
    }
}
