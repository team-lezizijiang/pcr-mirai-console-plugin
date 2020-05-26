package com.viger.plugin;

public class Constant {
    public static final String[] one = {" 日和莉 ", " 怜 ", " 未奏希 ", " 胡桃 ", " 依里 ", " 由加莉 ", " 铃莓 ", " 碧 ", " 美咲 ", " 莉玛 "};
    public static final String[] two = {" 茜里 ", " 宫子 ", " 雪 ", " 铃奈 ", " 香织 ", " 美美 ", " 惠理子 ", " 忍 ", " 真阳 ", " 栞 ", " 千歌 ", " 空花 ", " 珠希 ", " 美冬 ", " 深月 ", " 铃 "};
    public static final String[] Three = {" 杏奈 ", " 真步 ", " 璃乃 ", " 初音 ", " 依绪 ", " 咲恋 ", " 望 ", " 妮胧 ", " 秋乃 ", " 真琴 ", " 静流 ", " 莫妮卡 ", " 姬塔 ", " 纯 ", " 亚里莎 "};
    public static final String[] noUpThree = {" 杏奈 ", " 真步 ", " 璃乃 ", " 初音 ", " 依绪 ", " 咲恋 ", " 望 ", " 妮胧 ", " 秋乃 ", " 真琴 ", " 静流 ", " 莫妮卡 ", " 姬塔 ", " 纯 "};
    public static final String[] Three_plus = {" 镜华 "};
    public static final String[] two_plus = {" 真阳 ", " 栞 ", " 香织 ", " 铃 "};
    public static final String[] one_plus = {" 莉玛 "};
    public static final String helpMsg = " 命令总览：\n" +
            " 打 / 为管理员指令\n" +
            " 工会战命令:\n" +
            "\t1. 开始进行会战 [开始记刀]\n" +
            "\t2. 结束会战 [结束记刀]\n" +
            "\t3. 出完刀向机器人提交成绩 [记刀 [尾刀] 伤害值]\n" +
            "\t\t 如果有自信不会挂树可以直接用这个命令提交伤害 \n" +
            "\t\t 但是如果挂了请注意不要透露给工会长自己的住宅地址，注意人身安全 \n" +
            "\t\t 尾刀 可选,统计时自动忽略次数 \n" +
            "\t4.* 撤除出刀资料 [/records remove QQ号 伤害值]\n" +
            "\t\t 需要管理手动调整 \n" +
            "\t5. 查询今日全部成员出刀情况，[查刀]\n" +
            " 杂项指令：\n" +
            "\t1. 抽一发井 (300 抽)[#(up) 井]\n" +
            "\t\t 带 up 就是 up 池，不带 up 就是白金池 \n" +
            "\t2. 抽一发十连 (10 抽)[#(up) 十连]\n" +
            "\t3. 抽 n 发 [#(up) 抽卡 n]\n" +
            "\t4. 生成 excel 统计表格 [生成 excel (时间)](待加入)\n" +
            "\t\t 时间参数与上面相同 \n" +
            "\t5. 在一个群里关闭 / 开启提醒买药小助手 [# 关闭 / 开启提醒买药小助手](待加入)\n" +
            "\t6. 删除不需要记录的成员信息 [/member remove QQ号]\n" +
            "\t7. 查看当前正在记录的成员信息 [/member list]\n" +
            " 默认设定：\n" +
            "\t 这个插件和插件的所有功能都是开启的 \n" +
            "\t 每天 0 点检查工会战结束的工会 \n" +
            "\t 抽一发井是有冷却的，大概 2 分钟吧 \n" +
            "\t 只要是管理员就可以踢掉任何人（包括工会长(会长求别踢)）";
}
