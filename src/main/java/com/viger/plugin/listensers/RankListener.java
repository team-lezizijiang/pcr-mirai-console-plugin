package com.viger.plugin.listensers;

import com.viger.plugin.pcrMain;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

public class RankListener extends SimpleListenerHost {
    public static final RankListener INSTANCE = new RankListener();

    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        super.handleException(context, exception);
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {
        if (event.getMessage().contentToString().equals("排名")) {
            pcrMain.group.sendMessage(pcrMain.INSTANCE.rank.query("L.S.P."));
        } else if (event.getMessage().contentToString().startsWith("排名 ")) {
            pcrMain.group.sendMessage(pcrMain.INSTANCE.rank.query(event.getMessage().contentToString().replace("排名 ", "")));
        }
    } // 会战排名查询
}

