package space.atnibam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import space.atnibam.common.swagger.annotation.EnableCustomSwagger;

@SpringBootApplication
@EnableCustomSwagger
@MapperScan("space.atnibam.minio.mapper")
public class MinioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MinioApplication.class, args);
    }
}
