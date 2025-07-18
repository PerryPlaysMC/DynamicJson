package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.JsonBuilder;
import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.DynamicClickAction;
import io.dynamicstudios.json.data.util.DynamicHoverAction;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public abstract class DynamicComponent implements IComponent {
  public static final DynamicComponent EMPTY_COMPONENT = new DynamicComponent() {{
    dirtify();
  }

    @Override
    public DynamicComponent color(CColor color) {
      return this;
    }

    @Override
    public String keyType() {
      return null;
    }

    @Override
    public String keyValue() {
      return "";
    }

    @Override
    public IComponent keyValue(String text) {
      return dirtify();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public DynamicComponent addDefault(String... children) {
      return dirtify();
    }

    @Override
    public DynamicComponent add(String... children) {
      return dirtify();
    }
  };

  private final Map<DynamicStyle, Boolean> styles = new HashMap<>();
  private final Map<DynamicStyle, Boolean> nextStyles = new HashMap<>();
  private final List<IComponent> children = new ArrayList<>();
  private final List<IComponent> with = new ArrayList<>();
  protected IComponent edit = EMPTY_COMPONENT;
  private IComponent parent = EMPTY_COMPONENT;
  private boolean isClone = false;
  private CColor color = CColor.NONE, nextColor = CColor.NONE;
  private boolean dirty = false, gradient = false, reset = false;
  private String hover = "";
  private DynamicHoverAction hoverAction = DynamicHoverAction.NONE;
  private String click = "";
  private DynamicClickAction clickAction = DynamicClickAction.NONE;
  private String insertion = "", font = "";


  @Override
  public IComponent clone() {
    try {
      DynamicComponent component = (DynamicComponent) super.clone();
      component.parent = null;
      component.isClone = true;
      return component;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean isGradient() {
    return gradient;
  }

  public DynamicComponent gradient(boolean toggle) {
    this.gradient = toggle;
    return dirtify();
  }

  @Override
  public IComponent parent() {
    return parent;
  }

  public DynamicComponent color(CColor color) {
    this.color = color == null || color == CColor.RESET ? CColor.NONE : color;
    if(color == CColor.RESET || color == null) {
      styles.clear();
      reset = true;
    }else reset = false;
    children.stream().filter(c->c.color()==CColor.NONE).forEach(c -> c.color(color));
    return dirtify();
  }

  @Override
  public DynamicComponent colorIn(CColor color) {
    this.color = color == null || color == CColor.RESET ? CColor.NONE : color;
    if(color == CColor.RESET || color == null) {
      styles.clear();
      reset = true;
    }else reset = false;
    children.forEach(c -> c.color(color));
    return dirtify();
  }


  public CColor color() {
    return color;
  }

  protected void additionalJson(JsonBuilder builder) {
  }

  public boolean canWriteJson() {
    return !(equal(EMPTY_COMPONENT) && asString().equals(EMPTY_COMPONENT.asString())) || isEmpty();
  }

  @Override
  public boolean isEmpty() {
    return isNullOrEmpty(children()) && !hasData()
       && (edit == null || edit.equal(EMPTY_COMPONENT) || edit.isEmpty() ? 0 : edit.length()) + length() == 0;
  }

  @Override
  public boolean hasData(ExcludeCheck... excludes) {
    List<ExcludeCheck> exclude = Arrays.asList(excludes);
    List<Boolean> bool = new LinkedList<>();
    if(!exclude.contains(ExcludeCheck.TEXT)) bool.add(!isNullOrEmpty(keyValue()));
    if(!exclude.contains(ExcludeCheck.CLICK_EVENT)) bool.add(!isNullOrEmpty(click())&&!isNullOrEmpty(clickAction()));
    if(!exclude.contains(ExcludeCheck.HOVER_EVENT)) bool.add(!isNullOrEmpty(hover())&&!isNullOrEmpty(hoverAction()));
    if(!exclude.contains(ExcludeCheck.INSERTION))bool.add(!isNullOrEmpty(insertion()));
    if(!exclude.contains(ExcludeCheck.CHILDREN))bool.add(!isNullOrEmpty(with()));
    if(!exclude.contains(ExcludeCheck.CHILDREN))bool.add(!isNullOrEmpty(edit()) || !children().isEmpty());
    if(!exclude.contains(ExcludeCheck.STYLES))bool.add(!isNullOrEmpty(styles()));
    if(!exclude.contains(ExcludeCheck.COLOR))bool.add(!isNullOrEmpty(color()));
    return bool.stream().anyMatch(Boolean::booleanValue);
  }


  boolean isNullOrEmpty(Object o) {
    if(o == null) return true;
    if(o instanceof CColor) return o == CColor.NONE;
    if(o instanceof Map) return ((Map<?,?>) o).isEmpty();
    return (o instanceof Collection ? ((Collection<?>) o).isEmpty() : o.toString().isEmpty());
  }

  @Override
  public int length() {
    return keyValue().length() + children().stream().mapToInt(IComponent::length).sum();
  }
  @Override
  public int lengthIgnoreWhitespace() {
    return keyValue().replaceAll("\\s","").length() + children().stream().mapToInt(IComponent::lengthIgnoreWhitespace).sum();
  }

  @Override
  public List<IComponent> with() {
    return with;
  }

  @Override
  public DynamicComponent with(IComponent... text) {
    with.addAll(Arrays.asList(text));
    return this;
  }

  public IComponent edit() {
    return edit;
  }

  @Override
  public String font() {
    return font;
  }

  @Override
  public DynamicComponent font(String font) {
    this.font = font;
    return dirtify();
  }

  public DynamicComponent add(IComponent child) {
    complete();
    if(child instanceof DynamicComponent) ((DynamicComponent) child).parent = this;
    edit = child;
    return dirtify();
  }

  protected String resetText(String message) {
    if(nextColor != null && nextColor != CColor.NONE) {
      message = nextColor + message;
      nextColor = CColor.NONE;
    }
    if(!nextStyles.isEmpty()) {
      StringBuilder style = new StringBuilder();
      nextStyles.keySet().stream().filter(nextStyles::get).forEach(toNext -> style.append(toNext.getAsColor().toString()));
      message = style + message;
      nextStyles.clear();
    }
    return message;
  }

  @Override
  public DynamicComponent addReset(String... children) {
    if(!edit().isEmpty()) applyToNext(edit().styles()).applyToNext(edit().color());
    else if(lastChild() != null && !lastChild().isEmpty()) applyToNext(lastChild().styles()).applyToNext(lastChild().color());
    addDefault(CColor.translateCommon(String.join("\n", children)));
    return dirtify();
  }

  private DynamicComponent applyToNext(Map<DynamicStyle, Boolean> styles) {
    nextStyles.clear();
    nextStyles.putAll(styles);
    return dirtify();
  }

  private DynamicComponent applyToNext(CColor color) {
    this.nextColor = color == null ? CColor.NONE : color == CColor.NONE ? CColor.WHITE : CColor.NONE;
    return dirtify();
  }

  public List<IComponent> children() {
    return children;
  }

  @Override
  public IComponent childAt(int index) {
    if(children.isEmpty()) return null;
    return index > children.size() ? children.get(children.size() - 1) : index < 0 ? children.get(0) : children.get(index);
  }

  @Override
  public IComponent lastChild() {
    return childAt(children().size() - 1);
  }

  public DynamicComponent dirtify() {
    if(parent != null && !isClone && !parent.dirty()) parent.dirtify();
    dirty = true;
    return this;
  }

  public DynamicComponent clean() {
    dirty = false;
    return this;
  }

  public String hover() {
    return hover == null ? hover = "" : hover;
  }

  public DynamicHoverAction hoverAction() {
    return hoverAction == null ? hoverAction = DynamicHoverAction.NONE : hoverAction;
  }

  public String click() {
    return click == null ? click = "" : click;
  }

  public DynamicClickAction clickAction() {
    return clickAction == null ? clickAction = DynamicClickAction.NONE : clickAction;
  }

  public String insertion() {
    return insertion == null ? insertion = "" : insertion;
  }


  public DynamicComponent hover(DynamicHoverAction hoverAction, String hover) {
    this.hoverAction = hoverAction == null ? DynamicHoverAction.NONE : hoverAction;
    this.hover = hover == null ? "" : hover;
    return dirtify();
  }

  @Override
  public DynamicComponent command(String command) {
    return (DynamicComponent) IComponent.super.command(command);
  }

  @Override
  public DynamicComponent chat(String text) {
    return (DynamicComponent) IComponent.super.chat(text);
  }

  @Override
  public DynamicComponent suggest(String text) {
    return (DynamicComponent) IComponent.super.suggest(text);
  }

  @Override
  public DynamicComponent url(String url) {
    return (DynamicComponent) IComponent.super.url(url);
  }

  @Override
  public DynamicComponent copy(String clipboard) {
    return (DynamicComponent) IComponent.super.copy(clipboard);
  }

  @Override
  public DynamicComponent changePage(String page) {
    return (DynamicComponent) IComponent.super.changePage(page);
  }

  @Override
  public DynamicComponent hover(String... text) {
    return (DynamicComponent) IComponent.super.hover(text);
  }

  @Override
  public DynamicComponent hoverPlain(String... text) {
    return (DynamicComponent) IComponent.super.hoverPlain(text);
  }

  @Override
  public DynamicComponent hover(List<String> text) {
    return (DynamicComponent) IComponent.super.hover(text);
  }

  @Override
  public DynamicComponent hoverPlain(List<String> text) {
    return (DynamicComponent) IComponent.super.hoverPlain(text);
  }

  @Override
  public DynamicComponent hover(ItemStack item) {
    return (DynamicComponent) IComponent.super.hover(item);
  }

  @Override
  public DynamicComponent hover(Entity entity) {
    return (DynamicComponent) IComponent.super.hover(entity);
  }

  @Override
  public DynamicComponent tooltip(String... text) {
    return (DynamicComponent) IComponent.super.tooltip(text);
  }

  @Override
  public DynamicComponent tooltipPlain(String... text) {
    return (DynamicComponent) IComponent.super.tooltipPlain(text);
  }

  @Override
  public DynamicComponent tooltip(List<String> text) {
    return (DynamicComponent) IComponent.super.tooltip(text);
  }

  @Override
  public DynamicComponent tooltipPlain(List<String> text) {
    return (DynamicComponent) IComponent.super.tooltipPlain(text);
  }

  @Override
  public DynamicComponent tooltip(ItemStack item) {
    return (DynamicComponent) IComponent.super.tooltip(item);
  }

  @Override
  public DynamicComponent tooltip(Entity entity) {
    return (DynamicComponent) IComponent.super.tooltip(entity);
  }

  public DynamicComponent click(DynamicClickAction clickAction, String click) {
    this.clickAction = clickAction == null ? DynamicClickAction.NONE : clickAction;
    click = click == null ? "" : click;
    if(!click.isEmpty()) {
      if(clickAction == DynamicClickAction.RUN_COMMAND && !click.startsWith("/"))
        click = "/" + click;
    }
    this.click = click;
    return dirtify();
  }

  public DynamicComponent insertion(String insertion) {
    this.insertion = insertion == null ? "" : insertion;
    return dirtify();
  }

  @Override
  public Map<DynamicStyle, Boolean> styles() {
    return styles;
  }

  @Override
  public DynamicComponent enableStyles(DynamicStyle... styles) {
    if(styles.length>0) reset=false;
    return (DynamicComponent) IComponent.super.enableStyles(styles);
  }

  @Override
  public DynamicComponent disableStyles(DynamicStyle... styles) {
    if(styles.length>0) reset=false;
    return (DynamicComponent) IComponent.super.disableStyles(styles);
  }

  @Override
  public DynamicComponent enableStyles(Collection<DynamicStyle> styles) {
    if(!styles.isEmpty()) reset=false;
    return (DynamicComponent) IComponent.super.enableStyles(styles);
  }

  @Override
  public DynamicComponent disableStyles(Collection<DynamicStyle> styles) {
    if(!styles.isEmpty()) reset=false;
    return (DynamicComponent) IComponent.super.disableStyles(styles);
  }

  @Override
  public boolean useJsonValue() {
    return false;
  }

  @Override
  public boolean reset() {
    return reset;
  }

  @Override
  public boolean isSimilar(IComponent future, ExcludeCheck... excludes) {
    List<ExcludeCheck> exclude = Arrays.asList(excludes);
    List<Boolean> bool = new LinkedList<>();
    if(!exclude.contains(ExcludeCheck.COLOR)) bool.add(future.color()==CColor.NONE||color().compare(future.color()));
    if(!exclude.contains(ExcludeCheck.CLICK_EVENT)) bool.add(clickAction() == future.clickAction() && click().equals(future.click()));
    if(!exclude.contains(ExcludeCheck.HOVER_EVENT)) bool.add(hoverAction() == future.hoverAction() && hover().equals(future.hover()));
    if(!exclude.contains(ExcludeCheck.INSERTION))bool.add(insertion().equals(future.insertion()));
    if(!exclude.contains(ExcludeCheck.FONT))bool.add(font().equals(future.font()));
    if(!exclude.contains(ExcludeCheck.STYLES))bool.add((!exclude.contains(ExcludeCheck.HAS_EMPTY_STYLES) && !styles().isEmpty()&&future.styles().isEmpty()) ||
       (styles().isEmpty()&&future.styles().isEmpty()&&!future.reset()) || (!future.reset()&&!styles().isEmpty() &&
       styles().size() >= future.styles().size() &&
       styles().entrySet().stream().allMatch(e -> !future.styles().containsKey(e.getKey()) || future.styles().get(e.getKey()) == e.getValue())));

    return bool.stream().allMatch(Boolean::booleanValue);
  }

  public boolean dirty() {
    return dirty;
  }

  @Override
  public boolean complete() {
    if(edit != null && (!edit.getClass().getName().equals(EMPTY_COMPONENT.getClass().getName()))) {
      edit.children().forEach(IComponent::complete);
      edit.complete();
      if(!edit.children().isEmpty() && (!edit.equalsIgnoreChildren(EMPTY_COMPONENT) || edit instanceof IChildPriority))
        children().add(edit);
      else if(!edit.children().isEmpty()) children().addAll(edit.children());
      else children().add(edit);
      edit = EMPTY_COMPONENT;
      return true;
    }
    return false;
  }

  @Override
  public String plainText() {
    String color = "§r";
    if(color() != CColor.NONE)
      if(parent == null || (!(parent.color() == CColor.NONE && color() == CColor.WHITE))) color = this.color.toString();
    return color + (styles().keySet().stream().filter(styles()::get).map(DynamicStyle::toString).collect(Collectors.joining()) + keyValue()
       + with().stream().map(IComponent::plainText).collect(Collectors.joining())
       + children().stream().map(IComponent::plainText).collect(Collectors.joining()));
  }

  @Override
  public void writeJson(JsonBuilder builder) {
    if(!canWriteJson()) return;
    complete();
    if(length() == 0) return;
    if(keyType().equals("text") &&
       !(this instanceof IChildPriority) && children().size() == 1 && children().get(0).children().isEmpty() &&
       children().get(0).keyType().equals("text")) {
      IComponent child = children().get(0);
      if(!hasData(ExcludeCheck.TEXT, ExcludeCheck.CHILDREN)) {
        child.writeJson(builder);
        return;
      }
      keyValue(child.keyValue());
    }
    boolean hasChildren = false;
    if(!children().isEmpty()) {
      if(dirty()) compareChildren();
      if(equalsIgnoreChildren(EMPTY_COMPONENT) || isEmpty() || (!hasData() && styles().isEmpty() && keyValue().isEmpty())) {
        boolean newObject = builder.topObject() != JsonBuilder.ObjectType.ARRAY;
        if(children().size() > 1) {
          if(children().stream().filter(c -> !c.hasData(ExcludeCheck.TEXT)).count() == 1) {
            if(!children().get(0).isEmpty()) {
              IComponent initial = children().get(0);
              List<IComponent> children = new ArrayList<>();
              for(IComponent child : children()) {
                if(child.equal(initial)) continue;
                if(initial.keyType().equalsIgnoreCase(child.keyType()))
                  initial.keyValue(initial.keyValue() + child.keyValue());
                else children.add(initial);
              }
              if(!children.contains(initial)) children.add(initial);
              if(children.size() > 1) {
                builder.beginObject().name("text").value("");
                if(!isEmpty()) writeData(builder, this);

                builder.name("extra").beginArray();
              }
              for(IComponent child : children) child.writeJson(builder);
              if(children.size() > 1) builder.end().end();
              clean();
              return;
            }
          }
        }
        if(children().size() > 1 && newObject) {
          builder.beginObject().name("text").value("");
          if(!isEmpty()) writeData(builder, this);
          builder.name("extra").beginArray();
        }
        for(IComponent child : children()) child.writeJson(builder);
        if(children().size() > 1 && newObject) builder.end().end();
        clean();
        return;
      }
      hasChildren = children().stream().filter(IComponent::canWriteJson).anyMatch(c -> !c.isEmpty());
      if(keyType().equals("text"))
        if(children().size() == 1 && children().get(0).children().isEmpty()
           && !equalsIgnoreChildren(EMPTY_COMPONENT)&& keyType().equals(children().get(0).keyType())) {
          keyValue(children().get(0).keyValue());
        } else if(equalsIgnoreChildren(EMPTY_COMPONENT)) {
          children().get(0).writeJson(builder);
          clean();
          return;
        }
    }
    builder.beginObject();
    builder.name(keyType());
    if(useJsonValue()) builder.jsonValue(keyValue());
    else builder.value(keyValue());
    if(font != null && !font.isEmpty()) builder.name("font").value(font);
    additionalJson(builder);
    writeData(builder,this);
    if(!children().isEmpty()) {
      if(hasChildren) {
        builder.name("extra").beginArray();
        for(IComponent child : children()) {
          child.writeJson(builder);
        }
        builder.end();
      }
    }
    if(!with().isEmpty()) {
      if(dirty()) compareWith();
      boolean hasWith = with().stream().filter(IComponent::canWriteJson).anyMatch(c -> !c.isEmpty());
      if(hasWith) {
        builder.name("with").beginArray();
        for(IComponent with : with()) with.writeJson(builder);
        builder.end();
      }
    }
    builder.end();
    clean();
  }

  private String json = "";

  @Override
  public String json() {
    if(dirty()||json==null||json.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      JsonBuilder b = new JsonBuilder(sb);
      writeJson(b);
      json = b.toString();
    }
    return json;
  }

  private void writeData(JsonBuilder builder, IComponent component) {
    CColor color = component.color() == CColor.RESET ? CColor.NONE : component.color();
    CColor par = component.parent() != null ?
       (component.parent().color() == CColor.RESET ? CColor.NONE : component.parent().color()) : CColor.NONE;
    if(color != CColor.NONE)
      if(component.parent() == null || par != color)
        builder.name("color").value(color.getName());
    IComponent parent;
    for(Map.Entry<DynamicStyle, Boolean> entry : component.styles().entrySet()) {
      boolean writeStyle = entry.getValue();
      parent = component.parent();
      if(writeStyle)
        while(parent != null) {
          if(parent.styles().containsKey(entry.getKey())&&parent.styles().get(entry.getKey())==entry.getValue()) {
            writeStyle = false;
            break;
          }
          parent = parent.parent();
        }
      if(writeStyle)
        builder.name(entry.getKey().getName()).value(entry.getValue());
    }
    if(component.clickAction() != DynamicClickAction.NONE && !component.click().isEmpty())
      builder.name("clickEvent").beginObject()
         .name("action").value(component.clickAction().id().toLowerCase())
         .name("value").value(component.click())
         .end();
    if(component.hoverAction() != DynamicHoverAction.NONE && !component.hover().isEmpty()) {
      builder.name("hoverEvent").beginObject()
         .name("action").value(component.hoverAction().name().toLowerCase());
      if(component.hoverAction() == DynamicHoverAction.SHOW_ITEM) builder.name("value").jsonValue(component.hover()).end();
      else builder.name("value").value(component.hover()).end();
    }
    if(!component.insertion().isEmpty()) builder.name("insertion").value(component.insertion());
  }

  @Override
  public String toString() {
    return asString();
  }

  private String asString = getClass().getSimpleName();

  public String asString() {
    if(!dirty && asString.length() > getClass().getSimpleName().length()) return asString;
    String title = getClass().getSimpleName();
    title = title.equals("") ? "DynamicComponent" : title;
    StringBuilder toString = new StringBuilder(title + "{");
    if(!styles.isEmpty())
      toString.append("styles=").append(styles);
    if(color() != CColor.NONE) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("color=").append(color.getName());
    }
    if(gradient) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("gradient=").append(isGradient());
    }
    if(reset) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("reset=").append(reset());
    }
    if(!children.isEmpty()) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("children=").append(children);
    }
    if(edit != null && !edit.equals(EMPTY_COMPONENT)) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("edit=").append(edit);
    }
    if(!hover.equals("")) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("hover='").append(hover.replace("\n", "\\n")).append('\'');
    }
    if(!click.equals("")) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("click='").append(click.replace("\n", "\\n")).append('\'');
    }
    if(!insertion.equals("")) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("insertion='").append(insertion.replace("\n", "\\n")).append('\'');
    }
    if(!keyValue().equals("")) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("value='").append(keyValue()).append('\'');
    }
    if(this instanceof DynamicGradientComponent) {
      if(toString.charAt(toString.length() - 1) != '{') toString.append(", ");
      toString.append("colors='").append(Arrays.stream(((DynamicGradientComponent) this).colors())
         .map(CColor::getName).collect(Collectors.joining(", "))).append("'");
    }
    toString.append('}');
    return (asString = toString.toString().replace("\n","\\n"));
  }

  protected void compareChildren() {
    compare(children());
    if(children.size() == 1 && children.get(0).children().size() == 0 && keyType().equals("text")) {
      IComponent ch = children.get(0);
      if(color() == CColor.NONE || color() != ch.color()) color(ch.color());
      if(styles().isEmpty() && !ch.styles().isEmpty()) {
        styles.putAll(ch.styles());
        reset = false;
      }
    }
  }

  private void compare(List<IComponent> components) {
    List<IComponent> ignore = new ArrayList<>();
    List<IComponent> newComponents = new ArrayList<>();
    DynamicComponent parent = null;
    IComponent prev = null;
    IComponent prevParent = null;
    for(IComponent current : components) {
      if(!ignore.contains(current))newComponents.add(current);
      if(current instanceof DynamicComponent)((DynamicComponent)current).parent = this;
      if(prev!=null&&current.isGradient() == prev.isGradient()&&current.getClass().equals(prev.getClass())) {
        if(!current.reset()&&((current.color() == CColor.NONE && prev.color() != CColor.NONE) || (prev.color()!=CColor.NONE&&prev.color().compare(current.color()))))
          current.color(prev.color());
        IComponent finalPrev = prev;
        if(!current.reset()) prev.styles().keySet().forEach(key -> current.styles().computeIfAbsent(key, finalPrev.styles()::get));
        if(prev.isSimilar(current) && !current.reset()) {
          prev.keyValue(prev.keyValue() + current.keyValue());
          ignore.add(current);
        } else if(prev.lengthIgnoreWhitespace()==0&&prev.length()>0) {
          current.keyValue(prev.keyValue() + current.keyValue());
          ignore.add(prev);
        }
      }
      if(!ignore.contains(current)) prev = current;
    }
    newComponents.removeAll(ignore);
    ignore.clear();
    List<IComponent> componentList = new ArrayList<>();
    for(int i = 0; i < newComponents.size(); i++) {
      prev = i-1 >= 0 ? newComponents.get(i-1) : null;
      IComponent current = newComponents.get(i);
      IComponent future = i + 1 < newComponents.size() ? newComponents.get(i + 1) : null;
      ExcludeCheck[] checks = current.isGradient() ? new ExcludeCheck[]{ExcludeCheck.COLOR, ExcludeCheck.HAS_EMPTY_STYLES} : new ExcludeCheck[]{ExcludeCheck.HAS_EMPTY_STYLES};
      if(future == null) {
        if(parent!=null){
          if(parent.isSimilar(current, checks)&&current.isGradient() == parent.isGradient()){
            parent.removeDuplicates(current);
            parent.children().add(current);
            parent.clean();
            ignore.add(current);
          }
          if(prevParent!=null && parent.isGradient()==prevParent.isGradient() && parent.isSimilar(prevParent,
             parent.isGradient() ? new ExcludeCheck[]{ExcludeCheck.COLOR} : new ExcludeCheck[0])){
            prevParent.children().add(parent);
            componentList.add(prevParent);
          } else{
            if(prevParent!=null&&!componentList.contains(prevParent))componentList.add(prevParent);
            componentList.add(parent);
          }
          if(parent.children().size() == 1) {
            parent.applyIfNotPresentData(current);
            componentList.remove(parent);
            componentList.add(current);
            continue;
          }
        }
        if(!componentList.contains(current) && !ignore.contains(current))  {
          componentList.add(current);
        }
        continue;
      }
      if(componentList.contains(current)) continue;
      if(parent != null &&current.isGradient() == parent.isGradient()&& parent.isSimilar(current, checks)) {
        parent.removeDuplicates(current);
        parent.children().add(current);
        continue;
      }else {
        if(parent != null) {
          parent.clean();
          if(prevParent != null && parent.isGradient() == prevParent.isGradient() && parent.isSimilar(prevParent,
             parent.isGradient() ? new ExcludeCheck[]{ExcludeCheck.COLOR} : new ExcludeCheck[0])) {
            prevParent.children().add(parent);
            continue;
          } else  {
            componentList.add(parent);
          }
          prevParent = parent;
        }
        if(parent!=null) {
          if(parent.children().size() == 1) {
            parent.applyIfNotPresentData(prev);
            componentList.remove(parent);
            componentList.add(prev);
          }
        }
        parent = null;
      }
      componentList.add(current);
      if(!current.children().isEmpty()&&!current.hasData(ExcludeCheck.CHILDREN))continue;
      if(current.isGradient() == future.isGradient()&&current.reset()==future.reset())
        if(current.children().isEmpty()&&future.children().isEmpty()&&current.isSimilar(future, checks)&&current.hasData(ExcludeCheck.TEXT)) {
          parent = new DynamicTextComponent();
          parent.gradient(current.isGradient());
          current.applyData(parent);
          current.gradient(false);
          parent.removeDuplicates(current);
          parent.children().add(current);
          componentList.remove(current);
        }
    }
    for(IComponent iComponent : componentList) setParents(iComponent);
    if(componentList.size() == 1&&!componentList.get(0).children().isEmpty()) {
      if(!componentList.get(0).children().get(0).isGradient()) {
        this.applyData(componentList.get(0));
        componentList.addAll(componentList.remove(0).children());
      }
    }
    components.clear();
    components.addAll(componentList);
  }

  private void setParents(IComponent component) {
    for(IComponent child : component.children()) {
      if(child instanceof DynamicComponent) ((DynamicComponent)child).parent = component;
      setParents(child);
    }
  }


  private void compareWith() {
    compare(with());
  }

  @Override
  public void removeDuplicates(IComponent applyTo) {
    if(applyTo.click().equalsIgnoreCase(click())&& applyTo.clickAction()==clickAction()) applyTo.click(DynamicClickAction.NONE,"");
    if(applyTo.hover().equalsIgnoreCase(hover())&& applyTo.hoverAction()==hoverAction()) applyTo.hover(DynamicHoverAction.NONE,"");
    if(applyTo.insertion().equalsIgnoreCase(insertion())) applyTo.insertion("");
    if(applyTo.color()==color()&&children().isEmpty()) applyTo.color(CColor.NONE);
    for(Map.Entry<DynamicStyle, Boolean> style : styles().entrySet()) {
      if(!applyTo.styles().containsKey(style.getKey()))continue;
      if(applyTo.styles().get(style.getKey())==style.getValue()) applyTo.styles().remove(style.getKey());
    }
  }

  @Override
  public void applyEventsIfNotPresent(IComponent applyTo) {
    if(applyTo.clickAction()==DynamicClickAction.NONE)
      applyTo.click(clickAction(),click());
    if(applyTo.hoverAction()==DynamicHoverAction.NONE)
      applyTo.hover(hoverAction(),hover());
    if(applyTo.insertion().isEmpty())
      applyTo.insertion(insertion());
  }

  @Override
  public void applyData(IComponent applyTo) {
    applyTo.styles().clear();
    applyTo.styles().putAll(styles());
    applyTo.click(clickAction(),click());
    applyTo.hover(hoverAction(),hover());
    applyTo.insertion(insertion());
    applyTo.font(font());
  }
  @Override
  public void applyIfNotPresentData(IComponent applyTo) {
    styles().forEach((k,v)->{
      if(applyTo.styles().containsKey(k))return;
      applyTo.styles().put(k,v);
    });
    if(applyTo.click().equals(""))
      applyTo.click(clickAction(),click());
    if(applyTo.hover().equals(""))
      applyTo.hover(hoverAction(),hover());
    if(applyTo.insertion().equals(""))
      applyTo.insertion(insertion());
    if(applyTo.font().equals(""))
      applyTo.font(font());
  }

  @Override
  public int hashCode() {
    return Objects.hash(styles(), children(), with(), color(), hover(), hoverAction(), click(), clickAction(), insertion(), keyType(), keyValue());
  }


}
