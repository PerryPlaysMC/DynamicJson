package dev.perryplaysmc.dynamicjson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import dev.perryplaysmc.dynamicjson.data.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Owner: PerryPlaysMC
 * Created: 2/21
 **/

public class DynamicJText implements IJsonSerializable {

   private static final DynamicJPart EMPTY_PART = new DynamicJPart("");

   private final String colorRegex = "(?:(§[mnolkr])*(#[a-fA-F0-9]{6}|§[0-9abcdefr])*(§[mnolkr])*)?((?:(?![#][a-fA-F0-9]{6})[^§])*)";
   private final String hexRegex = "§[x](?:§[a-fA-F0-9]){6}";
   private final Pattern COLOR_PATTERN = Pattern.compile(colorRegex, Pattern.CASE_INSENSITIVE);
   private final Pattern HEX_PATTERN = Pattern.compile(hexRegex, Pattern.CASE_INSENSITIVE);
   private List<DynamicJPart> parts = new ArrayList<>();
   private List<DynamicJPart> currentEdits;
   private Set<DynamicStyle> toNextS = null;
   private CColor toNextC = null;


   public DynamicJText(DynamicJPart text) {
      currentEdits = new ArrayList<>();
      currentEdits.add(text);
   }

   public DynamicJText(String text) {
      currentEdits = new ArrayList<>();
      add(text);
   }

   public DynamicJText() {
      this("");
   }

   private boolean similarTo(Color c1, Color c2){
      double distance = (c1.getRed() - c2.getRed())*(c1.getRed() - c2.getRed()) +
         (c1.getGreen() - c2.getGreen())*(c1.getGreen() - c2.getGreen()) +
         (c1.getBlue() - c2.getBlue())*(c1.getBlue() - c2.getBlue());
      return distance <= 20;
   }

   protected static double getSimilarity(Color c1, Color c2){
      float diffRed   = Math.abs(c1.getRed() - c2.getRed())/255f;
      float diffGreen = Math.abs(c1.getGreen() - c2.getGreen())/255f;
      float diffBlue  = Math.abs(c1.getBlue() - c2.getBlue())/255f;
      return (diffRed + diffGreen + diffBlue) / 3 * 100;
   }

   protected static double getSimilarity(java.awt.Color c1, java.awt.Color c2){
      return getSimilarity(Color.fromRGB(c1.getRed(), c1.getGreen(), c1.getBlue()),Color.fromRGB(c2.getRed(), c2.getGreen(), c2.getBlue()));
   }

   List<Color> createGradient(int steps, java.awt.Color start, java.awt.Color... gradients) {
      List<Color> gradientList = new ArrayList<>();
      java.awt.Color color1 = start;
      java.awt.Color color2 = gradients[0];
      int index = 0;
      steps = (int) (steps/(gradients.length/1.5));
      A:for(int i = 0; i <= steps; i++) {
         float ratio = (float) i / (float) steps;
         int green = (int) (color2.getGreen() * ratio + color1.getGreen() * (1 - ratio));
         int blue = (int) (color2.getBlue() * ratio + color1.getBlue() * (1 - ratio));
         int red = (int) (color2.getRed() * ratio + color1.getRed() * (1 - ratio));
         Color stepColor = Color.fromRGB(red, green, blue);
         if(i == steps-1) {
            if(index+1 >= gradients.length) break;
            color1 = color2;
            color2 = gradients[++index];
            i = 0;
            continue;
         }
         for(Color gradient : gradientList)
            if(similarTo(stepColor,gradient)) continue A;
         if(!gradientList.contains(stepColor))
            gradientList.add(stepColor);
      }
      return gradientList;
   }

   public DynamicJPart getPrevious() {
      if(parts.size() > 0) {
         if(currentEdits.size() > 0) {
            int size = currentEdits.size() - 1;
            DynamicJPart part = currentEdits.get(size);
            while(part.toString().equals(EMPTY_PART.toString()) || part.getColor() == null) {
               part = currentEdits.get(size--);
               if(size == -1)  break;
            }
            if(part != null && !(part.toString().equals(EMPTY_PART.toString()) || part.getColor() == null)) return part;
         }
         int size = parts.size() - 1;
         DynamicJPart part = parts.get(size);
         while(part.toString().equals(EMPTY_PART.toString()) || part.getColor() == null) {
            part = parts.get(size--);
            if(size == -1) return part;
         }
         return part;
      }
      return new DynamicJPart("").setColor(CColor.WHITE);
   }

