package io.dynamicstudios.json;


import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Version {
 v1_6(160), v1_6_R1(161), v1_6_R2(162), v1_6_R3(163),
 v1_7(170), v1_7_R2(172), v1_7_R4(174), v1_7_R10(1710),
 v1_8(180), v1_8_R1(181), v1_8_R2(182), v1_8_R3(183),
 v1_9(190), v1_9_R1(191), v1_9_R2(192),
 v1_10(1100), v1_10_R1(1101),
 v1_11(1110), v1_11_R1(1111),
 v1_12(1120), v1_12_R1(1121),
 v1_13(1130), v1_13_R1(1131), v1_13_R2(1132),
 v1_14(1140), v1_14_R1(1141),
 v1_15(1150), v1_15_R1(1151),
 v1_16(1160), v1_16_R1(1161), v1_16_R2(1162), v1_16_R3(1163),
 v1_17(1170), v1_17_R1(1171),
 v1_18(1180), v1_18_R1(1181), v1_18_R2(1182), v1_18_R3(1183), v1_18_R4(1184),
 v1_19(1190), v1_19_R1(1191), v1_19_R2(1192), v1_19_R3(1193), v1_19_R4(1194),
 v1_20(1200), v1_20_R1(1201), v1_20_R2(1202), v1_20_R4(1204), v1_20_R5(1205), v1_20_R6(1206),
 v1_21(1210), v1_21_R1(1211), v1_21_R2(1212), v1_21_R3(1213), v1_21_R4(1214), v1_21_R5(1215), v1_21_R6(1216),
 UNKNOWN(Integer.MAX_VALUE, "Unknown");

 static {
	currentExact();
	current();
 }

 private static Version current, exact;

 private int ver;
 private String version;

 Version(int ver) {
	this.ver = ver;
	this.version = "v" + name().substring(1).toUpperCase();
 }

 Version(int ver, String version) {
	this.version = version;
	this.ver = ver;
 }

 public String getVersion() {
	return version;
 }

 public int getVersionInt() {
	return ver;
 }

 public boolean is(Version v) {
	return getVersionInt() == v.getVersionInt();
 }

 public boolean isHigher(Version v) {
	return getVersionInt() > v.getVersionInt();
 }

 public boolean isOrHigher(Version v) {
	return getVersionInt() >= v.getVersionInt();
 }

 public boolean isLower(Version v) {
	return getVersionInt() < v.getVersionInt();
 }

 public static boolean isCurrentHigher(Version v) {
	if(v.name().contains("R"))
	 return currentExact().isHigher(v);
	return current().isHigher(v);
 }

 public static boolean isCurrentOrHigher(Version v) {
	if(v.name().contains("R"))
	 return currentExact().isOrHigher(v);
	return current().isOrHigher(v);
 }

 public static String getNMSPackage() {
	if(isCurrentHigher(v1_16_R3)) return "net.minecraft";
	return "net.minecraft.server." + currentExact().getVersion();
 }

 public static String getCBPackage() {
	String version = currentExact().getVersion();
	return "org.bukkit.craftbukkit" + (version.contains("R") ? "." + version : "");
 }

 public static String getCBPackage2() {
	String version = currentExact().getVersion();
	return "org.bukkit.craftbukkit";
 }


 public static Class<?> findClass(String name) {
	return findClass(name, false);
 }

 public static Class<?> findClass(String name, boolean stackTrace) {
	try {
	 return Class.forName(name);
	} catch(Exception e) {
	 if(stackTrace)
		e.printStackTrace();
	 return null;
	}
 }

 public static class CraftBukkit {
	private static final HashMap<String, Class<?>> CACHE = new HashMap<>();

	public static Class<?> getClass(String clazz) {
	 if(CACHE.containsKey(clazz)) return CACHE.get(clazz);
	 Class<?> cls = findClass(getCBPackage() + "." + clazz);
	 if(cls == null) {
		cls = findClass(getCBPackage() + "." + clazz);
	 }
	 if(cls == null) {
		cls = findClass(getCBPackage2() + "." + clazz);
	 }
	 CACHE.put(clazz, cls);
	 return cls;
	}
 }

 public static class Minecraft {
	private static final HashMap<String, Class<?>> CACHE = new HashMap<>();

	public static Class<?> getClass(String clazz) {
	 if(CACHE.containsKey(clazz)) return CACHE.get(clazz);
	 String nms = getNMSPackage();
	 Class<?> cls = null;
	 if(!isCurrentHigher(v1_16_R3))
		if(clazz.contains("."))
		 cls = findClass(nms + "." + clazz.split("\\.")[clazz.split("\\.").length - 1]);
	 if(cls == null)
		cls = findClass(nms + "." + clazz);
	 if(cls != null)
		CACHE.put(clazz, cls);
	 return cls;
	}
 }


 public static class Mojang {
	private static final HashMap<String, Class<?>> CACHE = new HashMap<>();

	public static Class<?> getClass(String clazz) {
	 if(CACHE.containsKey(clazz)) return CACHE.get(clazz);
	 String nms = Version.isCurrentHigher(v1_7) ? "com.mojang" : "net.minecraft.util.com.mojang";
	 Class<?> cls = Version.findClass(nms + "." + clazz);
	 CACHE.put(clazz, cls);
	 return cls;
	}
 }


 public static Version currentExact() {
	if(exact != null) return exact;
	String pack = Bukkit.getServer().getClass().getPackage().getName();
	String version = pack.substring(pack.lastIndexOf('.') + 1);
	Version ret = value(version);
	if(ret == null) {
	 ret = Version.UNKNOWN;
	 if(version.matches("v\\d+_\\d+_R\\d+")) {
		ret.ver = Integer.parseInt(version.replace("v", "").replace("R", "").replace("_", ""));
		ret.version = version;
		return exact = ret;
	 }
	 Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)-R(\\d+)\\.(\\d+).+");
	 Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());
	 if(matcher.find()) {
		ret = value("v" + matcher.group(1) + "_" + matcher.group(2) + "_R" + matcher.group(3));
		if(ret != null) return exact = ret;
		ret = Version.UNKNOWN;
		ret.ver = Integer.parseInt(matcher.group(1) + matcher.group(2) + matcher.group(3));
		ret.version = "v" + matcher.group(1) + "_" + matcher.group(2) + "_R" + matcher.group(3);
	 } else {
		version = Bukkit.getBukkitVersion().replaceAll("-.+", "");
		ret.version = (version.startsWith("v") ? version : "v" + version).replace(".", "_");
		ret.ver = Integer.parseInt(version.toLowerCase().replaceAll("\\D", ""));
	 }
	 if(value(ret.ver) != null) ret = value(ret.ver);
	 else if(value(ret.version) != null) ret = value(ret.version);
	}
	return exact = ret;
 }

 public static Version current() {
	if(current != null && (current != UNKNOWN || (current.ver != Integer.MAX_VALUE && current.version.equalsIgnoreCase("Unknown"))))
	 return current;
	String pack = Bukkit.getServer().getClass().getPackage().getName();
	String version = pack.substring(pack.lastIndexOf('.') + 1);
	Version ret = value(version.split("_R")[0]);
	if(ret == null) {
	 ret = Version.UNKNOWN;
	 if(version.matches("v\\d+_\\d+_R\\d+")) {
		ret.ver = Integer.parseInt(version.replace("v", "").split("R")[0].replace("_", ""));
		ret.version = version;
		return exact = ret;
	 }
	 Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)-R(\\d+)\\.(\\d+).+");
	 Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());
	 if(matcher.find()) {
		ret = value("v" + matcher.group(1) + "_" + matcher.group(2));
		if(ret != null) return current = ret;
		ret = Version.UNKNOWN;
		ret.ver = Integer.parseInt(matcher.group(1) + matcher.group(2) + "0");
		ret.version = "v" + matcher.group(1) + "_" + matcher.group(2);
	 } else {
		version = Bukkit.getBukkitVersion().replaceAll("-.+", "");
		ret.version = (version.startsWith("v") ? version : "v" + version).replace(".", "_");
		ret.ver = Integer.parseInt(version.toLowerCase().replaceAll("\\D", ""));
	 }
	 if(value(ret.ver) != null) ret = value(ret.ver);
	 else if(value(ret.version) != null) ret = value(ret.version);
	}
	return current = ret;
 }

 @Override
 public String toString() {
	return name() + "(versionId='" + ver + "', version='" + version + "')";
 }

 public static Version value(String versionId) {
	for(Version version : values())
	 if(versionId.equalsIgnoreCase(version.name()) || versionId.equalsIgnoreCase(version.getVersion()))
		return version;
	return null;
 }

 public static Version value(int versionId) {
	for(Version version : values())
	 if(versionId == version.ver)
		return version;
	return null;
 }

}