package club.ccit.network.news.model;

public enum NewsType {
    TOP("top", "推荐"),
    DOMESTIC("guonei", "国内"),
    INTERNATIONAL("guoji", "国际"),
    ENTERTAINMENT("yule", "娱乐"),
    SPORTS("tiyu", "体育"),
    MILITARY("junshi", "军事"),
    TECHNOLOGY("keji", "科技"),
    FINANCE("caijing", "财经"),
    GAMES("youxi", "游戏"),
    AUTO("qiche", "汽车"),
    HEALTH("jiankang", "健康");

    private final String value;
    private final String desc;

    NewsType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据描述获取对应的枚举值
     * @param desc 中文描述
     * @return 对应的value值，未找到返回null
     */
    public static String getValueByDesc(String desc) {
        if (desc == null) return null;
        for (NewsType type : values()) {
            if (type.desc.equals(desc)) {
                return type.value;
            }
        }
        return null;
    }


    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
