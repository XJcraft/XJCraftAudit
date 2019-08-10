package org.jim.bukkit.audit;

public enum Status {

    UNAPPLIED(1), // 未通过审核
    APPLIED(2), // 通过但未使用命令命令方块
    APPLIED_VILLAGE(3), // 通过且已经使用命令方块
    APPLIED_VILLAGE_BASE(4);// 通过并建立回家的路

    private Status(int type) {
        this.type = type;
    }

    private static Status[] types = new Status[5];

    static {
        for (Status s : Status.values())
            types[s.getType()] = s;
    }

    private int type;

    public int getType() {
        return type;
    }

    public static Status get(int type) {
        if (type > 0 && type < types.length)
            return types[type];
        return null;
    }

}
