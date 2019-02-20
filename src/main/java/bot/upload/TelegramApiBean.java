package bot.upload;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@Configuration
public class TelegramApiBean {
    @Bean
    public TelegramBotsApi initBotApi(){
        return new TelegramBotsApi();
    }

}
