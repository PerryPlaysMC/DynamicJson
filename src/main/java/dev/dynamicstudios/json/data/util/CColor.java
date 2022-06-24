package dev.dynamicstudios.json.data.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CColor {

  private static double COLOR_MATCH_STRENGTH = 92.5;

  public static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
  public static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
  public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile(COLOR_CHAR + "[\\dA-FK-ORX]", Pattern.CASE_INSENSITIVE);
  public static final Pattern STRIP_COLOR_ONLY_PATTERN = Pattern.compile("(?!"+COLOR_CHAR+"[lmnkor])"+COLOR_CHAR + "[\\da-fk-ox]",
    Pattern.CASE_INSENSITIVE);

  private static final String hexRegex = "§x(?:§[a-fA-F\\d]){6}";
  private static final Pattern HEX_PATTERN = Pattern.compile(hexRegex, Pattern.CASE_INSENSITIVE);

  private static final Set<Function<String, String>> COLOR_TRANSLATE_FUNCTIONS = new HashSet<>();
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
  public static final CColor ORANGE = new CColor('g', "orange", new Color(16755200));
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
  public static final CColor NONE = new CColor("none", new Color(0x0000000, true));
  private static int count = 0;
  private final String toString;
  private final String name;
  private final int ordinal;
  private final Color color, grayScale;
  private boolean hex = false;
  private boolean isColor = true;
  private final float hue, saturation, brightness;
  private final float[] lab;

  private CColor(String toString, String name, int ordinal, Color color, float[] hsl) {
    this.toString = toString;
    this.name = name.toLowerCase().replace(" ","_");
    this.ordinal = ordinal;
    this.color = color;
    int x = Math.max(red(), Math.max(green(), blue()));
    this.grayScale = new Color(x,x,x);
    this.hue = hsl[0];
    this.saturation = hsl[1];
    this.brightness = hsl[2];
    lab = rgbToLab(color);
    BY_NAME.put(name.replace(" ","_").toLowerCase(Locale.ROOT), this);
  }

  private CColor(char code, String name) {
    this(code, name, new Color(0x0000000, true));
    isColor = false;
  }

  private CColor(char code, String name, Color color) {
    this(new String(new char[]{COLOR_CHAR, code}), name, count++, color, rgbToHsl(color.getRGB()));
    BY_CHAR.put(code, this);
  }

  private CColor(char code, String name, String toString, int color) {
    this(toString, name, count++, new Color(color), rgbToHsl(color));
    this.hex = true;
    BY_CHAR.put(code, this);
  }

  private CColor(String name, Color rgb) {
    this("", name, -1, rgb, rgbToHsl(rgb.getRGB()));
  }


  private CColor(String name, String toString, int rgb) {
    this(toString, name, -1, new Color(rgb), rgbToHsl(rgb));
  }

  public boolean isColor() {
    return isColor;
  }

  public boolean isHex() {
    return hex;
  }

  public int hashCode() {
    return 53 * 7 + Objects.hashCode(this.toString);
  }

  public boolean equals(Object obj) {
    if(this == obj) return true;
    else if(obj instanceof CColor) return Objects.equals(this.toString, ((CColor) obj).toString);
    return false;
  }

  public double similarity(Color other) {
    return similarity(this.getColor(), other);
  }

  public double similarity(CColor other) {
    return similarity(this.lab, other.lab);
  }

  public double brightnessModifier() {
    double modifier = grayScale.getGreen() >175 ? 1.5 : grayScale.getGreen() < 65 ? -1.5 : 0;
    if(brightness() <= 30)
      return -2.5 + modifier;
    if(brightness() >= 75)
      return 2.5 + modifier;
    return modifier;
  }

  public boolean compare(CColor other) {
    return similarity(other) >= (COLOR_MATCH_STRENGTH + other.brightnessModifier());
  }

  public String name() {
    return this.getName().toUpperCase();
  }

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

  public String rgbString() {
    return this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue();
  }
  public String hslString() {
    return this.hue() + ", " + this.saturation() + ", " + this.brightness();
  }



  public String hexString() {
    String hex = Integer.toHexString(this.color.getRGB());
    if(hex.length() > 2)
      return "#" + Integer.toHexString(this.color.getRGB()).substring(2);
    else return "#" + hex;
  }
  public float[] toHSL() {
    float[] hsl = new float[3];
    rgbToHsl(this.color.getRGB(), hsl);
    return hsl;
  }

  public int red() {
    return color == null ? 0 : color.getRed();
  }

  public int green() {
    return color == null ? 0 : color.getGreen();
  }

  public int blue() {
    return color == null ? 0 : color.getBlue();
  }

  public float hue() {
    return hue;
  }

  public float saturation() {
    return saturation;
  }

  public float brightness() {
    return brightness;
  }

  public String toString() {
    return this.toString;
  }

  public static CColor[] values() {
    return BY_CHAR.values().toArray(new CColor[BY_CHAR.values().size()]);
  }

  public static CColor registerColor(char code, String name, Color color) {
    name = name.replace(" ", "_").toLowerCase();
    if(BY_CHAR.containsKey(code) || BY_NAME.containsKey(name.replace(" ", "_").toUpperCase()))
      return BY_CHAR.containsKey(code) ? BY_CHAR.get(code) : BY_NAME.get(name.replace(" ", "_").toUpperCase());
    StringBuilder magic = new StringBuilder("§x");
    char[] chars = Integer.toHexString(color.getRGB()).substring(2).toCharArray();
    for(char c : chars) magic.append('§').append(c);
    return new CColor(code, name, magic.toString(), color.getRGB());
  }

  public static String stripColor(String input) {
    return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
  }
  public static String stripColorKeepStyles(String input) {
    return input == null ? null : STRIP_COLOR_ONLY_PATTERN.matcher(input).replaceAll("");
  }

  public static String translateStyles(String text) {
    return text.replaceAll("&([lmnkor])", "§$1");
  }

  public static void registerColorTranslator(Function<String, String> colorTranslator) {
    COLOR_TRANSLATE_FUNCTIONS.add(colorTranslator);
  }

  public static String translateCommon(String text) {
    for(Function<String, String> translateFunction : COLOR_TRANSLATE_FUNCTIONS) text = translateFunction.apply(text);
    return translateAlternateColorCodes('&', translateHex('#', text));
  }

  public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
    String pattern = altColorChar + "([" + BY_CHAR.keySet().stream().map((c) -> c + "").collect(Collectors.joining()) + "])";
    Pattern pat = CHAT_COLOR_PATTERN_CACHE.get(altColorChar);
    if(!CHAT_COLOR_PATTERN_CACHE.containsKey(altColorChar) || pat == null || !CHAT_COLOR_PATTERN_CACHE.get(altColorChar).pattern().equals(pattern))
      CHAT_COLOR_PATTERN_CACHE.put(altColorChar, pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
    Matcher matcher = pat.matcher(textToTranslate);
    while(matcher.find()) {
      textToTranslate = textToTranslate.substring(0, matcher.start()) + fromTranslated(matcher.group()) + textToTranslate.substring(matcher.end());
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


  public static double similarity(CColor c1, CColor c2) {
    if(c1 == null || c2 == null) return c1 == c2 ? 100 : 0;
    return 100.0 - distance(c1.lab,c2.lab);
  }

  public static double similarity(Color c1, Color c2) {
    if(c1 == null || c2 == null) return c1 == c2 ? 100 : 0;
    return 100.0 - distance(c1,c2);
  }

  public static double similarity(float[] c1, float[] c2) {
    if(c1 == null || c2 == null) return c1 == c2 ? 100 : 0;
    return 100.0 - distance(c1,c2);
  }
  public static double distance(Color target, Color control) {
    float[] a = rgbToLab(target), b = rgbToLab(control);
    double L = a[0] - b[0], A = a[1] - b[1], B = a[2] - b[2];

    return Math.round(Math.sqrt((L * L) + (A * A) + (B * B)));
  }
  public static double distance(float[] target, float[] control) {
    double L = target[0] - control[0], A = target[1] - control[1], B = target[2] - control[2];
    return Math.round(Math.sqrt((L * L) + (A * A) + (B * B)));
  }
  public static List<CColor> createGradient(int steps, CColor... gradients) {
    List<CColor> gradientList = new ArrayList<>();
    CColor currentColor = gradients[0];
    int index = 0;
    steps /= (gradients.length-1);
    for(CColor color : gradients) {
      if(currentColor == color) continue;
      for(float i = 0; i < steps; i++) {
        float ratio = (i / steps);
        int red = (int) (color.red() * ratio + currentColor.red() * (1 - ratio));
        int green = (int) (color.green() * ratio + currentColor.green() * (1 - ratio));
        int blue = (int) (color.blue() * ratio + currentColor.blue() * (1 - ratio));
        CColor stepColor = CColor.of(new Color(red, green, blue));
        if(!gradientList.contains(stepColor)) gradientList.add(stepColor);
      }
      currentColor = color;
    }
    return gradientList;
  }

  public static String translateGradient(String text, GradientCenter center, int steps, int initialStep, CColor... transitions) {
    Validate.isTrue(transitions.length > 1, "You must have at least 2 colors for the gradient to work!");
    StringBuilder newText = new StringBuilder();
    List<CColor> gradient = CColor.createGradient(center == GradientCenter.LEFT || center == GradientCenter.RIGHT ?
      steps - (steps / 2) : steps, transitions);
    int increment = 1;
    int index = initialStep < 0 ? 0 : initialStep >= gradient.size() ? gradient.size() - 1 : initialStep;
    int stepMin = steps / 2;
    if(center == GradientCenter.RIGHT) index = initialStep < stepMin ? 0 : index - stepMin;
    int textIndex = initialStep;
    StringBuilder style = new StringBuilder();
    String reset = "";
    char[] chars = text.toCharArray();
    for(int i = 0; i < chars.length; i++) {
      char c = chars[i];
      if(c == ' ' || c == '\n' || (c+"").matches("\\s")) {
        newText.append(c);
        continue;
      }
      if(c == '§') {
        if(i+1 >= chars.length)continue;
        char next = chars[i+1];
        if("lmnokr".contains(next+"")&&!style.toString().contains("§"+next)) style.append(c).append(next);
        if(style.toString().toLowerCase().endsWith("§r"))style = new StringBuilder("§r");
        i++;
        continue;
      }
      int colorIndex = index < 0 ? 0 : index >= gradient.size() ? gradient.size() - 1 : index;
      String color = (style.toString().startsWith("§r") ? "§r" : "") + gradient.get(colorIndex).toString() +
        (style.toString().startsWith("§r") ? style.substring(2) : style.toString());
      newText.append(color).append(c);
      if(center == GradientCenter.RIGHT) {
        if(textIndex >= stepMin) index += increment;
      } else index += increment;
      if(index >= gradient.size() || index < 0) {
        if(center == GradientCenter.MIDDLE) {
          increment = index < 0 ? 1 : -1;
          index += increment;
        } else index = index >= gradient.size() ? gradient.size() - 1 : 0;
      }
      textIndex++;
    }
    return newText.toString();
  }


  public static String translateGradient(String text, GradientCenter center, int initialStep, CColor... transitions) {
    return translateGradient(text, center, text.replaceAll(("\\s"),("")).length(), initialStep, transitions);
  }

  public static String translateGradient(String text, int steps, int initialStep, CColor... transitions) {
    return translateGradient(text, GradientCenter.MIDDLE, steps, initialStep, transitions);
  }

  public static String translateGradient(String text, int initialStep, CColor... transitions) {
    return translateGradient(text, text.replaceAll(("\\s"),("")).length(), initialStep, transitions);
  }

  public static String translateGradient(String text, CColor... transitions) {
    return translateGradient(text, 0, transitions);
  }

  public static String translateGradient(String text, GradientCenter center, CColor... transitions) {
    return translateGradient(text, center, 0, transitions);
  }


  public static CColor of(int rgb) {
    return of(new Color(rgb));
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
    } else {
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
      return fromHex(resetHex(name));
    }
    char c = 'f';
    if(name.length() >= 2) c = name.charAt(1);
    else if(name.length() == 1) c = name.charAt(0);
    CColor defined = BY_CHAR.get(c);
    Preconditions.checkArgument(defined != null, "No enum constant " + CColor.class.getName() + "." + name);
    return defined;
  }

  public static boolean isColor(String name) {
    Preconditions.checkNotNull(name, "Name is null");
    try {
      return fromTranslated(name) != null;
    } catch (Exception e) {
      return false;
    }
  }

  public static String resetHex(String hex) {
    Matcher matcher = HEX_PATTERN.matcher(hex);
    while(matcher.find())
      if((matcher.group(0) != null && !matcher.group(0).isEmpty()))
        hex = hex.replace(matcher.group(), "#" + matcher.group().replace("§", "").substring(1));
    return hex;
  }

  private static void rgbToHsl(int rgb, float[] hsl) {
    float r = ((0x00ff0000 & rgb) >> 16) / 255.f;
    float g = ((0x0000ff00 & rgb) >> 8) / 255.f;
    float b = ((0x000000ff & rgb)) / 255.f;
    float max = Math.max(Math.max(r, g), b);
    float min = Math.min(Math.min(r, g), b);
    float c = max - min;

    float h_ = 0.f;
    if(c == 0)h_ = 0;
    else if(max == r) {
      h_ = (g - b) / c;
      if(h_ < 0) h_ += 6.f;
    } else if(max == g) h_ = (b - r) / c + 2.f;
    else if(max == b) h_ = (r - g) / c + 4.f;
    float h = 60.f * h_;

    float l = (max + min) * 0.5f;
    float s;
    if(c == 0) s = 0.f;
    else s = c / (1 - Math.abs(2.f * l - 1.f));

    hsl[0] = h;
    hsl[1] = s * 100f;
    hsl[2] = l * 100f;
  }

  private static float[] rgbToHsl(int rgb) {
    float[] hsl = new float[3];
    float r = ((0x00ff0000 & rgb) >> 16) / 255.f;
    float g = ((0x0000ff00 & rgb) >> 8) / 255.f;
    float b = ((0x000000ff & rgb)) / 255.f;
    float max = Math.max(Math.max(r, g), b);
    float min = Math.min(Math.min(r, g), b);
    float c = max - min;

    float h_ = 0.f;
    if(c == 0)h_ = 0;
    else if(max == r) {
      h_ = (g - b) / c;
      if(h_ < 0) h_ += 6.f;
    } else if(max == g) h_ = (b - r) / c + 2.f;
    else if(max == b) h_ = (r - g) / c + 4.f;
    float h = 60.f * h_;

    float l = (max + min) * 0.5f;
    float s = c == 0 ? 0f : c / (1-Math.abs(2.f * l - 1f));
    hsl[0] = h % 360;
    hsl[1] = s * 100f;
    hsl[2] = l * 100f;
    return hsl;
  }


  private static float[] rgbToLab(Color color) {
    int R = color.getRed(), G = color.getGreen(), B = color.getBlue();
    float r, g, b, X, Y, Z, xr, yr, zr;

    // D65/2°
    float Xr = 95.047f;
    float Yr = 100.0f;
    float Zr = 108.883f;


    // --------- RGB to XYZ ---------//

    r = R/255.0f;
    g = G/255.0f;
    b = B/255.0f;

    if (r > 0.04045)
      r = (float) Math.pow((r+0.055)/1.055,2.4);
    else
      r = r/12.92f;

    if (g > 0.04045)
      g = (float) Math.pow((g+0.055)/1.055,2.4);
    else
      g = g/12.92f;

    if (b > 0.04045)
      b = (float) Math.pow((b+0.055)/1.055,2.4);
    else
      b = b/12.92f ;

    r*=100;
    g*=100;
    b*=100;

    X =  0.4124f*r + 0.3576f*g + 0.1805f*b;
    Y =  0.2126f*r + 0.7152f*g + 0.0722f*b;
    Z =  0.0193f*r + 0.1192f*g + 0.9505f*b;


    // --------- XYZ to Lab --------- //

    xr = X/Xr;
    yr = Y/Yr;
    zr = Z/Zr;

    if ( xr > 0.008856 )
      xr =  (float) Math.pow(xr, 1/3.);
    else
      xr = (float) ((7.787 * xr) + 16 / 116.0);

    if ( yr > 0.008856 )
      yr =  (float) Math.pow(yr, 1/3.);
    else
      yr = (float) ((7.787 * yr) + 16 / 116.0);

    if ( zr > 0.008856 )
      zr =  (float) Math.pow(zr, 1/3.);
    else
      zr = (float) ((7.787 * zr) + 16 / 116.0);


    float[] lab = new float[3];

    lab[0] = (116*yr)-16;
    lab[1] = 500*(xr-yr);
    lab[2] = 200*(yr-zr);

    return lab;

  }

  public static double colorMatchStrength() {
    return COLOR_MATCH_STRENGTH;
  }

  public static void colorMatchStrength(double COLOR_MATCH_STRENGTH) {
    CColor.COLOR_MATCH_STRENGTH = COLOR_MATCH_STRENGTH;
  }

  public enum GradientCenter {
    LEFT, RIGHT, MIDDLE;

    public static GradientCenter fromName(String name) {
      for(GradientCenter value : values())
        if(value.name().equalsIgnoreCase(name)) return value;
      return null;
    }

  }
}