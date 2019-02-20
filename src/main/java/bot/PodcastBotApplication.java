package bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
public class PodcastBotApplication{

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(PodcastBotApplication.class, args);
    }

}
