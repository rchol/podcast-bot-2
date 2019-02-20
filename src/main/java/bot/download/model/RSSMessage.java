package bot.download.model;

import java.util.Date;
import java.util.List;

public class RSSMessage {

    private final String guid;
    private final String channelName;
    private final String title;
    private final String enclosure;
    private final String link;

    private final Date pubDate;

    private final List<String> hashtags;
    private final String description;

    public RSSMessage(String guid, String title, String channelName, String enclosure, String description, String link,
        List<String> hashtags, Date pubDate) {
        this.guid = guid;
        this.channelName = channelName;
        this.title = title;
        this.enclosure = enclosure;
        this.description = description;
        this.hashtags = hashtags;
        this.link = link;
        this.pubDate = pubDate;

    }

    public String getTitle() {
        return title;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public String getGuid() {
        return guid;
    }


    @Override
    public String toString() {
        return "RSSMsg{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", enclosure='" + enclosure + '\'' +
            ", channelName='" + channelName + '\'' +
            ", guid='" + guid + '\'' +
            '}';
    }
}
