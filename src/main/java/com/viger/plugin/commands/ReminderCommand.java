package com.viger.plugin.commands;


import com.viger.plugin.pcrMain;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;


public class ReminderCommand extends JCompositeCommand {
    public static ReminderCommand INSTANCE = new ReminderCommand();
    pcrMain plugin = pcrMain.INSTANCE;

    public ReminderCommand() {
        super(pcrMain.INSTANCE, "reminder", "switch");
        setDescription(" 开关买药提醒 ");
    }

    @SubCommand(value = "switch")
    public void onCommand(@NotNull CommandSender commandSender) {
        if (!plugin.ReminderSwitch) {
            plugin.ReminderSwitch = true;
            try {
                plugin.imgReminder = pcrMain.group.uploadImage(new File("./config/xyz.viger.pcrplugin/reminder.jpg"));
                pcrMain.group.sendMessage(plugin.imgReminder);
                commandSender.sendMessage(" 开启成功 ");
                plugin.getLogger().info("reminder on");
            } catch (Exception e) {
                commandSender.sendMessage(" 开启失败. 请检查图片是否在正确路径下?");
                plugin.getLogger().error(e);
                plugin.ReminderSwitch = false;
            }
        } else {
            plugin.ReminderSwitch = false;
            commandSender.sendMessage("关闭成功");
            plugin.getLogger().info("reminder off");
        }
    }

    @Nullable
    @Override
    public Object onDefault(@NotNull CommandSender commandSender, @NotNull MessageChain messageChain, @NotNull Continuation<? super Unit> continuation) {
        return super.onDefault(commandSender, messageChain, continuation);
    }

}
