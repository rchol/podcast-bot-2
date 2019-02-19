package bot.download;

import bot.download.model.RSSChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RSSChannelBuilder {

    private RSSChannel oldChannel;

    private String urlFeed;

    private List<String> hashtag;
    private String title;

    public RSSChannelBuilder() {
        this.hashtag = new ArrayList<>();
    }

    public RSSChannelBuilder(RSSChannel oldChannel) {
        this.oldChannel = oldChannel;
    }

    public RSSChannel build() {
        return title.isEmpty() ? new RSSChannel(urlFeed, hashtag) : new RSSChannel(title, urlFeed, hashtag);
    }

    public RSSChannelBuilder addHashtag(String newHashtag) {
        String[] hashtags = newHashtag.split(",");
        Arrays.stream(hashtags).forEach(tag -> {
            tag = tag.replaceAll("[;/:*?\"<>|&' ]", "_");
            tag = tag.replaceAll("_{2,}", "_");
            this.hashtag.add(tag.toLowerCase());
        });
        return this;
    }

    public RSSChannelBuilder addHashtag(String[] tags) {
        Arrays.stream(tags).forEach(this::addHashtag);
        return this;
    }

    public RSSChannelBuilder setUrl(String url) {
        this.urlFeed = url;
        return this;
    }

    public RSSChannelBuilder setHashtag(String[] hashtags) {
        this.hashtag = new ArrayList<>(Arrays.asList(hashtags));
        return this;
    }

    public RSSChannelBuilder setHashtag(List<String> hashtag) {
        this.hashtag = hashtag;
        return this;
    }

    public RSSChannelBuilder setTitle(String title) {
        this.title = title;
        return this;
    }


}
