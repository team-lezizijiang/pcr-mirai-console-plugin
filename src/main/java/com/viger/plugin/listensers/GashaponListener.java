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
        plugin.getLogger().info(plugin.getNameCard(sender) + ':' + messageInString);
        if (messageInString.contains("#up十连")) {
            if (Gashapon.isCool(sender.getId())) {
                Gashapon gashapon = new Gashapon(10, true);
                event.getSubject().sendMessage(new At(sender).plus(gashapon.getData()));
                Gashapon.refreshCoolDown(sender.getId());
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("还抽?还有钻吗?给你两分钟去氪一单"));
            }
        } else if (messageInString.contains("#十连")) {
            if (Gashapon.isCool(sender.getId())) {
                Gashapon gashapon = new Gashapon(10, false);
                event.getSubject().sendMessage(new At(sender).plus(gashapon.getData()));
                Gashapon.refreshCoolDown(sender.getId());
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        } else if (messageInString.contains("#井")) {
            if (Gashapon.isCool(sender.getId())) {
                Gashapon gashapon = new Gashapon(300, false);
                event.getSubject().sendMessage(new At(sender).plus(gashapon.getData()));
                Gashapon.refreshCoolDown(sender.getId());
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        } else if (messageInString.contains("#up井")) {
            if (Gashapon.isCool(sender.getId())) {
                Gashapon gashapon = new Gashapon(300, true);
                event.getSubject().sendMessage(new At(sender).plus(gashapon.getData()));
                Gashapon.refreshCoolDown(sender.getId());
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        } else if (messageInString.contains("#up抽卡 ")) {
            if (Gashapon.isCool(sender.getId())) {
                String str = messageInString.replaceAll("#up抽卡 ", "");
                try {
                    int q = Integer.parseInt(str);
                    Gashapon gashapon = new Gashapon(q, true);
                    event.getSubject().sendMessage(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } catch (NumberFormatException e) {
                    event.getSubject().sendMessage(("数字解析错误"));
                }
            } else {
                //发送冷却提示消息
                event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
            }
        } else if (messageInString.contains("#抽卡 ")) {
            if (Gashapon.isCool(sender.getId())) {
                String str = messageInString.replaceAll("#抽卡 ", "");
                try {
                    int q = Integer.parseInt(str);
                    Gashapon gashapon = new Gashapon(q, false);
                    event.getSubject().sendMessage(new At(sender).plus(gashapon.getData()));
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
