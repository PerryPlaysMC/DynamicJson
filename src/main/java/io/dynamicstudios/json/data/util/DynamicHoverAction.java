package io.dynamicstudios.json.data.util;

import io.dynamicstudios.json.JsonBuilder;
import io.dynamicstudios.json.Version;
import io.dynamicstudios.json.data.util.packet.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DynamicHoverAction implements Serializable {


 SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY, NONE;

 private static final Pattern ITEM_PARSER = Pattern.compile("(?<!\\\\)'((?:(?=\\\\')..|[^'])+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

 private static final Class<?> nmsStackC, cbStack, cmp, comp, jsonOps, dynamicOps, craftRegistry, registryAccess, iregistryCustom, registryOps, codecClazz,
		dataResult, holderLookupProvider;
 private static final Method asNMS, save, encode, emptymap, getMinecraftRegistry, createSerializationContext, getOrThrow, getAsString;
 private static final Field CODEC, INSTANCE;

 static {
	nmsStackC = Version.Minecraft.getClass("world.item.ItemStack");
	cbStack = Version.CraftBukkit.getClass("inventory.CraftItemStack");
	craftRegistry = Version.CraftBukkit.getClass("CraftRegistry");
	registryAccess = Version.Minecraft.getClass("core.RegistryAccess");
	iregistryCustom = Version.Minecraft.getClass("core.IRegistryCustom");
	cmp = Version.Minecraft.getClass("nbt.NBTTagCompound");
	comp = Version.Minecraft.getClass("core.component.DataComponentMap");
	holderLookupProvider = Version.Minecraft.getClass("core.HolderLookup.Provider");
	jsonOps = Version.Mojang.getClass("serialization.JsonOps");
	dynamicOps = Version.Mojang.getClass("serialization.DynamicOps");
	codecClazz = Version.Mojang.getClass("serialization.Codec");
	dataResult = Version.Mojang.getClass("serialization.DataResult");
	registryOps = Version.Minecraft.getClass("resources.RegistryOps");
	CODEC = ReflectionUtils.getField(nmsStackC, codecClazz, "Codec");
	Method asNMS1 = null, save1 = null;
	if(cbStack != null) {
	 try {
		asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
		save1 = nmsStackC.getMethod("save", cmp);
	 } catch(Exception e) {
	 }
	 try {
		if(asNMS1 == null) asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
		if(save1 == null) save1 = ReflectionUtils.getMethod(nmsStackC, cmp, cmp);
	 } catch(NoSuchMethodException e1) {
	 }
	 try {
		if(asNMS1 == null) asNMS1 = cbStack.getMethod("asNMSCopy", ItemStack.class);
		if(save1 == null) save1 = ReflectionUtils.getMethod(nmsStackC, comp);
	 } catch(NoSuchMethodException e1) {
		e1.printStackTrace();
	 }
	}
	asNMS = asNMS1;
	save = save1;
	Method getMinecraftRegistry1 = null;
	Method createSerializationContext1 = null;
	Method emptymap1 = null;
	Method encode1 = null;
	Method getOrThrow1 = null;
	getAsString = ReflectionUtils.getMethod(ItemMeta.class, "getAsString");
	if(getAsString != null) getAsString.setAccessible(true);
	Field INSTANCE1 = null;
	if(getAsString == null && asNMS1 != null && craftRegistry != null) {
	 try {
		getMinecraftRegistry1 = ReflectionUtils.getMethod(craftRegistry, iregistryCustom);
		if(getMinecraftRegistry1 != null) {
		 createSerializationContext1 = ReflectionUtils.getMethod(getMinecraftRegistry1.getReturnType(), registryOps, dynamicOps);
		 emptymap1 = ReflectionUtils.getMethod(dynamicOps, "emptyMap");
		 encode1 = ReflectionUtils.getMethod(CODEC.getType(), dataResult, Object.class, dynamicOps, Object.class);
		 getOrThrow1 = ReflectionUtils.getMethod(dataResult, "getOrThrow");
		 INSTANCE1 = ReflectionUtils.getField(jsonOps, jsonOps);
		}
	 } catch(Exception e) {
		e.printStackTrace();
	 }
	}
	INSTANCE = INSTANCE1;
	getMinecraftRegistry = getMinecraftRegistry1;
	getOrThrow = getOrThrow1;
	createSerializationContext = createSerializationContext1;
	emptymap = emptymap1;
	encode = encode1;
 }

 public static String itemstackToString(ItemStack item) {
	try {
	 if(item == null) item = new ItemStack(Material.AIR);
	 Object nmsStack = asNMS.invoke(null, item);
	 if(cmp == null) {
		if(getAsString == null) {
		 return save.invoke(asNMS.invoke(null, item)).toString();
		}
		ItemMeta meta = item.getItemMeta();
		String text = (String) getAsString.invoke(meta);
		StringBuilder itemStr = new StringBuilder();
		Matcher matcher = ITEM_PARSER.matcher(text);
		char[] chars = text.toCharArray();
		boolean found = matcher.find();
		if(found) {
		 for(int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if(found && i == matcher.start()) {
			 String group = matcher.group(1);
			 JsonBuilder.prepareString(itemStr, group);
			 i = matcher.end();
			 found = matcher.find();
			} else {
			 itemStr.append(c);
			}
		 }
		} else {
		 JsonBuilder.prepareString(itemStr, text);
		 itemStr.deleteCharAt(0).deleteCharAt(itemStr.length() - 1);
		}
		if(itemStr.length() == 2) return "{\"id\":\"" + item.getType().name().toLowerCase() + "\",\"count\":1}";
		return "{\"id\":\"" + item.getType().name().toLowerCase() + "\",\"count\":1,\"components\":" + itemStr + "}"; //((Optional<?>)nmsStack.getClass().getMethod("b").invoke(nmsStack)).orElse(null).toString();
	 }
	 if(nmsStackC == null || cbStack == null) return "";
	 return save.invoke(nmsStack, cmp.newInstance()).toString();
	} catch(Exception e) {
	 e.printStackTrace();
	 return "";
	}
 }

 public static DynamicHoverAction fromName(String text) {
	for(DynamicHoverAction value : values()) {
	 if(value.name().equalsIgnoreCase(text)) return value;
	}
	return null;
 }

}