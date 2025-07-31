package io.dynamicstudios.json.data.component;

import io.dynamicstudios.json.data.util.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 03/2022
 **/
public class DynamicKeybindComponent extends DynamicComponent {

 private Keybind keybind = null;


 public static DynamicKeybindComponent of(Keybind keybind) {
	return new DynamicKeybindComponent(keybind);
 }

 private DynamicKeybindComponent(Keybind keybind) {
	keybind(keybind);
 }

 public Keybind keybind() {
	return keybind;
 }

 public DynamicKeybindComponent keybind(Keybind keybind) {
	this.keybind = keybind;
	return dirtify();
 }

 @Override
 public DynamicKeybindComponent addDefault(String... children) {
	return add(IComponent.textComponent(resetText(String.join("\n", children))));
 }

 @Override
 public DynamicKeybindComponent add(String... children) {
	return add(IComponent.textComponent(resetText(String.join("\n", children))));
 }


 @Override
 public String keyType() {
	return "keybind";
 }

 @Override
 public String keyValue() {
	return keybind.id();
 }

 @Override
 public DynamicKeybindComponent keyValue(String text) {
	keybind = Keybind.byId(text);
	if(keybind == null) keybind = Keybind.KEY_JUMP;
	return dirtify();
 }

 @Override
 public DynamicKeybindComponent gradient(boolean toggle) {
	return (DynamicKeybindComponent) super.gradient(toggle);
 }

 @Override
 public DynamicKeybindComponent color(CColor color) {
	return (DynamicKeybindComponent) super.color(color);
 }

 @Override
 public DynamicKeybindComponent with(IComponent... text) {
	return (DynamicKeybindComponent) super.with(text);
 }

 @Override
 public DynamicKeybindComponent add(IComponent child) {
	return (DynamicKeybindComponent) super.add(child);
 }

 @Override
 public DynamicKeybindComponent addReset(String... children) {
	return (DynamicKeybindComponent) super.addReset(children);
 }

 @Override
 public DynamicKeybindComponent dirtify() {
	return (DynamicKeybindComponent) super.dirtify();
 }

 @Override
 public DynamicKeybindComponent hover(DynamicHoverAction hoverAction, String hover) {
	return (DynamicKeybindComponent) super.hover(hoverAction, hover);
 }

 @Override
 public DynamicKeybindComponent command(String command) {
	return (DynamicKeybindComponent) super.command(command);
 }

 @Override
 public DynamicKeybindComponent chat(String text) {
	return (DynamicKeybindComponent) super.chat(text);
 }

 @Override
 public DynamicKeybindComponent suggest(String text) {
	return (DynamicKeybindComponent) super.suggest(text);
 }

 @Override
 public DynamicKeybindComponent url(String url) {
	return (DynamicKeybindComponent) super.url(url);
 }

 @Override
 public DynamicKeybindComponent copy(String clipboard) {
	return (DynamicKeybindComponent) super.copy(clipboard);
 }

 @Override
 public DynamicKeybindComponent changePage(String page) {
	return (DynamicKeybindComponent) super.changePage(page);
 }

 @Override
 public DynamicKeybindComponent hover(String... text) {
	return (DynamicKeybindComponent) super.hover(text);
 }

 @Override
 public DynamicKeybindComponent hoverPlain(String... text) {
	return (DynamicKeybindComponent) super.hoverPlain(text);
 }

 @Override
 public DynamicKeybindComponent hover(List<String> text) {
	return (DynamicKeybindComponent) super.hover(text);
 }

 @Override
 public DynamicKeybindComponent hoverPlain(List<String> text) {
	return (DynamicKeybindComponent) super.hoverPlain(text);
 }

 @Override
 public DynamicKeybindComponent hover(ItemStack item) {
	return (DynamicKeybindComponent) super.hover(item);
 }

 @Override
 public DynamicKeybindComponent hover(Entity entity) {
	return (DynamicKeybindComponent) super.hover(entity);
 }

 @Override
 public DynamicKeybindComponent tooltip(String... text) {
	return (DynamicKeybindComponent) super.tooltip(text);
 }

 @Override
 public DynamicKeybindComponent tooltipPlain(String... text) {
	return (DynamicKeybindComponent) super.tooltipPlain(text);
 }

 @Override
 public DynamicKeybindComponent tooltip(List<String> text) {
	return (DynamicKeybindComponent) super.tooltip(text);
 }

 @Override
 public DynamicKeybindComponent tooltipPlain(List<String> text) {
	return (DynamicKeybindComponent) super.tooltipPlain(text);
 }

 @Override
 public DynamicKeybindComponent tooltip(ItemStack item) {
	return (DynamicKeybindComponent) super.tooltip(item);
 }

 @Override
 public DynamicKeybindComponent tooltip(Entity entity) {
	return (DynamicKeybindComponent) super.tooltip(entity);
 }

 @Override
 public DynamicKeybindComponent click(DynamicClickAction clickAction, String click) {
	return (DynamicKeybindComponent) super.click(clickAction, click);
 }

 @Override
 public DynamicKeybindComponent insertion(String insertion) {
	return (DynamicKeybindComponent) super.insertion(insertion);
 }

 @Override
 public DynamicKeybindComponent enableStyles(DynamicStyle... styles) {
	return (DynamicKeybindComponent) super.enableStyles(styles);
 }

 @Override
 public DynamicKeybindComponent disableStyles(DynamicStyle... styles) {
	return (DynamicKeybindComponent) super.disableStyles(styles);
 }

 @Override
 public DynamicKeybindComponent enableStyles(Collection<DynamicStyle> styles) {
	return (DynamicKeybindComponent) super.enableStyles(styles);
 }

 @Override
 public DynamicKeybindComponent disableStyles(Collection<DynamicStyle> styles) {
	return (DynamicKeybindComponent) super.disableStyles(styles);
 }

 @Override
 public DynamicKeybindComponent font(String font) {
	return (DynamicKeybindComponent) super.font(font);
 }

 @Override
 public DynamicKeybindComponent clean() {
	return (DynamicKeybindComponent) super.clean();
 }


}
