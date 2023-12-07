package dev.dynamicstudios.json;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import dev.dynamicstudios.json.data.component.DynamicGradientComponent;
import dev.dynamicstudios.json.data.component.DynamicTextComponent;
import dev.dynamicstudios.json.data.component.IComponent;
import dev.dynamicstudios.json.data.util.*;
import dev.dynamicstudios.json.data.util.packet.JsonSender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DynamicJText extends DynamicTextComponent {

  private static final boolean USE_SPIGOT = Version.findClass("net.md_5.bungee.chat.ComponentSerializer") != null;

  private static final String REPLACEMENT_SEARCH_REGEX = "\\\"(?:text|value|contents)\\\":\\s*\\\"(?<data>(?:(?=\\\\\\\")..|(?!\\\").)*)\\\"";
  private static final Pattern REPLACEMENT_SEARCH = Pattern.compile(REPLACEMENT_SEARCH_REGEX);

  private static final String SELECTOR_SEARCH_REGEX = "\\\"selector\\\":\\\"(?<data>@[apers](?:\\[.*])?)\\\"";
  private static final Pattern SELECTOR_SEARCH = Pattern.compile(SELECTOR_SEARCH_REGEX);

  private static final String SCORE_SEARCH_REGEX = "\\\"score\\\":\\{\\\"name\\\":\\\"(?<name>[^\\\"]+)\\\",\\\"objective\\\":\\\"(?<obj>[^\\\"]+)\\\"";
  private static final Pattern SCORE_SEARCH = Pattern.compile(SCORE_SEARCH_REGEX);

  private static final String NBT_SEARCH_REGEX = "\\\"nbt\\\":\\s*\\\"(?<path>[^\\\"]+)\\\",\\\"entity\\\":\\\"(?<entity>[a-z0-9]{8}-(?:[a-z0-9]{4}-){3}[a-z0-9]{12})\\\"\\}";
  private static final Pattern NBT_SEARCH = Pattern.compile(NBT_SEARCH_REGEX);

  private static final String PARSE_REGEX = "((<(?<id>[^=]+)=\\\"(?<data>(?:(?=\\\\\\\")..|(?!\\\").)*)\\\">)(?<text>(?:(?!</\\k<id>).)*(?:(?!>(?:\\s|[.])).)*)</\\k<id>>)";
  private static final Pattern PARSE_PATTERN = Pattern.compile(PARSE_REGEX, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private static final List<String> PARSE_IDS = Arrays.asList("hover", "command", "chat", "copy", "suggest", "insert", "url", "gradient");


  private final Map<String, Function<CommandSender, String>> REPLACEMENTS = new HashMap<>();
  private String json = "";

  public DynamicJText(String... text) {
    if(text.length > 0) add(text);
  }

  public DynamicJText(IComponent component, IComponent... components) {
    add(component);
    if(components.length > 0) Arrays.stream(components).forEach(this::add);
  }

  @Override
  public DynamicJText text(String... text) {
    if(!edit().equals(EMPTY_COMPONENT)) edit().keyValue(String.join("\n", text));
    else add(text);
    return dirtify();
  }

  @Override
  public DynamicJText color(CColor color) {
    super.edit().color(color);
    return dirtify();
  }

  @Override
  public DynamicJText addDefault(String... children) {
    return (DynamicJText) super.addDefault(children);
  }

  @Override
  public DynamicJText add(IComponent child) {
    return (DynamicJText) super.add(child);
  }

  @Override
  public DynamicJText addReset(String... children) {
    return (DynamicJText) super.addReset(children);
  }

  public DynamicJText translate(Translation translation) {
    return (DynamicJText) super.add(IComponent.translationComponent(translation));
  }

  public DynamicJText keybind(Keybind keybind) {
    return (DynamicJText) super.add(IComponent.keybindComponent(keybind));
  }

  @Override
  public DynamicJText add(String... children) {
    return (DynamicJText) super.add(children);
  }

  @Override
  public DynamicJText command(String command) {
    super.edit().command(command);
    return dirtify();
  }

  @Override
  public DynamicJText chat(String text) {
    super.edit().chat(text);
    return dirtify();
  }

  @Override
  public DynamicJText suggest(String text) {
    super.edit().suggest(text);
    return dirtify();
  }

  @Override
  public DynamicJText url(String url) {
    super.edit().url(url);
    return dirtify();
  }

  @Override
  public DynamicJText copy(String clipboard) {
    super.edit().copy(clipboard);
    return dirtify();
  }

  @Override
  public DynamicJText changePage(String page) {
    super.edit().changePage(page);
    return dirtify();
  }

  @Override
  public DynamicJText hover(String... text) {
    super.edit().hover(text);
    return dirtify();
  }

  @Override
  public DynamicJText hoverPlain(String... text) {
    super.edit().hoverPlain(text);
    return dirtify();
  }

  @Override
  public DynamicJText hover(ItemStack item) {
    super.edit().hover(item);
    return dirtify();
  }

  @Override
  public DynamicJText hover(Entity entity) {
    super.edit().hover(entity);
    return dirtify();
  }

  @Override
  public DynamicJText tooltip(String... text) {
    super.edit().tooltip(text);
    return dirtify();
  }

  @Override
  public DynamicJText tooltipPlain(String... text) {
    super.edit().tooltipPlain(text);
    return dirtify();
  }

  @Override
  public DynamicJText tooltip(ItemStack item) {
    super.edit().tooltip(item);
    return dirtify();
  }

  @Override
  public DynamicJText tooltip(Entity entity) {
    super.edit().tooltip(entity);
    return dirtify();
  }

  @Override
  public DynamicJText dirtify() {
    return (DynamicJText) super.dirtify();
  }

  @Override
  public DynamicJText hover(DynamicHoverAction hoverAction, String hover) {
    super.edit().hover(hoverAction, hover);
    return dirtify();
  }

  @Override
  public DynamicJText click(DynamicClickAction clickAction, String click) {
    super.edit().click(clickAction, click);
    return dirtify();
  }

  @Override
  public DynamicJText insertion(String insertion) {
    super.edit().insertion(insertion);
    return dirtify();
  }

  @Override
  public DynamicJText enableStyles(DynamicStyle... styles) {
    super.edit().enableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicJText disableStyles(DynamicStyle... styles) {
    super.edit().disableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicJText enableStyles(Collection<DynamicStyle> styles) {
    super.edit().enableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicJText disableStyles(Collection<DynamicStyle> styles) {
    super.edit().disableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicJText font(String font) {
    super.edit().font(font);
    return dirtify();
  }

  public DynamicJText defaultColor(CColor color) {
    return (DynamicJText) super.color(color);
  }

  public DynamicJText enableDefaultStyles(DynamicStyle... styles) {
    return (DynamicJText) super.enableStyles(styles);
  }


  public DynamicJText disableDefaultStyles(DynamicStyle... styles) {
    return (DynamicJText) super.disableStyles(styles);
  }

  public DynamicJText enableDefaultStyles(Collection<DynamicStyle> styles) {
    return (DynamicJText) super.enableStyles(styles);
  }

  public DynamicJText disableDefaultStyles(Collection<DynamicStyle> styles) {
    return (DynamicJText) super.disableStyles(styles);
  }


  public DynamicJText replace(String key, Function<CommandSender, String> replacement) {
    REPLACEMENTS.put(key, replacement);
    return this;
  }

  @Override
  public String toString() {
    if(dirty()||json==null||json.isEmpty()) {
	    StringBuilder stringWriter = new StringBuilder();
	    JsonBuilder writer = new JsonBuilder(stringWriter);
	    writeJson(writer);
	    json = stringWriter.toString();
	    clean();
    }
    return json;
  }

  @Override
  public DynamicJText clean() {
    return (DynamicJText) super.clean();
  }

  public static DynamicJText parseJson(String jsonString) {
    JsonElement json = parseJsonString(jsonString);
    DynamicJText text = new DynamicJText();
    if(json.isJsonArray()) {
      parseArray(json.getAsJsonArray()).forEach(text.children()::add);
    } else if(json.isJsonPrimitive()) {
      return new DynamicJText(json.getAsJsonPrimitive().getAsString());
    } else if(json.isJsonObject()) {
      text.add(parseJsonObject(json.getAsJsonObject())).complete();
      return text;
    }
    return null;
  }

  private static List<IComponent> parseArray(JsonArray array) {
    List<IComponent> results = new ArrayList<>();
    for(JsonElement jsonElement : array) {
      if(jsonElement.isJsonPrimitive()) results.add(IComponent.textComponent(jsonElement.getAsString()));
      else if(jsonElement.isJsonArray()) {
        results.addAll(parseArray(jsonElement.getAsJsonArray()));
      } else if(jsonElement.isJsonObject()) {
        IComponent component = parseJsonObject(jsonElement.getAsJsonObject());
        if(component != null) results.add(component);
      }
    }
    return results;
  }

  private static IComponent parseJsonObject(JsonObject jObject) {
    String type = "text";
    IComponent component = jObject.has(type) ? IComponent.textComponent(jObject.get(type).getAsString()) : null;
    type = "translate";
    if(component == null) {
      component = jObject.has(type) ? IComponent.translationComponent(Translation.byId(jObject.get(type).getAsString())) : null;
    }
    type = "keybind";
    if(component == null) component = jObject.has(type) ? IComponent.keybindComponent(Keybind.byId(jObject.get(type).getAsString())) : null;
    type = "nbt";
    if(component == null) {
      component = jObject.has(type) ? IComponent.nbtComponent(jObject.get(type).getAsString(), UUID.fromString(jObject.get("entity").getAsString())) : null;
    }
    type = "selector";
    if(component == null) component = jObject.has(type) ? IComponent.selectorComponent(jObject.get(type).getAsString()) : null;
    type = "score";
    if(component == null) {
      component = jObject.has(type) ? IComponent.scoreComponent(jObject.get(type).getAsJsonObject().get("name").getAsString(),
        jObject.get(type).getAsJsonObject().get("objective").getAsString()) : null;
    }
    if(component == null) return null;
    type = component.keyType();
    if(jObject.has("extra")) {
      List<IComponent> children = parseArray(jObject.get("extra").getAsJsonArray());
      if(type.equals("text")) {
        List<CColor> gradientColors = new ArrayList<>();
        HashMap<Integer, List<IComponent>> insertChildren = new HashMap<>();
        for(int i = 0; i < children.size(); i++) {
          IComponent child = children.get(i);
          if(child.keyValue().isEmpty() && ((child instanceof DynamicGradientComponent||child.hasData(ExcludeCheck.TEXT)||child.isGradient())&&!child.children().isEmpty())) {
            if(child.isGradient() || child instanceof DynamicGradientComponent) {
              for(IComponent iComponent : child.children()) if(iComponent.color().isHex()) iComponent.gradient(true);
              if(child instanceof DynamicGradientComponent)
                gradientColors.addAll(Arrays.asList(((DynamicGradientComponent)child).colors()));
            }
            if(!child.styles().isEmpty()||child.hasData(ExcludeCheck.COLOR)) child.children().forEach(child::applyData);
            insertChildren.put(i, child.children());
          }
        }
        int last = 0;
        for(Map.Entry<Integer, List<IComponent>> entry : insertChildren.entrySet()) {
          int pos = entry.getKey();
          children.remove(pos);
          children.addAll(pos+last < children.size() ? pos+last : children.size()-1, entry.getValue());
          last = children.size()-pos;
        }
        CColor lastColor = CColor.NONE;
        CColor firstHex = CColor.NONE, newFirstHex;
        List<CColor> totalColors = new ArrayList<>();
        List<IComponent> gradients = new ArrayList<>();
        for(IComponent child : children) {
          if(child.color()==CColor.NONE&&!child.isGradient())continue;
          if(!totalColors.contains(CColor.fromHex(child.color().hexString()))) totalColors.add(child.color());
          double similarity = lastColor == CColor.NONE ? 100 : lastColor.similarity(child.color());
          if(child.isGradient()||similarity >= CColor.colorMatchStrength() + child.color().brightnessModifier()) gradients.add(child);
          if(firstHex == CColor.NONE) firstHex = child.color();
          lastColor = child.color();
        }
        int gradientCount = gradients.size();
        if(gradientCount >= (children.size() / 3)) {
          int totalGradientLength = gradients.stream().mapToInt(IComponent::length).sum();
          newFirstHex = firstHex;
          List<CColor> gradientColor = new ArrayList<>();
          if(firstHex!=CColor.NONE) gradientColor.add(firstHex);
          for(IComponent child : gradients) {
            child.gradient(true);
            double colorStrength = CColor.colorMatchStrength() + child.color().brightnessModifier();
            if(child.color().hue() > 0 && (child.color().saturation() > 10 || child.color().saturation() < 5) && child.color().brightness() > 50)
              if(child.color().similarity(newFirstHex) <= (colorStrength - totalGradientLength)) {
                gradientColor.add(child.color());
                newFirstHex = child.color();
              }
          }
          IComponent parent = null;
          int parentT = 0;
          List<IComponent> ignore = new ArrayList<>();
          for(IComponent child : children) {
            String styles = child.styles().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).map(s->s.getAsColor()+"")
              .collect(Collectors.joining()).replace(("§"),("&"));
            if(child.isGradient() || child instanceof DynamicGradientComponent) {
              if(parentT == 1) parent = null;
              parentT = 0;
            }else {
              if(parentT == 0) parent = null;
              parentT = 1;
            }
            if(parent == null) {
              parent = child;
              parent.keyValue(styles + parent.keyValue());
              if(parentT == 0)parent.color(CColor.NONE);
            } else if(parent.isSimilar(child,ExcludeCheck.COLOR,ExcludeCheck.STYLES)){
              parent.keyValue(parent.keyValue() +styles+ child.keyValue());
              ignore.add(child);
            }
          }
          if(!gradientColor.contains(lastColor) && lastColor != CColor.NONE)
            gradientColor.add(lastColor);
          if(lastColor == CColor.NONE||firstHex==CColor.NONE||!new HashSet<>(gradientColor).containsAll(gradientColors))gradientColor.addAll(gradientColors
            .subList(gradientColors.size()>0&&!gradientColor.isEmpty() ?
                gradientColor.get(gradientColor.size()-1).similarity(gradientColors.get(0)) >= CColor.colorMatchStrength() ? 1 : 0 : 0,
              gradientColors.size()));
          children.removeAll(ignore);
          component = IComponent.gradientComponent(gradientColor.toArray(new CColor[0]));
          if(children.size() > 1) children.forEach(component::add);
          else {
            component.keyValue(children.get(0).styles().entrySet().stream()
              .filter(Map.Entry::getValue).map(s -> s.getKey().getAsColor().toString()).collect(Collectors.joining()).replace(("§"), ("&")) + children.get(0).keyValue());
          }
        } else children.forEach(component.children()::add);
      } else children.forEach(component.children()::add);
    }
    if(jObject.has("with")) parseArray(jObject.get("with").getAsJsonArray()).forEach(component.with()::add);
    if(jObject.has("color")) component.color(CColor.fromTranslated(jObject.get("color").getAsString()));
    for(DynamicStyle value : DynamicStyle.values()) {
      if(jObject.has(value.getName()))
        component.styles().put(value,jObject.get(value.getName()).getAsBoolean());
    }
    if(jObject.has("hoverEvent")) {
      JsonObject he = jObject.get("hoverEvent").getAsJsonObject();
      if(he.has("action")) {
        JsonElement get = he.has("value") ? he.get("value") : he.has("contents") ? he.get("contents") : null;
        StringBuilder val = new StringBuilder();
        if(get instanceof JsonArray) {
          for(JsonElement jsonElement : get.getAsJsonArray()) {
            IComponent jp = parseJsonObject(jsonElement.getAsJsonObject());
            if(jp != null && jp.keyType().equals("text"))
              val.append(jp.color()).append(jp.styles().keySet().stream().filter(jp.styles()::get)
                .map(s -> s.getAsColor().toString()).collect(Collectors.joining())).append(jp.keyValue()).append("\n");
          }
        } else if(get instanceof JsonObject) {
          IComponent jp = parseJsonObject(get.getAsJsonObject());
          if(jp != null && jp.keyType().equals("text")) val.append(jp.color()).append(jp.styles().keySet().stream().filter(jp.styles()::get)
            .map(s -> s.getAsColor().toString()).collect(Collectors.joining())).append(jp.keyValue()).append("\n");
        } else if(get != null) val = new StringBuilder(get.getAsString());
        component.hover(DynamicHoverAction.valueOf(he.get("action").getAsString().toUpperCase()), val.toString().trim());
      }
    }
    if(jObject.has("clickEvent")) {
      JsonObject ce = jObject.get("clickEvent").getAsJsonObject();
      if(ce.has("action") && ce.has("value")) {
        DynamicClickAction action = DynamicClickAction.valueOf(ce.get("action").getAsString().toUpperCase());
        if(action == DynamicClickAction.RUN_COMMAND && !ce.get("value").getAsString().startsWith("/")) action = DynamicClickAction.CHAT;
        component.click(action, ce.get("value").getAsString());
      }
    }
    if(jObject.has("insertion")) component.insertion(jObject.get("insertion").getAsString());
    if(component instanceof DynamicGradientComponent) {
      component.complete();
    }
    return component;
  }


  public static DynamicJText parseText(String text) {
    DynamicJText jText = new DynamicJText();
    parseText(jText, text, null, DynamicClickAction.NONE, null, null, null);
    return jText;
  }

  private static void parseText(DynamicJText jText, String text, String hover, DynamicClickAction action, String clickData, String insert, String gradient) {
    Matcher matcher = PARSE_PATTERN.matcher(text);
    boolean foundAny = matcher.find();
    String build = "";

    if(gradient != null) {
      String[] split = gradient.split(",");
      CColor.GradientCenter center = CColor.GradientCenter.fromName(split[0]);
      CColor[] colors = new CColor[split.length - (center == null ? 0 : 1)];
      for(int i1 = 0; i1 < colors.length; i1++)
        colors[i1] = CColor.fromTranslated(split[i1 + (center == null ? 0 : 1)]);
      if(center == null) center = CColor.GradientCenter.MIDDLE;
      List<CColor> colors1 = new ArrayList<>(Arrays.asList(colors));
      if(jText.edit() instanceof DynamicGradientComponent)
        colors1.removeAll(Arrays.asList(((DynamicGradientComponent) jText.edit()).colors()));
      if(!colors1.isEmpty()) jText.add(IComponent.gradientComponent(colors).center(center));
    }

    char[] chars = text.toCharArray();
    for(int i = 0; i < chars.length; i++) {
      char current = chars[i];
      if(foundAny) {
        if(i == matcher.start()) {
          String id = matcher.group("id");
          String data = matcher.group("data").replace("\\\"", "\"");
          String nText = matcher.group("text");
          if(!PARSE_IDS.contains(id.toLowerCase())) {
            foundAny = matcher.find();
            i--;
            continue;
          }
          if(!build.isEmpty())
            if(matcher.start() != 0 || matcher.end() != chars.length) {
              if(!PARSE_PATTERN.matcher(build).find()) {
                (gradient != null ? jText.edit() : jText).add(build);
                append(jText, hover, action, clickData, insert);
                build = "";
              }
            }
          boolean newFound = PARSE_PATTERN.matcher(nText).find();
          if(!newFound) {
            if((id.equalsIgnoreCase("gradient") && !(gradient = data).isEmpty())) {
              String[] split = gradient.split(",");
              CColor.GradientCenter center = CColor.GradientCenter.fromName(split[0]);
              CColor[] colors = new CColor[split.length - (center == null ? 0 : 1)];
              for(int i1 = 0; i1 < colors.length; i1++) colors[i1] = CColor.fromTranslated(split[i1 + (center == null ? 0 : 1)]);
              if(center == null) center = CColor.GradientCenter.MIDDLE;
              jText.add(IComponent.gradientComponent(colors).center(center).add(nText));
            } else (gradient != null ? jText.edit() : jText).add(nText);
          }
          if(newFound) {
            switch(id.toLowerCase()) {
              case "hover":
                parseText(jText, nText, hover = data, action, clickData, insert, gradient);
                break;
              case "insert":
                parseText(jText, nText, hover, action, clickData, insert = data, gradient);
                break;
              case "chat":
                parseText(jText, nText, hover, DynamicClickAction.CHAT, clickData = data, insert, gradient);
                break;
              case "gradient":
                parseText(jText, nText, hover, action, clickData, insert, data);
                break;
              default:
                DynamicClickAction cAction = DynamicClickAction.fromName(id);
                if(cAction != null)
                  parseText(jText, nText, hover, cAction, cAction == DynamicClickAction.RUN_COMMAND && !data.startsWith("/") ? "/" + data : data, insert, gradient);
                break;
            }
            append(jText, hover, action, clickData, insert);
          } else {
            append(jText, hover, action, clickData, insert);
            switch(id.toLowerCase()) {
              case "hover":
                jText.hover(data);
                break;
              case "insert":
                jText.insertion(data);
                break;
              case "chat":
                jText.chat(data);
                break;
              default:
                DynamicClickAction cAction = DynamicClickAction.fromName(id);
                if(cAction != null) {
                  data = cAction == DynamicClickAction.RUN_COMMAND && !data.startsWith("/") ? "/" + data : data;
                  jText.click(cAction, data);
                }
                break;
            }
          }
          i = matcher.end() - 1;
          foundAny = matcher.find();
          continue;
        }
      }
      build += current;
    }
    if(!build.isEmpty())
      if(!foundAny || matcher.end() != chars.length) {
        if(!PARSE_PATTERN.matcher(build).find()) {
          (gradient != null ? jText.edit() : jText).add(build);
          append(jText, hover, action, clickData, insert);
        }
      }
  }

  private static void append(DynamicJText jText, String hover, DynamicClickAction action, String clickData, String insert) {
    if(hover != null) jText.hover(hover);
    if(action != DynamicClickAction.NONE) jText.click(action, clickData);
    if(insert != null) jText.insertion(insert);
  }


  private static JsonElement parseJsonString(String json) throws JsonSyntaxException {
    return parseJsonReader(new StringReader(json));
  }

  private static JsonElement parseJsonReader(Reader json) throws JsonIOException, JsonSyntaxException {
    try {
      JsonReader jsonReader = new JsonReader(json);
      JsonElement element = parseJsonReader(jsonReader);
      if(!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
        throw new JsonSyntaxException("Did not consume the entire document.");
      } else {
        return element;
      }
    } catch (MalformedJsonException | NumberFormatException var4) {
      throw new JsonSyntaxException(var4);
    } catch (IOException var5) {
      throw new JsonIOException(var5);
    }
  }

  private static JsonElement parseJsonReader(JsonReader json) throws JsonIOException, JsonSyntaxException {
    boolean lenient = json.isLenient();
    json.setLenient(true);

    JsonElement var3;
    try {
      var3 = Streams.parse(json);
    } catch (StackOverflowError | OutOfMemoryError var8) {
      throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", var8);
    } finally {
      json.setLenient(lenient);
    }

    return var3;
  }

  public void send(Player sender, String json) {
    for(Map.Entry<String, Function<CommandSender, String>> replacement : REPLACEMENTS.entrySet()) {
      Matcher matcher = REPLACEMENT_SEARCH.matcher(json);
      while(matcher.find()) {
        String text = matcher.group(1);
        int start = matcher.start(1);
        int end = matcher.end(1);
        if(text.contains(replacement.getKey())) {
          json = json.substring(0, start) + text.replace(replacement.getKey(), replacement.getValue().apply(sender)) + json.substring(end);
          matcher = REPLACEMENT_SEARCH.matcher(json);
        }
      }
    }

    Matcher selector = SELECTOR_SEARCH.matcher(json);
    while(selector.find()) {
      String select = selector.group("data");
      String group = selector.group();
      if(select == null) continue;
      List<Entity> entities = SelectEntities.parseSelector(sender, select);
      if(entities == null) continue;
      StringBuilder newJson = new StringBuilder();
      JsonBuilder jb = new JsonBuilder(new StringBuilder());
      boolean hasPlayer = entities.stream().anyMatch((e) -> e instanceof Player);
      if(hasPlayer && entities.size() > 1) {
        jb.beginObject().name("text").value("").name("extra").beginArray();
      }
      for(Entity entity : entities) {
        if(entity instanceof Player) {
          if(entities.size() > 1 && newJson.length() > 0) jb.beginObject().name("text").value(newJson).end().beginObject().name("text");
          else jb.beginObject().name("text");
          newJson = new StringBuilder();
          jb.value(entity.getName()).name("hoverEvent")
            .beginObject().name("action").value("show_entity")
            .name("value").jsonValue("{\"type\":\"" + entity.getType().getName().toLowerCase() + "\",\"id\":\"" + entity.getUniqueId() +
              "\"," +
              "\"name\":\"" + entity.getName() + "\"}").end()
            .name("clickEvent").beginObject()
            .name("action").value("suggest_command").name("value").value("/msg " + entity.getName()).end().end();
          newJson.append(", ");
          continue;
        }
        newJson.append(entity.getCustomName() != null ?
          entity.getCustomName() + "[" + entity.getType().getName() + "]" :
          entity.getName()).append(", ");
      }
      if(newJson.length() > 2) jb.beginObject().name("text").value(newJson.substring(0, newJson.length() - 2)).end();
      while(jb.topObject() != null) jb.end();
      if(jb.toString().length()==0)jb.beginObject().name("text").value("").end();
      String js = jb.toString().substring(1, jb.toString().length() - 1);
      json = json.replace(group, js);
      selector = SELECTOR_SEARCH.matcher(json);
    }
    Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
    Matcher scoreRegex = SCORE_SEARCH.matcher(json);
    while(scoreRegex.find()) {
      String scoreStr = scoreRegex.group("name");
      String objectiveStr = scoreRegex.group("obj");
      String group = scoreRegex.group();
      Objective objective = sb.getObjective(objectiveStr);
      if(objective == null) continue;
      String js = "\"text\":\"" + objective.getScore(scoreStr).getScore() + "\"";
      json = json.replace(group, js);
      scoreRegex = SCORE_SEARCH.matcher(json);
    }
    if(Version.isCurrentHigher(Version.v1_15)) json = json.replaceAll("(\"hoverEvent\":\\{\"action\":\"[^\"]+\",)\"value\":", "$1\"contents\":");
    JsonSender.sendJson(json, sender);
  }

  private void send(ConsoleCommandSender sender, String json) {
    for(Map.Entry<String, Function<CommandSender, String>> replacement : REPLACEMENTS.entrySet())
      if(json.contains(replacement.getKey())) json = json.replace(replacement.getKey(), replacement.getValue().apply(sender));
    sender.sendMessage(json);
  }

  public void send(CommandSender... senders) {
    String json = toString();
    String plainText = plainText();
    for(CommandSender sender : senders) {
      if(sender instanceof ConsoleCommandSender) send((ConsoleCommandSender) sender, plainText);
      else if(sender instanceof Player) send((Player) sender, json);
    }
  }

}
