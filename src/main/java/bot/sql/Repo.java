package bot.sql;

import bot.download.RSSFeedParser.PostStatus;
import bot.download.model.RSSChannel;
import bot.download.model.RSSMessage;
import bot.upload.model.TelegramMessage;
import java.util.List;

public interface Repo {
    void addMessage(RSSMessage message);

    boolean isNew(String guid);

    void addChannel(String url, List<String> hashtagsList);

    void addChannel(String url);

    long updateProcessed(String guid, PostStatus status);

    void removeChannel(String url);

    String getChannelTags(String url);

    List<TelegramMessage> getUnprocessedPosts();

    List<RSSChannel> getAllChannels();
}