   public DynamicJText add(DynamicJPart part) {
      if(part.getText().isEmpty())return this;
      for(DynamicJPart editing : currentEdits) {
         if(editing != null) {
            if(editing.getText().isEmpty()) continue;
            if(parts.size() > 0) if(parts.get(parts.size() - 1).isSimilar(editing)) {
               DynamicJPart prev = parts.get(parts.size() - 1);
               prev.setText(prev.getText() + editing.getText());
               parts.set(parts.size() - 1, prev);
            }
            parts.add(editing);
         }
      }
      currentEdits = new ArrayList<>();
      currentEdits.add(part);
      return this;
   }

   public DynamicJText addPlain(String text) {
      if(text.isEmpty())return this;
      if(toNextC != null) {
         text = toNextC + text;
         toNextC = null;
      }
      if(toNextS != null) {
         String style = "";
         for(DynamicStyle toNext : toNextS) style+=toNext.getAsColor().toString();
         text = style + text;
         toNextS = null;
      }
      findColors(text);
      return this;
   }

   public DynamicJText addGradient(String text, java.awt.Color start, java.awt.Color... transition) {
      String newText = "";
      List<Color> gradient = createGradient(text.length(), start, transition);
      int index = 0;
      int increment = 1;
      for(char c : text.toCharArray()) {
         newText+=CColor.of(gradient.get(index)).toString()+c;
         index+=increment;
         if(index >= gradient.size()) {
            increment = -1;
            index+=increment;
         }
         if(index < 0){
            increment = 1;
            index+=increment;
         }
      }
      return add(newText);
   }

   public DynamicJText addGradient(String text, CColor start, CColor... transitions) {
      java.awt.Color[] transition = new java.awt.Color[transitions.length];
      for(int i = 0; i < transition.length; i++) transition[i] = transitions[i].getColor();
      return addGradient(text, start.getColor(), transition);
   }

   public DynamicJText add(String text) {
      return addPlain(CColor.translateAlternateColorCodes('&',text));
   }


   public DynamicJText add(DynamicJText dynamicJText) {
      if(!currentEdits.isEmpty()) {
         for(DynamicJPart currentEdit : currentEdits) if(!parts.contains(currentEdit))parts.add(currentEdit);
         currentEdits.clear();
      }
      if(!dynamicJText.currentEdits.isEmpty()) {
         for(DynamicJPart currentEdit : dynamicJText.currentEdits) if(!dynamicJText.parts.contains(currentEdit))dynamicJText.parts.add(currentEdit);
         dynamicJText.currentEdits.clear();
      }
      for(DynamicJPart part : dynamicJText.parts)
         if(!parts.contains(part)) parts.add(part);
      currentEdits = new ArrayList<>();
      return this;
   }


   public DynamicJText addReset(String text) {
      applyToNext(getPrevious().getStyles()).applyToNext(getPrevious().getColor());
      findColors(text);
      return this;
   }

   private DynamicJText applyToNext(Set<DynamicStyle> styles) {
      toNextS = styles;
      return this;
   }

   private DynamicJText applyToNext(CColor color) {
      toNextC = color;
      return this;
   }

   private String changeHex(String hex) {
      Matcher matcher = HEX_PATTERN.matcher(hex);
      while(matcher.find())
         if((matcher.group(0) != null && !matcher.group(0).isEmpty()))
            hex = hex.replace(matcher.group(), "#" + matcher.group().replace("§", "").substring(1));
      return hex;
   }

