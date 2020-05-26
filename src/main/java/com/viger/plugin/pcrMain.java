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
 * 插件主体
 */
class pcrMain extends PluginBase {
    private Config settings;
    private HashSet<Member> memberList;
    private boolean enabled;
    private Connection con;
    private static HashMap<String, String> coolDown; //抽卡冷却时间


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
            this.getLogger().error("请在settings.yaml中设置您的mysql账号及密码!");
        }
        // 初始化
    }


    public void onEnable() {

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();

            if (messageInString.contains("开始记刀")) {
                this.getLogger().info("开始记刀");
                this.settings.set("Enabled", Boolean.TRUE);
                this.enabled = true;
                this.settings.save();
                for (Member i : event.getGroup().getMembers()) {
                    memberList.add(i);
                }
            } // 开始记刀时,根据群员列表建立记录表
            else if (messageInString.contains("结束记刀")) {
                this.getLogger().info("结束记刀");
                this.settings.set("Enabled", Boolean.FALSE);
                this.enabled = false;
                this.settings.save();
            } // 结束记刀

            else if (messageInString.contains("记刀 ")) {
                if (this.enabled) {
                    try {
                        boolean isFinal = false;

                        if (messageInString.contains("尾刀")) {
                            isFinal = true;
                            messageInString = messageInString.replace("尾刀 ", "");
                        }
                        messageInString = messageInString.replace("记刀 ", "");
                        long damage = Long.parseLong((messageInString));
                        PreparedStatement sql = con.prepareStatement("insert into records values (?,?,?,?)");
                        sql.setLong(3, damage);
                        sql.setBoolean(2, isFinal);
                        sql.setInt(4, new Date().getDay());
                        sql.setLong(1, event.getSender().getId());
                        sql.executeUpdate();
                        event.getSubject().sendMessage("记刀成功");
                    } catch (Exception e) {
                        event.getSubject().sendMessage("记刀失败");
                        this.getLogger().warning(e);
                    }
                }
            } //记刀,内容包括伤害 时间 是否尾刀

            else if (messageInString.contains("#帮助")) {
                event.getSubject().sendMessage(helpMsg);
            } // 显示帮助信息

            if (messageInString.contains("查刀")) {
                event.getSubject().sendMessage("开始查今天的出刀情况");
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
                            this.getLogger().info("查询数据库...");
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
                            this.getLogger().info("查询完成");

                        } catch (SQLException e) {
                            e.printStackTrace();
                            this.getLogger().info("查询失败");
                        }
                        if (count < 3) {
                            loudao.add(member);
                        }
                        event.getSubject().sendMessageAsync("今天" + member.getNick() + "共出" + count + "刀, " + "造成" + totalDamage + "点伤害;");
                    }

                    MessageChainBuilder message = new MessageChainBuilder();
                    message.add("牙白德斯内,今天");
                    for (Member nmsl : loudao) {
                        message.add(new At(nmsl));
                        message.append(", ");
                    }
                    message.append("还没有出满三道,请接受制裁~~~");
                    event.getSubject().sendMessage(message.toString());
                }); // 揪出漏刀的小朋友


                // 查询一天的记录情况并发送提醒


            } // 查看当天出刀情况
        });

        this.getEventListener().subscribeAlways(GroupMessage.class, (GroupMessage event) -> {
            String messageInString = event.getMessage().contentToString();
            Member sender = event.getSender();
            this.getLogger().info(sender.getNick() + ':' + messageInString);
            if (messageInString.contains("#up十连")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(10, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#十连")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(10, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#井")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(300, false);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#up井")) {
                if (isCool(sender.getId())) {
                    Gashapon gashapon = dp_Gashapon(300, true);
                    event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                    reFlashCoolDown(sender.getId());
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#up抽卡")) {
                if (isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#up抽卡 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = dp_Gashapon(q, true);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                        reFlashCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessage(("数字解析错误"));
                    }
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }
            } else if (messageInString.contains("#抽卡")) {
                if (isCool(sender.getId())) {
                    String str = messageInString.replaceAll("#抽卡 ", "");
                    try {
                        int q = Integer.parseInt(str);
                        Gashapon gashapon = dp_Gashapon(q, false);
                        event.getSubject().sendMessageAsync(new At(sender).plus(gashapon.data));
                        reFlashCoolDown(sender.getId());
                    } catch (NumberFormatException e) {
                        event.getSubject().sendMessage(("数字解析错误"));
                    }
                } else {
                    //发送冷却提示消息
                    event.getSubject().sendMessage(new At(sender).plus("抽卡抽的那么快，人家会受不了的"));
                }

            }

        }); // 模拟卡池

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
        this.getLogger().info("Plugin loaded!");
    }


    /**
     * up池的概率
     *
     * @param num
     * @return
     */
    public Gashapon dp_Gashapon(int num, boolean isUp) {
        if (isUp) {

        }
        Random random = new Random();
        random.setSeed(new Date().getTime());
        int on = 0, tw = 0, thre = 0; // 抽出来的三星二星有几个

        //无保底
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
        //有保底
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
            if (q < 7 && isUp) {//抽不抽的出来亚里沙
                map1.merge(Three_plus[random.nextInt(Three_plus.length)], 1, Integer::sum);
            } else {
                map1.merge(noUpThree[random.nextInt(Three.length)], 1, Integer::sum);
            }
        }
        for (int i = 0; i < tw; i++) {
            int j = random.nextInt(two.length);
            map2.merge(two[j], 1, Integer::sum); // todo: 二星up
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
     * 组织抽卡结果
     */
    public String get_GashaponString(int on, int tw, int thre, HashMap<String, Integer> map1, HashMap<String, Integer> map2, HashMap<String, Integer> map3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("一共抽出了");
        if (thre != 0) {
            stringBuilder.append(thre).append("个三星");
        }
        if (tw != 0) {
            stringBuilder.append(tw).append("个二星");
        }
        if (on != 0) {
            stringBuilder.append(on).append("个一星");
        }
        Set<String> set1 = map1.keySet();
        Set<String> set2 = map2.keySet();
        Set<String> set3 = map3.keySet();
        if (thre != 0) {
            stringBuilder.append("\n三星角色有：");
            for (String s : set1) {
                stringBuilder.append(s).append("*").append(map1.get(s)).append(",");
            }
        }
        if (tw != 0) {
            stringBuilder.append("\n二星角色有：");
            for (String s : set2) {
                stringBuilder.append(s).append("*").append(map2.get(s)).append(",");
            }
        }

        if (on != 0) {
            stringBuilder.append("\n一星角色有：");
            for (String s : set3) {
                stringBuilder.append(s).append("*").append(map3.get(s)).append(",");
            }
        }

        return stringBuilder.toString();
    }


    /**
     * 刷新抽卡冷却时间
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
     * 获取冷却时间是不是到了
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
// TODO: add timer (定时查刀, 提醒买药小助手(pic))
// TODO: 加入简单阵容记录(pic)
// todo: 使用excel或网页展示统计数据