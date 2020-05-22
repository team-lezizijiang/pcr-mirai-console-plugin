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
        // ��ʼ��
    }


    public void onEnable(){

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event)->{
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(sender.getNick() + ':' + messageInString);

            if(messageInString.contains("/��ʼ�ǵ�")){
                this.getLogger().info("/��ʼ�ǵ�");
                settings.set("Enabled", "true");
                for(Member i : event.getGroup().getMembers()){
                    records.put(i, new LinkedHashSet<records>());
                }
            } // ��ʼ�ǵ�ʱ,����ȺԱ�б�����¼��
            else if(messageInString.contains("�ǵ� ")){
                if(settings.get("Enabled") == "true"){
                    boolean isFinal = false;

                    if (messageInString.contains("β��")){
                        isFinal = true;
                        messageInString = messageInString.replace("β�� ", "");
                    }
                    messageInString = messageInString.replace("�ǵ� ", "");
                    long damage = Long.parseLong((messageInString));
                    records.get(sender).add(new records(damage, isFinal, new Date().getDay()));
                }
            } //�ǵ�,���ݰ����˺� ʱ�� �Ƿ�β��

            if(messageInString.contains( "�鵶")){
                long totalDamage;
                LinkedList<Member> loudao = new LinkedList<Member>();
                int count;
                event.getSubject().sendMessage("��ʼ�����ĳ������");
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
                    event.getSubject().sendMessage("����" + member.getNick() + "����" + count + "��, " + "���" + totalDamage + "���˺�;");
                }
                // ��ѯһ��ļ�¼�������������


            }
        });

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "member", new ArrayList<>()," ������Ҫ�ǵ��ĳ�Ա ","/member remove/list"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if(list.size() < 1){
                    return false;
                }
                switch (list.get(0)){
                    case "remove":
                        if(list.size() < 3){
                            commandSender.sendMessageBlocking("/member remove ��Աqq ");
                            return true;
                        }
                        long qq = Long.getLong(list.get(1).trim());
                        try {
                            for (Member member : records.keySet()) {
                                if (member.getId() == qq) {
                                    records.remove(member);
                                }
                            }
                            commandSender.sendMessageBlocking(" �Ƴ��ɹ� ");
                        } catch (Exception e) {
                            commandSender.sendMessageBlocking(" �Ƴ�ʧ��. �Ƿ��Ѿ��Ƴ�? ");
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
    // TODO: ��������ݼ�¼(pic)

}