   private void findColors(String message) {
      Matcher matcher = COLOR_PATTERN.matcher(changeHex(message));
      if(currentEdits.size()>0) parts.addAll(currentEdits);
      currentEdits.clear();
      List<DynamicStyle> styles = new ArrayList<>();
      String r = ("§r");
      while(matcher.find()) {
         CColor cColor = null;
         String text = matcher.group(4);
         boolean cleared = false;
         if((matcher.group(1)!=null&&!matcher.group(1).isEmpty())||(matcher.group(2)!=null&&!matcher.group(2).isEmpty())||(matcher.group(3)!=null&&!matcher.group(3).isEmpty())) {
            String color = (matcher.group(2) == null ? "" : matcher.group(2));
            String s1 = matcher.group(1) == null ? "" : matcher.group(1);
            String s2 = matcher.group(3) == null ? "" : matcher.group(3);
            if(color.endsWith(r)) {
               cColor = CColor.WHITE;
               styles.clear();
            }else try {
               for(String s : s1.toLowerCase().split("§")) {
                  if(s.isEmpty())continue;
                  if(s.charAt(0) == r.charAt(r.length()-1)) {
                     styles.clear();cColor = null;cleared = true;
                     continue;
                  }
                  if(DynamicStyle.byChar(s.charAt(0)) != null) styles.add(DynamicStyle.byChar(s.charAt(0)));
               }
               if(!color.isEmpty()) if(color.endsWith(r)) {
                  styles.clear();
                  cColor = null;
               }else cColor = CColor.fromHex(color);
               for(String s : s2.toLowerCase().split("§")) {
                  if(s.isEmpty())continue;
                  if(s.charAt(0) == r.charAt(r.length()-1)) {
                     styles.clear();cColor = null;cleared = true;
                     continue;
                  }
                  if(DynamicStyle.byChar(s.charAt(0)) != null) styles.add(DynamicStyle.byChar(s.charAt(0)));
               }
            }catch (IllegalArgumentException ignored){}
         }
         if(text == null && cColor==null&&styles.isEmpty()) continue;
         text = text == null ? "" : text;
         DynamicJPart p = new DynamicJPart(text);
         p.setColor(cColor);
         p.setStyles(styles);
         if(currentEdits.size() > 0) {
            DynamicJPart prev = currentEdits.get(currentEdits.size() - 1);
            if(p.matches(prev) && prev.checkColors(p)) {
               currentEdits.remove(prev);
               prev.setText(prev.getText() + p.getText());
               currentEdits.add(prev);
            }else {
               if(!cleared && cColor == null) {
                  DynamicJPart part = currentEdits.get(currentEdits.size()-1);
                  if(part.getText() == null || part.getText().isEmpty()) p.setColor(part.getColor()).setStyles(part.getStyles());
               }currentEdits.add(p);
            }
         }else {
            if(parts.size() > 0) {
               DynamicJPart prev = parts.get(parts.size() - 1);
               if(prev.getColor() != null && p.getColor() == null) p.setColor(prev.getColor());
            }currentEdits.add(p);
         }
      }
      if(currentEdits.isEmpty()) currentEdits.add(new DynamicJPart(message));
   }


   public DynamicJText onHover(ItemStack item) {
      Validate.notNull(item);
      currentEdits.forEach(edit -> edit.onHover(item));
      return this;
   }

   public DynamicJText onHover(Entity entity) {
      Validate.notNull(entity);
      currentEdits.forEach(edit -> edit.onHover(entity));
      return this;
   }

   public DynamicJText onHover(String... text) {
      currentEdits.forEach(edit -> edit.onHover(text));
      return this;
   }

   public DynamicJText onHover(DynamicHoverAction action, String... text) {
      currentEdits.forEach(edit -> edit.onHover(action, String.join("\n",text)));
      return this;
   }


   public DynamicJText chat(String text) {
      return onClick(DynamicClickAction.RUN_COMMAND, text);
   }


   public DynamicJText command(String text) {
      text = text.startsWith("/") ? text : "/" + text;
      String finalText = text;
      return onClick(DynamicClickAction.RUN_COMMAND, finalText);
   }

   public DynamicJText suggest(String text) {
      return onClick(DynamicClickAction.SUGGEST_COMMAND, text);
   }

   public DynamicJText insert(String text) {
      currentEdits.forEach(edit -> edit.insert(text));
      return this;
   }

   public DynamicJText copy(String text) {
      return onClick(DynamicClickAction.COPY_TO_CLIPBOARD, text);
   }

   public DynamicJText url(String text) {
      return onClick(DynamicClickAction.OPEN_URL, text);
   }

   public DynamicJText onClick(DynamicClickAction action, String text) {
      currentEdits.forEach(edit -> edit.onClick(action,text));
      return this;
   }


   public DynamicJText color(CColor color) {
      if(color==CColor.STRIKETHROUGH||color==CColor.BOLD||color==CColor.MAGIC||color==CColor.ITALIC||color==CColor.UNDERLINE)
         throw new IllegalArgumentException("Invalid CColor!");
      currentEdits.forEach(edit -> edit.setColor(color));
      return this;
   }

   public DynamicJText color(org.bukkit.ChatColor color) {
      return color(CColor.fromHex(color.toString()));
   }

   public DynamicJText addStyle(DynamicStyle style) {
      currentEdits.forEach(edit -> edit.addStyle(style));
      return this;
   }

   public DynamicJText addStyle(DynamicStyle... styles) {
      for(DynamicStyle style : styles) addStyle(style);
      return this;
   }


