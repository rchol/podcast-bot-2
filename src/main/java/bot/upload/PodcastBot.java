package bot.upload;

import bot.sql.Repo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class PodcastBot extends TelegramLongPollingBot{
    private final Repo repo;
    @Value("${bot.name}")
    private static String BOT_USERNAME;
    @Value("${bot.token}")
    private static String BOT_TOKEN;
    @Value("${bot.channel-id}")
    private static String CHANEL_ID;

    public PodcastBot(Repo repo) {
        this.repo = repo;
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
