package dev.perryplaysmc.dynamicjson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import dev.perryplaysmc.dynamicjson.data.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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

    private final String colorRegex = "(?:(#[a-fA-F0-9]{6})|((?:§[m|n|o|l|k])*(?:§[0-9|a|b|c|d|e|f|r](?:§[m|n|o|l|k])*)))?((?:(?![#][a-fA-F0-9]{6})[^§])+)?";
    private final String hexRegex = "§[x]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]§[a-fA-F0-9]";
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
    public DynamicJPart getPrevious() {
        if(parts.size()>0) {
            int size = parts.size()-1;
            DynamicJPart part = parts.get(size);
            while(part.toString().equals("DynamicJPart{text='', hoverAction=NONE, hoverData='', clickAction=NONE, clickActionData='', insertionData='', color=null, styles=[]}")) {
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
                if(parts.size() > 0) {
                    if(parts.get(parts.size() - 1).matches(editing)) {
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

    public DynamicJText add(String text) {
        if(toNextC != null) {
            text = toNextC.toString() + text;
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

    public DynamicJText addTranslated(String text) {
        return add(CColor.translateAlternateColorCodes(('&'),text));
    }


    public DynamicJText add(DynamicJText dynamicJText) {
        if(!currentEdits.isEmpty()) {
            for(DynamicJPart currentEdit : currentEdits) {
                if(!parts.contains(currentEdit))parts.add(currentEdit);
            }
            currentEdits.clear();
        }
        if(!dynamicJText.currentEdits.isEmpty()) {
            for(DynamicJPart currentEdit : dynamicJText.currentEdits) {
                if(!dynamicJText.parts.contains(currentEdit))dynamicJText.parts.add(currentEdit);
            }
            dynamicJText.currentEdits.clear();
        }
        for(DynamicJPart part : dynamicJText.parts)
            if(!parts.contains(part)) parts.add(part);
        currentEdits = new ArrayList<>();
        return this;
    }


    public DynamicJText addReset(String text) {
        return add(text).applyToNext(getPrevious().getStyles()).applyToNext(getPrevious().getColor());
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
        if(currentEdits.size()>0) {
            parts.addAll(currentEdits);
            currentEdits.clear();
        }
        while(matcher.find()) {
            String text = matcher.group(3);
            List<DynamicStyle> styles = new ArrayList<>();
            CColor cColor = null;
            if((matcher.group(1)!=null&&!matcher.group(1).isEmpty())||(matcher.group(2)!=null&&!matcher.group(2).isEmpty())) {
                String color = (matcher.group(2) == null ? matcher.group(1) : matcher.group(2));
                boolean checkColor = true;
                if(color.endsWith("r")) {
                    cColor = CColor.WHITE;
                    checkColor = false;
                }
                if(checkColor) if(color.matches("(#[a-fA-F0-9]{6})"))
                    try {
                        cColor = CColor.fromHex(color);
                        checkColor = false;
                    }catch (IllegalArgumentException ignored){}
                if(checkColor)
                    for(String s : color.split("§")) if(!s.isEmpty())
                        if(DynamicStyle.byChar(s.charAt(0))!=null) styles.add(DynamicStyle.byChar(s.charAt(0)));
                        else cColor = CColor.getByChar(s.charAt(0));
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
                    currentEdits.add(p);
                }
            }else {
                if(parts.size() > 0) {
                    DynamicJPart prev = parts.get(parts.size() - 1);
                    if(prev.getColor() != null && p.getColor() == null)
                        p.setColor(prev.getColor());
                }
                currentEdits.add(p);
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
        currentEdits.forEach(edit -> edit.onClick(action, text));
        return this;
    }

    public DynamicJText color(CColor color) {
        if(color==CColor.STRIKETHROUGH||color==CColor.BOLD||color==CColor.MAGIC||color==CColor.ITALIC||color==CColor.UNDERLINE)
            throw new IllegalArgumentException("Invalid CColor!");
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

    public DynamicJText merge(DynamicJText other) {
        parts.addAll(currentEdits);
        currentEdits.clear();
        other.parts.addAll(other.currentEdits);
        other.currentEdits.clear();
        parts.addAll(other.parts);
        other.parts.clear();
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
        jText.parts = new ArrayList<>(this.parts);
        jText.currentEdits = new ArrayList<>(this.currentEdits);
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
        for(DynamicJPart jPart : parts) if(jPart.matches(empty) && jPart.getText().equals("")) remove.add(jPart);
        parts.removeAll(remove);
        for(int i = 0; i < parts.size(); i++) {
            DynamicJPart jPart = parts.get(i);
            if(jPart.matches(empty) && jPart.getText().equals(""))continue;
            if(i+1 < parts.size() && !jPart.getText().equals("")) {
                DynamicJPart fut = parts.get(i+1);
                if(jPart.isSimilar(fut) && jPart.hasEvents()) {
                    combine.add(jPart);
                    if(i+2 < parts.size()) {
                        DynamicJPart jp = parts.get(i+2);
                        if(!jPart.isSimilar(jp)) {
                            combine.add(fut);
                            i++;
                        }
                    }
                }else {
                    if(!jPart.hasEvents() && jPart.matches(fut) && jPart.checkColors(fut)) {
                        jPart.setText(jPart.getText() + fut.getText());
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
        parts.removeAll(remove);
        writer.endArray().endObject();
    }

    private void writeExtra(JsonWriter writer, List<DynamicJPart> extra) throws IOException {
        if(extra.size() > 1) {
            DynamicJPart pt = extra.get(0).copy();
            pt.override = true;
            List<DynamicStyle> styles = new ArrayList<>();
            HashMap<DynamicStyle, Integer> stylesMap = new HashMap<>();
            for(DynamicJPart dynamicJPart : extra) {
                for(DynamicStyle style : pt.getStyles()) {
                    if(dynamicJPart.getStyles().contains(style))
                        stylesMap.put(style, stylesMap.getOrDefault(style,0)+1);
                }
            }
            stylesMap.forEach((dynamicStyle, integer) ->{
                if(integer >= extra.size()) styles.add(dynamicStyle);
            });
            pt.setText("").setColor(null).setStyles(styles).toJson(writer, false);
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
        send(sender, toJsonString());
    }

    public void send(CommandSender sender, String json) {
        sender.spigot().sendMessage(ComponentSerializer.parse(json));
    }

    public void send(CommandSender... senders) {
        BaseComponent[] comps = toComponents();
        for(CommandSender sender : senders) sender.spigot().sendMessage(comps);
    }

    public void broadcast() {
        String json = toJsonString();
        BaseComponent[] comps = ComponentSerializer.parse(json);
        Bukkit.getOnlinePlayers().forEach(p-> p.spigot().sendMessage(comps));
    }


    public static DynamicJText fromComponents(BaseComponent[] comp) {
        return fromJson(ComponentSerializer.toString(comp));
    }

    public static DynamicJText fromJson(String json) {
        return fromExtra(JsonParser.parseString(json).getAsJsonObject());
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
                                ret.insert(part.getInsertionData());
                            add.parts.addAll(add.currentEdits);
                            add.currentEdits.clear();
                            add.toJsonString();
                        }
                    }
                    text.add(add);
                    ret.add(text);
                } else {
                    DynamicJPart fromJ = fromJObject(jO);
                    if(fromJ != null) {
                        ret.add((fromJ.getColor()!=null?fromJ.getColor()+"":"") + fromJ.getStyles().stream().map(s->s.getAsColor().toString()).collect(Collectors.joining()) + fromJ.getText());
                        if(fromJ.getText().isEmpty()) {
                            if(fromJ.getHoverAction() != DynamicHoverAction.NONE)
                                ret.onHover(fromJ.getHoverAction(), fromJ.getHoverData());
                            if(fromJ.getClickAction()!=DynamicClickAction.NONE)
                                ret.onClick(fromJ.getClickAction(),fromJ.getClickActionData());
                            if(fromJ.getInsertionData().isEmpty())
                                ret.insert(fromJ.getInsertionData());
                        }
                    }
                }
            }
        }
        return ret;
    }


    private static DynamicJPart fromJObject(JsonObject jObject) {
        if(!jObject.has("text"))return null;
        DynamicJPart part = new DynamicJPart(jObject.get("text").getAsString());
        if(jObject.has("color")) {
            String color = jObject.get("color").getAsString();
            if(color.contains("#")) part.setColor(CColor.fromHex(color));
            else part.setColor(CColor.fromName(color.toUpperCase()));
        }
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
