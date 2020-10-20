package com.viger.plugin;

import com.viger.plugin.commands.*;
import com.viger.plugin.listensers.ClanListener;
import com.viger.plugin.listensers.GashaponListener;
import com.viger.plugin.listensers.NewsFeederListener;
import com.viger.plugin.listensers.RankListener;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;


/**
 * 插件主体
 */
public class pcrMain extends JavaPlugin {
    public final static pcrMain INSTANCE = new pcrMain();
    public static Group group;
    static List<String> one, two, three, noUpThree, one_plus, two_plus, three_plus, noUpTwo, noUpOne;
    static HashMap<String, String> coolDown;     //抽卡冷却时间
    public final PcrData data = PcrData.INSTANCE; // 配置文件
    public String username;
    public String password;
    public Set<Long> memberList; // 记刀的成员列表
    public boolean enabled; // 团队战开关
    public Long groupID;
    public boolean ReminderSwitch; // 小助手开关
    public boolean feederSwitch; // 新闻推送开关
    public boolean rankSwitch; // 会战排名开关
    public Image imgReminder; // 小助手资源
    public NewsFeeder feeder; // 新闻订阅器
    public Rank rank;
    public Connection con;// 数据库链接

    public pcrMain() {
        super(new JvmPluginDescriptionBuilder("xyz.viger.pcrplugin", "1.0.0-dev-1").author("viger").build());
    }


    public void onLoad() {
        reloadPluginConfig(data);

    }

    @Override
    public void onEnable() {
        this.feeder = NewsFeeder.INSTANCE;
        this.rank = Rank.INSTANCE;
        one = data.getGashpon().getOne();
        two = data.getGashpon().getTwo();
        three = data.getGashpon().getThree();
        noUpThree = data.getGashpon().getNoUpThree();
        noUpTwo = data.getGashpon().getNoUpTwo();
        noUpOne = data.getGashpon().getNoUpOne();
        three_plus = data.getGashpon().getThree_plus();
        two_plus = data.getGashpon().getTwo_plus();
        one_plus = data.getGashpon().getOne_plus();
        memberList = data.getMemberList();
        username = data.getDataBase().getDBUserName();
        password = data.getDataBase().getDBPassword();
        groupID = data.getGroup();
        rank.add("L.S.P.");
        this.enabled = data.getClanSwitch();
        this.ReminderSwitch = data.getReminderSwitch();
        this.rankSwitch = data.getRankSwitch();
        this.feederSwitch = data.getFeederSwitch();
        Objects.requireNonNull(getScheduler()).delayed(4000, () -> group = Bot.getBotInstances().get(0).getGroup(groupID));
        if (!username.equals("username")) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/pcr", username, password);
                con.close();
            } catch (Exception e) {
                this.getLogger().error(e);
                this.getLogger().error("请在settings.yaml中设置您的mysql账号及密码!");
            }// 初始化数据库连接
        }


        Objects.requireNonNull(getScheduler()).repeating(60000, () -> {
            if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 6 ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0 ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 12 ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18) &&
                    Calendar.getInstance().get(Calendar.MINUTE) == 0 &&
                    ReminderSwitch) {
                if (imgReminder == null) {
                    imgReminder = group.uploadImage(new File("./plugins/test/reminder.jpg"));
                }
                group.sendMessage(imgReminder);
            }
            this.getLogger().debug("checking time");
        }); // 使用最笨的方法实现自动查刀, 买药提醒

        getScheduler().delayed(4000, () -> Objects.requireNonNull(getScheduler()).repeating(600000, () -> {
            try {
                if (rankSwitch) {
                    rank.update();
                }

                if (feeder.unread() && feederSwitch) {
                    getLogger().debug("检查到更新");
                    for (Message msg : feeder.fetch(group)) {
                        group.sendMessage(msg);
                        getLogger().debug(msg.contentToString());
                    }
                }
                this.getLogger().debug("检查动态更新");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        getScheduler().delayed(4000, () -> Objects.requireNonNull(getScheduler()).repeating(1800000, () -> {
            if (rankSwitch) {
                MessageChainBuilder builder = new MessageChainBuilder();
                try {
                    rank.update();
                    for (String clanname :
                            rank.clanName.keySet()) {
                        builder.add(rank.query(clanname));
                    }
                    this.getLogger().debug("检查排名更新");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                group.sendMessage(builder.asMessageChain());
            }
        }));

        Events.registerEvents(ClanListener.INSTANCE);
        Events.registerEvents(GashaponListener.INSTANCE);
        Events.registerEvents(NewsFeederListener.INSTANCE);
        Events.registerEvents(RankListener.INSTANCE);

        CommandManager.INSTANCE.registerCommand(ClanMemberCommand.INSTANCE, true);
        CommandManager.INSTANCE.register(ClanRecordsCommand.INSTANCE, true);
        CommandManager.INSTANCE.register(RankCommand.INSTANCE, true);
        CommandManager.INSTANCE.register(NewsFeederCommand.INSTANCE, true);
        CommandManager.INSTANCE.register(ReminderCommand.INSTANCE, true);


        this.getLogger().info("记刀器已就绪");
    } // 注册监听器们 todo: 模块化


    /**
     * 因为api里的nameCardOrNick调用不了,手动写一个因为api里的nameCardOrNick调用不了,手动写一个
     *
     * @param temp 成员
     * @return 成员群名片, 为空时返回昵称
     */
    public String getNameCard(Member temp) {
        return (temp.getNameCard().equals("") ? temp.getNick() : temp.getNameCard());
    }


}
// todo: 使用excel或网页展示统计数据
// todo: 多群支持
