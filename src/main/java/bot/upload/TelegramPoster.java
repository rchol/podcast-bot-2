package bot.upload;

import bot.sql.Repo;
import bot.upload.model.TelegramMessage;
import bot.upload.utils.AudioPreparator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramPoster {
    private static final String BOT_USERNAME = "pr";
    private static final String BOT_TOKEN = "";
    private static final String CHANEL_ID = "@";

    private final Repo repo;
    private PodcastBot bot;

    @Autowired
    public TelegramPoster(Repo repo, PodcastBot bot) {
        this.repo = repo;
        this.bot = bot;
    }

    @Scheduled(initialDelay = 2000L, fixedDelay = 60_000L)
    public void postNew() {
        List<TelegramMessage> posts = repo.getUnprocessedPosts();
        posts.forEach(post -> {
            int replyId = sendMessage(post.buildText());
            sendAudio(post, replyId);
            repo.updateProcessed(post.getGuid());
        });
    }

    private int sendMessage(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(CHANEL_ID);
        sendMessage.disableWebPagePreview();
        sendMessage.setParseMode("HTML");
        sendMessage.setText(text);
        try {
            Message sent = bot.execute(sendMessage);
            return sent.getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAudio(TelegramMessage post, int replyId) {
        AudioPreparator preparator = new AudioPreparator();
        String audioFile = preparator.retrive(post.getMp3link());
        List<String> filesToSend = preparator.splitAudio(audioFile, 49);
        AtomicInteger idx = new AtomicInteger(1);
        filesToSend.forEach(filename -> {
            File file = new File(filename);
            try (FileInputStream ain = new FileInputStream(file)) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(CHANEL_ID);
                String audioName = String.format("%s - %2d", post.getTitle(), idx.getAndIncrement());
                sendAudio.setAudio(audioName, ain);
                sendAudio.setCaption(audioName);
                sendAudio.setReplyToMessageId(replyId);
                bot.execute(sendAudio);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
