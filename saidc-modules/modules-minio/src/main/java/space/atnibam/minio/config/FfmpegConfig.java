package space.atnibam.minio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

/**
 * @ClassName: FfmpegConfig
 * @Description: FFmpeg配置类，用于加载和管理与FFmpeg相关的配置项
 * @Author: AtnibamAitay
 * @CreateTime: 2023-10-23 01:04
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "ffmpeg")
public class FfmpegConfig {
    
    /**
     * FFmpeg的路径
     */
    private String path;
}
