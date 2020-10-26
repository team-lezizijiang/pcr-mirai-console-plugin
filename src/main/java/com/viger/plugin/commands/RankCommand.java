package com.viger.plugin.commands;

import com.viger.plugin.pcrMain;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RankCommand extends JCompositeCommand {
    public static RankCommand INSTANCE = new RankCommand();
    pcrMain plugin = pcrMain.INSTANCE;

    public RankCommand() {
        super(pcrMain.INSTANCE, "rank", "switch");
        setDescription("切换自动排名开关");
    }

    @SubCommand(value = "switch")
    public void onCommand(@NotNull CommandSender commandSender) {
        plugin.rankSwitch = !plugin.rankSwitch;
        plugin.getLogger().debug("自动排名已" + (plugin.rankSwitch ? "开启" : "关闭"));
        commandSender.sendMessage("自动排名已" + (plugin.rankSwitch ? "开启" : "关闭"));
    }

    @Nullable
    @Override
    public Object onDefault(@NotNull CommandSender commandSender, @NotNull MessageChain messageChain, @NotNull Continuation<? super Unit> continuation) {
        return super.onDefault(commandSender, messageChain, continuation);
    }
}