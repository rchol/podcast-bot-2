package bot.upload.model;

import java.util.ArrayList;
import java.util.List;

public class TelegramMessage {

    private String title;
    private String guid;
    private String description;
    private List<String> tags = new ArrayList<>();
    private String link;
    private String linkDesc;
    private String mp3link;

    public String buildText() {
        StringBuilder hashBuilder = new StringBuilder();
        tags.forEach(tag -> hashBuilder.append("&#35").append(tag).append(" "));
        StringBuilder sb = new StringBuilder()
            .append(title)
            .append("\n\n")
            .append(description)
            .append("\n\n")
            .append(String.format("<a href=\"%s\">%s</a>", link, linkDesc))
            .append("\n\n")
            .append(hashBuilder.toString().toLowerCase());

        return sb.toString();
    }


    public TelegramMessage setLink(String link, String description) {
        this.link = link;
        this.linkDesc = description.isEmpty() ? "SOURCE" : description;
        return this;
    }

    public TelegramMessage setTitle(String title) {
        this.title = title;
        return this;
    }

    public TelegramMessage setDescription(String description) {
        this.description = description;
        return this;
    }

    public TelegramMessage setHashtags(List<String> tags) {
        this.tags.addAll(tags);
        return this;
    }

    public TelegramMessage addHashtag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public String getMp3link() {
        return mp3link;
    }

    public TelegramMessage setMp3link(String mp3link) {
        this.mp3link = mp3link;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}

