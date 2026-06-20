package ms_hilos.ms_hilos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- Importación

@SpringBootApplication
@EnableFeignClients 
public class MsHilosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsHilosApplication.class, args);
	}
}