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
 * 插件主体
 */
class pcrMain extends PluginBase {
    private static Long[] QqExclude = {1466131295L, // Elish
            2314125066L, // 浮
            3378035874L, // 我很好奇
            3236214319L, // 黑
            11413446L, // 哈特度
            2855958676L, // 群主宝宝妈妈爱你
            2371617404L};// 预定义的排除列表
    static String[] one, two, three, noUpThree, one_plus, two_plus, three_plus, noUpTwo, noUpOne;
    private Config settings; // 配置文件
    private HashSet<Member> memberList; // 记刀的成员列表
    private boolean enabled; // 团队战开关
    private boolean Reminder; // 小助手开关
    private Image imgReminder; // 小助手资源
    static HashMap<String, String> coolDown; //抽卡冷却时间
    String username;
    String password;// 数据库链接
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
        this.settings.setIfAbsent("one", Constant.one); // 写入默认值


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
        this.settings.save(); // 读配置文件


        if (username.equals("null")) {
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
            con.close();
        } catch (Exception e) {
            this.getLogger().error(e);
            this.getLogger().error("请在settings.yaml中设置您的mysql账号及密码!");
        }// 初始化数据库连接

    }


    public void onEnable() {

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();

            if (messageInString.contains("#开始记刀") || (!enabled && settings.getBoolean("Enabled"))) {
                try {
                    jidaoStart(event);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (messageInString.contains("#开始记刀")) {
                    event.getSubject().sendMessage("开始记刀");
                }
            } // 开始记刀时,根据群员列表建立记录表
            else if (messageInString.contains("#结束记刀")) {
                event.getSubject().sendMessage("结束记刀");
                settings.set("Enabled", Boolean.FALSE);
                enabled = false;
                settings.save();
            } // 结束记刀

            else if (messageInString.contains("#记刀 ")) {
                if (enabled) {
                    try {
                        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
                        boolean isFinal = false;
                        Calendar date = Calendar.getInstance();
                        if (messageInString.contains("尾刀")) {
                            isFinal = true;
                            messageInString = messageInString.replace("尾刀 ", "");
                        }
                        messageInString = messageInString.replace("#记刀 ", "");
                        long damage = Long.parseLong((messageInString));
                        PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
                        sql.setLong(3, damage);
                        sql.setBoolean(2, isFinal);
                        sql.setInt(4, date.get(Calendar.HOUR_OF_DAY) >= 5 ? date.get(Calendar.DATE) : date.get(Calendar.DATE) - 1);
                        sql.setLong(1, event.getSender().getId());
                        sql.executeUpdate();
                        event.getSubject().sendMessage("记刀成功");
                        con.close();
                    } catch (Exception e) {
                        event.getSubject().sendMessage("记刀失败");
                        this.getLogger().warning(e);
                    }
                }
            } //记刀,内容包括伤害 时间 是否尾刀

            if (messageInString.contains("#帮助")) {
                event.getSubject().sendMessage(helpMsg);
            } // 显示帮助信息

            if (messageInString.contains("#查刀")) {
                if (messageInString.contains("#查刀 ")) {
                    messageInString = messageInString.replace("#查刀 ", "");
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
                        event.getSubject().sendMessage("开始查今天的出刀情况");
                        Objects.requireNonNull(getScheduler()).async((Runnable) this::queryAll); // 揪出漏刀的小朋友
                    } else {
                        event.getSubject().sendMessage(("该指令为管理员专用的哦~")); // 防止刷屏
                    }
                }

            } // 查看当天出刀情况
        }); // 团队战

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(getNameCard(sender) + ':' + messageInString);
            if (messageInString.contains("#up十连")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(10, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessageAsync(new At(sender).plus("还抽?还有钻吗?给你两分钟去氪一单"));
                }
            } else if (messageInString.contains("#十连")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(10, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessageAsync(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#井")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(300, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessageAsync(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#up井")) {
                if (Gashapon.isCool(sender.getId())) {
                    Gashapon gashapon = new Gashapon(300, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                    Gashapon.refreshCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessageAsync(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#up抽卡 ")) {
                if (Gashapon.isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#up抽卡 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = new Gashapon(q, true);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                        Gashapon.refreshCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessageAsync(("数字解析错误"));
                    }
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessageAsync(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#抽卡 ")) {
                if (Gashapon.isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#抽卡 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = new Gashapon(q, false);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.getData()));
                        Gashapon.refreshCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessageAsync(("数字解析错误"));
                    }
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }

            }

        }); // 模拟卡池

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            if (event.getMessage().toString().contains("at") && event.getMessage().first(At.Key).getTarget() == event.getBot().getId()) {
                Random random = new Random();
                random.setSeed(new Date().getTime());

                if (event.getMessage().toString().contains("我爱你")) {
                    event.getSubject().sendMessage("请用你的伤害来表达你的爱");
                    event.getSubject().sendMessage(getDamageString(query(event.getSender())[0], query(event.getSender())[1], event.getSender()));
                    event.getSubject().sendMessage("就这水平?爬");
                } else if (event.getMessage().toString().contains("妈")) {
                    event.getSubject().sendMessage(kimo_Definde[random.nextInt(kimo_Definde.length)]);
                } else {
                    event.getSubject().sendMessage(responseStr[random.nextInt(responseStr.length)]);
                }
            }
        }); // 写着玩

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "member", new ArrayList<>(), " 管理需要记刀的成员 ", "/member remove/list"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if (list.size() < 1) {
                    return false;
                }
                switch (list.get(0)) {
                    case "remove":
                        if (list.size() < 2) {
                            commandSender.sendMessageBlocking("/member remove 成员qq ");
                            return true;
                        }
                        long qq = Long.parseLong(list.get(1).trim());
                        try {
                            con.prepareStatement(String.format("delete from records where pcr.records.memberID=%d", qq)).executeUpdate();
                            memberList.removeIf(member -> member.getId() == qq);
                            commandSender.sendMessageBlocking(" 移除成功 ");
                        } catch (Exception e) {
                            commandSender.sendMessageBlocking(" 移除失败. 是否已经移除? ");
                            getLogger().warning(e);
                            return false;
                        }
                        break; // 移除不需要记刀的成员


                    case "list":
                        for (Member member : memberList) {
                            try {
                                commandSender.sendMessageBlocking(member.getNameCard());
                            } catch (IllegalArgumentException e) {
                                commandSender.sendMessageBlocking(member.getNick());
                            }
                        }
                        break; // 查看当前记刀的成员
                    default:
                        return false;
                }
                return true;
            }
        });


        JCommandManager.getInstance().register(this, new BlockingCommand(
                "records", new ArrayList<>(), " 管理数据库数据 ", "/records remove qq damage"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                if (list.size() < 1) {
                    return false;
                }
                if ("remove".equals(list.get(0))) {
                    if (list.size() < 2) {
                        commandSender.sendMessageBlocking("/member remove 成员qq 成员伤害");
                        return true;
                    }
                    long qq = Long.parseLong(list.get(1).trim());
                    long damage = Long.parseLong(list.get(2).trim());
                    try {
                        con.prepareStatement(String.format("delete from records where pcr.records.memberID=%d and pcr.records.damage=%d", qq, damage)).executeUpdate();
                        commandSender.sendMessageBlocking(" 移除成功 ");
                    } catch (Exception e) {
                        commandSender.sendMessageBlocking(" 移除失败. 是否已经移除? ");
                        return false;
                    }
                    // 移除记录错误的记录
                } else {
                    return false;
                }
                return true;
            }
        });

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "reminder", new ArrayList<>(), " 开关买药提醒 ", "/reminder [enable/disable]"
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
                        commandSender.sendMessageBlocking(" 开启成功 ");
                        return true;
                    } catch (Exception e) {
                        commandSender.sendMessageBlocking(" 开启失败. 请检查图片是否在正确路径下?");
                        getLogger().error(e);
                        settings.set("Reminder", Boolean.FALSE);
                        Reminder = false;
                        settings.save();
                        return false;
                    }
                    // 移除记录错误的记录
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
                message.add("牙白德斯内,今天");
                for (Member nmsl : loudao) {
                    message.add(new At(nmsl));
                    message.add(", ");
                }
                message.add("还没有出满三刀,请接受制裁~~~");
                ((Member) memberList.toArray()[0]).getGroup().sendMessage(message.asMessageChain());
            }*/
            if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 6 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 12 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18) && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Reminder) {
                if (imgReminder == null) {
                    imgReminder = ((Member) memberList.toArray()[0]).getGroup().uploadImage(new File("./plugins/test/reminder.jpg"));
                }
                ((Member) memberList.toArray()[0]).getGroup().sendMessageAsync(imgReminder);
            }
            this.getLogger().debug("checking time");
        }, 60000); // 使用最笨的方法实现自动查刀, 买药提醒


        JCommandManager.getInstance().register(this, new BlockingCommand(
                "查刀", new ArrayList<>(), " 测试用 ", ""
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                LinkedList<Member> loudao = queryAll();
                MessageChainBuilder msg = new MessageChainBuilder();
                msg.add("牙白德斯内,今天");
                for (Member nmsl : loudao) {
                    msg.add(new At(nmsl));
                    msg.add(", ");
                }
                msg.add("还没有出满三刀,请接受制裁~~~");
                getLogger().debug(msg.toString());
                return true;
            }
        });


        this.getLogger().info("记刀器已就绪");
    } // 注册监听器们

    /**
     * 查询特定成员当天的出刀情况
     *
     * @param temp 待查询的成员
     * @return {出刀数, 总伤害}
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
            this.getLogger().info("查询数据库...");
            rs.next();
            totalDamage = rs.getLong(2);
            count = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            temp.getGroup().sendMessage("查询失败");
        }  // 查询单人单日记录
        return new long[]{count, totalDamage};
    } // 单人查刀的实现

    /**
     * 记刀器的初始化
     *
     * @param event 上下文
     */
    private void jidaoStart(GroupMessage event) throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
        this.getLogger().info("开始记刀");
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
     * 全员查刀,发送消息
     *
     * @return 返回没有打满三刀的成员
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
                this.getLogger().info("查询完成" + getDamageString(count, totalDamage, member));
            } catch (Exception e) {
                e.printStackTrace();
                this.getLogger().error("查询失败" + e);
            }
            if (count < 3) {
                loudao.add(member);
            }
        }
        ((Member) memberList.toArray()[0]).getGroup().sendMessage(msg.asMessageChain());
        return loudao;
    } // 全员查刀的实现

    /**
     * 因为api里的nameCardOrNick调用不了,手动写一个因为api里的nameCardOrNick调用不了,手动写一个
     *
     * @param temp 成员
     * @return 成员群名片, 为空时返回昵称
     */
    public String getNameCard(Member temp) {
        return (temp.getNameCard().equals("") ? temp.getNick() : temp.getNameCard());
    }

    /**
     * 组织查刀消息
     *
     * @param count  出道次数
     * @param damage 伤害
     * @param member 成员
     * @return 组织好的字符串
     */
    public String getDamageString(long count, long damage, Member member) {
        return ("今天" + getNameCard(member) + "共出" + count + "刀, " + "造成" + damage + "点伤害, 约" + damage / 80000 + "淡");
    }
}
// TODO: 加入简单阵容记录(pic)
// todo: 使用excel或网页展示统计数据
// todo: 多群支持
