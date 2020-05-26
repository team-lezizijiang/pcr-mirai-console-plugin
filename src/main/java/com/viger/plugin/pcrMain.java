package com.viger.plugin;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.*;

import static com.viger.plugin.Constant.*;


/**
 * �������
 */
class pcrMain extends PluginBase {
    private Config settings;
    private HashSet<Member> memberList;
    private boolean enabled;
    private Connection con;
    private static HashMap<String, String> coolDown; //�鿨��ȴʱ��


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
        if (settings.getString("DBUsername").equals("null")) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
        } catch (Exception e) {
            this.getLogger().error(e);
            this.getLogger().error("����settings.yaml����������mysql�˺ż�����!");
        }
        // ��ʼ��
    }


    public void onEnable() {

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();

            if (messageInString.contains("��ʼ�ǵ�")) {
                this.getLogger().info("��ʼ�ǵ�");
                this.settings.set("Enabled", Boolean.TRUE);
                this.enabled = true;
                this.settings.save();
                for (Member i : event.getGroup().getMembers()) {
                    memberList.add(i);
                }
            } // ��ʼ�ǵ�ʱ,����ȺԱ�б�����¼��
            else if (messageInString.contains("�����ǵ�")) {
                this.getLogger().info("�����ǵ�");
                this.settings.set("Enabled", Boolean.FALSE);
                this.enabled = false;
                this.settings.save();
            } // �����ǵ�

            else if (messageInString.contains("�ǵ� ")) {
                if (this.enabled) {
                    try {
                        boolean isFinal = false;

                        if (messageInString.contains("β��")) {
                            isFinal = true;
                            messageInString = messageInString.replace("β�� ", "");
                        }
                        messageInString = messageInString.replace("�ǵ� ", "");
                        long damage = Long.parseLong((messageInString));
                        PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
                        sql.setLong(3, damage);
                        sql.setBoolean(2, isFinal);
                        sql.setInt(4, new Date().getDay());
                        sql.setLong(1, event.getSender().getId());
                        sql.executeUpdate();
                        event.getSubject().sendMessage("�ǵ��ɹ�");
                    } catch (Exception e) {
                        event.getSubject().sendMessage("�ǵ�ʧ��");
                        this.getLogger().warning(e);
                    }
                }
            } //�ǵ�,���ݰ����˺� ʱ�� �Ƿ�β��

            else if (messageInString.contains("#����")) {
                event.getSubject().sendMessage(helpMsg);
            } // ��ʾ������Ϣ

            if (messageInString.contains("�鵶")) {
                event.getSubject().sendMessage("��ʼ�����ĳ������");
                getScheduler().async(() -> {
                    long totalDamage;
                    LinkedList<Member> loudao = new LinkedList<>();
                    int count;
                    for (Member member : memberList) {
                        count = 0;
                        totalDamage = 0;
                        try {
                            PreparedStatement sql;
                            sql = con.prepareStatement("select sum(damage) from records where memberID=? and date=?");
                            sql.setInt(2, new Date().getDay());
                            sql.setLong(1, member.getId());
                            ResultSet rs = sql.executeQuery();
                            this.getLogger().info("��ѯ���ݿ�...");
                            rs.next();
                            totalDamage = rs.getLong(1);
                            sql = con.prepareStatement(
                                    "select count(damage) from records where memberID=? and isFinal=? and date=?");
                            sql.setInt(3, new Date().getDay());
                            sql.setLong(1, member.getId());
                            sql.setBoolean(2, false);//
                            rs = sql.executeQuery();
                            rs.next();
                            count = rs.getInt(1);
                            this.getLogger().info("��ѯ���");

                        } catch (SQLException e) {
                            e.printStackTrace();
                            this.getLogger().info("��ѯʧ��");
                        }
                        if (count < 3) {
                            loudao.add(member);
                        }
                        event.getSubject().sendMessageAsync("����" + member.getNick() + "����" + count + "��, " + "���" + totalDamage + "���˺�;");
                    }

                    MessageChainBuilder message = new MessageChainBuilder();
                    message.add("���׵�˹��,����");
                    for (Member nmsl : loudao) {
                        message.add(new At(nmsl));
                        message.append(", ");
                    }
                    message.append("��û�г�������,������Ʋ�~~~");
                    event.getSubject().sendMessage(message.toString());
                }); // ����©����С����


                // ��ѯһ��ļ�¼�������������


            } // �鿴����������
        });

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(sender.getNick() + ':' + messageInString);
            if (messageInString.contains("#upʮ��")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(10, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#ʮ��")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(10, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#��")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(300, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#up��")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(300, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#up�鿨")) {
                if (isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#up�鿨 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = dp_Gashapon(q, true);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                        reFlashCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessage(("���ֽ�������"));
                    }
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#�鿨")) {
                if (isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#�鿨 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = dp_Gashapon(q, false);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                        reFlashCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessage(("���ֽ�������"));
                    }
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }

            }

        }); // ģ�⿨��

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "member", new ArrayList<>(), " ������Ҫ�ǵ��ĳ�Ա ", "/member remove/list"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if (list.size() < 1) {
                    return false;
                }
                switch (list.get(0)) {
                    case "remove":
                        if (list.size() < 2) {
                            commandSender.sendMessageBlocking("/member remove ��Աqq ");
                            return true;
                        }
                        long qq = Long.parseLong(list.get(1).trim());
                        try {
                            con.prepareStatement(String.format("delete from records where pcr.records.memberID=%d", qq)).executeUpdate();
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


        JCommandManager.getInstance().register(this, new BlockingCommand(
                "records", new ArrayList<>(), " �������ݿ����� ", "/records remove qq damage"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if (list.size() < 1) {
                    return false;
                }
                if ("remove".equals(list.get(0))) {
                    if (list.size() < 2) {
                        commandSender.sendMessageBlocking("/member remove ��Աqq ��Ա�˺�");
                        return true;
                    }
                    long qq = Long.parseLong(list.get(1).trim());
                    long damage = Long.parseLong(list.get(2).trim());
                    try {
                        con.prepareStatement(String.format("delete from records where pcr.records.memberID=%d and pcr.records.damage=%d", qq, damage)).executeUpdate();
                        commandSender.sendMessageBlocking(" �Ƴ��ɹ� ");
                    } catch (Exception e) {
                        commandSender.sendMessageBlocking(" �Ƴ�ʧ��. �Ƿ��Ѿ��Ƴ�? ");
                        return false;
                    }
                    // �Ƴ���¼����ļ�¼
                } else {
                    return false;
                }
                return true;
            }
        });
        this.getLogger().info("Plugin loaded!");
    }


    /**
     * up�صĸ���
     *
     * @param num
     * @return
     */
    public Gashapon dp_Gashapon(int num, boolean isUp) {
        if (isUp) {

        }
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0; // ����������Ƕ����м���

        //�ޱ���
        for (int i = 0; i < num - num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else if (j > 795) {
                tw++;
            } else {
                on++;
            }
        }
        //�б���
        for (int i = 0; i < num / 10; i++) {
            int j = random.nextInt(1000);
            if (j > 975) {
                thre++;
            } else {
                tw++;
            }
        }
        HashMap<String, Integer> map1 = new HashMap<>();
        HashMap<String, Integer> map2 = new HashMap<>();
        HashMap<String, Integer> map3 = new HashMap<>();

        for (int i = 0; i < thre; i++) {
            int q = random.nextInt(25);
            if (q < 7 && isUp) {//�鲻��ĳ�������ɳ
                map1.merge(Three_plus[random.nextInt(Three_plus.length)], 1, Integer::sum);
            } else {
                map1.merge(noUpThree[random.nextInt(Three.length)], 1, Integer::sum);
            }
        }
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(two.length);
            map2.merge(two[j], 1, Integer::sum); // todo: ����up
        }
        for (int i = 0; i < on; i++) {
            int j = random.nextInt(one.length);
            map3.merge(one[j], 1, Integer::sum);
        }
        Gashapon g = new Gashapon();
        g.setData(get_GashaponString(on, tw, thre, map1, map2, map3));
        try {
            g.setBan(num / thre < 20);
        } catch (ArithmeticException e) {
            g.setBan(false);
        }
        return g;
    }

    /**
     * ��֯�鿨���
     */
    public String get_GashaponString(int on, int tw, int thre, HashMap<String, Integer> map1, HashMap<String, Integer> map2, HashMap<String, Integer> map3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("һ�������");
        if (thre != 0) {
            stringBuilder.append(thre).append("������");
        }
        if (tw != 0) {
            stringBuilder.append(tw).append("������");
        }
        if (on != 0) {
            stringBuilder.append(on).append("��һ��");
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        if (thre != 0) {
            stringBuilder.append("\n���ǽ�ɫ�У�");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n���ǽ�ɫ�У�");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\nһ�ǽ�ɫ�У�");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
        }

        return stringBuilder.toString();
    }


    /**
     * ˢ�³鿨��ȴʱ��
     */
    public void reFlashCoolDown(long QQ) {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusSeconds(200);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = localDateTime.format(dateTimeFormatter);
        if (coolDown == null) {
            coolDown = new HashMap<>();
            coolDown.put(String.valueOf(QQ), time);
        } else {
            coolDown.put(String.valueOf(QQ), time);
        }
    }

    /**
     * ��ȡ��ȴʱ���ǲ��ǵ���
     *
     * @param QQ
     */
    public boolean isCool(long QQ) {
        if (coolDown == null) {
            coolDown = new HashMap<>();
            return true;
        } else {
            if (coolDown.get(String.valueOf(QQ)) != null) {
                return coolDown.get(String.valueOf(QQ)).compareTo(new SimpleDateFormat("HH:mm").format(new Date())) < 0;
            } else {
                return true;
            }
        }
    }
}
// TODO: add timer (��ʱ�鵶, ������ҩС����(pic))
// TODO: ��������ݼ�¼(pic)
// todo: ʹ��excel����ҳչʾͳ������