package ureca.ureca_mini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class UrecaMiniApplication {
	public static void main(String[] args) {
		SpringApplication.run(UrecaMiniApplication.class, args);
	}
}