package com.viger.plugin.listensers;

import com.viger.plugin.pcrMain;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;

public class NewsFeederListener extends SimpleListenerHost {
    public static final NewsFeederListener INSTANCE = new NewsFeederListener();
    private final pcrMain plugin = pcrMain.INSTANCE;

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
        plugin.getLogger().error(exception);
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {
        String content = event.getMessage().contentToString();
        if ("新闻".equals(content)) {
            try {
                event.getSubject().sendMessage(plugin.feeder.last(pcrMain.group));
            } catch (MalformedURLException e) {
                plugin.getLogger().error(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (content.startsWith("订阅 ")) {
            String title = plugin.feeder.subscribe(content.replace("订阅 ", ""));
            plugin.getLogger().info("订阅" + title);
            event.getSender().sendMessage("订阅" + title);
        }
    } // 手动看新闻
}
