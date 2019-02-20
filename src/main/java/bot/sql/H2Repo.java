package bot.sql;

import bot.download.RSSChannelBuilder;
import bot.download.RSSFeedParser.PostStatus;
import bot.download.model.RSSChannel;
import bot.download.model.RSSMessage;
import bot.upload.model.TelegramMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class H2Repo implements Repo {

    private final SimpleDataSource ds;

    @Autowired
    public H2Repo(SimpleDataSource ds) {
        this.ds = ds;
    }

    @PostConstruct
    public void initTables() {
        ds.executeUpdate("CREATE TABLE IF NOT EXISTS posts (\n"
            + " id int AUTO_INCREMENT PRIMARY KEY NOT NULL,\n"
            + " guid varchar(32) NOT NULL,\n"
            + " title varchar(150),\n"
            + " link varchar(200),\n"
            + " link_tag varchar(50),\n"
            + "  enclosure varchar(200),\n"
            + " description varchar(2000),\n"
            + "publish_date long,"
            + " hashtags varchar(200), status varchar(10) NOT NULL);\n"
            + "CREATE TABLE IF NOT EXISTS channels\n"
            + "(id int AUTO_INCREMENT PRIMARY KEY NOT NULL,\n"
            + "    feed_url varchar(200) NOT NULL\n,"
            + "     hashtags varchar(200) DEFAULT ''"
            + ");\n"
            + "CREATE UNIQUE INDEX IF NOT EXISTS channels_feed_url_uindex ON channels (feed_url)"
        );
    }

    @Override
    public void addMessage(RSSMessage message) {
        String guid = message.getGuid();
        String title = message.getTitle();
        String link = message.getLink();
        String linkTag = message.getChannelName();
        String enclosure = message.getEnclosure();
        String description = message.getDescription();
        long publishDateMillis = message.getPubDate().getTime();
        StringBuilder hashtags = new StringBuilder();
        message.getHashtags().forEach(tag -> hashtags.append(tag).append(" "));

        ds.executeUpdate(
            "INSERT INTO posts (guid, title, link, link_tag, enclosure, description, publish_date, hashtags, status) VALUES"
                + "(?,?,?,?,?,?,?,?,?)", guid, title, link, linkTag, enclosure, description, publishDateMillis
            ,hashtags.toString(), PostStatus.NEW.name());
    }

    @Override
    public boolean isNew(String guid) {
        ResultSet rs = ds.executeQuery("SELECT status FROM posts WHERE guid='" + guid + "'");
        try {
            return !rs.first();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addChannel(String url, List<String> hashtagsList) {
        StringBuilder hashtags = new StringBuilder();
        hashtagsList.forEach(tag -> hashtags.append(tag).append(" "));
        ds.executeUpdate("INSERT INTO channels (feed_url, hashtags) VALUES"
            + "(?,?)", url, hashtags.toString());
    }

    @Override
    public void addChannel(String url) {
        ds.executeUpdate("INSERT INTO channels (feed_url) VALUES"
            + "(?)", url);
    }

    @Override
    public long updateProcessed(String guid, PostStatus status) {
        return ds.executeUpdate("UPDATE posts SET status='" + status.name() + "' WHERE guid='" + guid + "'");
    }

    @Override
    public String getChannelTags(String url) {
        try {
            ResultSet rs = ds.executeQuery("SELECT hashtags FROM channels WHERE feed_url ='" + url + "'");
            if (rs.first()) {
                return rs.getString("hashtags");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    @Override
    public List<TelegramMessage> getUnprocessedPosts() {
        try {
            ResultSet rs = ds.executeQuery("SELECT * FROM posts WHERE status ='" + PostStatus.NEW.name() + "' ORDER BY publish_date");
            List<TelegramMessage> posts = new ArrayList<>();
            while (rs.next()) {
                List<String> hashtags = new ArrayList<>(Arrays.asList(rs.getString("hashtags").split("\\s+")));
                TelegramMessage tgMsg = new TelegramMessage();
                tgMsg.setTitle(rs.getString("title"))
                    .setDescription(rs.getString("description"))
                    .setLink(rs.getString("link"), rs.getString("link_tag"))
                    .setHashtags(hashtags)
                    .setMp3link(rs.getString("enclosure"));
                tgMsg.setGuid(rs.getString("guid"));
                posts.add(tgMsg);
            }
            return posts;
        } catch (SQLException se) {
            throw new RuntimeException(se);
        }
    }

    @Override
    public List<RSSChannel> getAllChannels() {
        List<RSSChannel> channels = new ArrayList<>();
        try {
            ResultSet rs = ds.executeQuery("SELECT feed_url, hashtags FROM channels");
            while (rs.next()) {
                String url = rs.getString("feed_url");
                String hashtags = rs.getString("hashtags");
                RSSChannelBuilder builder = new RSSChannelBuilder();
                builder.setUrl(url).addHashtag(hashtags);
                channels.add(builder.build());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return channels;
    }
}
