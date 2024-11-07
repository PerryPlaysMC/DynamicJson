package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.DynamicClickAction;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 03/2022
 **/
@SuppressWarnings("unused")
public class DynamicGradientComponent extends DynamicComponent implements IChildPriority {

  private CColor[] colors;
  private CColor.GradientCenter center = CColor.GradientCenter.MIDDLE;
  private int gradientSteps = -1;

  public static DynamicGradientComponent of(String text, CColor... colors) {
    return new DynamicGradientComponent(text, colors);
  }

  public static DynamicGradientComponent of(CColor... colors) {
    return new DynamicGradientComponent(colors);
  }

  private DynamicGradientComponent(String text, CColor... colors) {
    add(text).colors(colors);
  }

  private DynamicGradientComponent(CColor... colors) {
    colors(colors);
  }

  public DynamicGradientComponent() {
  }

  public int gradientSteps() {
    return gradientSteps;
  }

  public DynamicGradientComponent gradientSteps(int gradientSteps) {
    this.gradientSteps = gradientSteps;
    return dirtify();
  }

  public CColor[] colors() {
    return colors;
  }

  public DynamicGradientComponent colors(CColor... colors) {
    this.colors = colors;
    return dirtify();
  }

  public CColor.GradientCenter center() {
    return center;
  }

  public DynamicGradientComponent center(CColor.GradientCenter center) {
    this.center = center;
    return dirtify();
  }

  @Override
  public DynamicGradientComponent keyValue(String text) {
    if(children().isEmpty()) children().add(IComponent.textComponent(text).gradient(true));
    else children().set(0, IComponent.textComponent(text));
    return this;
  }

  @Override
  public String keyValue() {
    return "";
  }

  @Override
  public String keyType() {
    return "text";
  }

  @Override
  public DynamicGradientComponent addDefault(String... children) {
    return add(IComponent.textComponent(resetText(CColor.translateCommon(String.join("\n", children)))).gradient(false));
  }

  @Override
  public DynamicGradientComponent addReset(String... children) {
    return (DynamicGradientComponent) super.addReset(children);
  }

  @Override
  public DynamicGradientComponent add(IComponent child) {
    return (DynamicGradientComponent) super.add(child);
  }

  @Override
  public DynamicGradientComponent add(String... children) {
    return add(IComponent.textComponent(CColor.stripColor(String.join("\n", children).replaceAll(CColor.COLOR_CHAR+"([lmnkor])","&$1"))).gradient(true));
  }

  @Override
  public DynamicGradientComponent dirtify() {
    super.dirtify();
    return this;
  }


  @Override
  protected void compareChildren() {
    complete();
    List<IComponent> oldChildren = new ArrayList<>(children());
    List<IComponent> newChildren = new ArrayList<>();
    int steps = gradientSteps > -1 ? gradientSteps :
      oldChildren.stream().filter(IComponent::isGradient).mapToInt(IComponent::lengthIgnoreWhitespace).sum();
    int initialStep = 0;
    for(IComponent oldChild : oldChildren) {
      if(oldChild.canWriteJson()) {
        if(oldChild.isGradient()) {
          DynamicTextComponent newChild =
            IComponent.textComponent(CColor.translateGradient(CColor.translateStyles(oldChild.keyValue()), center, steps, initialStep, colors()));
          initialStep += oldChild.lengthIgnoreWhitespace();
          newChild.children().forEach(c->c.gradient(true));
          oldChild.applyEventsIfNotPresent(newChild);
          newChildren.add(newChild);
        } else newChildren.add(oldChild);
      }
    }
    children().clear();
    children().addAll(newChildren);
    super.compareChildren();
  }


  @Override
  public DynamicGradientComponent gradient(boolean toggle) {
    return this;
  }

  @Override
  public DynamicGradientComponent color(CColor color) {
    return (DynamicGradientComponent) super.color(color);
  }

