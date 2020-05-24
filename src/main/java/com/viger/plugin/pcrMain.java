package com.viger.plugin;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.GroupMessage;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Date;
import java.util.*;


/**
 * 插件主体
 */
class pcrMain extends PluginBase {
    private Config settings;
    private HashSet<Member> memberList;
    private boolean enabled;
    private Connection con;



    public void onLoad() {
        super.onLoad();
        this.settings = this.loadConfig("settings.yaml");
        memberList = new HashSet<>();
        this.settings.setIfAbsent("Enabled", Boolean.FALSE);
        this.settings.setIfAbsent("DBUsername", "null");
        this.settings.setIfAbsent("DBPassword", "null");
        String username = this.settings.getString("DBUsername");
        String password = this.settings.getString("DBPassword");
        this.enabled = this.settings.getBoolean("Enabled");
        this.settings.save();
        if (settings.getString("DBUsername").equals("null")){
            return;
        }
        try{
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
        }catch (Exception e){
            this.getLogger().error(e);
            this.getLogger().error("请在settings.yaml中设置您的mysql账号及密码!");
        }
        // 初始化
    }


    public void onEnable(){

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event)->{
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(sender.getNick() + ':' + messageInString);

            if(messageInString.contains("开始记刀")){
                this.getLogger().info("开始记刀");
                this.settings.set("Enabled", Boolean.TRUE);
                this.enabled = true;
                this.settings.save();
                for(Member i : event.getGroup().getMembers()){
                    memberList.add(i);
                }
            } // 开始记刀时,根据群员列表建立记录表

            else if(messageInString.contains("记刀 ")){
                if(this.enabled){
                    try {
                        boolean isFinal = false;

                        if (messageInString.contains("尾刀")) {
                            isFinal = true;
                            messageInString = messageInString.replace("尾刀 ", "");
                        }
                        messageInString = messageInString.replace("记刀 ", "");
                        long damage = Long.parseLong((messageInString));
                        PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
                        sql.setLong(3,damage);
                        sql.setBoolean(2,isFinal);
                        sql.setInt(4,new Date().getDay());
                        sql.setLong(1,event.getSender().getId());
                        sql.executeUpdate();
                        event.getSubject().sendMessage("记刀成功");
                    } catch (Exception e){
                        event.getSubject().sendMessage("记刀失败");
                        this.getLogger().warning(e);
                        }
                    }
            } //记刀,内容包括伤害 时间 是否尾刀

            if(messageInString.contains( "查刀")){
                event.getSubject().sendMessage("开始查今天的出刀情况");
                getScheduler().async(()-> {
                    long totalDamage;
                    LinkedList<Member> loudao = new LinkedList<>();
                    int count;
                    for (Member member : memberList) {
                        count = 0;
                        totalDamage = 0;
                        try {
                            PreparedStatement sql = con.prepareStatement("select sum(damage) from records where memberID=? and date=?");
                            sql.setInt(2, new Date().getDay());
                            sql.setLong(1,member.getId());
                            ResultSet rs = sql.executeQuery();
                            this.getLogger().info("查询数据库...");
                            rs.next();
                            totalDamage = rs.getLong(1);
                            sql = con.prepareStatement("select count(damage) from records where memberID=? and isFinal=? and date=?");
                            sql.setInt(3, new Date().getDay());
                            sql.setLong(1,member.getId());
                            sql.setBoolean(2,false);//
                            rs = sql.executeQuery();
                            rs.next();
                            count = rs.getInt(1);
                            this.getLogger().info("查询完成");

                        } catch (SQLException e) {
                            e.printStackTrace();
                            this.getLogger().info("查询失败");
                        }
                        if (count<3){
                            loudao.add(member);
                        }
                        event.getSubject().sendMessage("今天" + member.getNick() + "共出" + count + "刀, " + "造成" + totalDamage + "点伤害;");
                    }

                    StringBuilder message = new StringBuilder("牙白德斯内,今天");
                    for (Member nmsl:loudao) {
                        message.append(nmsl.getNick());
                        message.append(", ");
                    }
                    message.append("还没有出满三道,请接受制裁~~~");
                    event.getSubject().sendMessage(message.toString()); // 揪出漏刀的小朋友
                });



                // 查询一天的记录情况并发送提醒


            }
        });

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "member", new ArrayList<>()," 管理需要记刀的成员 ","/member remove/list"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if(list.size() < 1){
                    return false;
                }
                switch (list.get(0)){
                    case "remove":
                        if(list.size() < 2){
                            commandSender.sendMessageBlocking("/member remove 成员qq ");
                            return true;
                        }
                        long qq = Long.parseLong(list.get(1).trim());
                        try {
                            con.prepareStatement("delete from records where pcr.records.memberID=" + qq).executeUpdate();
                            memberList.removeIf(member -> member.getId() == qq);
                            commandSender.sendMessageBlocking(" 移除成功 ");
                        } catch (Exception e) {
                            commandSender.sendMessageBlocking(" 移除失败. 是否已经移除? ");
                            return false;
                        }
                        break; // 移除不需要记刀的成员


                    case "list":
                        for (Member member : memberList) {
                            commandSender.sendMessageBlocking(member.getNick());
                        }
                        break; // 查看当前记刀的成员
                    default:
                        return false;
                }
                return true;
            }
        });
        this.getLogger().info("Plugin loaded!");
    }
    // TODO: add timer
    // TODO: 加入简单阵容记录(pic)
}