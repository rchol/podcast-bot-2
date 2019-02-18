package bot.sql;

import bot.download.model.RSSChannel;
import bot.download.model.RSSMessage;
import bot.upload.model.TelegramMessage;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class H2Repo implements Repo {

    private final DataSource ds;

    @Autowired
    public H2Repo(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void addMessage(RSSMessage message) {

    }

    @Override
    public boolean isPosted(String guid) {
        return false;
    }

    @Override
    public void addChannel(String url, List<String> hashtagsList) {

    }

    @Override
    public void addChannel(String url) {

    }

    @Override
    public long updateProcessed(String guid) {
        return 0;
    }

    @Override
    public String getChannelTags(String url) {
        return null;
    }

    @Override
    public List<TelegramMessage> getUnprocessedPosts() {
        //TODO FIX IT OMG
        List<TelegramMessage> posts = new ArrayList<>();
        if (!newMsgs.isEmpty()) {
            newMsgs.forEach(msg -> {
                TelegramMessage tgMsg = new TelegramMessage();
                tgMsg.setTitle(msg.getTitle())
                    .setDescription(msg.getDescription())
                    .setLink(msg.getLink(), "SOURCE")
                    .setHashtags(msg.getHashtags())
                    .setMp3link(msg.getEnclosure());
                tgMsg.setGuid(msg.getGuid());
                posts.add(tgMsg);
            });
        }
        return posts;
    }

    @Override
    public List<RSSChannel> getAllChannels() {
        return null;
    }
}
