package com.viger.plugin.commands;

import com.viger.plugin.Gashapon;
import com.viger.plugin.pcrMain;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.jetbrains.annotations.NotNull;


public class GashaponCommand extends JCompositeCommand {
    public static GashaponCommand INSTANCE = new GashaponCommand();
    pcrMain plugin = pcrMain.INSTANCE;

    public GashaponCommand() {
        super(pcrMain.INSTANCE, "gashapon", "loc");
        setDescription("切换卡池区域。JP BL TW ALL 可选");
    }

    @SubCommand(value = "loc")
    public void loc(@NotNull CommandSender commandSender, String s) {
        if (s.equalsIgnoreCase("tw")
                || s.equalsIgnoreCase("bl")
                || s.equalsIgnoreCase("all")
                || s.equalsIgnoreCase("jp")) {
            Gashapon.INSTANCE.loc = s.toUpperCase();
            Gashapon.INSTANCE.update();
            plugin.getLogger().debug("卡池切换到" + s.toUpperCase());
            commandSender.sendMessage("卡池切换到" + s.toUpperCase());
        } else {
            commandSender.sendMessage("JP BL TW ALL 可选");
        }
    }

}