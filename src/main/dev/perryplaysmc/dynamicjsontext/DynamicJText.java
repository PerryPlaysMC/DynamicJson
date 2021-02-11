package dev.perryplaysmc.dynamicjsontext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import dev.perryplaysmc.dynamicjsontext.data.DynamicClickAction;
import dev.perryplaysmc.dynamicjsontext.data.DynamicHoverAction;
import dev.perryplaysmc.dynamicjsontext.data.DynamicStyle;
import dev.perryplaysmc.dynamicjsontext.data.IJsonSerializable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 01/2021-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/

public class DynamicJText implements IJsonSerializable {


    private final String colorRegex = "(?:(#[a-fA-F0-9]{6})|((?:§[m|n|o|l|k])*(?:§[0-9|a|b|c|d|e|f|r](?:§[m|n|o|l|k])*)))?((?:(?![#][a-fA-F0-9]{6})[^§])+)?";
    private final String hexRegex = "§[x]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]";
    private final Pattern COLOR_PATTERN = Pattern.compile(colorRegex, Pattern.CASE_INSENSITIVE);
    private final Pattern HEX_PATTERN = Pattern.compile(hexRegex, Pattern.CASE_INSENSITIVE);
    private final Pattern SPACES_PATTERN = Pattern.compile("(\\S+)(\\s*)");
    private List<DynamicJPart> parts = new ArrayList<>();
    private List<DynamicJPart> currentEdits;

    public DynamicJText(DynamicJPart text) {
        currentEdits = new ArrayList<>();
        currentEdits.add(text);
    }

    public DynamicJText(String text) {
        currentEdits = new ArrayList<>();
        findColors(text);
    }

    public DynamicJText() {
        this("");
    }


    public DynamicJPart getPrevious() {
        if(parts.size()>0)
            return parts.get(parts.size()-1);
        return new DynamicJPart("").setColor(ChatColor.WHITE);
    }

    public DynamicJText add(DynamicJPart part) {
        if(part.getText().isEmpty())return this;
        for(DynamicJPart editing : currentEdits) {
            if(editing != null) {
                if(editing.getText().isEmpty()) continue;
                if(parts.size() > 0) {
                    if(parts.get(parts.size() - 1).isSimilar(editing)) {
                        DynamicJPart prev = parts.get(parts.size() - 1);
                        prev.setText(prev.getText() + editing.getText());
                        parts.set(parts.size() - 1, prev);
                    }
                }
                parts.add(editing);
            }
        }
        currentEdits = new ArrayList<>();
        currentEdits.add(part);
        return this;
    }

    public DynamicJText setText(String text) {
        currentEdits.clear();
        findColors(text);
        return this;
    }

    public DynamicJText setTextTranslated(String text) {
        currentEdits.clear();
        findColors(ChatColor.translateAlternateColorCodes(('&'),text));
        return this;
    }

    public DynamicJText add(String text) {
        findColors(text);
        return this;
    }

    public DynamicJText addTranslated(String text) {
        return add(ChatColor.translateAlternateColorCodes(('&'),text));
    }

    private String changeHex(String hex) {
        Matcher matcher = HEX_PATTERN.matcher(hex);
        while (matcher.find())
            if((matcher.group(0) != null && !matcher.group(0).isEmpty()))
                hex = hex.replace(matcher.group(), "#" + matcher.group().replace("§", "").substring(1));
        return hex;
    }

