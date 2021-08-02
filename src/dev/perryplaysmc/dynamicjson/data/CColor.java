package dev.perryplaysmc.dynamicjson.data;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class CColor {
   public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
   public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
   public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile(COLOR_CHAR + "[0-9A-FK-ORX]", Pattern.CASE_INSENSITIVE);

   private static final String hexRegex = "(?:§[x](?:§[a-fA-F0-9]){6})";
   private static final Pattern HEX_PATTERN = Pattern.compile(hexRegex, Pattern.CASE_INSENSITIVE);

   private static final Map<Character, Pattern> CHAT_COLOR_PATTERN_CACHE = new HashMap<>();
   private static final Map<Character, Pattern> HEX_PATTERN_CACHE = new HashMap<>();
   private static final Map<Character, CColor> BY_CHAR = new HashMap<>();
   private static final Map<String, CColor> BY_NAME = new HashMap<>();

   public static final CColor BLACK = new CColor('0', "black", new Color(0));
   public static final CColor DARK_BLUE = new CColor('1', "dark_blue", new Color(170));
   public static final CColor DARK_GREEN = new CColor('2', "dark_green", new Color(43520));
   public static final CColor DARK_AQUA = new CColor('3', "dark_aqua", new Color(43690));
   public static final CColor DARK_RED = new CColor('4', "dark_red", new Color(11141120));
   public static final CColor DARK_PURPLE = new CColor('5', "dark_purple", new Color(11141290));
   public static final CColor GOLD = new CColor('6', "gold", new Color(16755200));
   public static final CColor GRAY = new CColor('7', "gray", new Color(11184810));
   public static final CColor DARK_GRAY = new CColor('8', "dark_gray", new Color(5592405));
   public static final CColor BLUE = new CColor('9', "blue", new Color(5592575));
   public static final CColor GREEN = new CColor('a', "green", new Color(5635925));
   public static final CColor AQUA = new CColor('b', "aqua", new Color(5636095));
   public static final CColor RED = new CColor('c', "red", new Color(16733525));
   public static final CColor LIGHT_PURPLE = new CColor('d', "light_purple", new Color(16733695));
   public static final CColor YELLOW = new CColor('e', "yellow", new Color(16777045));
   public static final CColor WHITE = new CColor('f', "white", new Color(16777215));
   public static final CColor MAGIC = new CColor('k', "obfuscated");
   public static final CColor BOLD = new CColor('l', "bold");
   public static final CColor STRIKETHROUGH = new CColor('m', "strikethrough");
   public static final CColor UNDERLINE = new CColor('n', "underline");
   public static final CColor ITALIC = new CColor('o', "italic");
   public static final CColor RESET = new CColor('r', "reset");
   private static int count = 0;
   private final String toString;
   private final String name;
   private final int ordinal;
   private final Color color;

   private CColor(char code, String name) {
      this(code, name, null);
   }

   private CColor(char code, String name, Color color) {
      this.name = name;
      this.toString = new String(new char[]{COLOR_CHAR, code});
      this.ordinal = count++;
      this.color = color;
      BY_CHAR.put(code, this);
      BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
   }

   private CColor(String name, String toString, int rgb) {
      this.name = name;
      this.toString = toString;
      this.ordinal = -1;
      this.color = new Color(rgb);
   }

   public int hashCode() {
      return 53 * 7 + Objects.hashCode(this.toString);
   }

   public boolean equals(Object obj) {
      if(this == obj) return true;
      else if(obj instanceof CColor) return Objects.equals(this.toString, ((CColor) obj).toString);
      return false;
   }

   public String toString() { return this.toString; }

   public static String stripColor(String input) {
      return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
   }

   public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
      Pattern pat = CHAT_COLOR_PATTERN_CACHE.getOrDefault(altColorChar, Pattern.compile(altColorChar + "([0-9a-fk-or])", Pattern.CASE_INSENSITIVE));
      if(!CHAT_COLOR_PATTERN_CACHE.containsKey(altColorChar)) CHAT_COLOR_PATTERN_CACHE.put(altColorChar, pat);
      return pat.matcher(textToTranslate).replaceAll(COLOR_CHAR + "$1");
   }

   public static String translateHex(char startChar, String textToTranslate) {
      Pattern pat = HEX_PATTERN_CACHE.getOrDefault(startChar, Pattern.compile(startChar + "[a-fA-F0-9]{6}", Pattern.CASE_INSENSITIVE));
      if(!HEX_PATTERN_CACHE.containsKey(startChar)) HEX_PATTERN_CACHE.put(startChar, pat);
      if(textToTranslate.contains(startChar + "")) {
         Matcher matcher = pat.matcher(textToTranslate);
         while(matcher.find()) {
            String color = "#" + textToTranslate.substring(matcher.start(), matcher.end()).substring(1);
            textToTranslate = textToTranslate.substring(0, matcher.start()) + CColor.fromHex(color) + textToTranslate.substring(matcher.end());
            matcher = pat.matcher(textToTranslate);
         }
      }
      return textToTranslate;
   }

   public static CColor getByChar(char code) {
      return BY_CHAR.get(code);
   }

   public static CColor of(Color color) {
      return fromHex("#" + String.format("%08x", color.getRGB()).substring(2));
   }

   public static CColor fromHex(String string) {
      Preconditions.checkArgument(string != null, "string cannot be null");
      if(string.startsWith("#") && string.length() == 7) {
         int rgb;
         try {
            rgb = Integer.parseInt(string.substring(1), 16);
         } catch (NumberFormatException var7) {
            throw new IllegalArgumentException("Illegal hex string " + string);
         }
         StringBuilder magic = new StringBuilder("§x");
         char[] chars = string.substring(1).toCharArray();
         for(char c : chars) magic.append('§').append(c);
         return new CColor(string, magic.toString(), rgb);
      } else {
         CColor defined = BY_NAME.get(string.toUpperCase());
         if(defined != null) return defined;
         defined = BY_CHAR.get(string.length() == 2 ? string.charAt(1) : string.charAt(0));
         if(defined != null) return defined;
         else throw new IllegalArgumentException("Could not parse CColor " + string);
      }
   }

   public static CColor fromName(String name) {
      Preconditions.checkNotNull(name, "Name is null");
      CColor defined = BY_NAME.get(name.toUpperCase());
      Preconditions.checkArgument(defined != null, "No enum constant " + CColor.class.getName() + "." + name);
      return defined;
   }

   public static CColor fromTranslated(String name) {
      Preconditions.checkNotNull(name, "Name is null");
      if(BY_NAME.containsKey(name.toUpperCase())) return BY_NAME.get(name.toUpperCase());
      if(name.length() >= 6) {
         return fromHex(changeHex(name));
      }
      char c = 'f';
      if(name.length() == 2)
         c = name.charAt(1);
      else if(name.length() == 1) c = name.charAt(0);
      CColor defined = BY_CHAR.get(c);
      Preconditions.checkArgument(defined != null, "No enum constant " + CColor.class.getName() + "." + name);
      return defined;
   }

   private static String changeHex(String hex) {
      Matcher matcher = HEX_PATTERN.matcher(hex);
      while(matcher.find())
         if((matcher.group(0) != null && !matcher.group(0).isEmpty())) hex = hex.replace(matcher.group(), "#" + matcher.group().replace("§", "").substring(1));
      return hex;
   }

   /**
    * @deprecated
    */
   @Deprecated
   public static CColor[] values() {
      return BY_CHAR.values().toArray(new CColor[BY_CHAR.values().size()]);
   }

   /**
    * @deprecated
    */
   @Deprecated
   public String name() {
      return this.getName().toUpperCase();
   }

   /**
    * @deprecated
    */
   @Deprecated
   public int ordinal() {
      Preconditions.checkArgument(this.ordinal >= 0, "Cannot get ordinal of hex color");
      return this.ordinal;
   }

   public String getName() {
      return this.name;
   }

   public Color getColor() {
      return this.color;
   }
}
