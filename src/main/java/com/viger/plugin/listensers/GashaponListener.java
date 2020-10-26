package com.viger.plugin.listensers;

import com.viger.plugin.Gashapon;
import com.viger.plugin.pcrMain;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import org.jetbrains.annotations.NotNull;

public class GashaponListener extends SimpleListenerHost {
    public static final GashaponListener INSTANCE = new GashaponListener();
    pcrMain plugin = pcrMain.INSTANCE;


    @Override
    public void handleException(@NotNull CoroutineContext context, @NotNull Throwable exception) {
        plugin.getLogger().error(exception);
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {
        String messageInString = event.getMessage().contentToString();
        Member sender = event.getSender();
        if (messageInString.contains("#十连")) {
            if (Gashapon.isCool(sender.getId())) {
                event.getSubject().sendMessage(new At(sender).plus(plugin.gashapon.gacha(10)));
                Gashapon.refreshCoolDown(sender.getId());
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        } else if (messageInString.contains("#井")) {
            if (Gashapon.isCool(sender.getId())) {
                event.getSubject().sendMessage(new At(sender).plus(plugin.gashapon.gacha(300)));
                Gashapon.refreshCoolDown(sender.getId());
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        } else if (messageInString.contains("#抽卡 ")) {
            if (Gashapon.isCool(sender.getId())) {
                String str = messageInString.replaceAll("#抽卡 ", "");
                try {
                    int q = Integer.parseInt(str);
                    event.getSubject().sendMessage(new At(sender).plus(plugin.gashapon.gacha(q)));
                    Gashapon.refreshCoolDown(sender.getId());
                } catch (NumberFormatException e) {
                    event.getSubject().sendMessage(("数字解析错误"));
                }
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        }

    } // 模拟卡池
}
