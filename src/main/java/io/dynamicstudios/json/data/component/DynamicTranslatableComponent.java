package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.data.util.*;
import io.dynamicstudios.json.data.util.DynamicClickAction;
import io.dynamicstudios.json.data.util.DynamicHoverAction;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Creator: PerryPlaysMC
 * Created: 03/2022
 **/
public class DynamicTranslatableComponent extends DynamicComponent {


  public static DynamicTranslatableComponent of(Translation translation) {
    return new DynamicTranslatableComponent(translation);
  }


  private Translation translation = null;

  private DynamicTranslatableComponent(Translation translation) {
    translation(translation);
  }

  public Translation translation() {
    return translation;
  }

  public DynamicTranslatableComponent translation(Translation translation) {
    this.translation = translation;
    return dirtify();
  }

  @Override
  public String keyType() {
    return "translate";
  }

  @Override
  public String keyValue() {
    return translation + "";
  }

  @Override
  public DynamicTranslatableComponent keyValue(String text) {
    translation(Translation.byId(text));
    return dirtify();
  }

  @Override
  public DynamicTranslatableComponent addDefault(String... children) {
    return add(IComponent.textComponent(resetText(String.join("\n", children))));
  }

  @Override
  public DynamicTranslatableComponent addReset(String... children) {
    return (DynamicTranslatableComponent) super.addReset(children);
  }

  @Override
  public DynamicTranslatableComponent add(String... children) {
    return add(IComponent.textComponent(resetText(String.join("\n", children))));
  }

  @Override
  public DynamicTranslatableComponent gradient(boolean toggle) {
    return (DynamicTranslatableComponent) super.gradient(toggle);
  }

  @Override
  public DynamicTranslatableComponent color(CColor color) {
    return (DynamicTranslatableComponent) super.color(color);
  }

  @Override
  public DynamicTranslatableComponent with(IComponent... text) {
    return (DynamicTranslatableComponent) super.with(text);
  }

  @Override
  public DynamicTranslatableComponent add(IComponent child) {
    return (DynamicTranslatableComponent) super.add(child);
  }

  @Override
  public DynamicTranslatableComponent dirtify() {
    return (DynamicTranslatableComponent) super.dirtify();
  }

  @Override
  public DynamicTranslatableComponent hover(DynamicHoverAction hoverAction, String hover) {
    return (DynamicTranslatableComponent) super.hover(hoverAction, hover);
  }

  @Override
  public DynamicTranslatableComponent command(String command) {
    return (DynamicTranslatableComponent) super.command(command);
  }

  @Override
  public DynamicTranslatableComponent chat(String text) {
    return (DynamicTranslatableComponent) super.chat(text);
  }

  @Override
  public DynamicTranslatableComponent suggest(String text) {
    return (DynamicTranslatableComponent) super.suggest(text);
  }

  @Override
  public DynamicTranslatableComponent url(String url) {
    return (DynamicTranslatableComponent) super.url(url);
  }

  @Override
  public DynamicTranslatableComponent copy(String clipboard) {
    return (DynamicTranslatableComponent) super.copy(clipboard);
  }

  @Override
  public DynamicTranslatableComponent changePage(String page) {
    return (DynamicTranslatableComponent) super.changePage(page);
  }

  @Override
  public DynamicTranslatableComponent hover(String... text) {
    return (DynamicTranslatableComponent) super.hover(text);
  }

  @Override
  public DynamicTranslatableComponent hoverPlain(String... text) {
    return (DynamicTranslatableComponent) super.hoverPlain(text);
  }

  @Override
  public DynamicTranslatableComponent hover(ItemStack item) {
    return (DynamicTranslatableComponent) super.hover(item);
  }

  @Override
  public DynamicTranslatableComponent hover(Entity entity) {
    return (DynamicTranslatableComponent) super.hover(entity);
  }

  @Override
  public DynamicTranslatableComponent tooltip(String... text) {
    return (DynamicTranslatableComponent) super.tooltip(text);
  }

  @Override
  public DynamicTranslatableComponent tooltipPlain(String... text) {
    return (DynamicTranslatableComponent) super.tooltipPlain(text);
  }

  @Override
  public DynamicTranslatableComponent tooltip(ItemStack item) {
    return (DynamicTranslatableComponent) super.tooltip(item);
  }

  @Override
  public DynamicTranslatableComponent tooltip(Entity entity) {
    return (DynamicTranslatableComponent) super.tooltip(entity);
  }

  @Override
  public DynamicTranslatableComponent click(DynamicClickAction clickAction, String click) {
    return (DynamicTranslatableComponent) super.click(clickAction, click);
  }

  @Override
  public DynamicTranslatableComponent insertion(String insertion) {
    return (DynamicTranslatableComponent) super.insertion(insertion);
  }

  @Override
  public DynamicTranslatableComponent enableStyles(DynamicStyle... styles) {
    return (DynamicTranslatableComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicTranslatableComponent disableStyles(DynamicStyle... styles) {
    return (DynamicTranslatableComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicTranslatableComponent enableStyles(Collection<DynamicStyle> styles) {
    return (DynamicTranslatableComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicTranslatableComponent disableStyles(Collection<DynamicStyle> styles) {
    return (DynamicTranslatableComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicTranslatableComponent font(String font) {
    return (DynamicTranslatableComponent) super.font(font);
  }

  @Override
  public DynamicTranslatableComponent clean() {
    return (DynamicTranslatableComponent) super.clean();
  }
}
