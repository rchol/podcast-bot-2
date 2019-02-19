package bot.upload;

import bot.sql.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Configuration
public class PodcastBotInit {
    private final Repo repo;

    @Autowired
    public PodcastBotInit(Repo repo) {
        this.repo = repo;
    }

    @Bean
    public PodcastBot initBot(){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        PodcastBot bot = new PodcastBot(repo);
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            throw new RuntimeException(e);
        }
        return bot;
    }
}
