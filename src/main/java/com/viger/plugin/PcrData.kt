package com.viger.plugin

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PcrData : AutoSavePluginConfig("Pcr") {
    val clanSwitch: Boolean by value(false)
    val group: Long by value(1091221719L)
    val rankSwitch: Boolean by value(true)
    val cookie: String by value("sid=5hsmuqp9; DedeUserID=12931312; DedeUserID__ckMd5=c4c255f8f534bf24; SESSDATA=d7daff29%2C1616689534%2C0f1f9*91; bili_jct=a1f310c738dab5858c2e63f47e0855e4; session-api=6305edelp16eojkl09k0al4phq; _csrf=qtzT1ebnxEws8NZ7_Nvl4_gS")
    val reminderSwitch: Boolean by value(true)
    val feederSwitch: Boolean by value(true)
    val gashpon: Gashpon by value()
    val dataBase: DataBase by value()
    val rss: List<String> by value(
        mutableListOf(
            "https://rsshub.viger.xyz/bilibili/user/dynamic/401742377"
        )
    )
    val memberList: Set<Long> by value<Set<Long>>(HashSet<Long>())// 写入默认值

}

@Serializable
data class Gashpon(
    val up_prob: Int = 7,
    val s3_prob: Int = 25,
    val s2_prob: Int = 180,
    val star3: List<Int> = mutableListOf(
        1009,
        1011,
        1012,
        1018,
        1029,
        1030,
        1032,
        1049,
        1053,
        1057,
        1047,
        1063,
        1036,
        1044,
        1037,
        1701
    ),
    val star2: List<Int> = mutableListOf(
        1006,
        1007,
        1008,
        1016,
        1017,
        1020,
        1022,
        1027,
        1033,
        1038,
        1042,
        1045,
        1046,
        1048,
        1051,
        1026,
        1023,
        1015,
        1054,
        1005
    ),
    val star1: List<Int> = mutableListOf(1001, 1003, 1004, 1021, 1022, 1025, 1034, 1040, 1050, 1052),
    val up: List<Int> = mutableListOf(1081),
    val chara: Map<Int, List<String>> = mutableMapOf(
        1001 to mutableListOf("ヒヨリ", "日和莉", "日和", "Hiyori", "猫拳", "🐱👊"),
        1002 to mutableListOf("ユイ", "优衣", "優衣", "Yui", "种田", "普田", "由衣", "结衣", "ue", "↗↘↗↘"),
        1003 to mutableListOf("レイ", "怜", "Rei", "剑圣", "普怜", "伶"),
        1004 to mutableListOf("ミソギ", "未奏希", "禊", "Misogi", "炸弹", "炸弹人", "💣"),
        1005 to mutableListOf("マツリ", "茉莉", "Matsuri", "跳跳虎", "老虎", "虎", "🐅"),
        1006 to mutableListOf("アカリ", "茜里", "Akari", "妹法", "妹妹法", "阿卡丽"),
        1007 to mutableListOf("ミヤコ", "宫子", "宮子", "Miyako", "布丁", "布", "🍮"),
        1008 to mutableListOf("ユキ", "雪", "Yuki", "小雪", "镜子", "镜法", "伪娘", "男孩子", "男孩纸", "雪哥"),
        1009 to mutableListOf("アンナ", "杏奈", "Anna", "中二", "煤气罐"),
        1010 to mutableListOf("マホ", "真步", "Maho", "狐狸", "真扎", "咕噜灵波", "真布", "🦊"),
        1011 to mutableListOf("リノ", "璃乃", "Rino", "妹弓"),
        1012 to mutableListOf("ハツネ", "初音", "Hatsune", "hego", "星法", "星星法", "⭐法", "睡法"),
        1013 to mutableListOf("ナナカ", "七七香", "Nanaka", "娜娜卡", "77k", "77香"),
        1014 to mutableListOf("カスミ", "霞", "Kasumi", "香澄", "侦探", "杜宾犬", "驴", "驴子", "🔍"),
        1015 to mutableListOf("ミサト", "美里", "Misato", "圣母"),
        1016 to mutableListOf("スズナ", "铃奈", "鈴奈", "Suzuna", "暴击弓", "暴弓", "爆击弓", "爆弓", "政委"),
        1017 to mutableListOf("カオリ", "香织", "香織", "Kaori", "琉球犬", "狗子", "狗", "狗拳", "🐶", "🐕", "🐶👊🏻", "🐶👊"),
        1018 to mutableListOf("イオ", "伊绪", "伊緒", "Io", "老师", "魅魔"),
        1020 to mutableListOf("ミミ", "美美", "Mimi", "兔子", "兔兔", "兔剑", "萝卜霸断剑", "人参霸断剑", "天兔霸断剑", "🐇", "🐰"),
        1021 to mutableListOf("クルミ", "胡桃", "Kurumi", "铃铛", "🔔"),
        1022 to mutableListOf("ヨリ", "依里", "Yori", "姐法", "姐姐法"),
        1023 to mutableListOf("アヤネ", "绫音", "綾音", "Ayane", "熊锤", "🐻🔨", "🐻"),
        1025 to mutableListOf("スズメ", "铃莓", "鈴莓", "Suzume", "女仆", "妹抖"),
        1026 to mutableListOf("リン", "铃", "鈴", "Rin", "松鼠", "🐿", "🐿️"),
        1027 to mutableListOf("エリコ", "惠理子", "Eriko", "病娇"),
        1028 to mutableListOf("サレン", "咲恋", "咲戀", "Saren", "充电宝", "青梅竹马", "幼驯染", "院长", "园长", "🔋", "普电"),
        1029 to mutableListOf("ノゾミ", "望", "Nozomi", "偶像", "小望", "🎤"),
        1030 to mutableListOf("ニノン", "妮侬", "妮諾", "妮诺", "Ninon", "扇子"),
        1031 to mutableListOf("シノブ", "忍", "Shinobu", "普忍", "鬼父", "💀"),
        1032 to mutableListOf("アキノ", "秋乃", "Akino", "哈哈剑"),
        1033 to mutableListOf("マヒル", "真阳", "真陽", "Mahiru", "奶牛", "🐄", "🐮", "真☀"),
        1034 to mutableListOf("ユカリ", "由加莉", "優花梨", "优花梨", "Yukari", "黄骑", "酒鬼", "奶骑", "圣骑", "🍺", "🍺👻"),
        1036 to mutableListOf("キョウカ", "镜华", "鏡華", "Kyouka", "小仓唯", "xcw", "小苍唯", "8岁", "八岁", "喷水萝", "八岁喷水萝", "8岁喷水萝"),
        1037 to mutableListOf("トモ", "智", "Tomo", "卜毛"),
        1038 to mutableListOf("シオリ", "栞", "Shiori", "tp弓", "小栞", "白虎弓", "白虎妹"),
        1040 to mutableListOf("アオイ", "碧", "Aoi", "香菜", "香菜弓", "绿毛弓", "毒弓", "绿帽弓", "绿帽"),
        1042 to mutableListOf("チカ", "千歌", "Chika", "绿毛奶"),
        1043 to mutableListOf("マコト", "真琴", "Makoto", "狼", "🐺", "月月", "朋", "狼姐"),
        1044 to mutableListOf("イリヤ", "伊莉亚", "伊莉亞", "Iriya", "伊利亚", "伊莉雅", "伊利雅", "yly", "吸血鬼", "那个女人"),
        1045 to mutableListOf("クウカ", "空花", "Kuuka", "抖m", "抖"),
        1046 to mutableListOf("タマキ", "珠希", "Tamaki", "猫剑", "🐱剑", "🐱🗡️"),
        1047 to mutableListOf("ジュン", "纯", "純", "Jun", "黑骑", "saber"),
        1048 to mutableListOf("ミフユ", "美冬", "Mifuyu", "子龙", "赵子龙"),
        1049 to mutableListOf("シズル", "静流", "靜流", "Shizuru", "姐姐"),
        1050 to mutableListOf("ミサキ", "美咲", "Misaki", "大眼", "👀", "👁️", "👁"),
        1051 to mutableListOf("ミツキ", "深月", "Mitsuki", "眼罩", "抖s"),
        1052 to mutableListOf("リマ", "莉玛", "莉瑪", "Rima", "Lima", "草泥马", "羊驼", "🦙", "🐐"),
        1053 to mutableListOf("モニカ", "莫妮卡", "Monika", "毛二力"),
        1054 to mutableListOf("ツムギ", "纺希", "紡希", "Tsumugi", "裁缝", "蜘蛛侠", "🕷️", "🕸️"),
        1055 to mutableListOf("アユミ", "步未", "Ayumi", "步美", "路人", "路人妹"),
        1056 to mutableListOf("ルカ", "流夏", "Ruka", "大姐", "大姐头", "儿力", "luka", "刘夏"),
        1057 to mutableListOf("ジータ", "姬塔", "吉塔", "Jiita", "团长", "吉他", "🎸", "骑空士", "qks"),
        1058 to mutableListOf("ペコリーヌ", "佩可莉姆", "貪吃佩可", "贪吃佩可", "Pecoriinu", "吃货", "佩可", "公主", "饭团", "🍙"),
        1059 to mutableListOf("コッコロ", "可可萝", "可可蘿", "Kokkoro", "可可罗", "妈", "普白"),
        1060 to mutableListOf(
            "キャル",
            "凯露",
            "凱留",
            "凯留",
            "Kyaru",
            "百地希留耶",
            "希留耶",
            "Kiruya",
            "黑猫",
            "臭鼬",
            "普黑",
            "接头霸王",
            "街头霸王"
        ),
        1061 to mutableListOf("ムイミ", "矛依未", "Muimi", "诺维姆", "Noemu", "夏娜", "511", "无意义", "天楼霸断剑"),
        1063 to mutableListOf(
            "アリサ",
            "亚里莎",
            "亞里莎",
            "Arisa",
            "鸭梨瞎",
            "瞎子",
            "亚里沙",
            "鸭梨傻",
            "亚丽莎",
            "亚莉莎",
            "瞎子弓",
            "🍐🦐",
            "yls"
        ),
        1065 to mutableListOf("カヤ", "嘉夜", "Kaya", "憨憨龙", "龙拳", "🐲👊🏻", "🐉👊🏻", "接龙笨比"),
        1066 to mutableListOf("イノリ", "祈梨", "Inori", "梨老八", "李老八", "龙锤"),
        1067 to mutableListOf("ホマレ", "帆稀", "穗希", "Homare"),
        1068 to mutableListOf("ラビリスタ", "拉比林斯達", "拉比林斯达", "Rabirisuta", "迷宫女王", "模索路晶", "模索路", "晶", "晶姐"),
        1070 to mutableListOf(
            "ネネカ",
            "似似花",
            "Neneka",
            "变貌大妃",
            "现士实似似花",
            "現士実似々花",
            "現士実",
            "现士实",
            "nnk",
            "448",
            "捏捏卡",
            "变貌",
            "大妃"
        ),
        1071 to mutableListOf(
            "クリスティーナ", "克莉絲提娜", "克莉丝提娜", "Kurisutiina", "誓约女君", "克莉丝提娜·摩根", "Christina", "Cristina", "克总", "女帝", "克",
            "摩根"
        ),
        1075 to mutableListOf(
            "ペコリーヌ（サマー）", "佩可莉姆（夏日）", "貪吃佩可（夏日）", "贪吃佩可(夏日)", "ペコリーヌ(サマー)", "Pekoriinu(Summer)", "佩可莉姆(夏日)", "水吃", "水饭",
            "水吃货", "水佩可", "水公主", "水饭团", "水🍙", "泳吃", "泳饭", "泳吃货", "泳佩可", "泳公主", "泳饭团", "泳🍙", "泳装吃货", "泳装公主", "泳装饭团",
            "泳装🍙", "佩可(夏日)", "🥡", "👙🍙", "泼妇"
        ),
        1076 to mutableListOf(
            "コッコロ（サマー）", "可可萝（夏日）", "可可蘿（夏日）", "可可萝(夏日)", "コッコロ(サマー)", "Kokkoro(Summer)", "水白", "水妈", "水可", "水可可",
            "水可可萝", "水可可罗", "泳装妈", "泳装可可萝", "泳装可可罗"
        ),
        1077 to mutableListOf("スズメ（サマー）", "铃莓（夏日）", "鈴莓（夏日）", "铃莓(夏日)", "スズメ(サマー)", "Suzume(Summer)", "水女仆", "水妹抖"),
        1078 to mutableListOf(
            "キャル（サマー）", "凯露（夏日）", "凱留（夏日）", "凯留(夏日)", "キャル(サマー)", "Kyaru(Summer)", "水黑", "水黑猫", "水臭鼬", "泳装黑猫", "泳装臭鼬",
            "潶", "溴", "💧黑"
        ),
        1079 to mutableListOf(
            "タマキ（サマー）",
            "珠希（夏日）",
            "珠希(夏日)",
            "タマキ(サマー)",
            "Tamaki(Summer)",
            "水猫剑",
            "水猫",
            "渵",
            "💧🐱🗡️",
            "水🐱🗡️"
        ),
        1080 to mutableListOf("ミフユ（サマー）", "美冬（夏日）", "美冬(夏日)", "ミフユ(サマー)", "Mifuyu(Summer)", "水子龙", "水美冬"),
        1081 to mutableListOf(
            "シノブ（ハロウィン）",
            "忍（萬聖節）",
            "忍(万圣节)",
            "シノブ(ハロウィン)",
            "Shinobu(Halloween)",
            "万圣忍",
            "瓜忍",
            "🎃忍",
            "🎃💀"
        ),
        1082 to mutableListOf(
            "ミヤコ（ハロウィン）", "宮子（萬聖節）", "宫子(万圣节)", "ミヤコ(ハロウィン)", "Miyako(Halloween)", "万圣宫子", "万圣布丁", "狼丁", "狼布丁", "万圣🍮",
            "🐺🍮", "🎃🍮", "👻🍮"
        ),
        1083 to mutableListOf(
            "ミサキ（ハロウィン）", "美咲（萬聖節）", "美咲(万圣节)", "ミサキ(ハロウィン)", "Misaki(Halloween)", "万圣美咲", "万圣大眼", "瓜眼", "🎃眼", "🎃👀",
            "🎃👁️", "🎃👁"
        ),
        1084 to mutableListOf(
            "チカ（クリスマス）", "千歌（聖誕節）", "千歌(圣诞节)", "チカ(クリスマス)", "Chika(Xmas)", "圣诞千歌", "圣千", "蛋鸽", "🎄💰🎶", "🎄千🎶",
            "🎄1000🎶"
        ),
        1085 to mutableListOf("クルミ（クリスマス）", "胡桃（聖誕節）", "胡桃(圣诞节)", "クルミ(クリスマス)", "Kurumi(Xmas)", "圣诞胡桃", "圣诞铃铛"),
        1086 to mutableListOf(
            "アヤネ（クリスマス）",
            "綾音（聖誕節）",
            "绫音(圣诞节)",
            "アヤネ(クリスマス)",
            "Ayane(Xmas)",
            "圣诞熊锤",
            "蛋锤",
            "圣锤",
            "🎄🐻🔨",
            "🎄🐻"
        ),
        1087 to mutableListOf(
            "ヒヨリ（ニューイヤー）",
            "日和（新年）",
            "日和(新年)",
            "ヒヨリ(ニューイヤー)",
            "Hiyori(NewYear)",
            "新年日和",
            "春猫",
            "👘🐱"
        ),
        1088 to mutableListOf("ユイ（ニューイヤー）", "優衣（新年）", "优衣(新年)", "ユイ(ニューイヤー)", "Yui(NewYear)", "新年优衣", "春田", "新年由衣"),
        1089 to mutableListOf(
            "レイ（ニューイヤー）",
            "怜（新年）",
            "怜(新年)",
            "レイ(ニューイヤー)",
            "Rei(NewYear)",
            "春剑",
            "春怜",
            "春伶",
            "新春剑圣",
            "新年怜",
            "新年剑圣"
        ),
        1090 to mutableListOf(
            "エリコ（バレンタイン）",
            "惠理子（情人節）",
            "惠理子(情人节)",
            "エリコ(バレンタイン)",
            "Eriko(Valentine)",
            "情人节病娇",
            "恋病",
            "情病",
            "恋病娇",
            "情病娇"
        ),
        1091 to mutableListOf(
            "シズル（バレンタイン）",
            "靜流（情人節）",
            "静流(情人节)",
            "シズル(バレンタイン)",
            "Shizuru(Valentine)",
            "情人节静流",
            "情姐",
            "情人节姐姐"
        ),
        1092 to mutableListOf("アン", "安", "An", "胖安", "55kg"),
        1093 to mutableListOf("ルゥ", "露", "Ruu", "逃课女王"),
        1094 to mutableListOf("グレア", "古蕾婭", "古蕾娅", "Gurea", "龙姬", "古雷娅", "古蕾亚", "古雷亚", "🐲🐔", "🐉🐔", "龙女"),
        1095 to mutableListOf(
            "クウカ（オーエド）",
            "空花（大江戶）",
            "空花(大江户)",
            "クウカ(オーエド)",
            "Kuuka(Ooedo)",
            "江户空花",
            "江户抖m",
            "江m",
            "花m",
            "江花"
        ),
        1096 to mutableListOf("ニノン（オーエド）", "妮諾（大江戶）", "妮诺(大江户)", "ニノン(オーエド)", "Ninon(Ooedo)", "江户扇子", "忍扇"),
        1097 to mutableListOf("レム", "雷姆", "Remu", "蕾姆"),
        1098 to mutableListOf("ラム", "拉姆", "Ramu"),
        1099 to mutableListOf("エミリア", "愛蜜莉雅", "爱蜜莉雅", "Emiria", "艾米莉亚", "emt"),
        1100 to mutableListOf(
            "スズナ（サマー）", "鈴奈（夏日）", "铃奈(夏日)", "スズナ(サマー)", "Suzuna(Summer)", "瀑击弓", "水爆", "水爆弓", "水暴", "瀑", "水暴弓", "瀑弓",
            "泳装暴弓", "泳装爆弓"
        ),
        1101 to mutableListOf("イオ（サマー）", "伊緒（夏日）", "伊绪(夏日)", "イオ(サマー)", "Io(Summer)", "水魅魔", "水老师", "泳装魅魔", "泳装老师"),
        1102 to mutableListOf("ミサキ（サマー）", "美咲（夏日）", "美咲(夏日)", "ミサキ(サマー)", "Misaki(Summer)", "水大眼", "泳装大眼"),
        1103 to mutableListOf(
            "サレン（サマー）", "咲戀（夏日）", "咲恋(夏日)", "サレン(サマー)", "Saren(Summer)", "水电", "泳装充电宝", "泳装咲恋", "水着咲恋", "水电站", "水电宝",
            "水充", "👙🔋"
        ),
        1104 to mutableListOf(
            "マコト（サマー）", "真琴（夏日）", "真琴(夏日)", "マコト(サマー)", "Makoto(Summer)", "水狼", "浪", "水🐺", "泳狼", "泳月", "泳月月", "泳朋",
            "水月", "水月月", "水朋", "👙🐺"
        ),
        1105 to mutableListOf(
            "カオリ（サマー）",
            "香織（夏日）",
            "香织(夏日)",
            "カオリ(サマー)",
            "Kaori(Summer)",
            "水狗",
            "泃",
            "水🐶",
            "水🐕",
            "泳狗"
        ),
        1106 to mutableListOf(
            "マホ（サマー）", "真步（夏日）", "真步(夏日)", "マホ(サマー)", "Maho(Summer)", "水狐狸", "水狐", "水壶", "水真步", "水maho", "氵🦊", "水🦊",
            "💧🦊"
        ),
        1107 to mutableListOf("アオイ（編入生）", "碧（插班生）", "碧(插班生)", "アオイ(編入生)", "Aoi(Hennyuusei)", "生菜", "插班碧"),
        1108 to mutableListOf("クロエ", "克蘿依", "克萝依", "Kuroe", "华哥", "黑江", "黑江花子", "花子"),
        1109 to mutableListOf("チエル", "琪愛兒", "琪爱儿", "Chieru", "切露", "茄露", "茄噜", "切噜"),
        1110 to mutableListOf(
            "ユニ", "優妮", "优妮", "Yuni", "真行寺由仁", "由仁", "u2", "优妮辈先", "辈先", "书记", "uni", "先辈", "仙贝", "油腻", "优妮先辈", "学姐",
            "18岁黑丝学姐"
        ),
        1111 to mutableListOf(
            "キョウカ（ハロウィン）", "鏡華（萬聖節）", "镜华(万圣节)", "キョウカ(ハロウィン)", "Kyouka(Halloween)", "万圣镜华", "万圣小仓唯", "万圣xcw", "猫仓唯",
            "黑猫仓唯", "mcw", "猫唯", "猫仓", "喵唯"
        ),
        1112 to mutableListOf(
            "ミソギ（ハロウィン）", "禊（萬聖節）", "禊(万圣节)", "ミソギ(ハロウィン)", "Misogi(Halloween)", "万圣禊", "万圣炸弹人", "瓜炸弹人", "万圣炸弹", "万圣炸",
            "瓜炸", "南瓜炸", "🎃💣"
        ),
        1113 to mutableListOf(
            "ミミ（ハロウィン）", "美美（萬聖節）", "美美(万圣节)", "ミミ(ハロウィン)", "Mimi(Halloween)", "万圣兔", "万圣兔子", "万圣兔兔", "绷带兔", "绷带兔子",
            "万圣美美", "绷带美美", "万圣🐰", "绷带🐰", "🎃🐰", "万圣🐇", "绷带🐇", "🎃🐇"
        ),
        1114 to mutableListOf("ルナ", "露娜", "Runa", "露仓唯", "露cw"),
        1115 to mutableListOf(
            "クリスティーナ（クリスマス）", "克莉絲提娜（聖誕節）", "克莉丝提娜(圣诞节)", "クリスティーナ(クリスマス)", "Kurisutiina(Xmas)", "Christina(Xmas)",
            "Cristina(Xmas)", "圣诞克", "圣诞克总", "圣诞女帝", "蛋克", "圣克", "必胜客"
        ),
        1116 to mutableListOf(
            "ノゾミ（クリスマス）",
            "望（聖誕節）",
            "望(圣诞节)",
            "ノゾミ(クリスマス)",
            "Nozomi(Xmas)",
            "圣诞望",
            "圣诞偶像",
            "蛋偶像",
            "蛋望"
        ),
        1117 to mutableListOf(
            "イリヤ（クリスマス）", "伊莉亞（聖誕節）", "伊莉亚(圣诞节)", "イリヤ(クリスマス)", "Iriya(Xmas)", "圣诞伊莉亚", "圣诞伊利亚", "圣诞伊莉雅", "圣诞伊利雅",
            "圣诞yly", "圣诞吸血鬼", "圣伊", "圣yly"
        ),
        1118 to mutableListOf("ペコリーヌ（ニューイヤー）", "貪吃佩可（新年）"),
        1119 to mutableListOf(
            "コッコロ（ニューイヤー）",
            "可可蘿（新年）",
            "可可萝(新年)",
            "コッコロ(ニューイヤー)",
            "Kokkoro(NewYear)",
            "春可可",
            "春白",
            "新年妈",
            "春妈"
        ),
        1120 to mutableListOf(
            "キャル（ニューイヤー）", "凱留（新年）", "凯留(新年)", "キャル(ニューイヤー)", "Kyaru(NewYear)", "春凯留", "春黑猫", "春黑", "春臭鼬", "新年凯留",
            "新年黑猫", "新年臭鼬", "唯一神"
        ),
        1121 to mutableListOf(
            "スズメ（ニューイヤー）", "鈴莓（新年）", "铃莓(新年)", "スズメ(ニューイヤー)", "Suzume(NewYear)", "春铃莓", "春女仆", "春妹抖", "新年铃莓", "新年女仆",
            "新年妹抖"
        ),
        1122 to mutableListOf(
            "カスミ（マジカル）", "霞（魔法少女）", "霞(魔法少女)", "カスミ(マジカル)", "Kasumi(MagiGirl)", "魔法少女霞", "魔法侦探", "魔法杜宾犬", "魔法驴", "魔法驴子",
            "魔驴", "魔法霞", "魔法少驴"
        ),
        1123 to mutableListOf(
            "シオリ（マジカル）", "栞（魔法少女）", "栞(魔法少女)", "シオリ(マジカル)", "Shiori(MagiGirl)", "魔法少女栞", "魔法tp弓", "魔法小栞", "魔法白虎弓",
            "魔法白虎妹", "魔法白虎", "魔栞"
        ),
        1124 to mutableListOf(
            "ウヅキ（デレマス）",
            "卯月（NGs）",
            "卯月(偶像大师)",
            "ウヅキ(デレマス)",
            "Udsuki(DEREM@S)",
            "卯月",
            "卵用",
            "Udsuki(DEREMAS)",
            "岛村卯月"
        ),
        1125 to mutableListOf(
            "リン（デレマス）",
            "凜（NGs）",
            "凛(偶像大师)",
            "リン(デレマス)",
            "Rin(DEREM@S)",
            "凛",
            "Rin(DEREMAS)",
            "涩谷凛",
            "西部凛"
        ),
        1126 to mutableListOf(
            "ミオ（デレマス）",
            "未央（NGs）",
            "未央(偶像大师)",
            "ミオ(デレマス)",
            "Mio(DEREM@S)",
            "未央",
            "Mio(DEREMAS)",
            "本田未央"
        ),
        1127 to mutableListOf(
            "リン（レンジャー）",
            "鈴（遊俠）",
            "铃(游侠)",
            "リン(レンジャー)",
            "Rin(Ranger)",
            "骑兵松鼠",
            "游侠松鼠",
            "游骑兵松鼠",
            "护林员松鼠",
            "护林松鼠",
            "游侠🐿️",
            "武松",
            "铃(游骑兵)"
        ),
        1128 to mutableListOf(
            "マヒル（レンジャー）", "真陽（遊俠）", "真阳(游侠)", "マヒル(レンジャー)", "Mahiru(Ranger)", "骑兵奶牛", "游侠奶牛", "游骑兵奶牛", "护林员奶牛", "护林奶牛",
            "游侠🐄", "游侠🐮", "牛叉", "真阳(游骑兵)"
        ),
        1129 to mutableListOf(
            "リノ（ワンダー）", "璃乃（奇幻）", "璃乃(奇境)", "リノ(ワンダー)", "Rino(Wonder)", "璃乃(仙境)", "爽弓", "爱丽丝弓", "爱弓", "兔弓", "奇境妹弓",
            "仙境妹弓", "白丝妹弓", "璃乃(奇幻)"
        ),
        1130 to mutableListOf(
            "アユミ（ワンダー）", "步未（奇幻）", "步未(奇境)", "アユミ(ワンダー)", "Ayumi(Wonder)", "步未(仙境)", "路人兔", "兔人妹", "爱丽丝路人", "奇境路人",
            "仙境路人", "步未(奇幻)"
        ),
        1131 to mutableListOf(
            "ルカ（サマー）",
            "流夏(夏日)",
            "ルカ(サマー)",
            "Ruka(Summer)",
            "泳装流夏",
            "水流夏",
            "泳装刘夏",
            "水刘夏",
            "泳装大姐",
            "泳装大姐头",
            "水大姐",
            "水大姐头",
            "水儿力",
            "泳装儿力",
            "水流",
            "水亚索",
            "泳装亚索"
        ),
        1132 to mutableListOf(
            "アンナ（サマー）",
            "杏奈(夏日)",
            "アンナ(サマー)",
            "Anna(Summer)",
            "泳装中二",
            "泳装煤气罐",
            "水中二",
            "水煤气罐",
            "冲",
            "冲二"
        ),
        1133 to mutableListOf(
            "ナナカ（サマー）",
            "七七香(夏日)",
            "ナナカ(サマー)",
            "Nanaka(Summer)",
            "泳装娜娜卡",
            "泳装77k",
            "泳装77香",
            "水娜娜卡",
            "水77k",
            "水77香"
        ),
        1134 to mutableListOf(
            "ハツネ（サマー）",
            "初音(夏日)",
            "ハツネ(サマー)",
            "Hatsune(Summer)",
            "水星",
            "海星",
            "水hego",
            "水星法",
            "泳装星法",
            "水⭐法",
            "水睡法",
            "湦"
        ),
        1135 to mutableListOf("ミサト（サマー）", "美里(夏日)", "ミサト(サマー)", "Misato(Summer)", "水母", "泳装圣母", "水圣母", "泳装美里"),
        1136 to mutableListOf("ジュン（サマー）", "纯(夏日)", "ジュン(サマー)", "Jun(Summer)", "泳装黑骑", "水黑骑", "泳装纯", "水纯", "小次郎", "潶骑"),
        1137 to mutableListOf("アカリ（エンジェル）", "茜里(天使)", "アカリ(エンジェル)", "Akari(Angel)", "天使茜里", "天使阿卡丽", "天使妹法", "天使妹妹法"),
        1138 to mutableListOf("ヨリ（エンジェル）", "依里(天使)", "ヨリ(エンジェル)", "Yori(Angel)", "天使依里", "天使姐姐法", "天使姐法"),
        1139 to mutableListOf(
            "ツムギ（ハロウィン）", "纺希(万圣节)", "ツムギ(ハロウィン)", "Tsumugi(Halloween)", "万圣裁缝", "万圣蜘蛛侠", "🎃🕷️", "🎃🕸️", "万裁", "瓜裁",
            "鬼裁", "鬼才"
        ),
        1140 to mutableListOf("レイ（ハロウィン）", "怜(万圣节)", "レイ(ハロウィン)", "Rei(Halloween)", "万圣剑圣", "万剑", "瓜剑"),
        1141 to mutableListOf(
            "マツリ（ハロウィン）",
            "茉莉(万圣节)",
            "マツリ(ハロウィン)",
            "Matsuri(Halloween)",
            "万圣跳跳虎",
            "万圣老虎",
            "瓜虎",
            "🎃🐅"
        ),
        1802 to mutableListOf(
            "ユイ（プリンセス）",
            "优衣(公主)",
            "ユイ(プリンセス)",
            "Yui(Princess)",
            "公主优衣",
            "公主yui",
            "公主种田",
            "公主田",
            "公主ue",
            "掉毛优衣",
            "掉毛yui",
            "掉毛ue",
            "掉毛",
            "飞翼优衣",
            "飞翼ue",
            "飞翼",
            "飞翼高达",
            "fesue",
            "公田",
            "毛衣"
        ),
        1804 to mutableListOf(
            "ペコリーヌ（プリンセス）", "貪吃佩可（公主）", "贪吃佩可(公主)", "ペコリーヌ(プリンセス)", "Pekoriinu(Princess)", "公主吃", "公主饭", "公主吃货", "公主佩可",
            "公主饭团", "公主🍙", "命运高达", "高达", "命运公主", "高达公主", "命吃", "春哥高达", "🤖🍙", "🤖"
        ),
        1805 to mutableListOf(
            "コッコロ（プリンセス）", "可可蘿（公主）", "可可萝(公主)", "Kokkoro(Princess)", "公主妈", "月光妈", "蝶妈", "蝴蝶妈", "月光蝶妈", "公主可", "公主可萝",
            "公主可可萝", "月光可", "月光可萝", "月光可可萝", "蝶可", "蝶可萝", "蝶可可萝", "月光蝶"
        ),
        1908 to mutableListOf("カリン", "花凛", "花凜", "Karin", "绿毛恶魔"),
        1000 to mutableListOf("未知角色", "未知キャラ", "Unknown"),
        1069 to mutableListOf("真那", "マナ", "Mana", "霸瞳皇帝", "千里真那", "千里", "霸瞳", "霸铜"),
        1701 to mutableListOf("环奈"),
        1072 to mutableListOf("可萝爹", "長老", "Chourou", "岳父", "爷爷"),
        1073 to mutableListOf("拉基拉基", "ラジニカーント", "Rajinigaanto", "跳跃王", "Rajiraji", "Lajilaji", "垃圾垃圾", "教授"),
        4031 to mutableListOf("骷髅", "髑髏", "Dokuro", "骷髅老爹", "老爹"),
        9000 to mutableListOf("祐树", "ユウキ", "Yuuki", "骑士", "骑士君"),
        9401 to mutableListOf("爱梅斯", "アメス", "Amesu", "菲欧", "フィオ", "Fio"),
    )
)

@Serializable
data class DataBase(
    val DBUserName: String = "username",
    val DBPassword: String = "password"
)
