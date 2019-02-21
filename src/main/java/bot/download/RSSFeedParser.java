package bot.download;

import bot.download.model.RSSChannel;
import bot.download.model.RSSMessage;
import bot.sql.Repo;
import com.rometools.modules.itunes.EntryInformation;
import com.rometools.modules.itunes.FeedInformationImpl;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class RSSFeedParser {
    private static final String ITUNES = "http://www.itunes.com/dtds/podcast-1.0.dtd";

    private final Repo repo;
    private final SyndFeed feed;
    private final RSSChannel channel;

    public RSSFeedParser(RSSChannel channel, Repo repo) {
        try {
            this.repo = repo;
            URL url = new URL(channel.getUrlFeed());
            SyndFeedInput in = new SyndFeedInput();
            feed = in.build(new XmlReader(url));
            Module itunesModule = feed.getModule(ITUNES);
            FeedInformationImpl infoTunes = (FeedInformationImpl) itunesModule;
            RSSChannelBuilder builder = new RSSChannelBuilder(channel);
            channel = builder.setTitle(feed.getTitle())
                .addHashtag(infoTunes.getKeywords())
                .addHashtag(infoTunes.getAuthor())
                .addHashtag(repo.getChannelTags(url.toString()).split("\\s+")).build();
            this.channel = channel;
        } catch (FeedException e) {
            repo.removeChannel(channel.getUrlFeed());
            throw new RuntimeException("Not a valid RSS Feed. URL was removed",e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RSSMessage> readFeed() {
        List<RSSMessage> newMessages = new ArrayList<>();
        List<SyndEntry> entries = feed.getEntries();
        for (SyndEntry entry : entries) {
            String htmlDesc = entry.getDescription().getValue();
            Document document = Jsoup.parse(htmlDesc);
            String text = document.text();
            String mediaLink = null;
            Optional<SyndEnclosure> encl = entry.getEnclosures().stream().findFirst();
            if (encl.isPresent()){
                mediaLink = encl.get().getUrl();
            }
            if (repo.isNew(entry.getUri())) {
                newMessages.add(new RSSMessage(entry.getUri(),
                    entry.getTitle(),
                    channel.getTitle(),
                    mediaLink,
                    text,
                    entry.getLink(),
                    genHashtags(entry),
                    entry.getPublishedDate()));
            }
        }
        return newMessages;
    }

    private List<String> genHashtags(SyndEntry entry){
        Module itunes = entry.getModule(ITUNES);
        EntryInformation info = (EntryInformation) itunes;
        List<String> hastagsMsg = new ArrayList<>(Arrays.asList(info.getKeywords()));
        hastagsMsg.addAll(channel.getHashtags());
        return hastagsMsg;
    }


   public enum PostStatus {
        NEW, PROCESSING, POSTED, FAILED
    }
}
