package dev.perryplaysmc.dynamicjson.data;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
   public static final CColor ORANGE = new CColor('6', "orange", new Color(16755200));
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
      this.name = name.replace(" ","_").toLowerCase();
      this.toString = new String(new char[]{COLOR_CHAR, code});
      this.ordinal = count++;
      this.color = color;
      BY_CHAR.put(code, this);
      BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
   }
   private CColor(char code, String name, String toString, int color) {
      this.name = name.replace(" ","_").toLowerCase();
      this.toString = toString;
      this.ordinal = count++;
      this.color = new Color(color);
      BY_CHAR.put(code, this);
      BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
   }

   private CColor(String name, String toString, int rgb) {
      this.name = name.replace(" ","_").toLowerCase();
      this.toString = toString;
      this.ordinal = -1;
      this.color = new Color(rgb);
      BY_NAME.put(name.toUpperCase(Locale.ROOT), this);
   }

   public static void registerColor(char code, String name, Color color) {
      name = name.replace(" ","_").toLowerCase();
      if(BY_CHAR.containsKey(code)||BY_NAME.containsKey(name.replace(" ","_").toUpperCase()))return;
      StringBuilder magic = new StringBuilder("§x");
      char[] chars = Integer.toHexString(color.getRGB()).substring(2).toCharArray();
      for(char c : chars) magic.append('§').append(c);
      new CColor(code,name,magic.toString(),color.getRGB());
   }

   public int hashCode() {
      return 53 * 7 + Objects.hashCode(this.toString);
   }

   public boolean equals(Object obj) {
      if(this == obj) return true;
      else if(obj instanceof CColor) return Objects.equals(this.toString, ((CColor) obj).toString);
      return false;
   }

   public String toString() {
      return this.toString;
   }

   public static String stripColor(String input) {
      return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
   }

   public static String translateCommon(String text) {
      return translateAlternateColorCodes('&',translateHex('#',text));
   }

   public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
      String pattern = altColorChar + "(["+BY_CHAR.keySet().stream().map((c)->c+"").collect(Collectors.joining())+"])";
      Pattern pat = CHAT_COLOR_PATTERN_CACHE.get(altColorChar);
      if(!CHAT_COLOR_PATTERN_CACHE.containsKey(altColorChar)||pat==null||!CHAT_COLOR_PATTERN_CACHE.get(altColorChar).pattern().equals(pattern))
         CHAT_COLOR_PATTERN_CACHE.put(altColorChar,pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
      Matcher matcher = pat.matcher(textToTranslate);
      while(matcher.find()) {
         textToTranslate = textToTranslate.substring(0,matcher.start()) + fromTranslated(matcher.group()) + textToTranslate.substring(matcher.end());
         matcher = pat.matcher(textToTranslate);
      }
      return textToTranslate;
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

   public static boolean similarTo(Color c1, Color c2){
      double distance = (c1.getRed() - c2.getRed())*(c1.getRed() - c2.getRed()) +
         (c1.getGreen() - c2.getGreen())*(c1.getGreen() - c2.getGreen()) +
         (c1.getBlue() - c2.getBlue())*(c1.getBlue() - c2.getBlue());
      return distance <= 6;
   }

   public static double getSimilarity(Color c1, Color c2) {
      float diffRed = Math.abs(c1.getRed() - c2.getRed()) / 255f;
      float diffGreen = Math.abs(c1.getGreen() - c2.getGreen()) / 255f;
      float diffBlue = Math.abs(c1.getBlue() - c2.getBlue()) / 255f;
      return (diffRed + diffGreen + diffBlue) / 3 * 100;
   }

   public static List<Color> createGradient(int steps, Color start, Color... gradients) {
      List<Color> gradientList = new ArrayList<>();
      Color currentColor = start;
      int index = 0;
      steps = (steps / gradients.length);
      for(Color color : gradients){
         for(float i = 0; i < steps; i++) {
            float ratio = (i / steps);
            int red   = (int) (color.getRed()   * ratio + currentColor.getRed()   * (1 - ratio));
            int green = (int) (color.getGreen() * ratio + currentColor.getGreen() * (1 - ratio));
            int blue  = (int) (color.getBlue()  * ratio + currentColor.getBlue()  * (1 - ratio));
            Color stepColor = new Color(red, green, blue);
            if(!gradientList.contains(stepColor)) gradientList.add(stepColor);
         }
         currentColor = color;
      }
      return gradientList;
   }

   public static String translateGradient(String text, GradientCenter center, int steps, int initialStep, Color... transitions) {
      Validate.isTrue(transitions.length > 1, "You must have at least 2 colors for the gradient to work!");
      StringBuilder newText = new StringBuilder();
      List<Color> gradient = CColor.createGradient(center == GradientCenter.LEFT || center == GradientCenter.RIGHT ?
         steps - (steps/2) : steps, transitions[0], Arrays.copyOfRange(transitions, 1, transitions.length));
      int increment = 1;
      int index = initialStep < 0 ? 0 : initialStep >= gradient.size() ? gradient.size() - 1 : initialStep;
      int stepMin = steps/2;
      if(center == GradientCenter.RIGHT) index = initialStep < stepMin ? 0 : index - stepMin;
      int textIndex = initialStep;
      for(char c : text.toCharArray()) {
         newText.append(CColor.of(gradient.get(index < 0 ? 0 : index >= gradient.size() ? gradient.size()-1 : index))).append(c);
         if(center == GradientCenter.RIGHT){
            if(textIndex >= stepMin)index+=increment;
         }else index+=increment;
         if(index >= gradient.size() || index < 0) {
            if(center == GradientCenter.MIDDLE) {
               increment = index < 0 ? 1 : -1;
               index += increment;
            } else index = index >= gradient.size() ? gradient.size()-1 : 0;
         }
         textIndex++;
      }
      return newText.toString();
   }

   public static String translateGradient(String text, int steps, int initialStep, Color... transitions) {
      return translateGradient(text, GradientCenter.MIDDLE, steps, initialStep, transitions);
   }

   public static String translateGradient(String text, GradientCenter center, int initialStep, Color... transition) {
      return translateGradient(text, center, text.length(), initialStep, transition);
   }

   public static String translateGradient(String text, int initialStep, Color... transition) {
      return translateGradient(text, text.length(), initialStep, transition);
   }

   public static String translateGradient(String text, GradientCenter center, Color... transition) {
      return translateGradient(text, center, 0, transition);
   }

   public static String translateGradient(String text, Color... transition) {
      return translateGradient(text, 0, transition);
   }

   public static String translateGradient(String text, GradientCenter center, int steps, int initialStep, CColor... transitions) {
      return translateGradient(text, center, steps, initialStep, toJavaColor(transitions));
   }

   public static String translateGradient(String text, int steps, int initialStep, CColor... transitions) {
      return translateGradient(text, GradientCenter.MIDDLE, steps, initialStep, transitions);
   }

   public static String translateGradient(String text, GradientCenter center, int initialStep, CColor... transitions) {
      return translateGradient(text, center, text.length(), initialStep, transitions);
   }

   public static String translateGradient(String text, int initialStep, CColor... transitions) {
      return translateGradient(text, text.length(), initialStep, transitions);
   }

   public static String translateGradient(String text, GradientCenter center, CColor... transitions) {
      return translateGradient(text, center, 0, transitions);
   }

   public static String translateGradient(String text, CColor... transitions) {
      return translateGradient(text, 0, transitions);
   }

   public static Color[] toJavaColor(CColor... oldColors) {
      java.awt.Color[] newColors = new java.awt.Color[oldColors.length];
      for(int i = 0; i < newColors.length; i++) newColors[i] = oldColors[i].getColor();
      return newColors;
   }


   public static CColor of(Color color) {
      return fromHex("#" + String.format("%08x", color.getRGB()).substring(2));
   }

   public static CColor of(org.bukkit.Color color) {
      return fromHex("#" + String.format("%08x", color.asRGB()).substring(2));
   }

   public static CColor fromHex(String string) {
      Preconditions.checkArgument(string != null, "string cannot be null");
      if(string.startsWith("#") && string.length() == 7) {
         CColor defined = BY_NAME.get(string);
         if(defined != null) return defined;
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
      } else{
         CColor defined = BY_NAME.get(string.toUpperCase());
         if(defined != null) return defined;
         defined = BY_CHAR.get(string.length() == 2 ? string.charAt(1) : string.charAt(0));
         if(defined != null) return defined;
         else throw new IllegalArgumentException("Could not parse CColor " + string);
      }
   }

   public static Color colorFromHex(String string) {
      Preconditions.checkArgument(string != null, "string cannot be null");
      Preconditions.checkArgument(string.length() == 7, "invalid hex string");
      if(string.startsWith("#")) string = string.substring(1);
      int rgb;
      try {
         rgb = Integer.parseInt(string, 16);
      } catch (NumberFormatException var7) {
         throw new IllegalArgumentException("Illegal hex string " + string);
      }
      return new Color(rgb);
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
         if((matcher.group(0) != null && !matcher.group(0).isEmpty()))
            hex = hex.replace(matcher.group(), "#" + matcher.group().replace("§", "").substring(1));
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

   public enum GradientCenter {
      LEFT, RIGHT, MIDDLE;

      public static GradientCenter fromName(String name) {
         for(GradientCenter value : values())
            if(value.name().equalsIgnoreCase(name))return value;
         return null;
      }

   }
}
