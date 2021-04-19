package com.viger.plugin;

import com.viger.plugin.commands.GashaponCommand;
import com.viger.plugin.commands.NewsFeederCommand;
import com.viger.plugin.commands.ReminderCommand;
import com.viger.plugin.listensers.GashaponListener;
import com.viger.plugin.listensers.NewsFeederListener;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.util.*;


/**
 * 插件主体
 */
public class pcrMain extends JavaPlugin {
    public final static pcrMain INSTANCE = new pcrMain();
    public static Group group;
    static List<Integer> star3, star2, star1, up;
    static Map<Integer, List<String>> chara;
    static int up_prob, s3_prob, s2_prob;
    static HashMap<String, String> coolDown;     //抽卡冷却时间
    public final PcrData data = PcrData.INSTANCE; // 配置文件
    public String username;
    public String password;
    public String cookie; // bigfun cookie
    public Set<Long> memberList; // 记刀的成员列表
    public boolean enabled; // 团队战开关
    public Long groupID;
    public boolean ReminderSwitch; // 小助手开关
    public boolean feederSwitch; // 新闻推送开关
    public boolean rankSwitch; // 会战排名开关
    public Image imgReminder; // 小助手资源
    public NewsFeeder feeder; // 新闻订阅器
    public Gashapon gashapon;
    public Connection con;// 数据库链接

    public pcrMain() {
        super(new JvmPluginDescriptionBuilder("xyz.viger.pcrplugin", "1.0.0-dev-2").author("viger").build());
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        reloadPluginConfig(data);

    }

    @Override
    public void onEnable() {
        s3_prob = data.getGashpon().getS3_prob();
        s2_prob = data.getGashpon().getS2_prob();
        up_prob = data.getGashpon().getUp_prob();
        star3 = data.getGashpon().getStar3();
        star2 = data.getGashpon().getStar2();
        star1 = data.getGashpon().getStar1();
        chara = data.getGashpon().getChara();
        up = data.getGashpon().getUp();
        memberList = data.getMemberList();
        username = data.getDataBase().getDBUserName();
        password = data.getDataBase().getDBPassword();
        cookie = data.getCookie();
        groupID = data.getGroup();
        this.enabled = data.getClanSwitch();
        this.ReminderSwitch = data.getReminderSwitch();
        this.rankSwitch = data.getRankSwitch();
        this.feederSwitch = data.getFeederSwitch();
        getScheduler().delayed(12000, () -> {
            Bot bot = Bot.getInstances().get(0);
            group = bot.getGroup(1091221719);
            this.feeder = NewsFeeder.getInstance();
            this.gashapon = Gashapon.INSTANCE;
            bot.getEventChannel().registerListenerHost(GashaponListener.INSTANCE);
            bot.getEventChannel().registerListenerHost(NewsFeederListener.INSTANCE);
        }); //延时初始化


        getScheduler().delayed(12000, () -> getScheduler().repeating(60000, () -> {
            if ((Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 6 ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0 ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 12 ||
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 18) &&
                    Calendar.getInstance().get(Calendar.MINUTE) == 0) {
                if (ReminderSwitch) {
                    if (imgReminder == null) {
                        imgReminder = Contact.uploadImage(group, new File("./config/xyz.viger.pcrplugin/reminder.jpg"));
                    }
                    group.sendMessage(imgReminder);
                }
                gashapon.update();
            }
            this.getLogger().debug("checking time");
        })); // 使用最笨的方法实现自动查刀, 买药提醒, 卡池更新

        getScheduler().delayed(16000, () -> getScheduler().repeating(600000, () -> {
            try {
                if (feeder.unread() && feederSwitch) {
                    getLogger().debug("检查到更新");
                    for (MessageChain msg : feeder.fetch(group)) {
                        group.sendMessage(msg.contentToString());
                        getLogger().debug(msg.contentToString());
                    }
                }
                this.getLogger().debug("检查动态更新");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        CommandManager.INSTANCE.registerCommand(GashaponCommand.INSTANCE, true);
        CommandManager.INSTANCE.registerCommand(NewsFeederCommand.INSTANCE, true);
        CommandManager.INSTANCE.registerCommand(ReminderCommand.INSTANCE, true);


        this.getLogger().info("记刀器已就绪");
    } // 注册监听器们


    /**
     * 因为api里的nameCardOrNick调用不了,手动写一个因为api里的nameCardOrNick调用不了,手动写一个
     *
     * @param temp 成员
     * @return 成员群名片, 为空时返回昵称
     */
    public String getNameCard(Member temp) {
        return ("".equals(temp.getNameCard()) ? temp.getNick() : temp.getNameCard());
    }


}
