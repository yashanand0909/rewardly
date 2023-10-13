import com.bloodDonation.view.Homepage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.bloodDonation.*")
public class Application {

  public static void main(String[] args) {

    new Homepage();
    SpringApplication.run(Application.class, args);
  }
}
