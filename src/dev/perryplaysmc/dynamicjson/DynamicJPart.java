package dev.perryplaysmc.dynamicjson;

import com.google.gson.stream.JsonWriter;
import dev.perryplaysmc.dynamicjson.data.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.Nullable;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Owner: PerryPlaysMC
 * Created: 2/21
 **/

public class DynamicJPart implements IJsonSerializable {

   private String text;
   private DynamicHoverAction hoverAction = DynamicHoverAction.NONE;
   private String hoverData = "";
   private DynamicClickAction clickAction = DynamicClickAction.NONE;
   private String clickActionData = "";
   private String insertionData = "";
   private CColor color = null;
   private final Set<DynamicStyle> styles = new HashSet<>(), disabled = new HashSet<>();
   protected boolean override = false, ignoreHoverClickData = false;

   public DynamicJPart(String text) {
      this.text = text;
   }

   public String getText() {return text;}

   public DynamicHoverAction getHoverAction() {return hoverAction;}
   public String getHoverData() {return hoverData;}

   public DynamicClickAction getClickAction() {return clickAction;}
   public String getClickActionData() {return clickActionData;}

   public String getInsertionData() {return insertionData;}

   public Set<DynamicStyle> getStyles() {return new HashSet<>(styles);}
   public CColor getColor() {return color;}

   public DynamicJPart copy() {
      DynamicJPart part = new DynamicJPart(text);
      part.insert(insertionData);
      part.onClick(clickAction,clickActionData);
      part.onHover(hoverAction, hoverData);
      part.setStyles(styles);
      part.setColor(color);
      return part;
   }


   protected DynamicJPart disableStyle(DynamicStyle style) {
      disabled.add(style);
      styles.remove(style);
      return this;
   }

   protected DynamicJPart removeStyle(DynamicStyle style) {
      styles.remove(style);
      return this;
   }


   public DynamicJPart addStyle(DynamicStyle style) {
      styles.add(style);
      disabled.remove(style);
      return this;
   }

   public DynamicJPart setStyles(List<DynamicStyle> newStyles) {
      return setStyles(new HashSet<>(newStyles));
   }

   public DynamicJPart setStyles(Set<DynamicStyle> newStyles) {
      styles.clear();
      styles.addAll(newStyles);
      disabled.removeAll(styles);
      return this;
   }

   public DynamicJPart setDisabled(Set<DynamicStyle> newStyles) {
      disabled.clear();
      disabled.addAll(newStyles);
      styles.removeAll(disabled);
      return this;
   }

   public DynamicJPart setText(String newText) {
      this.text = newText;
      return this;
   }

   public DynamicJPart setColor(CColor color) {
      this.color = color;
      return this;
   }


   public boolean hasEvents() {
      return (getHoverAction() != DynamicHoverAction.NONE && !getHoverData().equals("")) ||
         (getClickAction() != DynamicClickAction.NONE && !getClickActionData().equals("")) ||
         !getInsertionData().equals("");
   }

   public boolean checkColors(DynamicJPart future) {
      return ((future.getStyles() == null && getStyles() != null) || future.getStyles().containsAll(getStyles()) || (future.getStyles().isEmpty() && getStyles().isEmpty()))
         &&((getColor() == null && future.getColor() == null) || (getColor() != null && future.getColor()==null) || (getColor() == future.getColor()));
   }



   public boolean matches(DynamicJPart future) {
      return checkColors(future) && isSimilar(future);
   }



   public boolean isSimilar(DynamicJPart future) {
      return (future.getHoverAction() == getHoverAction() &&
         future.getHoverData().equals(getHoverData())) &&
         (future.getClickAction() == getClickAction() &&
            future.getClickActionData().equals(getClickActionData())) &&
         future.getInsertionData().equals(getInsertionData());
   }

   public DynamicJPart onHover(DynamicHoverAction action, String value) {
      hoverAction = action;
      hoverData = value;
      return this;
   }

