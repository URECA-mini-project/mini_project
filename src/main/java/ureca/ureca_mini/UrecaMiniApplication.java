package ureca.ureca_mini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//임시 어노테이션 (DB 연결 후 수정)
@SpringBootApplication
public class UrecaMiniApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrecaMiniApplication.class, args);
	}

}
