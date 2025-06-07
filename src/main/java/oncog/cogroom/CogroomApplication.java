package oncog.cogroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class CogroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(CogroomApplication.class, args);
	}
}
