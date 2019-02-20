package bot.upload;

import bot.sql.Repo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Service
public class PodcastBot extends TelegramLongPollingBot{
    private final Repo repo;
    private final TelegramBotsApi botApi;
    @Value("${bot.name}")
    private String BOT_USERNAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;
    @Value("${bot.channel-id}")
    private String CHANEL_ID;

    @Autowired
    public PodcastBot(Repo repo, TelegramBotsApi botApi) {
        this.repo = repo;
        this.botApi = botApi;
    }
    @PostConstruct
    public void registerBot(){
        try {
            botApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            throw new RuntimeException("Can't register bot", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.getMessage().getText().isEmpty()){
            return;
        }
        String[] text = update.getMessage().getText().split("\\s+");
        List<String> textList = new LinkedList<>(Arrays.asList(text));
        if (text[0].equals("/add")) {
            if (textList.size() < 2) {
                return;
            }
            String url = textList.get(1);
            if (text.length > 2) {
                try {
                    // Это очень простая проверка на валидность урла!
                    URL urlValid = new URL(url);
                    List<String> params = new ArrayList<>();
                    if (params.isEmpty()) {
                        repo.addChannel(url);
                    } else {
                        repo.addChannel(url, params);
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException("Not a valid URL", e);
                }


            }
        }

    }
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
