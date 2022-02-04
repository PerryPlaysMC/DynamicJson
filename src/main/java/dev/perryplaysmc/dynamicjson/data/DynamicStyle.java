package dev.perryplaysmc.dynamicjson.data;

import java.io.Serializable;

/**
 * Owner: PerryPlaysMC
 * Created: 2/21
 **/

public enum DynamicStyle implements Serializable {
  
  BOLD(CColor.BOLD), ITALIC(CColor.ITALIC), UNDERLINED(CColor.UNDERLINE), STRIKETHROUGH(CColor.STRIKETHROUGH), OBFUSCATED(CColor.MAGIC);
  
  
  private static final DynamicStyle[] list = values();
  
  public static DynamicStyle[] list() {
    return list;
  }
  
  private final CColor style;

  DynamicStyle(CColor style) {
    this.style = style;
  }
  
  public CColor getAsColor() {
    return style;
  }
  
  public static DynamicStyle byChar(char c) {
    for(DynamicStyle style : values())
      if(style.getAsColor().toString().endsWith(c+""))return style;
    return null;
  }
}
