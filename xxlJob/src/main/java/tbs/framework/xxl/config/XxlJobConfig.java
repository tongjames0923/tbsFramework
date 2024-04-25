package tbs.framework.xxl.config;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import tbs.framework.base.log.ILogger;
import tbs.framework.base.utils.LogUtil;
import tbs.framework.xxl.interfaces.IXXLJobsConfig;
import tbs.framework.xxl.interfaces.impl.DefaultXxlJobExecutor;

public class XxlJobConfig {

    private final ILogger logger;

    public XxlJobConfig(LogUtil logUtil) {
        logger = logUtil.getLogger(XxlJobConfig.class.getName());
    }

    @Bean
    DefaultXxlJobExecutor executor(ApplicationContext application, LogUtil logUtil) {
        return new DefaultXxlJobExecutor(application, logUtil);
    }

    @Bean
    @ConditionalOnBean(IXXLJobsConfig.class)
    public XxlJobSpringExecutor xxlJobSpringExecutor(IXXLJobsConfig config) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        if (StrUtil.isEmpty(config.adminAddress())) {
            throw new RuntimeException("管理器地址错误");
        }
        xxlJobSpringExecutor.setAdminAddresses(config.adminAddress());
        logger.info(String.format("xxl job admin server: %s", config.adminAddress()));
        if (!StrUtil.isEmpty(config.appName())) {
            logger.info(String.format("app name: %s", config.appName()));
            xxlJobSpringExecutor.setAppname(config.appName());
        }
        if (!StrUtil.isEmpty(config.address())) {
            logger.info(String.format("address: %s", config.address()));
            xxlJobSpringExecutor.setAddress(config.address());
        }
        if (!StrUtil.isEmpty(config.ip())) {
            logger.info(String.format("ip: %s", config.ip()));
            xxlJobSpringExecutor.setIp(config.ip());
        }
        if (null != config.port()) {
            logger.info(String.format("port: %s", config.port()));
            xxlJobSpringExecutor.setPort(config.port());
        }
        if (!StrUtil.isEmpty(config.accessToken())) {
            logger.info(String.format("access token: %s", config.accessToken()));
            xxlJobSpringExecutor.setAccessToken(config.accessToken());
        }
        if (!StrUtil.isEmpty(config.logPath())) {
            logger.info(String.format("log path: %s", config.logPath()));
            xxlJobSpringExecutor.setLogPath(config.logPath());
        }
        if (null != config.logRetentionsDays()) {
            logger.info(String.format("logRetentionsDays: %s", config.logRetentionsDays()));
            xxlJobSpringExecutor.setLogRetentionDays(config.logRetentionsDays());
        }
        return xxlJobSpringExecutor;
    }
}
