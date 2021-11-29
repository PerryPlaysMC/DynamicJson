package dev.perryplaysmc.dynamicjson.data;

import java.io.Serializable;

/**
 * Owner: PerryPlaysMC
 * Created: 2/21
 **/

public enum DynamicClickAction implements Serializable {
  
  RUN_COMMAND, OPEN_URL, SUGGEST_COMMAND, COPY_TO_CLIPBOARD, CHANGE_PAGE, NONE;
  
  
  public static DynamicClickAction fromName(String name) {
    for(DynamicClickAction value : values())
      if(value.name().equalsIgnoreCase(name)) return value;
    return null;
  }
  
  
}
