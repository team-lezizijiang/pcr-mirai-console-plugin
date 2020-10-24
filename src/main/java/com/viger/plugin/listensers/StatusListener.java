package com.viger.plugin.listensers;

import com.viger.plugin.pcrMain;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

public class StatusListener extends SimpleListenerHost {
    public static final StatusListener INSTANCE = new StatusListener();
    private static final pcrMain plugin = pcrMain.INSTANCE;

    @EventHandler
    public void onMessage(GroupMessageEvent event) {
        if (event.getMessage().contentToString().equals("状态")) {
            event.getSubject().sendMessage(plugin.status.query());
        }
    }

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }


}
