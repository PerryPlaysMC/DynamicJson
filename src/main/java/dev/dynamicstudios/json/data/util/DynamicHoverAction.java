package dev.dynamicstudios.json.data.util;

import dev.dynamicstudios.json.Version;
import dev.dynamicstudios.json.data.util.packet.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.lang.reflect.Method;

public enum DynamicHoverAction implements Serializable {

  SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY, NONE;

  private static final Class<?> nmsStackC, cbStack, cmp, comp;
  private static final Method asNMS, save;

  static {
	  nmsStackC = Version.Minecraft.getClass("world.item.ItemStack");
	  cbStack = Version.CraftBukkit.getClass("inventory.CraftItemStack");
	  cmp = Version.Minecraft.getClass("nbt.NBTTagCompound");
	  comp = Version.Minecraft.getClass("core.component.DataComponentMap");
    Method asNMS1 = null, save1 = null;
    try {
      asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
      save1 = nmsStackC.getMethod("save", cmp);
    } catch (Exception e) {
    }
    try {
      if(asNMS1 == null) asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
	    if(save1 == null) save1 = ReflectionUtils.getMethod(nmsStackC, cmp, cmp);
    } catch (NoSuchMethodException e1) {
    }
    try {
      if(asNMS1 == null) asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
      if(save1 == null) save1 = ReflectionUtils.getMethod(nmsStackC, comp);
    } catch (NoSuchMethodException e1) {
      e1.printStackTrace();
    }
    asNMS = asNMS1;
    save = save1;
  }

  public static String itemstackToString(ItemStack item) {
    try {
      if(item == null) item = new ItemStack(Material.AIR);
      if(nmsStackC == null || cbStack == null || cmp == null) return "";
			if(comp != null)return save.invoke(asNMS.invoke(null,item)).toString();
      return save.invoke(asNMS.invoke(null, item), cmp.newInstance()).toString();
    } catch (Exception e) {
      return "";
    }
  }

  public static DynamicHoverAction fromName(String text) {
    for(DynamicHoverAction value : values()) {
      if(value.name().equalsIgnoreCase(text)) return value;
    }
    return null;
  }

}