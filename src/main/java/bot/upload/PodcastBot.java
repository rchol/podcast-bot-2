package bot.upload;

import bot.sql.Repo;
import com.google.common.collect.ImmutableMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Service
public class PodcastBot extends TelegramLongPollingBot {

    private final Repo repo;
    private final TelegramBotsApi botApi;
    @Value("${bot.name}")
    private String BOT_USERNAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;
    @Value("${bot.channel-id}")
    private String CHANEL_ID;
    @Value("${bot.admin}")
    private long ADMIN_ID;

    @Autowired
    public PodcastBot(Repo repo, TelegramBotsApi botApi) {
        this.repo = repo;
        this.botApi = botApi;
    }

    @PostConstruct
    public void registerBot() {
        try {
            botApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            throw new RuntimeException("Can't register bot", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage().getText().isEmpty()) {
            return;
        }
        Message msg = update.getMessage();
        if (!msg.isCommand()) {
            return;
        }
        if (!(msg.getFrom().getId() == ADMIN_ID)) {
            return;
        }

        List<MessageEntity> entities = msg.getEntities();
        if (entities.size() < 1) {
            return;
        }

        MessageEntity firstEntity = entities.get(0);
        if (!EntityType.BOTCOMMAND.equals(firstEntity.getType())) {
            // send help
            return;
        }

        Optional<Function<List<MessageEntity>, Function<Repo, Consumer<AbsSender>>>> procesorO = Optional
            .ofNullable(Processor.processors.get(firstEntity.getText()));

        if (!procesorO.isPresent()) {
            // unknown command
            return;
        } else {
            procesorO.get().apply(entities).apply(repo).accept(this);
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

    static class Processor {

        private static final Map<String, Function<List<MessageEntity>, Function<Repo, Consumer<AbsSender>>>> processors = ImmutableMap
            .of(
                "/add", (me) -> (repo) -> (bot) -> {
                    try {
                        if (me.size() < 2) {
                            return;
                        }
                        String url = me.get(1).getText();
                        // Это очень простая проверка на валидность урла!
                        URL urlValid = new URL(url);
                            repo.addChannel(url);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException("Not a valid URL", e);
                    }
                }
            );


    }
}
