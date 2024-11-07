package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.JsonBuilder;
import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.DynamicClickAction;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public class DynamicNBTComponent extends DynamicComponent {

  private String path = null;
  private UUID entity;

  public static DynamicNBTComponent of(String path, UUID entity) {
    return new DynamicNBTComponent(path, entity);
  }

  public static DynamicNBTComponent of(String path, Entity entity) {
    return of(path, entity.getUniqueId());
  }


  private DynamicNBTComponent(String path, UUID entity) {
    this.entity = entity;
    path(path);
  }

  public String path() {
    return path;
  }

  public UUID entity() {
    return entity;
  }

  public DynamicNBTComponent path(String path) {
    return keyValue(path);
  }

  public DynamicNBTComponent entity(UUID entity) {
    this.entity = entity;
    return dirtify();
  }

  public DynamicNBTComponent entity(Entity entity) {
    return entity(entity.getUniqueId());
  }

  @Override
  public String keyType() {
    return "nbt";
  }

  @Override
  public String keyValue() {
    return path;
  }

  @Override
  public DynamicNBTComponent keyValue(String text) {
    this.path = text;
    return dirtify();
  }

  @Override
  public boolean canWriteJson() {
    return super.canWriteJson() && (path != null && !path.isEmpty()) && entity != null;
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && (path != null && !path.isEmpty()) && entity != null;
  }

  @Override
  public DynamicNBTComponent addDefault(String... children) {
    return add(IComponent.textComponent(resetText(String.join("\n", children))));
  }

  @Override
  public DynamicNBTComponent add(String... children) {
    return add(IComponent.textComponent(resetText(String.join("\n", children))));
  }

  @Override
  public DynamicNBTComponent addReset(String... children) {
    return (DynamicNBTComponent) super.addReset(children);
  }

  @Override
  protected void additionalJson(JsonBuilder builder) {
    builder.name("entity").value(entity + "").name("interpret").value(false);
  }

  @Override
  public DynamicNBTComponent gradient(boolean toggle) {
    return (DynamicNBTComponent) super.gradient(toggle);
  }

  @Override
  public DynamicNBTComponent color(CColor color) {
    return (DynamicNBTComponent) super.color(color);
  }

  @Override
  public DynamicNBTComponent with(IComponent... text) {
    return (DynamicNBTComponent) super.with(text);
  }

  @Override
  public DynamicNBTComponent add(IComponent child) {
    return (DynamicNBTComponent) super.add(child);
  }

  @Override
  public DynamicNBTComponent dirtify() {
    return (DynamicNBTComponent) super.dirtify();
  }

  @Override
  public DynamicNBTComponent command(String command) {
    return (DynamicNBTComponent) super.command(command);
  }

  @Override
  public DynamicNBTComponent chat(String text) {
    return (DynamicNBTComponent) super.chat(text);
  }

  @Override
  public DynamicNBTComponent suggest(String text) {
    return (DynamicNBTComponent) super.suggest(text);
  }

  @Override
  public DynamicNBTComponent url(String url) {
    return (DynamicNBTComponent) super.url(url);
  }

  @Override
  public DynamicNBTComponent copy(String clipboard) {
    return (DynamicNBTComponent) super.copy(clipboard);
  }

  @Override
  public DynamicNBTComponent changePage(String page) {
    return (DynamicNBTComponent) super.changePage(page);
  }

  @Override
  public DynamicNBTComponent hover(String... text) {
    return (DynamicNBTComponent) super.hover(text);
  }

  @Override
  public DynamicNBTComponent hoverPlain(String... text) {
    return (DynamicNBTComponent) super.hoverPlain(text);
  }

  @Override
  public DynamicNBTComponent hover(ItemStack item) {
    return (DynamicNBTComponent) super.hover(item);
  }

  @Override
  public DynamicNBTComponent hover(Entity entity) {
    return (DynamicNBTComponent) super.hover(entity);
  }

  @Override
  public DynamicNBTComponent tooltip(String... text) {
    return (DynamicNBTComponent) super.tooltip(text);
  }

  @Override
  public DynamicNBTComponent tooltipPlain(String... text) {
    return (DynamicNBTComponent) super.tooltipPlain(text);
  }

  @Override
  public DynamicNBTComponent tooltip(ItemStack item) {
    return (DynamicNBTComponent) super.tooltip(item);
  }

  @Override
  public DynamicNBTComponent tooltip(Entity entity) {
    return (DynamicNBTComponent) super.tooltip(entity);
  }

  @Override
  public DynamicNBTComponent click(DynamicClickAction clickAction, String click) {
    return (DynamicNBTComponent) super.click(clickAction, click);
  }

  @Override
  public DynamicNBTComponent insertion(String insertion) {
    return (DynamicNBTComponent) super.insertion(insertion);
  }

  @Override
  public DynamicNBTComponent enableStyles(DynamicStyle... styles) {
    return (DynamicNBTComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicNBTComponent disableStyles(DynamicStyle... styles) {
    return (DynamicNBTComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicNBTComponent enableStyles(Collection<DynamicStyle> styles) {
    return (DynamicNBTComponent) super.enableStyles(styles);
  }

  @Override
  public DynamicNBTComponent disableStyles(Collection<DynamicStyle> styles) {
    return (DynamicNBTComponent) super.disableStyles(styles);
  }

  @Override
  public DynamicNBTComponent font(String font) {
    return (DynamicNBTComponent) super.font(font);
  }

  @Override
  public DynamicNBTComponent clean() {
    return (DynamicNBTComponent) super.clean();
  }

  
}
