package com.viger.plugin.listensers;

import com.viger.plugin.ClanBattle;
import com.viger.plugin.damageByMember;
import com.viger.plugin.pcrMain;
import com.viger.plugin.record;
import kotlin.coroutines.CoroutineContext;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Date;
import java.util.*;

import static com.viger.plugin.Constant.*;

public class ClanListener extends SimpleListenerHost {
    public static ClanListener INSTANCE = new ClanListener();
    pcrMain plugin = pcrMain.INSTANCE;

    String username;
    String password;// 数据库链接
    private Connection con;

    @Override
    public void handleException(@org.jetbrains.annotations.NotNull CoroutineContext context, @org.jetbrains.annotations.NotNull Throwable exception) {
        super.handleException(context, exception);
        plugin.getLogger().error(exception);
    }

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {
        String messageInString = event.getMessage().contentToString();
        Member sender = event.getSender();

        if (messageInString.contains("#开始记刀")) {
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
            plugin.getLogger().debug(event.getGroup().getName() + "结束记刀");
            plugin.enabled = false;
        } // 结束记刀

        else if (messageInString.contains("#记刀 ")) {
            if (plugin.enabled) {
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
                    plugin.getLogger().warning(e);
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
                    nick = messageInString.replace("@", "");
                }
                for (Long memberID : plugin.memberList) {
                    if (memberID == qq || plugin.getNameCard(sender.getGroup().getMembers().get(memberID)).equals(nick)) {
                        qq = memberID;
                        nick = plugin.getNameCard(sender.getGroup().getMembers().get(memberID));
                    }
                }
                event.getSubject().sendMessage(getDamageString(query(temp.getId())[0], query(temp.getId())[1], temp.getId()));
            } else {
                if (sender.getPermission().getLevel() > 0) {
                    event.getSubject().sendMessage("开始查今天的出刀情况");
                    Objects.requireNonNull(plugin.getScheduler()).async((Runnable) this::queryAll); // 揪出漏刀的小朋友
                } else {
                    event.getSubject().sendMessage(("该指令为管理员专用的哦~")); // 防止刷屏
                }
            }

        } // 查看当天出刀情况

        if (event.getMessage().toString().contains("at") &&
                Objects.requireNonNull(event.getMessage().first(At.Key)).getTarget() == event.getBot().getId()) {
            Random random = new Random();
            random.setSeed(new Date().getTime());

            if (event.getMessage().toString().contains("我爱你")) {
                event.getSubject().sendMessage("请用你的伤害来表达你的爱");
                event.getSubject().sendMessage(getDamageString(query(event.getSender().getId())[0], query(event.getSender().getId())[1], event.getSender().getId()));
                event.getSubject().sendMessage("就这水平?爬");
            } else if (event.getMessage().toString().contains("妈")) {
                event.getSubject().sendMessage(kimo_Definde[random.nextInt(kimo_Definde.length)]);
            } else {
                event.getSubject().sendMessage(responseStr[random.nextInt(responseStr.length)]);
            }
        }

        if (messageInString.equals("查刀")) {
            plugin.status.update();
            event.getSubject().sendMessage("今天已经出了" + ClanBattle.INSTANCE.getTotal_damage_num() + "刀");
            for (damageByMember i : ClanBattle.INSTANCE.getRecords()) {
                if (i.getNumber() < 3) {
                    event.getSubject().sendMessage(i.getName() + "已经出了" + i.getNumber() + "刀");
                }
            }
        } else if (event.getMessage().toString().contains("查刀 ")) {
            for (damageByMember i : ClanBattle.INSTANCE.getRecords()) {
                if (i.getName().contains(messageInString.replace("查刀 ", ""))) {
                    event.getSubject().sendMessage(i.getName() + "已经出了" + i.getNumber() + "刀, 造成了" + i.getDamage() + "伤害， 得分" + i.getScore());
                    for (record r : i.getDamage_list()) {
                        event.getSubject().sendMessage(r.toString());
                    }
                }
            }

        }
    } // 团队战


    /**
     * 全员查刀,发送消息
     *
     * @return 返回没有打满三刀的成员
     */

    public LinkedList<Long> queryAll() {
        long totalDamage;
        LinkedList<Long> loudao = new LinkedList<>();
        long count;
        MessageChainBuilder msg = new MessageChainBuilder();
        for (Long member : plugin.memberList) {
            count = 0;
            try {
                count = query(member)[0];
                totalDamage = query(member)[1];
                msg.add(getDamageString(count, totalDamage, member) + "\n");
                plugin.getLogger().info("查询完成" + getDamageString(count, totalDamage, member));
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().error("查询失败" + e);
            }
            if (count < 3) {
                loudao.add(member);
            }
        }
        pcrMain.group.sendMessage(msg.asMessageChain());
        return loudao;
    } // 全员查刀的实现

    /**
     * 组织查刀消息
     *
     * @param count    出刀次数
     * @param damage   伤害
     * @param memberID 成员
     * @return 组织好的字符串
     */
    public String getDamageString(long count, long damage, Long memberID) {
        return ("今天" + plugin.getNameCard(pcrMain.group.getMembers().get(memberID)) + "共出" + count + "刀, " + "造成" + damage + "点伤害, 约" + damage / 80000 + "淡");
    }

    /**
     * 查询特定成员当天的出刀情况
     *
     * @param memberID 待查询的成员
     * @return {出刀数, 总伤害}
     */
    private long[] query(Long memberID) {
        long count = 0;
        long totalDamage = 0;
        try {
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
            long qq = memberID;
            Calendar date = Calendar.getInstance();
            PreparedStatement sql;
            sql = con.prepareStatement("select -sum(isFinal-1), sum(damage) from records where memberID=? and date=?");
            sql.setInt(2, date.get(Calendar.HOUR_OF_DAY) >= 5 ? date.get(Calendar.DATE) : date.get(Calendar.DATE) - 1);
            sql.setLong(1, qq);
            ResultSet rs = sql.executeQuery();
            plugin.getLogger().info("查询数据库...");
            rs.next();
            totalDamage = rs.getLong(2);
            count = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            pcrMain.group.sendMessage("查询失败");
        }  // 查询单人单日记录
        return new long[]{count, totalDamage};
    } // 单人查刀的实现

    /**
     * 记刀器的初始化
     *
     * @param event 上下文
     */
    private void jidaoStart(GroupMessageEvent event) throws SQLException {
        con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
        plugin.getLogger().info("开始记刀");
        plugin.getLogger().debug(plugin.enabled + " " + pcrMain.group);
        plugin.enabled = true;
        plugin.groupID = event.getGroup().getId();
        pcrMain.group = Bot.getBotInstances().get(0).getGroup(plugin.groupID);
        if (plugin.memberList.size() == 0) {
            for (Member i : event.getGroup().getMembers()) {
                if (i != null) {
                    plugin.memberList.add(i.getId());
                    plugin.getLogger().debug(plugin.getNameCard(i));
                }
            }
        }
        con.close();
    }


}