    private void findColors(String message) {
        Matcher matcher = COLOR_PATTERN.matcher(changeHex(message));
        if(currentEdits.size()>0) {
            parts.addAll(currentEdits);
            currentEdits.clear();
        }
        while(matcher.find()) {
            String text = matcher.group(3);
            List<DynamicStyle> styles = new ArrayList<>();
            ChatColor cColor = null;
            if((matcher.group(1)!=null&&!matcher.group(1).isEmpty())||(matcher.group(2)!=null&&!matcher.group(2).isEmpty())) {
                String color = (matcher.group(2) == null ? matcher.group(1) : matcher.group(2));
                boolean checkColor = true;
                if(color.endsWith("r")) {
                    cColor = ChatColor.WHITE;
                    checkColor = false;
                }
                if(checkColor) if(color.matches("(#[a-fA-F0-9]{6})"))
                    try {
                        cColor = ChatColor.of(color);
                        checkColor = false;
                    }catch (IllegalArgumentException ignored){}
                if(checkColor)
                    for(String s : color.split("§")) if(!s.isEmpty())
                        if(DynamicStyle.byChar(s.charAt(0))!=null) styles.add(DynamicStyle.byChar(s.charAt(0)));
                        else cColor = ChatColor.getByChar(s.charAt(0));
            }
            if(text == null) continue;
            DynamicJPart p = new DynamicJPart(text);
            p.setColor(cColor);
            p.setStyles(styles);
            if(currentEdits.size() > 0) {
                DynamicJPart prev = currentEdits.get(currentEdits.size() - 1);
                if(p.isSimilar(prev) && prev.checkColors(p)) {
                    currentEdits.remove(prev);
                    prev.setText(prev.getText() + p.getText());
                    currentEdits.add(prev);
                }else {
                    currentEdits.add(p);
                }
            }else currentEdits.add(p);
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


    public DynamicJText command(String text) {
        currentEdits.forEach(edit -> edit.onClick(DynamicClickAction.RUN_COMMAND, text));
        return this;
    }

    public DynamicJText suggest(String text) {
        currentEdits.forEach(edit -> edit.onClick(DynamicClickAction.SUGGEST_COMMAND, text));
        return this;
    }

    public DynamicJText insert(String text) {
        currentEdits.forEach(edit -> edit.insert(text));
        return this;
    }

    public DynamicJText copy(String text) {
        currentEdits.forEach(edit -> edit.onClick(DynamicClickAction.COPY_TO_CLIPBOARD, text));
        return this;
    }

    public DynamicJText url(String text) {
        currentEdits.forEach(edit -> edit.onClick(DynamicClickAction.OPEN_URL, text));
        return this;
    }


    public DynamicJText color(ChatColor color) {
        if(color==ChatColor.STRIKETHROUGH||color==ChatColor.BOLD||color==ChatColor.MAGIC||color==ChatColor.ITALIC||color==ChatColor.UNDERLINE)
            throw new IllegalArgumentException("Invalid ChatColor!");
        currentEdits.forEach(edit -> edit.setColor(color));
        String newText = "";
        for(DynamicJPart edit : currentEdits) {
            newText+=edit.getText();
        }
        DynamicJPart origin = currentEdits.get(0);
        origin.setText(newText);
        currentEdits.clear();
        currentEdits.add(origin);
        return this;
    }


    public DynamicJText color(org.bukkit.ChatColor color) {
        return color(color.asBungee());
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



    public String toPlainText() {
        if(!currentEdits.isEmpty()) {
            for(DynamicJPart currentEdit : currentEdits) {
                if(!parts.contains(currentEdit))parts.add(currentEdit);
            }
            currentEdits.clear();
        }
        String text = "";
        for(DynamicJPart part : parts) {
            String styles = "";
            for(DynamicStyle s : part.getStyles()) styles+=s.getAsColor().toString();
            text+=(part.getColor()!=null?part.getColor():"") + styles + part.getText();
        }
        return text.replace("§x ","");
    }

    public DynamicJText clone() {
        DynamicJText jText = new DynamicJText();
        jText.parts = this.parts;
        jText.currentEdits = this.currentEdits;
        return jText;
    }


    @Override
    public void toJson(JsonWriter writer, boolean end) throws IOException {
        if(!currentEdits.isEmpty()) {
            for(DynamicJPart currentEdit : currentEdits) {
                if(!parts.contains(currentEdit))parts.add(currentEdit);
            }
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
        DynamicJPart empty = new DynamicJPart("");
        List<DynamicJPart> remove = new ArrayList<>();
        for(DynamicJPart jPart : parts) if(jPart.isSimilar(empty) && jPart.getText().equals("")) remove.add(jPart);
        parts.removeAll(remove);
        for(int i = 0; i < parts.size(); i++) {
            DynamicJPart jPart = parts.get(i);
            if(jPart.isSimilar(empty) && jPart.getText().equals(""))continue;
            if(i+1 < parts.size() && !jPart.getText().equals("")) {
                DynamicJPart fut = parts.get(i+1);
                if(jPart.isSimilar2(fut) && jPart.hasEvents()) {
                    combine.add(jPart);
                    if(i+2 < parts.size()) {
                        DynamicJPart jp = parts.get(i+2);
                        if(!jPart.isSimilar2(jp)) {
                            combine.add(fut);
                            i++;
                        }
                    }
                }else {
                    if(!jPart.hasEvents() && jPart.isSimilar(fut) && jPart.checkColors(fut)) {
                        jPart.setText(jPart.getText() + fut.getText());
                        remove.add(fut);
                        i++;
                    }
                    if(combine.size() > 0 && combine.get(combine.size()-1).isSimilar2(jPart))combine.add(jPart);
                    boolean con = combine.contains(jPart);
                    writeExtra(writer, combine);
                    if(!con) jPart.toJson(writer, true);
                }
            }else {
                if(combine.size() > 0 && combine.get(combine.size()-1).isSimilar2(jPart))combine.add(jPart);
                boolean con = combine.contains(jPart);
                writeExtra(writer, combine);
                if(!con) jPart.toJson(writer, true);
                System.out.println(i + "/" + (parts.size()-1));
            }
        }
        parts.removeAll(remove);
        writer.endArray().endObject();
    }

    private void writeExtra(JsonWriter writer, List<DynamicJPart> extra) throws IOException {
        if(extra.size() > 1) {
            DynamicJPart pt = extra.get(0).clone();
            pt.override = true;
            pt.setText("").setColor(null).setStyles(new ArrayList<>()).toJson(writer, false);
            writer.name("extra").beginArray();
            for(DynamicJPart jp : extra) {
                jp.ignoreHoverClickData = true;
                jp.toJson(writer,true);
            }
            writer.endArray().endObject();
        }
        extra.clear();
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    public void send(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(toPlainText().replace("§x",""));
            return;
        }
        String json = toJsonString();
        ((Player) sender).spigot().sendMessage(ComponentSerializer.parse(json));
    }

    public void broadcast() {
        String json = toJsonString();
        BaseComponent[] comps = ComponentSerializer.parse(json);
        Bukkit.getOnlinePlayers().forEach(p-> p.spigot().sendMessage(comps));
    }

    public static DynamicJText fromJson(String json) {
        JsonParser parser = new JsonParser();
        JsonElement ele = parser.parse(json);
        JsonObject jObject = ele.getAsJsonObject();
        DynamicJText ret = new DynamicJText();
        DynamicJPart fromJS = fromJObject(jObject);
        if(fromJS!=null) ret.add(fromJS);
        if(jObject.has("extra")) {
            JsonArray arr = jObject.get("extra").getAsJsonArray();
            for(int i = 0; i < arr.size(); i++) {
                JsonObject jO = arr.get(i).getAsJsonObject();
                DynamicJPart fromJ = fromJObject(jO);
                if(fromJ!=null) ret.add(fromJ);
            }
        }
        return ret;
    }

    private static DynamicJPart fromJObject(JsonObject jObject) {
        if(!jObject.has("text"))return null;
        DynamicJPart part = new DynamicJPart(jObject.get("text").getAsString());
        if(jObject.has("color")) {
            String color = jObject.get("color").getAsString();
            if(color.contains("#")) part.setColor(ChatColor.of(color));
            else part.setColor(org.bukkit.ChatColor.valueOf(color.toUpperCase()).asBungee());
        }
        if(jObject.has("hoverEvent")) {
            JsonObject he = jObject.get("hoverEvent").getAsJsonObject();
            if(he.has("action") && he.has("value"))
                part.onHover(DynamicHoverAction.valueOf(he.get("action").getAsString().toUpperCase()), he.get("value").getAsString());
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
