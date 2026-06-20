package foro.ms_reacciones; 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsReaccionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsReaccionesApplication.class, args);
    }
}
