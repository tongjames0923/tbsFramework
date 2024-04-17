package tbs.framework.xxl.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;


public class XxlJobConfig {

    @Bean
    @ConditionalOnBean(IXXLJobsConfig.class)
    public XxlJobSpringExecutor xxlJobSpringExecutor(IXXLJobsConfig config) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(config.adminAddress());
        xxlJobSpringExecutor.setAppname(config.appName());
        xxlJobSpringExecutor.setAddress(config.address());
        xxlJobSpringExecutor.setIp(config.ip());
        xxlJobSpringExecutor.setPort(config.port());
        xxlJobSpringExecutor.setAccessToken(config.accessToken());
        xxlJobSpringExecutor.setLogPath(config.logPath());
        xxlJobSpringExecutor.setLogRetentionDays(config.logRetentionsDays());
        return xxlJobSpringExecutor;
    }
}
