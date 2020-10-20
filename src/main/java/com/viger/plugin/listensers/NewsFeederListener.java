package com.viger.plugin.listensers;

import com.viger.plugin.pcrMain;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

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
        if (event.getMessage().contentToString().equals("新闻")) {
            try {
                event.getSubject().sendMessage(plugin.feeder.last(pcrMain.group));
            } catch (MalformedURLException e) {
                plugin.getLogger().error(e);
            }
        }
    } // 手动看新闻
}
