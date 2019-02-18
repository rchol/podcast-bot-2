package bot.download;

import bot.download.model.RSSChannel;
import bot.download.model.RSSMessage;
import bot.sql.Repo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RSSService {
    private Repo repo;

    @Autowired
    public RSSService(Repo repo) {
        this.repo = repo;
    }

    @Scheduled
    public void addPost() {
        List<RSSChannel> channels = repo.getAllChannels();
        List<RSSMessage> newMsgs = new ArrayList<>();
        channels.stream().map(channel -> new RSSFeedParser(channel, repo)).map(RSSFeedParser::readFeed)
            .forEach(newMsg -> newMsg.ifPresent(repo::addMessage));
    }
}
