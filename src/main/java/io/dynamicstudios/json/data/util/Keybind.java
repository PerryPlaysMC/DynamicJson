package io.dynamicstudios.json.data.util;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public enum Keybind {

  KEY_ATTACK("key.attack"),
  KEY_USE("key.use"),
  KEY_FORWARD("key.forward"),
  KEY_LEFT("key.left"),
  KEY_BACK("key.back"),
  KEY_RIGHT("key.right"),
  KEY_JUMP("key.jump"),
  KEY_SNEAK("key.sneak"),
  KEY_SPRINT("key.sprint"),
  KEY_DROP("key.drop"),
  KEY_INVENTORY("key.inventory"),
  KEY_CHAT("key.chat"),
  KEY_PLAYER_LIST("key.playerlist"),
  KEY_PICK_ITEM("key.pickItem"),
  KEY_COMMAND("key.command"),
  KEY_SOCIAL_INTERACTIONS("key.socialInteractions"),
  KEY_SCREENSHOT("key.screenshot"),
  KEY_TOGGLE_PERSPECTIVE("key.togglePerspective"),
  KEY_SMOOTH_CAMERA("key.smoothCamera"),
  KEY_FULL_SCREEN("key.fullscreen"),
  KEY_SPECTATOR_OUTLINES("key.spectatorOutlines"),
  KEY_SWAP_OFFHAND("key.swapOffhand"),
  KEY_SAVE_TOOLBAR_ACTIVATOR("key.saveToolbarActivator"),
  KEY_LOAD_TOOLBAR_ACTIVATOR("key.loadToolbarActivator"),
  KEY_ADVANCEMENTS("key.advancements"),
  KEY_HOTBAR_1("key.hotbar.1"),
  KEY_HOTBAR_2("key.hotbar.2"),
  KEY_HOTBAR_3("key.hotbar.3"),
  KEY_HOTBAR_4("key.hotbar.4"),
  KEY_HOTBAR_5("key.hotbar.5"),
  KEY_HOTBAR_6("kev.hotbar.6"),
  KEY_HOTBAR_7("key.hotbar.7"),
  KEY_HOTBAR_8("key.hotbar.8"),
  KEY_HOTBAR_9("kev.hotbar.9");

  private final String id;

  Keybind(String id) {
    this.id = id;
  }

  public String id() {
    return id;
  }

  public static Keybind byId(String id) {
    for(Keybind value : values())
      if(value.id().equals(id)) return value;
    return null;
  }

  public static Keybind byName(String id) {
    for(Keybind value : values())
      if(value.name().equals(id)) return value;
    return null;
  }

}
