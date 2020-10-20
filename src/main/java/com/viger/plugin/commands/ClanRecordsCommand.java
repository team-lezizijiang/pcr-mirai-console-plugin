package com.viger.plugin.commands;

import com.viger.plugin.pcrMain;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;

public class ClanRecordsCommand extends JCompositeCommand {
    public static final ClanRecordsCommand INSTANCE = new ClanRecordsCommand();
    public static final pcrMain plugin = pcrMain.INSTANCE;
    Connection con;

    public ClanRecordsCommand() {
        super(pcrMain.INSTANCE, "records", "remove", "add");
        setDescription(" 管理数据库数据 ");
    }

    @SubCommand(value = "remove")
    public void remove(CommandSender sender, Member target, Long damage) {
        long qq = target.getId();
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", plugin.username, plugin.password);
            con.prepareStatement(String.format("delete " +
                            "from records " +
                            "where pcr.records.memberID=%d " +
                            "and pcr.records.damage=%d",
                    qq,
                    damage)).executeUpdate();
            sender.sendMessage(" 移除成功 ");
            con.close();
        } catch (Exception e) {
            sender.sendMessage(" 移除失败. 是否已经移除? ");
            plugin.getLogger().warning(e);
        }
    }

    @SubCommand(value = "add")
    public void add(CommandSender sender, Member target, Long damage) {
        long qq = target.getId();
        Calendar date = Calendar.getInstance();
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", plugin.username, plugin.password);
            PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
            sql.setLong(3, damage);
            sql.setBoolean(2, false);
            sql.setInt(4, date.get(Calendar.HOUR_OF_DAY) >= 5 ? date.get(Calendar.DATE) : date.get(Calendar.DATE) - 1);
            sql.setLong(1, target.getId());
            sql.executeUpdate();
            con.close();
        } catch (Exception e) {
            sender.sendMessage(" 添加失败.");
            plugin.getLogger().warning(e);
        }
    }

    @Nullable
    @Override
    public Object onDefault(@NotNull CommandSender commandSender, @NotNull MessageChain messageChain, @NotNull Continuation<? super Unit> continuation) {
        return super.onDefault(commandSender, messageChain, continuation);
    }
}
