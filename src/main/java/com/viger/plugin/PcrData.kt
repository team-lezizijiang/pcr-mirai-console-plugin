package com.viger.plugin

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PcrData : AutoSavePluginConfig("Pcr") {
    val clanSwitch: Boolean by value(false)
    val group: Long by value(1091221719L)
    val rankSwitch: Boolean by value(true)
    val reminderSwitch: Boolean by value(true)
    val feederSwitch: Boolean by value(true)
    val gashpon: Gashpon by value()
    val dataBase: DataBase by value()
    val memberList: Set<Long> by value<Set<Long>>(HashSet<Long>())// 写入默认值

}

@Serializable
data class Gashpon(
    val one: List<String> = listOf(" 日和莉 ", " 怜 ", " 未奏希 ", " 胡桃 ", " 依里 ", " 由加莉 ", " 铃莓 ", " 碧 ", " 美咲 ", " 莉玛 "),
    val two: List<String> = listOf(" 日和莉 ", " 怜 ", " 未奏希 ", " 依里 ", " 由加莉 ", " 碧 ", " 美咲 ", " 莉玛 "),
    val three: List<String> = listOf(
        " 杏奈 ",
        " 真步 ",
        " 璃乃 ",
        " 初音 ",
        " 依绪 ",
        " 咲恋 ",
        " 望 ",
        " 妮侬 ",
        " 秋乃 ",
        " 真琴 ",
        " 静流 ",
        " 莫妮卡 ",
        " 姬塔 ",
        " 纯 ",
        " 亚里莎 "
    ),
    val noUpThree: List<String> = listOf(
        " 亚莉莎 ",
        " 杏奈 ",
        " 真步 ",
        " 璃乃 ",
        " 初音 ",
        " 依绪 ",
        " 望 ",
        " 妮侬 ",
        " 秋乃 ",
        " 真琴 ",
        " 静流 ",
        " 莫妮卡 ",
        " 姬塔 ",
        " 纯 "
    ),
    val noUpTwo: List<String> = listOf(
        " 茜里 ",
        " 宫子 ",
        " 雪 ",
        " 铃奈 ",
        " 香织 ",
        " 美美 ",
        " 惠理子 ",
        " 忍 ",
        " 真阳 ",
        " 栞 ",
        " 千歌 ",
        " 空花 ",
        " 珠希 ",
        " 美冬 ",
        " 深月 ",
        " 铃 "
    ),
    val noUpOne: List<String> = listOf(" 日和莉 ", " 怜 ", " 未奏希 ", " 依里 ", " 由加莉 ", " 碧 ", " 美咲 ", " 莉玛 "),
    val one_plus: List<String> = listOf(" 胡桃 ", " 铃莓 "),
    val two_plus: List<String> = listOf(" 绫音 "),
    val three_plus: List<String> = listOf(" 咲恋 ")
)

@Serializable
data class DataBase(
    val DBUserName: String = "username",
    val DBPassword: String = "password"
)
