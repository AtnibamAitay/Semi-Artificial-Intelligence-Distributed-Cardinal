package space.atnibam.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import space.atnibam.common.swagger.annotation.EnableCustomSwagger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCustomSwagger
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
