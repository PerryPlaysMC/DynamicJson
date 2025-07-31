package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.data.util.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Creator: PerryPlaysMC
 * Created: 03/2022
 **/
@SuppressWarnings("all")
public interface IComponent extends IJson, Cloneable {

 // IComponent copy();

 IComponent clone();

 List<IComponent> children();

 IComponent childAt(int index);

 IComponent lastChild();

 IComponent gradient(boolean toggle);

 IComponent parent();

 void applyData(IComponent applyTo);

 void applyEventsIfNotPresent(IComponent applyTo);

 void removeDuplicates(IComponent applyTo);

 boolean isGradient();

 IComponent colorIn(CColor color);

 CColor color();

 IComponent color(CColor color);

 String click();

 DynamicClickAction clickAction();

 IComponent click(DynamicClickAction clickAction, String text);

 default IComponent command(String command) {
	return click(DynamicClickAction.RUN_COMMAND, command);
 }

 default IComponent chat(String text) {
	return click(DynamicClickAction.CHAT, text);
 }

 default IComponent suggest(String text) {
	return click(DynamicClickAction.SUGGEST_COMMAND, text);
 }

 default IComponent url(String url) {
	return click(DynamicClickAction.OPEN_URL, url);
 }

 default IComponent copy(String clipboard) {
	return click(DynamicClickAction.COPY_TO_CLIPBOARD, clipboard);
 }

 default IComponent changePage(String page) {
	return click(DynamicClickAction.CHANGE_PAGE, page);
 }

 String hover();

 DynamicHoverAction hoverAction();

 IComponent hover(DynamicHoverAction hoverAction, String hover);

 default IComponent hover(String... text) {
	return hover(DynamicHoverAction.SHOW_TEXT, CColor.translateCommon(String.join("\n", text)));
 }

 default IComponent hoverPlain(String... text) {
	return hover(DynamicHoverAction.SHOW_TEXT, String.join("\n", text));
 }


 default IComponent hover(List<String> text) {
	return hover(DynamicHoverAction.SHOW_TEXT, CColor.translateCommon(String.join("\n", text)));
 }

 default IComponent hoverPlain(List<String> text) {
	return hover(DynamicHoverAction.SHOW_TEXT, String.join("\n", text));
 }

 default IComponent hover(ItemStack item) {
	return hover(DynamicHoverAction.SHOW_ITEM, CColor.translateCommon(DynamicHoverAction.itemstackToString(item)));
 }

 default IComponent hover(Entity entity) {
	return hover(DynamicHoverAction.SHOW_ENTITY, "{\"type\":\"" +
		 entity.getType().name().toLowerCase() + "\",\"id\":" +
		 entity.getUniqueId() + ",\"name\":\"" + entity.getName() + "\"}");
 }

 default IComponent tooltip(String... text) {
	return hover(text);
 }

 default IComponent tooltipPlain(String... text) {
	return hoverPlain(text);
 }

 default IComponent tooltip(List<String> text) {
	return hover(text);
 }

 default IComponent tooltipPlain(List<String> text) {
	return hoverPlain(text);
 }

 default IComponent tooltip(ItemStack item) {
	return hover(item);
 }

 default IComponent tooltip(Entity entity) {
	return hover(entity);
 }

 String insertion();

 String font();

 IComponent font(String font);

 IComponent insertion(String text);

 Map<DynamicStyle, Boolean> styles();

 default Set<DynamicStyle> enabledStyles() {
	return styles().entrySet().stream().filter(e -> e.getValue()).map(e -> e.getKey()).collect(Collectors.toSet());
 }

 default Set<DynamicStyle> disabledStyles() {
	return styles().entrySet().stream().filter(e -> !e.getValue()).map(e -> e.getKey()).collect(Collectors.toSet());
 }

 default IComponent enableStyles(DynamicStyle... styles) {
	for(DynamicStyle style : styles) styles().put(style, true);
	return dirtify();
 }

