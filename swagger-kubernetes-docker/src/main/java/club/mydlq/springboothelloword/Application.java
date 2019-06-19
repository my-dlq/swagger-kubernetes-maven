package club.mydlq.springboothelloword;

import org.springframework.boot.SpringApplication;
import club.mydlq.swagger.kubernetes.EnableSwaggerKubernetes;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSwaggerKubernetes
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