  @Override
  public DynamicGradientComponent with(IComponent... text) {
    return (DynamicGradientComponent) super.with(text);
  }

  @Override
  public DynamicGradientComponent command(String command) {
    if(edit().isEmpty()) super.command(command);
    else edit().command(command);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent chat(String text) {
    if(edit().isEmpty()) super.chat(text);
    else edit().chat(text);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent suggest(String text) {
    if(edit().isEmpty()) super.suggest(text);
    else edit().suggest(text);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent url(String url) {
    if(edit().isEmpty()) super.url(url);
    else edit().url(url);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent copy(String clipboard) {
    if(edit().isEmpty()) super.copy(clipboard);
    else edit().copy(clipboard);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent changePage(String page) {
    if(edit().isEmpty()) super.changePage(page);
    else edit().changePage(page);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent hover(String... text) {
    if(edit().isEmpty()) super.hover(text);
    else edit().hover(text);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent hoverPlain(String... text) {
    if(edit().isEmpty()) super.hoverPlain(text);
    else edit().hoverPlain(text);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent hover(ItemStack item) {
    if(edit().isEmpty()) super.hover(item);
    else edit().hover(item);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent hover(Entity entity) {
    if(edit().isEmpty()) super.hover(entity);
    else edit().hover(entity);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent tooltip(String... text) {
    if(edit().isEmpty()) super.tooltip(text);
    else edit().tooltip(text);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent tooltipPlain(String... text) {
    if(edit().isEmpty()) super.tooltipPlain(text);
    else edit().tooltipPlain(text);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent tooltip(ItemStack item) {
    if(edit().isEmpty()) super.tooltip(item);
    else edit().tooltip(item);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent tooltip(Entity entity) {
    if(edit().isEmpty()) super.tooltip(entity);
    else edit().tooltip(entity);
    return dirtify();
  }


  public DynamicGradientComponent globalHover(String... text) {
    super.hover(text);
    return dirtify();
  }


  public DynamicGradientComponent globalHoverPlain(String... text) {
    super.hoverPlain(text);
    return dirtify();
  }


  public DynamicGradientComponent globalHover(ItemStack item) {
    super.hover(item);
    return dirtify();
  }


  public DynamicGradientComponent globalHover(Entity entity) {
    super.hover(entity);
    return dirtify();
  }


  public DynamicGradientComponent globalTooltip(String... text) {
    super.tooltip(text);
    return dirtify();
  }


  public DynamicGradientComponent globalTooltipPlain(String... text) {
    super.tooltipPlain(text);
    return dirtify();
  }


  public DynamicGradientComponent globalTooltip(ItemStack item) {
    super.tooltip(item);
    return dirtify();
  }


  public DynamicGradientComponent globalTooltip(Entity entity) {
    super.tooltip(entity);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent click(DynamicClickAction clickAction, String click) {
    if(edit().isEmpty()) super.click(clickAction, click);
    else edit().click(clickAction, click);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent insertion(String insertion) {
    if(edit().isEmpty()) super.insertion(insertion);
    else edit().insertion(insertion);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent enableStyles(DynamicStyle... styles) {
    if(edit().isEmpty()) super.enableStyles(styles);
    else edit().enableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent disableStyles(DynamicStyle... styles) {
    if(edit().isEmpty()) super.disableStyles(styles);
    else edit().disableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent enableStyles(Collection<DynamicStyle> styles) {
    if(edit().isEmpty()) super.enableStyles(styles);
    else edit().enableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent disableStyles(Collection<DynamicStyle> styles) {
    if(edit().isEmpty()) super.disableStyles(styles);
    else edit().disableStyles(styles);
    return dirtify();
  }

  @Override
  public DynamicGradientComponent clean() {
    return (DynamicGradientComponent) super.clean();
  }

  @Override
  public DynamicGradientComponent font(String font) {
    return (DynamicGradientComponent) super.font(font);
  }
}
