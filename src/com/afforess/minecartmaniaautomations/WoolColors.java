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
    
    public static WoolColors getWoolColor(final byte type) {
        final WoolColors[] wc = values();
        for (final WoolColors element : wc) {
            if (element.ordinal() == type)
                return element;
        }
        return WHITE;
    }
}