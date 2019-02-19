package bot.upload;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class PodcastBot extends TelegramLongPollingBot{
    @Value("${bot.name}")
    private static final String BOT_USERNAME;
    @Value("${bot.token}")
    private static final String BOT_TOKEN;
    @Value("${bot.channel-id}")
    private static final String CHANEL_ID;

    @Override
    public void onUpdateReceived(Update update) {
        String[] text = update.getMessage().getText().split("\\s+");


    }
    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
    public void
}
