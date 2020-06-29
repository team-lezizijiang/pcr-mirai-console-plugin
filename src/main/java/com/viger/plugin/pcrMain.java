package com.viger.plugin;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;
import net.mamoe.mirai.console.plugins.Config;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.GroupMessage;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static com.viger.plugin.Constant.*;


/**
 * �������
 */
class pcrMain extends PluginBase {
    private static Long[] QqExclude = {1466131295L, // Elish
            2314125066L, // ��
            3378035874L, // �Һܺ���
            3236214319L, // ��
            11413446L, // ���ض�
            2855958676L, // Ⱥ���������谮��
            2371617404L};// Ԥ������ų��б�
    static String[] one, two, three, noUpThree, one_plus, two_plus, three_plus, noUpTwo, noUpOne;
    private Config settings; // �����ļ�
    private HashSet<Member> memberList; // �ǵ��ĳ�Ա�б�
    private boolean enabled; // �Ŷ�ս����
    private boolean Reminder; // С���ֿ���
    private Image imgReminder; // С������Դ
    static HashMap<String, String> coolDown; //�鿨��ȴʱ��
    String username;
    String password;// ���ݿ�����
    private Connection con;

    public void onLoad() {
        super.onLoad();
        this.settings = this.loadConfig("settings.yaml");
        memberList = new HashSet<>();
        this.settings.setIfAbsent("Enabled", Boolean.FALSE);
        this.settings.setIfAbsent("one", Constant.one);
        this.settings.setIfAbsent("two", Constant.two);
        this.settings.setIfAbsent("three", Constant.Three);
        this.settings.setIfAbsent("one_plus", Constant.one_plus);
        this.settings.setIfAbsent("two_plus", Constant.two_plus);
        this.settings.setIfAbsent("three_plus", Constant.Three_plus);
        this.settings.setIfAbsent("noUpThree", Constant.noUpThree);
        this.settings.setIfAbsent("noUpOne", Constant.noUpOne);
        this.settings.setIfAbsent("noUpTwo", Constant.noUpTwo);
        this.settings.setIfAbsent("QqExclude", QqExclude);
        this.settings.setIfAbsent("Reminder", Boolean.TRUE);
        this.settings.setIfAbsent("DBUsername", "null");
        this.settings.setIfAbsent("DBPassword", "null");
        this.settings.save();
        this.settings.setIfAbsent("one", Constant.one); // д��Ĭ��ֵ


        one = this.settings.getStringList("one").toArray(new String[0]);
        two = this.settings.getStringList("two").toArray(new String[0]);
        three = this.settings.getStringList("three").toArray(new String[0]);
        noUpThree = this.settings.getStringList("noUpThree").toArray(new String[0]);
        noUpTwo = this.settings.getStringList("noUpTwo").toArray(new String[0]);
        noUpOne = this.settings.getStringList("noUpOne").toArray(new String[0]);
        three_plus = this.settings.getStringList("three_plus").toArray(new String[0]);
        two_plus = this.settings.getStringList("two_plus").toArray(new String[0]);
        one_plus = this.settings.getStringList("one_plus").toArray(new String[0]);
        QqExclude = this.settings.getLongList("QqExclude").toArray(new Long[0]);
        username = this.settings.getString("DBUsername");
        password = this.settings.getString("DBPassword");
        this.enabled = false;
        this.Reminder = this.settings.getBoolean("Reminder");
        this.settings.save(); // �������ļ�


        if (username.equals("null")) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
            con.close();
        } catch (Exception e) {
            this.getLogger().error(e);
            this.getLogger().error("����settings.yaml����������mysql�˺ż�����!");
        }// ��ʼ�����ݿ�����

    }


    public void onEnable() {

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();

            if (messageInString.contains("#��ʼ�ǵ�") || (!enabled && settings.getBoolean("Enabled"))) {
                try {
                    jidaoStart(event);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (messageInString.contains("#��ʼ�ǵ�")) {
                    event.getSubject().sendMessage("��ʼ�ǵ�");
                }
            } // ��ʼ�ǵ�ʱ,����ȺԱ�б�����¼��
            else if (messageInString.contains("#�����ǵ�")) {
                event.getSubject().sendMessage("�����ǵ�");
                settings.set("Enabled", Boolean.FALSE);
                enabled = false;
                settings.save();
            } // �����ǵ�

            else if (messageInString.contains("#�ǵ� ")) {
                if (enabled) {
                    try {
                        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
                        boolean isFinal = false;
                        Calendar date = Calendar.getInstance();
                        if (messageInString.contains("β��")) {
                            isFinal = true;
                            messageInString = messageInString.replace("β�� ", "");
                        }
                        messageInString = messageInString.replace("#�ǵ� ", "");
                        long damage = Long.parseLong((messageInString));
                        PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
                        sql.setLong(3, damage);
                        sql.setBoolean(2, isFinal);
                        sql.setInt(4, date.get(Calendar.HOUR_OF_DAY) >= 5 ? date.get(Calendar.DATE) : date.get(Calendar.DATE) - 1);
                        sql.setLong(1, event.getSender().getId());
                        sql.executeUpdate();
                        event.getSubject().sendMessage("�ǵ��ɹ�");
                        con.close();
                    } catch (Exception e) {
                        event.getSubject().sendMessage("�ǵ�ʧ��");
                        this.getLogger().warning(e);
                    }
                }
            } //�ǵ�,���ݰ����˺� ʱ�� �Ƿ�β��

            if (messageInString.contains("#����")) {
                event.getSubject().sendMessage(helpMsg);
            } // ��ʾ������Ϣ

            if (messageInString.contains("#�鵶")) {
                if (messageInString.contains("#�鵶 ")) {
                    messageInString = messageInString.replace("#�鵶 ", "");
                    long qq = 0;
                    String nick = null;
                    Member temp = null;
                    try {
                        qq = Long.parseLong(messageInString);
                    } catch (Exception e) {
                        nick = messageInString;
                    }
                    for (Member member : memberList) {
                        if (member.getId() == qq || getNameCard(member).equals(nick)) {
                            temp = member;
                            qq = member.getId();
                            nick = getNameCard(member);
                        }
                    }
                    assert temp != null;
                    event.getSubject().sendMessage(getDamageString(query(temp)[0], query(temp)[1], temp));
                } else {
                    if (sender.getPermission().getLevel() > 0) {
                        event.getSubject().sendMessage("��ʼ�����ĳ������");
                        Objects.requireNonNull(getScheduler()).async((Runnable) this::queryAll); // ����©����С����
                    } else {
                        event.getSubject().sendMessage(("��ָ��Ϊ����Աר�õ�Ŷ~")); // ��ֹˢ��
                    }
                }

            } // �鿴����������
        }); // �Ŷ�ս

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(getNameCard(sender) + ':' + messageInString);
            if (messageInString.contains("#upʮ��")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(10, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessageAsync(new At(sender).plus("����?��������?����������ȥ�һ��"));
                }
            } else if (messageInString.contains("#ʮ��")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(10, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessageAsync(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#��")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(300, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessageAsync(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#up��")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(300, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessageAsync(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#up�鿨 ")) {
                if (Gashapon.isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#up�鿨 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = new Gashapon(q, true);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                        Gashapon.refreshCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessageAsync(("���ֽ�������"));
                    }
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessageAsync(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }
            } else if (messageInString.contains("#�鿨 ")) {
                if (Gashapon.isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#�鿨 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = new Gashapon(q, false);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                        Gashapon.refreshCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessageAsync(("���ֽ�������"));
                    }
                } else {
                    //������ȴ��ʾ��Ϣ
                    event.getSubject().sendMessage(new At(sender).plus("�鿨�����ô�죬�˼һ��ܲ��˵�"));
                }

            }

        }); // ģ�⿨��

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            if (event.getMessage().toString().contains("at") && event.getMessage().first(At.Key).getTarget() == event.getBot().getId()) {
                Random random = new Random();
                random.setSeed(new Date().getTime());

                if (event.getMessage().toString().contains("�Ұ���")) {
                    event.getSubject().sendMessage("��������˺��������İ�");
                    event.getSubject().sendMessage(getDamageString(query(event.getSender())[0], query(event.getSender())[1], event.getSender()));
                    event.getSubject().sendMessage("����ˮƽ?��");
                } else if (event.getMessage().toString().contains("��")) {
                    event.getSubject().sendMessage(kimo_Definde[random.nextInt(kimo_Definde.length)]);
                } else {
                    event.getSubject().sendMessage(responseStr[random.nextInt(responseStr.length)]);
                }
            }
        }); // д����

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
                            getLogger().warning(e);
                            return false;
                        }
                        break; // �Ƴ�����Ҫ�ǵ��ĳ�Ա


                    case "list":
                        for (Member member : memberList) {
                            try {
                                commandSender.sendMessageBlocking(member.getNameCard());
                            } catch (IllegalArgumentException e) {
                                commandSender.sendMessageBlocking(member.getNick());
                            }
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

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "reminder", new ArrayList<>(), " ������ҩ���� ", "/reminder [enable/disable]"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if (list.size() < 1) {
                    return false;
                }
                if ("enable".equals(list.get(0))) {
                    if (list.size() > 3) {
                        commandSender.sendMessageBlocking("/reminder [enable/disable]");
                        return false;
                    }
                    settings.set("Reminder", Boolean.TRUE);
                    Reminder = true;
                    settings.save();
                    try {
                        imgReminder = ((Member) memberList.toArray()[0]).getGroup().uploadImage(new File("./plugins/test/reminder.jpg"));
                        ((Member) memberList.toArray()[0]).getGroup().sendMessageAsync(imgReminder);
                        commandSender.sendMessageBlocking(" �����ɹ� ");
                        return true;
                    } catch (Exception e) {
                        commandSender.sendMessageBlocking(" ����ʧ��. ����ͼƬ�Ƿ�����ȷ·����?");
                        getLogger().error(e);
                        settings.set("Reminder", Boolean.FALSE);
                        Reminder = false;
                        settings.save();
                        return false;
                    }
                    // �Ƴ���¼����ļ�¼
                } else if ("disable".equals(list.get(0))) {
                    settings.set("Reminder", Boolean.FALSE);
                    Reminder = false;
                    settings.save();
                    return true;
                } else {
                    return false;
                }
            }
        });

        Objects.requireNonNull(getScheduler()).repeat(() -> {
            /*if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 2 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && this.enabled) {
                LinkedList<Member> loudao = queryAll();
                MessageChainBuilder message = new MessageChainBuilder();
                message.add("���׵�˹��,����");
                for (Member nmsl : loudao) {
                    message.add(new At(nmsl));
                    message.add(", ");
                }
                message.add("��û�г�������,������Ʋ�~~~");
                ((Member) memberList.toArray()[0]).getGroup().sendMessage(message.asMessageChain());
            }*/
            if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 6 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 12 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18) && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Reminder) {
                if (imgReminder == null) {
                    imgReminder = ((Member) memberList.toArray()[0]).getGroup().uploadImage(new File("./plugins/test/reminder.jpg"));
                }
                ((Member) memberList.toArray()[0]).getGroup().sendMessageAsync(imgReminder);
            }
            this.getLogger().debug("checking time");
        }, 60000); // ʹ����ķ���ʵ���Զ��鵶, ��ҩ����


        JCommandManager.getInstance().register(this, new BlockingCommand(
                "�鵶", new ArrayList<>(), " ������ ", ""
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                LinkedList<Member> loudao = queryAll();
                MessageChainBuilder msg = new MessageChainBuilder();
                msg.add("���׵�˹��,����");
                for (Member nmsl : loudao) {
                    msg.add(new At(nmsl));
                    msg.add(", ");
                }
                msg.add("��û�г�������,������Ʋ�~~~");
                getLogger().debug(msg.toString());
                return true;
            }
        });


        this.getLogger().info("�ǵ����Ѿ���");
    } // ע���������

    /**
     * ��ѯ�ض���Ա����ĳ������
     *
     * @param temp ����ѯ�ĳ�Ա
     * @return {������, ���˺�}
     */
    private long[] query(Member temp) {
        long count = 0;
        long totalDamage = 0;
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
            long qq = temp.getId();
            Calendar date = Calendar.getInstance();
            PreparedStatement sql;
            sql = con.prepareStatement("select -sum(isFinal-1), sum(damage) from records where memberID=? and date=?");
            sql.setInt(2, date.get(Calendar.HOUR_OF_DAY) >= 5 ? date.get(Calendar.DATE) : date.get(Calendar.DATE) - 1);
            sql.setLong(1, qq);
            ResultSet rs = sql.executeQuery();
            this.getLogger().info("��ѯ���ݿ�...");
            rs.next();
            totalDamage = rs.getLong(2);
            count = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            temp.getGroup().sendMessage("��ѯʧ��");
        }  // ��ѯ���˵��ռ�¼
        return new long[]{count, totalDamage};
    } // ���˲鵶��ʵ��

    /**
     * �ǵ����ĳ�ʼ��
     *
     * @param event ������
     */
    private void jidaoStart(GroupMessage event) throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
        this.getLogger().info("��ʼ�ǵ�");
        this.getLogger().debug(enabled + " " + this.settings.getBoolean("Enabled"));
        this.settings.set("Enabled", Boolean.TRUE);
        this.enabled = true;
        this.settings.save();
        for (Member i : event.getGroup().getMembers()) {
            if (i != null) {
                memberList.add(i);
                getLogger().debug(getNameCard(i));
            }
        }
        for (long qq : QqExclude) {
            try {
                con.prepareStatement(String.format("delete from records where pcr.records.memberID=%d", qq)).executeUpdate();
                memberList.removeIf(member -> member.getId() == qq);
            } catch (Exception e) {
                getLogger().debug(e);
            }
        }
        con.close();
    }



    /**
     * ȫԱ�鵶,������Ϣ
     *
     * @return ����û�д��������ĳ�Ա
     */
    public LinkedList<Member> queryAll() {
        long totalDamage;
        LinkedList<Member> loudao = new LinkedList<>();
        long count;
        MessageChainBuilder msg = new MessageChainBuilder();
        for (Member member : memberList) {
            count = 0;
            try {
                count = query(member)[0];
                totalDamage = query(member)[1];
                msg.add(getDamageString(count, totalDamage, member) + "\n");
                this.getLogger().info("��ѯ���" + getDamageString(count, totalDamage, member));
            } catch (Exception e) {
                e.printStackTrace();
                this.getLogger().error("��ѯʧ��" + e);
            }
            if (count < 3) {
                loudao.add(member);
            }
        }
        ((Member) memberList.toArray()[0]).getGroup().sendMessage(msg.asMessageChain());
        return loudao;
    } // ȫԱ�鵶��ʵ��

    /**
     * ��Ϊapi���nameCardOrNick���ò���,�ֶ�дһ����Ϊapi���nameCardOrNick���ò���,�ֶ�дһ��
     *
     * @param temp ��Ա
     * @return ��ԱȺ��Ƭ, Ϊ��ʱ�����ǳ�
     */
    public String getNameCard(Member temp) {
        return (temp.getNameCard().equals("") ? temp.getNick() : temp.getNameCard());
    }

    /**
     * ��֯�鵶��Ϣ
     *
     * @param count  ��������
     * @param damage �˺�
     * @param member ��Ա
     * @return ��֯�õ��ַ���
     */
    public String getDamageString(long count, long damage, Member member) {
        return ("����" + getNameCard(member) + "����" + count + "��, " + "���" + damage + "���˺�, Լ" + damage / 80000 + "��");
    }
}
// TODO: ��������ݼ�¼(pic)
// todo: ʹ��excel����ҳչʾͳ������
// todo: ��Ⱥ֧��
