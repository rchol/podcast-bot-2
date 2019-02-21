package bot.upload;

import bot.download.RSSFeedParser.PostStatus;
import bot.sql.Repo;
import bot.upload.model.TelegramMessage;
import bot.upload.utils.AudioPreparator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramPoster {
    @Value("${bot.channel-id}")
    private String CHANEL_ID;

    private final Repo repo;
    private PodcastBot bot;

    @Autowired
    public TelegramPoster(Repo repo, PodcastBot bot) {
        this.repo = repo;
        this.bot = bot;
    }

    @Scheduled(initialDelay = 20_000L, fixedDelay = 60_000L)
    public void postNew() {
        List<TelegramMessage> posts = repo.getUnprocessedPosts();
        posts.forEach(post -> {
            repo.updateProcessed(post.getGuid(), PostStatus.PROCESSING);
            int replyId = sendAudio(post);
            sendMessage(post.buildText(), replyId);
            repo.updateProcessed(post.getGuid(), PostStatus.POSTED);
        });
    }

    private void sendMessage(String text, int replyId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(false);
        sendMessage.setChatId(CHANEL_ID);
        sendMessage.disableWebPagePreview();
        sendMessage.setParseMode("HTML");
        sendMessage.setText(text);
        sendMessage.setReplyToMessageId(replyId);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private int sendAudio(TelegramMessage post) {
        AudioPreparator preparator = new AudioPreparator();
        String audioFile = preparator.retrive(post.getMp3link());
        List<String> filesToSend = preparator.splitAudio(audioFile, 49);
        AtomicInteger idx = new AtomicInteger(1);
        int replyId = -1;
        for (String filename : filesToSend) {
            File file = new File(filename);
            try (FileInputStream ain = new FileInputStream(file)) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(CHANEL_ID);
                String audioName = String.format("%s - %2d", post.getTitle(), idx.getAndIncrement());
                sendAudio.setAudio(audioName, ain);
                sendAudio.setCaption(audioName);
                Message msg = bot.execute(sendAudio);
                if (replyId == -1) replyId = msg.getMessageId();
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        return replyId;
    }
}
