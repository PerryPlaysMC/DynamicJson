package dev.perryplaysmc.dynamicjson;

import org.bukkit.Bukkit;

public enum Version {
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
   v1_18(1180), v1_18_R1(1181), UNKNOWN(Integer.MAX_VALUE, "Unknown");

   private int ver;
   private String version;

   Version(int ver) {
      this.ver = ver;
      this.version = name().toUpperCase().substring(1);
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

   public boolean isHigher(Version v) {
      return getVersionInt()>v.getVersionInt();
   }

   public static boolean isCurrentHigher(Version v) {
      if(v.name().contains("R"))
         return getCurrentVersionExact().isHigher(v);
      return getCurrentVersion().isHigher(v);
   }

   public static String getNMSPackage() {
      return "net.minecraft";
   }
   public static String getCBPackage() {
      return "org.bukkit.craftbukkit." + getCurrentVersionExact();
   }
  
  public static class CraftBukkit {
    public static Class<?> getClass(String name) {
      try {
        return Class.forName(getCBPackage() + "." + name);
      }catch (Exception e) {
        return null;
      }
    }
  }
  
  public static class Minecraft {
    public static Class<?> getClass(String clazz) {
      try {
        String nms = Version.isCurrentHigher(v1_16) ? getNMSPackage() : getNMSPackage() + ".server." + getCurrentVersionExact();
        if(!Version.isCurrentHigher(v1_16))
          if(clazz.contains(".")) return Class.forName(nms + "." + clazz.split("\\.")[clazz.split("\\.").length-1]);
        return Class.forName(nms + "." + clazz);
      }catch (Exception e) {
        return null;
      }
    }
  }

   private static Version current, exact;

   public static Version getCurrentVersionExact() {
      String pack =  Bukkit.getServer().getClass().getPackage().getName();
      String version = pack.substring(pack.lastIndexOf('.')+1);
      Version ret = value(version);
      if(ret == null) {
         ret = Version.UNKNOWN;
         ret.version = version.startsWith("v") ? version : "v" + version;
         ret.ver = Integer.parseInt(ret.version.substring(1).toLowerCase().replace(("_"),("")).replace(("r"),("")));
      }
      return ret;
   }

   public static Version getCurrentVersion() {
      String pack =  Bukkit.getServer().getClass().getPackage().getName();
      String version = pack.substring(pack.lastIndexOf('.')+1);
      Version ret = value(version.split("_R")[0]);
      if(ret == null) {
         ret = Version.UNKNOWN;
         ret.version = version.startsWith("v") ? version : "v" + version;
         ret.ver = Integer.parseInt(ret.version.substring(1).toLowerCase().split(("r"))[0].replace(("_"),("")));
      }
      return ret;
   }



   public static Version value(String versionId) {
      for(Version version : values())
         if(versionId.equalsIgnoreCase(version.name()) || versionId.equalsIgnoreCase(version.getVersion())) return version;
      return null;
   }

}