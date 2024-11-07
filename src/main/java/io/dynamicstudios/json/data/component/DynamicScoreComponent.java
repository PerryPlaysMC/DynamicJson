package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.DynamicClickAction;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public class DynamicScoreComponent extends DynamicComponent {


  public static DynamicScoreComponent of(String name, String objective) {
    return new DynamicScoreComponent(name, objective);
  }

  private final String asJson = "{\"name\":\"%s\",\"objective\":\"%s\"";
  private String score = "", objective = "";
  private String result = "";

  private DynamicScoreComponent(String name, String objective) {
    score(name, objective);
  }

  public DynamicScoreComponent score(String score, String objective) {
    this.score = score;
    this.objective = objective;
    return keyValue(String.format(asJson, score, objective));
  }

  public String score() {
    return score;
  }

  public String objective() {
    return objective;
  }

  @Override
  public DynamicScoreComponent addDefault(String... children) {
    return add(IComponent.textComponent(resetText(String.join("\n", children))));
  }

  @Override
  public DynamicScoreComponent addReset(String... children) {
    return (DynamicScoreComponent) super.addReset(children);
  }

  @Override
  public DynamicScoreComponent add(String... children) {
    return add(IComponent.textComponent(resetText(String.join("\n", children))));
  }

  @Override
  public boolean useJsonValue() {
    return true;
  }

  @Override
  public String keyType() {
    return "score";
  }

  @Override
  public String keyValue() {
    return result;
  }

  @Override
  public DynamicScoreComponent keyValue(String text) {
    this.result = text;
    return dirtify();
  }


  @Override
  public DynamicScoreComponent gradient(boolean toggle) {
    return (DynamicScoreComponent) super.gradient(toggle);
  }

  @Override
  public DynamicScoreComponent color(CColor color) {
    return (DynamicScoreComponent) super.color(color);
  }

  @Override
  public DynamicScoreComponent with(IComponent... text) {
    return (DynamicScoreComponent) super.with(text);
  }

  @Override
  public DynamicScoreComponent add(IComponent child) {
    return (DynamicScoreComponent) super.add(child);
  }

  @Override
  public DynamicScoreComponent dirtify() {
    return (DynamicScoreComponent) super.dirtify();
  }

  @Override
  public DynamicScoreComponent command(String command) {
    return (DynamicScoreComponent) super.command(command);
  }

  @Override
  public DynamicScoreComponent chat(String text) {
    return (DynamicScoreComponent) super.chat(text);
  }

  @Override
  public DynamicScoreComponent suggest(String text) {
    return (DynamicScoreComponent) super.suggest(text);
  }

  @Override
  public DynamicScoreComponent url(String url) {
    return (DynamicScoreComponent) super.url(url);
  }

  @Override
  public DynamicScoreComponent copy(String clipboard) {
    return (DynamicScoreComponent) super.copy(clipboard);
  }

  @Override
  public DynamicScoreComponent changePage(String page) {
    return (DynamicScoreComponent) super.changePage(page);
  }

  @Override
  public DynamicScoreComponent hover(String... text) {
    return (DynamicScoreComponent) super.hover(text);
  }

  @Override
  public DynamicScoreComponent hoverPlain(String... text) {
    return (DynamicScoreComponent) super.hoverPlain(text);
  }

  @Override
  public DynamicScoreComponent hover(ItemStack item) {
    return (DynamicScoreComponent) super.hover(item);
  }

  @Override
  public DynamicScoreComponent hover(Entity entity) {
    return (DynamicScoreComponent) super.hover(entity);
  }

  @Override
  public DynamicScoreComponent tooltip(String... text) {
    return (DynamicScoreComponent) super.tooltip(text);
  }

  @Override
  public DynamicScoreComponent tooltipPlain(String... text) {
    return (DynamicScoreComponent) super.tooltipPlain(text);
  }

  @Override
  public DynamicScoreComponent tooltip(ItemStack item) {
    return (DynamicScoreComponent) super.tooltip(item);
  }

  @Override
  public DynamicScoreComponent tooltip(Entity entity) {
    return (DynamicScoreComponent) super.tooltip(entity);
  }

  @Override
  public DynamicScoreComponent click(DynamicClickAction clickAction, String click) {
    return (DynamicScoreComponent) super.click(clickAction, click);
  }

  @Override
  public DynamicScoreComponent insertion(String insertion) {
    return (DynamicScoreComponent) super.insertion(insertion);
  }

  @Override
  public DynamicScoreComponent enableStyles(DynamicStyle... styles) {
    return (DynamicScoreComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicScoreComponent disableStyles(DynamicStyle... styles) {
    return (DynamicScoreComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicScoreComponent enableStyles(Collection<DynamicStyle> styles) {
    return (DynamicScoreComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicScoreComponent disableStyles(Collection<DynamicStyle> styles) {
    return (DynamicScoreComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicScoreComponent font(String font) {
    return (DynamicScoreComponent) super.font(font);
  }

  @Override
  public DynamicScoreComponent clean() {
    return (DynamicScoreComponent) super.clean();
  }
  
}
