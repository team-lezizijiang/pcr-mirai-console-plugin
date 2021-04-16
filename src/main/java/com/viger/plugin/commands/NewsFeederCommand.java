package com.viger.plugin.commands;

import com.viger.plugin.pcrMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.jetbrains.annotations.NotNull;


public class NewsFeederCommand extends JCompositeCommand {
    public static NewsFeederCommand INSTANCE = new NewsFeederCommand();
    pcrMain plugin = pcrMain.INSTANCE;

    public NewsFeederCommand() {
        super(pcrMain.INSTANCE, "feeder", "switch");
        setDescription("切换自动新闻推送开关");
    }

    @SubCommand(value = "switch")
    public void onCommand(@NotNull CommandSender commandSender) {
        plugin.feederSwitch = !plugin.feederSwitch;
        plugin.getLogger().debug("新闻推送已" + (plugin.feederSwitch ? "开启" : "关闭"));
        commandSender.sendMessage("新闻推送已" + (plugin.feederSwitch ? "开启" : "关闭"));
    }

}
