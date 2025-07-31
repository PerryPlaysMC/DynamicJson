package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.data.util.CColor;
import io.dynamicstudios.json.data.util.DynamicClickAction;
import io.dynamicstudios.json.data.util.DynamicStyle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public class DynamicSelectorComponent extends DynamicComponent {


 public static DynamicSelectorComponent of(String selector) {
	return new DynamicSelectorComponent(selector);
 }

 private String selector = "";

 private DynamicSelectorComponent(String selector) {
	selector(selector);
 }

 public DynamicSelectorComponent selector(String selector) {
	return keyValue(selector);
 }

 public String selector() {
	return keyValue();
 }

 @Override
 public DynamicSelectorComponent addDefault(String... children) {
	return add(IComponent.textComponent(resetText(String.join("\n", children))));
 }

 @Override
 public DynamicSelectorComponent addReset(String... children) {
	return (DynamicSelectorComponent) super.addReset(children);
 }

 @Override
 public DynamicSelectorComponent add(String... children) {
	return add(IComponent.textComponent(resetText(String.join("\n", children))));
 }

 @Override
 public String keyType() {
	return "selector";
 }

 @Override
 public String keyValue() {
	return selector == null ? selector = "" : selector;
 }

 @Override
 public DynamicSelectorComponent keyValue(String text) {
	this.selector = text;
	return dirtify();
 }


 @Override
 public DynamicSelectorComponent gradient(boolean toggle) {
	return (DynamicSelectorComponent) super.gradient(toggle);
 }

 @Override
 public DynamicSelectorComponent color(CColor color) {
	return (DynamicSelectorComponent) super.color(color);
 }

 @Override
 public DynamicSelectorComponent with(IComponent... text) {
	return (DynamicSelectorComponent) super.with(text);
 }

 @Override
 public DynamicSelectorComponent add(IComponent child) {
	return (DynamicSelectorComponent) super.add(child);
 }

 @Override
 public DynamicSelectorComponent dirtify() {
	return (DynamicSelectorComponent) super.dirtify();
 }

 @Override
 public DynamicSelectorComponent command(String command) {
	return (DynamicSelectorComponent) super.command(command);
 }

 @Override
 public DynamicSelectorComponent chat(String text) {
	return (DynamicSelectorComponent) super.chat(text);
 }

 @Override
 public DynamicSelectorComponent suggest(String text) {
	return (DynamicSelectorComponent) super.suggest(text);
 }

 @Override
 public DynamicSelectorComponent url(String url) {
	return (DynamicSelectorComponent) super.url(url);
 }

 @Override
 public DynamicSelectorComponent copy(String clipboard) {
	return (DynamicSelectorComponent) super.copy(clipboard);
 }

 @Override
 public DynamicSelectorComponent changePage(String page) {
	return (DynamicSelectorComponent) super.changePage(page);
 }

 @Override
 public DynamicSelectorComponent hover(String... text) {
	return (DynamicSelectorComponent) super.hover(text);
 }

 @Override
 public DynamicSelectorComponent hoverPlain(String... text) {
	return (DynamicSelectorComponent) super.hoverPlain(text);
 }

 @Override
 public DynamicSelectorComponent hover(List<String> text) {
	return (DynamicSelectorComponent) super.hover(text);
 }

 @Override
 public DynamicSelectorComponent hoverPlain(List<String> text) {
	return (DynamicSelectorComponent) super.hoverPlain(text);
 }

 @Override
 public DynamicSelectorComponent hover(ItemStack item) {
	return (DynamicSelectorComponent) super.hover(item);
 }

 @Override
 public DynamicSelectorComponent hover(Entity entity) {
	return (DynamicSelectorComponent) super.hover(entity);
 }

 @Override
 public DynamicSelectorComponent tooltip(String... text) {
	return (DynamicSelectorComponent) super.tooltip(text);
 }

 @Override
 public DynamicSelectorComponent tooltipPlain(String... text) {
	return (DynamicSelectorComponent) super.tooltipPlain(text);
 }

 @Override
 public DynamicSelectorComponent tooltip(List<String> text) {
	return (DynamicSelectorComponent) super.tooltip(text);
 }

 @Override
 public DynamicSelectorComponent tooltipPlain(List<String> text) {
	return (DynamicSelectorComponent) super.tooltipPlain(text);
 }

 @Override
 public DynamicSelectorComponent tooltip(ItemStack item) {
	return (DynamicSelectorComponent) super.tooltip(item);
 }

 @Override
 public DynamicSelectorComponent tooltip(Entity entity) {
	return (DynamicSelectorComponent) super.tooltip(entity);
 }

 @Override
 public DynamicSelectorComponent click(DynamicClickAction clickAction, String click) {
	return (DynamicSelectorComponent) super.click(clickAction, click);
 }

 @Override
 public DynamicSelectorComponent insertion(String insertion) {
	return (DynamicSelectorComponent) super.insertion(insertion);
 }

 @Override
 public DynamicSelectorComponent enableStyles(DynamicStyle... styles) {
	return (DynamicSelectorComponent) super.enableStyles(styles);
 }

 @Override
 public DynamicSelectorComponent disableStyles(DynamicStyle... styles) {
	return (DynamicSelectorComponent) super.disableStyles(styles);
 }

 @Override
 public DynamicSelectorComponent enableStyles(Collection<DynamicStyle> styles) {
	return (DynamicSelectorComponent) super.enableStyles(styles);
 }

 @Override
 public DynamicSelectorComponent disableStyles(Collection<DynamicStyle> styles) {
	return (DynamicSelectorComponent) super.disableStyles(styles);
 }

 @Override
 public DynamicSelectorComponent font(String font) {
	return (DynamicSelectorComponent) super.font(font);
 }

 @Override
 public DynamicSelectorComponent clean() {
	return (DynamicSelectorComponent) super.clean();
 }


}
