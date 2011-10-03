package com.afforess.minecartmaniaautomations;

public enum WoolColors {
    WHITE,
    ORANGE,
    MAGENTA,
    LIGHTBLUE,
    YELLOW,
    LIME,
    PINK,
    GRAY,
    LIGHTGRAY,
    CYAN,
    PURPLE,
    BLUE,
    BROWN,
    GREEN,
    RED,
    BLACK;
    
    public static WoolColors getWoolColor(byte type) {
        WoolColors[] wc = values();
        for(int i = 0;i<wc.length;i++) {
            if(wc[i].ordinal() == type)
                return wc[i];
        }
        return WHITE;
    }
}