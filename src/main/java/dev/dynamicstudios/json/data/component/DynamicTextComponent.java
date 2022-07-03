package dev.dynamicstudios.json.data.component;

import dev.dynamicstudios.json.data.util.CColor;
import dev.dynamicstudios.json.data.util.DynamicClickAction;
import dev.dynamicstudios.json.data.util.DynamicHoverAction;
import dev.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creator: PerryPlaysMC
 * Created: 03/2022
 **/

public class DynamicTextComponent extends DynamicComponent {
  private static final String generatorRegex = "(?:((?:§[mnolkr])+)?(#[a-fA-F\\d]{6}|§[\\dabcdef])*((?:§[mnolkr])+)?)?((?:" +
    "(?!#[a-fA-F\\d]{6}|§[\\dabcdefmnolkr]).)*)";
  private static final Pattern GENERATOR_PATTERN = Pattern.compile(generatorRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private String text = "";

  protected DynamicTextComponent(String text) {
    if(text != null) keyValue(text);
  }

  protected DynamicTextComponent() {}

  public DynamicTextComponent addDefault(String... children) {
    return add(IComponent.textComponent("§f" + String.join("\n", children)));
  }

  public DynamicTextComponent add(String... children) {
    return add(IComponent.textComponent(resetText(CColor.translateCommon(String.join("\n", children)))));
  }

  @Override
  public DynamicTextComponent addReset(String... children) {
    return (DynamicTextComponent) super.addReset(children);
  }

  public DynamicTextComponent text(String... text) {
    return keyValue(String.join("\n", text));
  }

  @Override
  public String keyType() {
    return "text";
  }

  @Override
  public String keyValue() {
    return (text == null ? text = "" : text);
  }

  @Override
  public DynamicTextComponent keyValue(String text) {
    this.text = text == null ? "" : text;
    return dirtify();
  }

  private static boolean isEmpty(String text) {
    return text == null || text.isEmpty();
  }

  private static void generateStyles(String text, Set<DynamicStyle> styles, Set<DynamicStyle> disableForNext) {
    for(String s : text.toLowerCase().split("§")) {
      if(s.isEmpty()) continue;
      if(s.equals("r")) {
        disableForNext.addAll(styles);
        styles.clear();
        continue;
      }
      DynamicStyle style = DynamicStyle.byChar(s.charAt(0));
      if(style != null) {
        styles.add(style);
        disableForNext.remove(style);
      }
    }
  }

  public static DynamicTextComponent of(String generateFrom) {
    Matcher results = GENERATOR_PATTERN.matcher(CColor.resetHex(generateFrom));
    DynamicTextComponent part = new DynamicTextComponent();
    Set<DynamicStyle> styles = new HashSet<>(), disableForNext = new HashSet<>();
    CColor cColor = CColor.RESET;
    CColor last;
    while(results.find()) {
      last = cColor;
      String text = results.group(4) == null ? "" : results.group(4);
      if(!isEmpty(results.group(1)) || !isEmpty(results.group(2)) || !isEmpty(results.group(3))) {
        String color = (isEmpty(results.group(2)) ? "" : results.group(2));
        String s1 = isEmpty(results.group(1)) ? "" : results.group(1);
        String s2 = isEmpty(results.group(3)) ? "" : results.group(3);
        if(s2.endsWith(CColor.RESET.toString())) {
          cColor = CColor.WHITE;
          disableForNext.addAll(styles);
          styles.clear();
        } else try {
          generateStyles(s1, styles, disableForNext);
          if(!color.isEmpty()) if(color.endsWith(CColor.RESET.toString())) {
            disableForNext.addAll(styles);
            styles.clear();
          } else cColor = CColor.fromHex(color);
          generateStyles(s2, styles, disableForNext);
        } catch (IllegalArgumentException ignored) {
        }
      }
      if(cColor == last && text.equals("")) continue;
      DynamicTextComponent p = new DynamicTextComponent(text);
      p.color(cColor).disableStyles(disableForNext).enableStyles(styles);
      part.add(p);
    }
    part.complete();
    if(part.children().size() == 1) part = (DynamicTextComponent) part.children().get(0);
    if(part.length() == 0) part = new DynamicTextComponent(generateFrom);
    part.complete();
    return part;
  }

  @Override
  public DynamicTextComponent gradient(boolean toggle) {
    return (DynamicTextComponent) super.gradient(toggle);
  }

  @Override
  public DynamicTextComponent color(CColor color) {
    return (DynamicTextComponent) super.color(color);
  }

  @Override
  public DynamicTextComponent with(IComponent... text) {
    return (DynamicTextComponent) super.with(text);
  }

  @Override
  public DynamicTextComponent add(IComponent child) {
    return (DynamicTextComponent) super.add(child);
  }

  @Override
  public DynamicTextComponent dirtify() {
    return (DynamicTextComponent) super.dirtify();
  }

  @Override
  public DynamicTextComponent hover(DynamicHoverAction hoverAction, String hover) {
    return (DynamicTextComponent) super.hover(hoverAction, hover);
  }

  @Override
  public DynamicTextComponent command(String command) {
    return (DynamicTextComponent) super.command(command);
  }

  @Override
  public DynamicTextComponent chat(String text) {
    return (DynamicTextComponent) super.chat(text);
  }

  @Override
  public DynamicTextComponent suggest(String text) {
    return (DynamicTextComponent) super.suggest(text);
  }

  @Override
  public DynamicTextComponent url(String url) {
    return (DynamicTextComponent) super.url(url);
  }

  @Override
  public DynamicTextComponent copy(String clipboard) {
    return (DynamicTextComponent) super.copy(clipboard);
  }

  @Override
  public DynamicTextComponent changePage(String page) {
    return (DynamicTextComponent) super.changePage(page);
  }

  @Override
  public DynamicTextComponent hover(String... text) {
    return (DynamicTextComponent) super.hover(text);
  }

  @Override
  public DynamicTextComponent hoverPlain(String... text) {
    return (DynamicTextComponent) super.hoverPlain(text);
  }

  @Override
  public DynamicTextComponent hover(ItemStack item) {
    return (DynamicTextComponent) super.hover(item);
  }

  @Override
  public DynamicTextComponent hover(Entity entity) {
    return (DynamicTextComponent) super.hover(entity);
  }

  @Override
  public DynamicTextComponent tooltip(String... text) {
    return (DynamicTextComponent) super.tooltip(text);
  }

  @Override
  public DynamicTextComponent tooltipPlain(String... text) {
    return (DynamicTextComponent) super.tooltipPlain(text);
  }

  @Override
  public DynamicTextComponent tooltip(ItemStack item) {
    return (DynamicTextComponent) super.tooltip(item);
  }

  @Override
  public DynamicTextComponent tooltip(Entity entity) {
    return (DynamicTextComponent) super.tooltip(entity);
  }

  @Override
  public DynamicTextComponent click(DynamicClickAction clickAction, String click) {
    return (DynamicTextComponent) super.click(clickAction, click);
  }

  @Override
  public DynamicTextComponent insertion(String insertion) {
    return (DynamicTextComponent) super.insertion(insertion);
  }

  @Override
  public DynamicTextComponent enableStyles(DynamicStyle... styles) {
    return (DynamicTextComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicTextComponent disableStyles(DynamicStyle... styles) {
    return (DynamicTextComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicTextComponent enableStyles(Collection<DynamicStyle> styles) {
    return (DynamicTextComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicTextComponent disableStyles(Collection<DynamicStyle> styles) {
    return (DynamicTextComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicTextComponent font(String font) {
    return (DynamicTextComponent) super.font(font);
  }

  @Override
  public DynamicTextComponent clean() {
    return (DynamicTextComponent) super.clean();
  }

}