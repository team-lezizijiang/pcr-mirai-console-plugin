package com.viger.plugin;

import java.util.ArrayList;
import java.util.List;

public class Feed {
    final String title;
    final String link;
    final String description;
    final String language;

    final List<FeedMessage> entries = new ArrayList<FeedMessage>();

    public Feed(String title, String link, String description, String language) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
    }

    public List<FeedMessage> getMessages() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }


    @Override
    public String toString() {
        return "Feed [" + ", description=" + description
                + ", language=" + language + ", link=" + link + ", title=" + title + "]";
    }

}


/*
 * Represents one RSS message
 */
class FeedMessage {

    String title;
    String description;
    String link;
    String guid;
    String pubDate;

    public FeedMessage(String title, String link, String description, String guid, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.guid = guid;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description
                + ", link=" + link + ", guid=" + guid
                + ", pubDate=" + pubDate + "]";
    }

}