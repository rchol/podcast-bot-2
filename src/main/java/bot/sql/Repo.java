package bot.sql;

import bot.download.RSSFeedParser.PostProcessing;
import bot.download.model.RSSChannel;
import bot.download.model.RSSMessage;
import bot.upload.model.TelegramMessage;
import java.util.List;

public interface Repo {
    void addMessage(RSSMessage message);

    boolean isPosted(String guid);

    void addChannel(String url, List<String> hashtagsList);

    void addChannel(String url);

    long updateProcessed(String guid, PostProcessing status);

    String getChannelTags(String url);

    List<TelegramMessage> getUnprocessedPosts();

    List<RSSChannel> getAllChannels();
}
