package com.viger.plugin;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsFeeder {
    volatile static NewsFeeder INSTANCE = new NewsFeeder();
    final Pattern IMG_PATTERN = Pattern.compile(
            "<img.*?src=\"(.*?)\".*?>",
            Pattern.CASE_INSENSITIVE);
    final HashMap<String, Feed> feeds;
    final HashMap<String, String> timestamp;


    private NewsFeeder() {
        this.feeds = new HashMap<>();
        this.timestamp = new HashMap<>();
        subscribe("https://rsshub.viger.xyz/bilibili/user/dynamic/401742377");
    }

    public static NewsFeeder getInstance() {
        if (INSTANCE == null) {
            synchronized (NewsFeeder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NewsFeeder();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * 添加rss到订阅
     *
     * @param url 订阅地址
     */
    public String subscribe(String url) {
        try {
            feeds.put(url, getFeed(url));
            timestamp.put(url, feeds.get(url).getGuid());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return feeds.get(url).title;
    }

    /**
     * 检查是否有未读文章
     *
     * @return 是否有未读文章
     */
    boolean unread() {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        feeds.forEach((String url, Feed last) -> {
            Feed feed = null;
            try {
                feed = getFeed(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (last == null) {
                if (feed != null) {
                    feed.getMessages().removeAll(feed.getMessages().subList(1, feed.getMessages().size()));
                    System.out.print(feed.getTitle() + "\n无更新内容");
                    feeds.put(url, feed);
                }
            } else if (feed != null && feed.getGuid().equals(last.getGuid())) {
                System.out.print(feed.getTitle() + "\n无更新内容");
            } else {
                feeds.put(url, feed);
                System.out.print(feed.getMessages().get(0) + "\n检查到更新");
                count.getAndSet(count.get() + 1);
            } // 检查是否有更新});
        });
        return count.get() != 0;
    }


    /**
     * 更新地址
     *
     * @param url 订阅地址
     * @return Feed对象
     * @throws IOException
     */
    Feed getFeed(String url) throws IOException {
        Document doc;
        Feed feed = null;
        Element root;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url);
            String title, link, description, language, guid, pubdate;
            root = ((Element) ((Element) doc.getElementsByTagName("rss").item(0))
                    .getElementsByTagName("channel").item(0));
            title = root.getElementsByTagName("title").item(0).getTextContent();
            link = root.getElementsByTagName("link").item(0).getTextContent();
            language = root.getElementsByTagName("language").item(0).getTextContent();
            description = root.getElementsByTagName("description").item(0).getTextContent();
            feed = new Feed(title, link, description, language);


            NodeList feedsList = root.getElementsByTagName("item");
            Element feedElement;

            for (int i = 0; i < feedsList.getLength(); i++) {
                feedElement = ((Element) feedsList.item(i));
                guid = feedElement.getElementsByTagName("guid").item(0).getTextContent();
                guid = guid.replace("https://t.bilibili.com/", "");
                title = feedElement.getElementsByTagName("title").item(0).getTextContent();
                description = feedElement.getElementsByTagName("description").item(0).getTextContent();
                link = feedElement.getElementsByTagName("link").item(0).getTextContent();
                pubdate = feedElement.getElementsByTagName("pubDate").item(0).getTextContent();
                feed.getMessages().add(new FeedMessage(title, link, description, guid, pubdate));
            } // 填充rss消息

        } catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return feed;
    }

    /**
     * @param contact 要发送的对象
     * @return 最近一条文章
     * @throws MalformedURLException .
     */
    public Message last(Contact contact) throws IOException {
        MessageChainBuilder message = new MessageChainBuilder();
        feeds.forEach((String url, Feed feed) -> {
            try {
                message.add(feed.title + "更新:\n");
                message.add(getSingleMessages(contact, feed.getMessages().get(0)).asMessageChain());
                message.add("/n/n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            timestamp.put(url, feed.getMessages().get(0).getGuid());
        });
        return message.asMessageChain();
    }

    /**
     * 获取最新的文章们
     *
     * @param contact 要发送的对象
     * @return 未读文章消息化
     */
    LinkedList<MessageChain> fetch(Contact contact) throws IOException {
        final LinkedList<MessageChain> result = new LinkedList<>();
        feeds.forEach((String url, Feed last) -> {
            MessageChainBuilder message = new MessageChainBuilder();
            for (FeedMessage feed : last.getMessages()) {
                if (Long.parseLong(feed.getGuid()) <= Long.parseLong(timestamp.get(url))) {
                    break;
                } else {
                    try {
                        message.add(last.title + "更新:\n");
                        message = getSingleMessages(contact, feed);
                        message.add("/n/n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    result.add(message.asMessageChain());
                }
            }
            timestamp.put(url, last.getGuid());
        });
        return result;
    }

    /**
     * @param contact 要发送的对象
     * @param feed    单条文章
     * @return 消息化文章
     * @throws MalformedURLException .
     */
    @NotNull
    private MessageChainBuilder getSingleMessages(Contact contact, FeedMessage feed) throws IOException {
        MessageChainBuilder message;
        String text;
        LinkedList<Image> img;
        message = new MessageChainBuilder();
        text = feed.getDescription();
        img = getImage(feed.getDescription(), contact);
        text = text.replaceAll("<img.*?src=\".*?\".*?>", "&flagImg");
        text = text.replaceAll("<br>", "\n");
        text = text.replaceAll("<.*>", "");
        if (!img.isEmpty()) {
            for (String s : text.split("&flagImg")) {
                message.add(s);
                message.add(img.pop());
            }
        }

        message.addAll(img);
        message.add(feed.getLink());
        return message;
    }

    /**
     * @param description 动态的html文本
     * @return 动态中的图片
     */
    private LinkedList<Image> getImage(String description, Contact contact) throws IOException {
        LinkedList<Image> img = new LinkedList<>();
        Matcher IMG_MATCHER = IMG_PATTERN.matcher(description);
        while (IMG_MATCHER.find()) {
            HttpURLConnection conn = (HttpURLConnection) (new URL(IMG_MATCHER.group(1))).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-agent", "Mozilla/4.0");
            InputStream inStream = conn.getInputStream();
            img.add(Contact.uploadImage(contact, inStream));
        }
        return img;
    }
}


