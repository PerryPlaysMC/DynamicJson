# DynamicJson
Simple util for Json messages (Works on books too!)
 
  
 

 
 

# Examples
```java
//Examples
public class DynamicJsonTest {

  public static void sendHelp(Player player) {
    DynamicJText text = new DynamicJText("&7------Help------");
    text.add("\n/jsontest help")
          .onHover("Shows this help message")
          .command("/jsontest help")
    .add("\n/jsontest gradient")
          .onHover("Show an example",
                   CColor.translateGradient("Of a gradient", CColor.AQUA, CColor.DARK_BLUE))
          .command("/jsontest gradient")
    .add("\n/jsontest parser")
          .onHover("Show an example",
                   "<gradient=\"red,blue\">Of the DynamicJson parser</gradient>")
          .command("/jsontest parser")
    .add("\n/jsontest parsejson")
          .onHover("Show an example",
                   "{\"text\":\"Of json parser\"}")
          .command("/jsontest parsejson");
    text.send(player); 
  }

  public static void sendParseJsonTest(Player player) {
      DynamicJText text = DynamicJText.fromJson("{\"text\":\"\",\"extra\":[{\"text\":\"------Help------\",\"color\":\"gray\"},{\"text\":\"\\n/jsontest help\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jsontest help\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Shows this help message\"}},{\"text\":\"\\n/jsontest gradient\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jsontest gradient\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Show an example\\n§x§5§5§f§f§f§fO§x§4§e§e§b§f§8f§x§4§7§d§7§f§1 §x§4§1§c§4§e§ba§x§3§a§b§0§e§4 §x§3§4§9§c§d§eg§x§2§d§8§9§d§7r§x§2§7§7§5§d§1a§x§2§0§6§2§c§ad§x§1§a§4§e§c§4i§x§1§3§3§a§b§de§x§0§d§2§7§b§7n§x§0§6§1§3§b§0t\"}},{\"text\":\"\\n/jsontest parser\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/jsontest parser\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Show an example\\n<gradient=\\\"red,blue\\\">Of the DynamicJson parser</gradient>\"}}]}");
      text.send(player);

  }

  public static void sendGradientTest(Player player) {
    {
      DynamicJText text = new DynamicJText();
      text.gradient(CColor.AQUA, CCoLOR.DARK_BLUE)
            .add("This is a gradient example")
              .onHover("This is the start of the gradient")
            .addDefault(" &fGradient break ")
            .add("Continue the gradient!")
              .onHover("This is a continuation of the gradient");
      text.send(player); 
    }
    {
      DynamicJText text = new DynamicJText();
      text.gradient(CColor.AQUA, CCoLOR.DARK_BLUE)
            .add("This is another gradient example")
              .onHover("This is the start of the gradient")
            .addDefault(" &fGradient break ")
            .add("Continue the gradient!")
              .onHover("This is a continuation of the gradient")
            .finish()
          .add(" Broke out of the gradient buider :D")
            .onHover("yay!");
      text.send(player); 
    }
  }

  public static void sendParserTest(Player player) {
    //Keys: [gradient, hover, command, suggest, copy, url, insert]
    //Pattern: <key="data">text</key>
    //gradient has an optional first argument [LEFT, RIGHT, CENTER] (where will the gradient center itself)
    DynamicJText text = DynamicJText.parse(
        "<gradient=\"left,red,blue\">" +
        "<command=\"say Hey\">" +
        "<hover=\":D\">" +
        "This is clickable and you can hover" +
        "</hover>" +
        " This is still clickable but no hover" +
        "</command>" +
        "<url=\"https://www.spigotmc.org/resources/authors/perryplaysmc.935728/\">"+
        "Click to go to my spigot resources!" +
        "</url>" +
        "</gradient>" +
        " &bExit the gradient!" =
        "<copy=\"Text to copy\">" +
        "Click to copy \"Text to copy\"" +
        "</copy>"
    );
    text.send(player); 
  }


}
```

# Maven
```maven
<repository>
 <id>jitpack.io</id>
 <url>https://jitpack.io</url>
</repository>
 
 
<dependency>
 <groupId>com.github.PerryPlaysMC</groupId>
 <artifactId>DynamicJson</artifactId>
 <version>v1.1.1</version>
</dependency>
```
[![](https://jitpack.io/v/PerryPlaysMC/DynamicJson.svg)](https://jitpack.io/#PerryPlaysMC/DynamicJson)