   public String toJsonString() {
      StringWriter sWriter = new StringWriter();
      JsonWriter jWriter = new JsonWriter(sWriter);
      try {
         toJson(jWriter, true);
         jWriter.close();
         return sWriter.toString();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return "Failed";
   }


   public BaseComponent[] toComponents() {
      return ComponentSerializer.parse(toJsonString());
   }

   public String toPlainText() {
      if(!currentEdits.isEmpty()) {
         for(DynamicJPart currentEdit : currentEdits) if(!parts.contains(currentEdit))parts.add(currentEdit);
         currentEdits.clear();
      }
      String text = "";
      for(DynamicJPart part : parts) {
         String styles = "";
         for(DynamicStyle s : part.getStyles()) styles+=s.getAsColor().toString();
         text+=(part.getColor()!=null?part.getColor():"") + styles + part.getText();
      }
      return text;
   }

   public DynamicJText clone() {
      DynamicJText jText = new DynamicJText();
      jText.parts = new ArrayList<>(this.parts);
      jText.currentEdits = new ArrayList<>(this.currentEdits);
      return jText;
   }


   @Override
   public void toJson(JsonWriter writer, boolean end) throws IOException {
      if(!currentEdits.isEmpty()) {
         for(DynamicJPart currentEdit : currentEdits) if(!parts.contains(currentEdit))parts.add(currentEdit);
         currentEdits.clear();
      }
      DynamicJPart part = parts.size() > 0 ? parts.get(0) : null;
      if(part==null) return;
      if(parts.size() == 1) {
         part.toJson(writer, true);
         return;
      }
      writer.beginObject().name("text").value("").name("extra").beginArray();
      List<DynamicJPart> combine = new ArrayList<>();
      List<DynamicJPart> remove = new ArrayList<>();
      for(DynamicJPart jPart : parts) if(jPart.matches(EMPTY_PART) && jPart.getText().equals("")) remove.add(jPart);
      parts.removeAll(remove);
      for(int i = 0; i < parts.size(); i++) {
         DynamicJPart jPart = parts.get(i);
         if(jPart.matches(EMPTY_PART) && jPart.getText().equals(""))continue;
         if(i+1 < parts.size() && !jPart.getText().equals("")) {
            DynamicJPart fut = parts.get(i+1);
            if(jPart.isSimilar(fut) && jPart.hasEvents()) {
               combine.add(jPart);
               if(i+2 < parts.size()) {
                  DynamicJPart jp = parts.get(i+2);
                  if(!jPart.isSimilar(jp)) {
                     combine.add(fut);
                     writeExtra(writer,combine);
                     i++;
                  }
               }
            }else {
               if((!jPart.hasEvents() && jPart.matches(fut) && jPart.checkColors(fut)) || (jPart.getText().replace(" ","").isEmpty() && !fut.hasEvents())) {
                  String x = jPart.getText().replace((" "), (""));
                  jPart.setText(jPart.getText() + fut.getText());
                  if(x.isEmpty())jPart.setColor(fut.getColor()).setStyles(fut.getStyles());
                  remove.add(fut);
                  i++;
               }
               writeExtra(writer, combine);
               jPart.toJson(writer,true);
            }
         }else {
            writeExtra(writer, combine);
            jPart.toJson(writer, true);
         }
      }

      if(!combine.isEmpty()) writeExtra(writer, combine);

      parts.removeAll(remove);
      writer.endArray().endObject();
   }

   private void writeExtra(JsonWriter writer, List<DynamicJPart> extra) throws IOException {
      if(extra.size() > 1) {
         DynamicJPart pt = extra.get(0).copy();
         pt.override = true;
         List<DynamicStyle> styles = new ArrayList<>();
         HashMap<DynamicStyle, Integer> stylesMap = new HashMap<>();
         for(DynamicJPart dynamicJPart : extra)
            for(DynamicStyle style : pt.getStyles())
               if(dynamicJPart.getStyles().contains(style)) stylesMap.put(style, stylesMap.getOrDefault(style, 0) + 1);
         stylesMap.forEach((dynamicStyle, integer) ->{
            if(integer >= (extra.size()/2)) styles.add(dynamicStyle);
         });
         pt.setText("").setColor(null).setStyles(new HashSet<>());
         for(DynamicStyle style : styles) pt.addStyle(style);
         pt.toJson(writer, false);
         writer.name("extra").beginArray();
         String add = null;
         for(DynamicJPart jp : new ArrayList<>(extra)) {
            jp = jp.copy();
            jp.ignoreHoverClickData = true;
            for(DynamicStyle style : styles)
               if(!jp.getStyles().contains(style)) jp.disableStyle(style);
               else jp.removeStyle(style);
            if(add!=null) {
               String text = jp.getText();
               jp.setText(add + text);
               add = null;
            }
            if(jp.getText().replace(" ","").isEmpty()) {
               add = jp.getText();
               extra.remove(jp);
               parts.remove(jp);
               continue;
            }
            jp.toJson(writer,true);
         }
         writer.endArray().endObject();
      }else {
         if(extra.size() == 1) {
            extra.get(0).toJson(writer,true);
         }
      }
      extra.clear();
   }

   @Override
   public String toString() {
      return toJsonString();
   }


   public void send(CommandSender sender, String json) {
      sender.spigot().sendMessage(ComponentSerializer.parse(json));
   }

   public void send(CommandSender... senders) {
      String json = toJsonString();
      String plain = toPlainText().replace("§x","");
      for(CommandSender sender : senders)
         if(sender instanceof Player) send(sender, json);
         else sender.sendMessage(plain);
   }

   public void send(Collection<CommandSender> senders) {
      String json = toJsonString();
      String plain = toPlainText().replace("§x","");
      for(CommandSender sender : senders)
         if(sender instanceof Player) send(sender, json);
         else sender.sendMessage(plain);
   }

   public void broadcast() {
      send(new HashSet<>(Bukkit.getOnlinePlayers()));
   }


   public static DynamicJText fromComponents(BaseComponent[] comp) {
      return fromJson(ComponentSerializer.toString(comp));
   }

   public static DynamicJText fromJson(String json) {
      try {
         return fromExtra(new JsonParser().parse(json).getAsJsonObject());
      }catch (Exception e) {
         return new DynamicJText("Failed to parse: \n" + json);
      }
   }

   private static DynamicJText fromExtra(JsonObject object) {
      DynamicJText ret = new DynamicJText();
      {
         DynamicJPart fromJ = fromJObject(object);
         if(fromJ != null) ret.add(fromJ);
      }
      if(object.has("extra")) {
         JsonArray arr = object.get("extra").getAsJsonArray();
         for(int i = 0; i < arr.size(); i++) {
            JsonObject jO = arr.get(i).getAsJsonObject();
            if(jO.has("extra")) {
               DynamicJText text = new DynamicJText();
               DynamicJPart part = fromJObject(jO);
               DynamicJText add = fromExtra(jO);
               if(part!=null) {
                  text.add(part);
                  if(part.getText().isEmpty()) {
                     add.toJsonString();
                     add.currentEdits.addAll(add.parts);
                     add.parts.clear();
                     if(part.getHoverAction() != DynamicHoverAction.NONE)
                        add.onHover(part.getHoverAction(), part.getHoverData());
                     if(part.getClickAction()!=DynamicClickAction.NONE)
                        add.onClick(part.getClickAction(), part.getClickActionData());
                     if(part.getInsertionData().isEmpty())
                        add.insert(part.getInsertionData());
                     add.parts.addAll(add.currentEdits);
                     add.currentEdits.clear();
                     add.toJsonString();
                  }
               }
               text.add(add);
               ret.add(text);
            } else {
               DynamicJPart fromJ = fromJObject(jO);
               if(fromJ != null) ret.add(fromJ);
            }
         }
      }
      return ret;
   }


   private static DynamicJPart fromJObject(JsonObject jObject) {
      if(!jObject.has("text"))return null;
      DynamicJPart part = new DynamicJPart(jObject.get("text").getAsString());
      if(jObject.has("color"))
         part.setColor(CColor.fromTranslated(jObject.get("color").getAsString()));
      if(jObject.has("hoverEvent")) {
         JsonObject he = jObject.get("hoverEvent").getAsJsonObject();
         if(he.has("action")) {
            JsonElement get = he.has("value") ? he.get("value") :
               he.has("contents") ? he.get("contents") : null;
            String val = "";
            if(get != null)
               if(get instanceof JsonArray)
                  for (JsonElement jsonElement : get.getAsJsonArray()) {
                     DynamicJPart jp = fromJObject(jsonElement.getAsJsonObject());
                     if(jp != null) val+=jp.getHoverData()+"\n";
                  }
               else val = get.getAsString();
            val = val.trim();
            part.onHover(DynamicHoverAction.valueOf(he.get("action").getAsString().toUpperCase()), val);
         }
      }
      if(jObject.has("clickEvent")) {
         JsonObject he = jObject.get("clickEvent").getAsJsonObject();
         if(he.has("action") && he.has("value"))
            part.onClick(DynamicClickAction.valueOf(he.get("action").getAsString().toUpperCase()), he.get("value").getAsString());
      }
      if(jObject.has("insertion")) part.insert(jObject.get("insertion").getAsString());
      return part;
   }
}
