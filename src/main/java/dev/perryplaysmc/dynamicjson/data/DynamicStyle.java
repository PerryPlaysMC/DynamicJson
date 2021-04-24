package dev.perryplaysmc.dynamicjson.data;

import net.md_5.bungee.api.ChatColor;

import java.io.Serializable;

/**
 * Owner: PerryPlaysMC
 * Created: 2/21
 **/

public enum DynamicStyle implements Serializable {

    BOLD(ChatColor.BOLD), ITALIC(ChatColor.ITALIC), UNDERLINED(ChatColor.UNDERLINE), STRIKETHROUGH(ChatColor.STRIKETHROUGH), OBFUSCATED(ChatColor.MAGIC);


    ChatColor style;
    DynamicStyle(ChatColor style) {
        this.style = style;
    }

    public ChatColor getAsColor() {
        return style;
    }

    public static DynamicStyle byChar(char c) {
        for(DynamicStyle style : values())
            if(style.getAsColor().toString().endsWith(c+""))return style;
        return null;
    }
}
