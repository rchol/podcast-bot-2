package bot.download.model;

import java.util.List;

public class RSSChannel {
    private String urlFeed;
    private List<String> hashtags;
    private String title;

    public RSSChannel(String urlFeed, List<String> hashtags) {
        this.urlFeed = urlFeed;
        this.hashtags = hashtags;
    }

    public RSSChannel(String title, String urlFeed, List<String> hashtags) {
        this.title = title;
        this.urlFeed = urlFeed;
        this.hashtags = hashtags;
    }


    public String getUrlFeed() {
        return urlFeed;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getHashtags() {
        return hashtags;
    }
}
