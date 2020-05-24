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
 * �������
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
            this.getLogger().error("����settings.yaml����������mysql�˺ż�����!");
        }
        // ��ʼ��
    }


    public void onEnable(){

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event)->{
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(sender.getNick() + ':' + messageInString);

            if(messageInString.contains("��ʼ�ǵ�")){
                this.getLogger().info("��ʼ�ǵ�");
                this.settings.set("Enabled", Boolean.TRUE);
                this.enabled = true;
                this.settings.save();
                for(Member i : event.getGroup().getMembers()){
                    memberList.add(i);
                }
            } // ��ʼ�ǵ�ʱ,����ȺԱ�б�����¼��

            else if(messageInString.contains("�ǵ� ")){
                if(this.enabled){
                    try {
                        boolean isFinal = false;

                        if (messageInString.contains("β��")) {
                            isFinal = true;
                            messageInString = messageInString.replace("β�� ", "");
                        }
                        messageInString = messageInString.replace("�ǵ� ", "");
                        long damage = Long.parseLong((messageInString));
                        PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
                        sql.setLong(3,damage);
                        sql.setBoolean(2,isFinal);
                        sql.setInt(4,new Date().getDay());
                        sql.setLong(1,event.getSender().getId());
                        sql.executeUpdate();
                        event.getSubject().sendMessage("�ǵ��ɹ�");
                    } catch (Exception e){
                        event.getSubject().sendMessage("�ǵ�ʧ��");
                        this.getLogger().warning(e);
                        }
                    }
            } //�ǵ�,���ݰ����˺� ʱ�� �Ƿ�β��

            if(messageInString.contains( "�鵶")){
                event.getSubject().sendMessage("��ʼ�����ĳ������");
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
                            this.getLogger().info("��ѯ���ݿ�...");
                            rs.next();
                            totalDamage = rs.getLong(1);
                            sql = con.prepareStatement("select count(damage) from records where memberID=? and isFinal=? and date=?");
                            sql.setInt(3, new Date().getDay());
                            sql.setLong(1,member.getId());
                            sql.setBoolean(2,false);//
                            rs = sql.executeQuery();
                            rs.next();
                            count = rs.getInt(1);
                            this.getLogger().info("��ѯ���");

                        } catch (SQLException e) {
                            e.printStackTrace();
                            this.getLogger().info("��ѯʧ��");
                        }
                        if (count<3){
                            loudao.add(member);
                        }
                        event.getSubject().sendMessage("����" + member.getNick() + "����" + count + "��, " + "���" + totalDamage + "���˺�;");
                    }

                    StringBuilder message = new StringBuilder("���׵�˹��,����");
                    for (Member nmsl:loudao) {
                        message.append(nmsl.getNick());
                        message.append(", ");
                    }
                    message.append("��û�г�������,������Ʋ�~~~");
                    event.getSubject().sendMessage(message.toString()); // ����©����С����
                });



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
                        if(list.size() < 2){
                            commandSender.sendMessageBlocking("/member remove ��Աqq ");
                            return true;
                        }
                        long qq = Long.parseLong(list.get(1).trim());
                        try {
                            con.prepareStatement("delete from records where pcr.records.memberID=" + qq).executeUpdate();
                            memberList.removeIf(member -> member.getId() == qq);
                            commandSender.sendMessageBlocking(" �Ƴ��ɹ� ");
                        } catch (Exception e) {
                            commandSender.sendMessageBlocking(" �Ƴ�ʧ��. �Ƿ��Ѿ��Ƴ�? ");
                            return false;
                        }
                        break; // �Ƴ�����Ҫ�ǵ��ĳ�Ա


                    case "list":
                        for (Member member : memberList) {
                            commandSender.sendMessageBlocking(member.getNick());
                        }
                        break; // �鿴��ǰ�ǵ��ĳ�Ա
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