   public DynamicJPart onHover(String... text) {
      hoverAction = DynamicHoverAction.SHOW_TEXT;
      if(text == null) {
         hoverData = "";
         return this;
      }
      hoverData = "";
      for(String s : text) hoverData+=s + "\n";
      if(hoverData.endsWith("\n")) hoverData = hoverData.substring(0, hoverData.length() - ("\n").length());
      return this;
   }

   public DynamicJPart onHover(ItemStack item) {
      hoverAction = DynamicHoverAction.SHOW_ITEM;
      hoverData = convertItemStack(item);
      return this;
   }

   public DynamicJPart onHover(Entity enity) {
      hoverAction = DynamicHoverAction.SHOW_ENTITY;
      hoverData = "{\"type\":\"" + enity.getType().name().toLowerCase() + "\",\"id\":" + enity.getUniqueId() + ",\"name\":{\"text\":\"" + enity.getName() + "\"}}";
      return this;
   }



   public DynamicJPart onClick(DynamicClickAction action, String data) {
      this.clickAction = action;
      this.clickActionData = CColor.stripColor(action == DynamicClickAction.RUN_COMMAND && !data.startsWith("/") ? "/" + data : data);
      return this;
   }

   public DynamicJPart insert(String data) {
      this.insertionData = CColor.stripColor(data);
      return this;
   }


   @Override
   public void toJson(JsonWriter writer, boolean end) throws IOException {
      if(text.equals("") && !override)return;
      writer.beginObject().name("text").value(text);
      if(color != null) writer.name("color").value(color.toString().startsWith("§x") ? color.toString()
         .replace("§x", "#")
         .replace("§","") : color.name().toLowerCase());
      for(DynamicStyle style : styles) writer.name(style.name().toLowerCase()).value(true);
      for(DynamicStyle style : disabled) writer.name(style.name().toLowerCase()).value(false);
      if(!ignoreHoverClickData){
         if(!insertionData.isEmpty()) writer.name("insertion").value(insertionData);
         if(clickAction != DynamicClickAction.NONE && !clickActionData.isEmpty())
            writer.name("clickEvent").beginObject()
               .name("action").value(clickAction.toString().toLowerCase())
               .name("value").value(clickActionData).endObject();
         if(hoverAction != DynamicHoverAction.NONE && !hoverData.isEmpty()) {
            writer.name("hoverEvent").beginObject()
               .name("action").value(hoverAction.name().toLowerCase())
               .name("value").value(hoverData).endObject();
         }
      }
      if(end) writer.endObject();
   }


   private final Class<?> nmsStackC = Version.Minecraft.getClass("world.item.ItemStack"),
      cbStack = Version.CraftBukkit.getClass("inventory.CraftItemStack"),
      cmp = Version.Minecraft.getClass("nbt.NBTTagCompound");
   private final Method asNMS, save;

   {
      Method asNMS1, save1;
      try {
         asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
         save1 = nmsStackC.getMethod("save", cmp);
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
         asNMS1 = null;
         save1 = null;
      }
      asNMS = asNMS1;
      save = save1;
   }

   private String convertItemStack(ItemStack item) {
      try {
         if(nmsStackC==null||cbStack==null||cmp==null)return "";
         return save.invoke(asNMS.invoke(null, item), cmp.newInstance()).toString();
      }catch (Exception e) {
         return "";
      }
   }

   @Override
   public String toString() {
      return "DynamicJPart{" +
         "text='" + text + '\'' +
         ", hoverAction=" + hoverAction +
         ", hoverData='" + hoverData + '\'' +
         ", clickAction=" + clickAction +
         ", clickActionData='" + clickActionData + '\'' +
         ", insertionData='" + insertionData + '\'' +
         ", color=" + color +
         ", styles=" + styles +
         ", disabled=" + disabled +
         '}';
   }
}