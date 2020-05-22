package com.viger.plugin;

import net.mamoe.mirai.console.command.*;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Message;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class pcrMain extends PluginBase {
    private Config settings;
    private HashMap<Member, LinkedHashSet<records>> records;
    public void onLoad() {
        super.onLoad();
        this.settings = this.loadConfig("settings.yaml");
        records = new HashMap<Member, LinkedHashSet<com.viger.plugin.records>>();
        this.settings.setIfAbsent("Enabled", "false");
        // 初始化
    }


    public void onEnable(){

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event)->{
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(sender.getNick() + ':' + messageInString);

            if(messageInString.contains("/开始记刀")){
                this.getLogger().info("/开始记刀");
                settings.set("Enabled", "true");
                for(Member i : event.getGroup().getMembers()){
                    records.put(i, new LinkedHashSet<records>());
                }
            } // 开始记刀时,根据群员列表建立记录表
            else if(messageInString.contains("记刀 ")){
                if(settings.get("Enabled") == "true"){
                    boolean isFinal = false;

                    if (messageInString.contains("尾刀")){
                        isFinal = true;
                        messageInString = messageInString.replace("尾刀 ", "");
                    }
                    messageInString = messageInString.replace("记刀 ", "");
                    long damage = Long.parseLong((messageInString));
                    records.get(sender).add(new records(damage, isFinal, new Date().getDay()));
                }
            } //记刀,内容包括伤害 时间 是否尾刀

            if(messageInString.contains( "查刀")){
                long totalDamage;
                LinkedList<Member> loudao = new LinkedList<Member>();
                int count;
                event.getSubject().sendMessage("开始查今天的出刀情况");
                for(Member member: records.keySet()){
                    count = 0;
                    totalDamage = 0;
                    for(records tempRecords: records.get(member)){
                        if (tempRecords.getDate() == new Date().getDate()){
                            if(!tempRecords.isFinal()){
                                count += 1;
                            }
                            totalDamage += tempRecords.getDamage();
                        }
                    }
                    if (count<3){
                        loudao.add(member);
                    }
                    event.getSubject().sendMessage("今天" + member.getNick() + "共出" + count + "刀, " + "造成" + totalDamage + "点伤害;");
                }
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
                        if(list.size() < 3){
                            commandSender.sendMessageBlocking("/member remove 成员qq ");
                            return true;
                        }
                        long qq = Long.getLong(list.get(1).trim());
                        try {
                            for (Member member : records.keySet()) {
                                if (member.getId() == qq) {
                                    records.remove(member);
                                }
                            }
                            commandSender.sendMessageBlocking(" 移除成功 ");
                        } catch (Exception e) {
                            commandSender.sendMessageBlocking(" 移除失败. 是否已经移除? ");
                            return false;
                        }
                        break;


                    case "list":
                        for (Member member : records.keySet()) {
                            commandSender.sendMessageBlocking(member.getNick());
                        }
                        break;
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