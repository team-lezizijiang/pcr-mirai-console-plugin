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

public class ClanMemberCommand extends JCompositeCommand {
    public final static ClanMemberCommand INSTANCE = new ClanMemberCommand();
    private final static pcrMain plugin = pcrMain.INSTANCE;
    Connection con;


    public ClanMemberCommand() {
        super(pcrMain.INSTANCE, "member");
        setDescription(" 管理需要记刀的成员 ");
    }

    @SubCommand({"add"})
    public void add(CommandSender sender, Member target) {
        plugin.memberList.add(target.getId());
        sender.sendMessage("已添加" + target.getNameCard());
    }

    @SubCommand({"remove"})
    public void remove(CommandSender sender, Member target) {

        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", plugin.username, plugin.password);
            con.prepareStatement(String.format("delete from records where pcr.records.memberID=%d", target.getId())).executeUpdate();
            sender.sendMessage(" 移除成功 ");
            plugin.memberList.remove(target.getId());
            con.close();
        } catch (Exception e) {
            sender.sendMessage(" 移除失败. 是否已经移除? ");
            plugin.getLogger().warning(e);
        }
    }

    @SubCommand({"list"})
    public void list(CommandSender sender) {
        for (Member member : pcrMain.group.getMembers()) {
            if (plugin.memberList.contains(member.getId())) {
                sender.sendMessage(plugin.getNameCard(member));
            }
        }
    }

    @Nullable
    @Override
    public Object onDefault(@NotNull CommandSender commandSender, @NotNull MessageChain messageChain, @NotNull Continuation<? super Unit> continuation) {
        return super.onDefault(commandSender, messageChain, continuation);
    }
}
