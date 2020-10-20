package com.viger.plugin.commands;

import com.viger.plugin.pcrMain;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class NewsFeederCommand extends JCompositeCommand {
    public static NewsFeederCommand INSTANCE = new NewsFeederCommand();
    pcrMain plugin = pcrMain.INSTANCE;

    public NewsFeederCommand() {
        super(pcrMain.INSTANCE, "feeder", "switch");
    }

    @SubCommand(value = "switch")
    public void onCommand(@NotNull CommandSender commandSender) {
        plugin.feederSwitch = !plugin.feederSwitch;
        plugin.getLogger().debug("新闻推送已" + (plugin.feederSwitch ? "开启" : "关闭"));
        commandSender.sendMessage("新闻推送已" + (plugin.feederSwitch ? "开启" : "关闭"));
    }

    @Nullable
    @Override
    public Object onDefault(@NotNull CommandSender commandSender, @NotNull MessageChain messageChain, @NotNull Continuation<? super Unit> continuation) {
        return super.onDefault(commandSender, messageChain, continuation);
    }
}