 default IComponent disableStyles(DynamicStyle... styles) {
	for(DynamicStyle style : styles) styles().put(style, false);
	return dirtify();
 }

 default IComponent enableStyles(Collection<DynamicStyle> styles) {
	for(DynamicStyle style : styles) styles().put(style, true);
	return dirtify();
 }

 default IComponent disableStyles(Collection<DynamicStyle> styles) {
	for(DynamicStyle style : styles) styles().put(style, false);
	return dirtify();
 }

 IComponent add(IComponent child);

 IComponent addDefault(String... children);

 IComponent add(String... children);

 IComponent addReset(String... children);

 boolean complete();

 boolean hasData(ExcludeCheck... excludes);

 int length();

 int lengthIgnoreWhitespace();

 boolean useJsonValue();

 String keyType();

 String keyValue();

 IComponent keyValue(String text);

 List<IComponent> with();

 IComponent with(IComponent... text);

 IComponent dirtify();

 boolean dirty();

 IComponent clean();

 boolean isSimilar(IComponent component, ExcludeCheck... excludes);

 boolean canWriteJson();

 boolean reset();

 boolean isEmpty();

 String asString();

 default boolean hasKeyValue() {
	return keyValue() == null || keyValue().isEmpty();
 }

 default boolean equal(Object o) {
	if(this == o) return true;
	if(o == null || !(o instanceof IComponent)) return false;
	IComponent that = (IComponent) o;
	return Objects.equals(styles(), that.styles()) &&
		 Objects.equals(children(), that.children()) && Objects.equals(with(), that.with()) && Objects.equals(color(), that.color()) && Objects.equals(hover(), that.hover()) &&
		 hoverAction() == that.hoverAction() && Objects.equals(click(), that.click()) && clickAction() == that.clickAction() &&
		 Objects.equals(insertion(), that.insertion()) && Objects.equals(keyValue().isEmpty(), that.keyValue().isEmpty()) && Objects.equals(keyType(), that.keyType());
 }


 default boolean equalsIgnoreChildren(Object o) {
	if(this == o) return true;
	if(o == null) return false;
	if(o == null || !(o instanceof IComponent)) return false;
	IComponent that = (IComponent) o;
	return styles().equals(that.styles()) &&
		 color().equals(that.color()) && hover().equals(that.hover()) &&
		 hoverAction() == that.hoverAction() && click().equals(that.click()) && clickAction() == that.clickAction() &&
		 insertion().equals(that.insertion()) && keyValue().isEmpty() == that.keyValue().isEmpty() &&
		 (that.keyType() == null || keyType().equals(that.keyType()));
 }

 static DynamicTextComponent textComponent(String text) {
	return DynamicTextComponent.of(text);
 }

 static DynamicTranslatableComponent translationComponent(Translation translation) {
	return DynamicTranslatableComponent.of(translation);
 }

 static DynamicKeybindComponent keybindComponent(Keybind keybind) {
	return DynamicKeybindComponent.of(keybind);
 }


 static DynamicNBTComponent nbtComponent(String path, Entity entity) {
	return DynamicNBTComponent.of(path, entity);
 }

 static DynamicNBTComponent nbtComponent(String path, UUID entity) {
	return DynamicNBTComponent.of(path, entity);
 }

 static DynamicGradientComponent gradientComponent(CColor... colors) {
	return DynamicGradientComponent.of(colors);
 }

 static DynamicGradientComponent gradientComponent(String text, CColor... colors) {
	return DynamicGradientComponent.of(text, colors);
 }

 static DynamicSelectorComponent selectorComponent(String text) {
	return DynamicSelectorComponent.of(text);
 }

 static DynamicScoreComponent scoreComponent(String name, String objective) {
	return DynamicScoreComponent.of(name, objective);
 }

 void applyIfNotPresentData(IComponent applyTo);

 public enum ExcludeCheck {
	COLOR, HOVER_EVENT, CLICK_EVENT, INSERTION, FONT, STYLES, HAS_EMPTY_STYLES, TEXT, CHILDREN
 }

}
