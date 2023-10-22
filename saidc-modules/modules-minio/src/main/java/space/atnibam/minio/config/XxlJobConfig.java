package space.atnibam.minio.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: XxlJobConfig
 * @Description: XXL-JOB的配置类，用于初始化并配置xxl-job执行器
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-22 15:43
 **/
@Configuration
public class XxlJobConfig {

    /**
     * 日志对象
     */
    private Logger logger = LoggerFactory.getLogger(XxlJobConfig.class);

    /**
     * xxl-job admin 的地址
     */
    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    /**
     * 访问xxl-job admin的token
     */
    @Value("${xxl.job.accessToken}")
    private String accessToken;

    /**
     * 执行器应用名称
     */
    @Value("${xxl.job.executor.appName}")
    private String appName;

    /**
     * 执行器地址
     */
    @Value("${xxl.job.executor.address}")
    private String address;

    /**
     * 执行器IP
     */
    @Value("${xxl.job.executor.ip}")
    private String ip;

    /**
     * 执行器端口
     */
    @Value("${xxl.job.executor.port}")
    private int port;

    /**
     * 执行器日志路径
     */
    @Value("${xxl.job.executor.logPath}")
    private String logPath;

    /**
     * 执行器日志保留天数
     */
    @Value("${xxl.job.executor.logRetentionDays}")
    private int logRetentionDays;

    /**
     * 创建并初始化xxl-job执行器
     *
     * @return 返回创建好的xxl-job执行器
     */
    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }

}