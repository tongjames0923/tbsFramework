package tbs.framework.xxl.config;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import tbs.framework.log.ILogger;
import tbs.framework.log.annotations.AutoLogger;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;
import tbs.framework.xxl.interfaces.impl.DefaultXxlJobExecutor;

public class XxlJobConfig {

    @AutoLogger
    private ILogger logger;

    public XxlJobConfig() {

    }

    @Bean
    DefaultXxlJobExecutor executor(final ApplicationContext application) {
        return new DefaultXxlJobExecutor(application);
    }

    @Bean
    @ConditionalOnBean(IXXLJobsConfig.class)
    public XxlJobSpringExecutor xxlJobSpringExecutor(final IXXLJobsConfig config) {
        final XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        if (StrUtil.isEmpty(config.adminAddress())) {
            throw new RuntimeException("管理器地址错误");
        }
        xxlJobSpringExecutor.setAdminAddresses(config.adminAddress());
        this.logger.info(String.format("xxl job admin server: %s", config.adminAddress()));
        if (!StrUtil.isEmpty(config.appName())) {
            this.logger.info(String.format("app name: %s", config.appName()));
            xxlJobSpringExecutor.setAppname(config.appName());
        }
        if (!StrUtil.isEmpty(config.address())) {
            this.logger.info(String.format("address: %s", config.address()));
            xxlJobSpringExecutor.setAddress(config.address());
        }
        if (!StrUtil.isEmpty(config.ip())) {
            this.logger.info(String.format("ip: %s", config.ip()));
            xxlJobSpringExecutor.setIp(config.ip());
        }
        if (null != config.port()) {
            this.logger.info(String.format("port: %s", config.port()));
            xxlJobSpringExecutor.setPort(config.port());
        }
        if (!StrUtil.isEmpty(config.accessToken())) {
            this.logger.info(String.format("access token: %s", config.accessToken()));
            xxlJobSpringExecutor.setAccessToken(config.accessToken());
        }
        if (!StrUtil.isEmpty(config.logPath())) {
            this.logger.info(String.format("log path: %s", config.logPath()));
            xxlJobSpringExecutor.setLogPath(config.logPath());
        }
        if (null != config.logRetentionsDays()) {
            this.logger.info(String.format("logRetentionsDays: %s", config.logRetentionsDays()));
            xxlJobSpringExecutor.setLogRetentionDays(config.logRetentionsDays());
        }
        return xxlJobSpringExecutor;
    }
}
