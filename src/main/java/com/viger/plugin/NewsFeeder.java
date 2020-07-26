package com.viger.plugin;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsFeeder {
    private final String url = "https://rsshub.viger.xyz/bilibili/user/dynamic/353840826";
    Feed last; // ��һ�θ�������
    private String timestamp; // ���һƪ�Ѷ����µ�ʱ���


    public NewsFeeder() {
        last = null;
        timestamp = "0";
    }


    /**
     * ����Ƿ���δ������
     *
     * @return �Ƿ���δ������
     */
    boolean unread() throws IOException {
        Document doc = null;
        Feed feed = null;
        Element root = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.url);
            String title, link, description, language, guid, pubdate;
            root = ((Element) ((Element) doc.getElementsByTagName("rss").item(0))
                    .getElementsByTagName("channel").item(0));
            title = root.getElementsByTagName("title").item(0).getTextContent();
            link = root.getElementsByTagName("link").item(0).getTextContent();
            language = root.getElementsByTagName("language").item(0).getTextContent();
            description = root.getElementsByTagName("description").item(0).getTextContent();
            feed = new Feed(title, link, description, language); // rss������feed��


            NodeList feedsList = root.getElementsByTagName("item");
            Element feedElement = null;

            for (int i = 0; i < feedsList.getLength(); i++) {
                feedElement = ((Element) feedsList.item(i));
                guid = feedElement.getElementsByTagName("guid").item(0).getTextContent();
                guid = guid.replace("https://t.bilibili.com/", "");
                title = feedElement.getElementsByTagName("title").item(0).getTextContent();
                description = feedElement.getElementsByTagName("description").item(0).getTextContent();
                link = feedElement.getElementsByTagName("link").item(0).getTextContent();
                pubdate = feedElement.getElementsByTagName("pubDate").item(0).getTextContent();
                feed.getMessages().add(new FeedMessage(title, link, description, guid, pubdate));
            } // ���rss��Ϣ

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (last == null) {
            feed.getMessages().removeAll(feed.getMessages().subList(1, feed.getMessages().size()));
            System.out.print(feed.getMessages().size() + "\n�޸�������");
            last = feed;
            return true;
        } else if (feed.getMessages().get(0).guid.equals(last.getMessages().get(0).guid)) {
            System.out.print(feed.getMessages().get(0) + "\n�޸�������");
            return false;
        } else {
            last = feed;
            System.out.print(feed.getMessages().get(0) + "\n��鵽����");
            return true;
        } // ����Ƿ��и���
    }

    /**
     * ��ȡ���µ�������
     *
     * @param contact Ҫ���͵Ķ���
     * @return δ��������Ϣ��
     */
    LinkedList<Message> fetch(Contact contact) throws MalformedURLException {
        LinkedList<Message> result = new LinkedList<>();
        MessageChainBuilder message = new MessageChainBuilder();
        LinkedList<Image> img = new LinkedList<>();
        String text = "";
        for (FeedMessage feed : last.getMessages()) {
            if (Long.parseLong(feed.getGuid()) <= Long.parseLong(timestamp)) {
                break;
            } else {
                message = new MessageChainBuilder();
                message.add("������̬����:\n");
                text = feed.getDescription();
                img = getImage(feed.getDescription(), contact);
                text.replaceAll("<br>", "\n");
                text = text.replaceAll("<.*>", "");
                message.add(text);
                message.addAll(img);
                message.add(feed.getLink());
                result.add(message.asMessageChain());
            }
        }
        timestamp = last.getMessages().get(0).getGuid();
        return result;
    }

    /**
     * @param description ��̬��html�ı�
     * @return ��̬�е�ͼƬ
     */
    private LinkedList<Image> getImage(String description, Contact contact) throws MalformedURLException {
        LinkedList<Image> img = new LinkedList<>();
        Pattern IMG_PATTERN = Pattern.compile(
                "<img.*?src=\"(.*?)\".*?>",
                Pattern.CASE_INSENSITIVE);
        Matcher IMG_MATCHER = IMG_PATTERN.matcher(description);
        while (IMG_MATCHER.find()) {
            img.add(contact.uploadImage(new URL(IMG_MATCHER.group(1))));
        }
        return img;
    }
}


