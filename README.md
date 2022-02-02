# DynamicJson
Simple util for Json messages (Works on books too!)
 
[Join the discord to be notified of updates!](https://discord.gg/QuG8R6c3ry)
 

 
 

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
    DynamicJText text = DynamicJText.parse(
        "<gradient=\"red,blue\">" +
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
 <version>v1.2.0</version>
</dependency>
```
[![](https://jitpack.io/v/PerryPlaysMC/DynamicJson.svg)](https://jitpack.io/#PerryPlaysMC/DynamicJson)

# Why bother using DynamicJson?


Well it significantly shortens the json that is sent to the client (Especially with gradients)

I'll show you an example with gradients using normal CommandSender#sendMessage and DynamicJson


This is Bukkit/Spigot when sending a json message:
```json
{"extra":[
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFFF","text":"T"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FEFA","text":"h"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#54FFF2","text":"i"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFEA","text":"s"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#54FFE2","text":" "},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FEDA","text":"i"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFD2","text":"s"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFCA","text":" "},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFC2","text":"a"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFBA","text":" "},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFB2","text":"l"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFAA","text":"o"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FFA5","text":"n"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF9D","text":"g"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF95","text":" "},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF8D","text":"t"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF85","text":"e"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF7D","text":"s"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF75","text":"t"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF6D","text":" "},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF65","text":"m"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF5D","text":"e"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF5D","text":"s"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF65","text":"s"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF6D","text":"s"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF75","text":"a"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF7D","text":"g"},
{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"color":"#55FF85","text":"e"}
],"text":""}
```
Code used ^:
```java
Player#sendMessage(CColor.translateGradient("This is a long test messsage", CColor.AQUA, CColor.GREEN));
```
<img width="294" alt="Screen_Shot_2022-01-25_at_3 00 01_PM" src="https://user-images.githubusercontent.com/25993701/151052554-abee4fba-c0e6-4663-929a-1851e37bc14c.png">



And this is DynamicJson:
```json
{"extra":[
{"color":"#55ffff","text":"Thi"},
{"color":"#55ffe9","text":"s i"},
{"color":"#55ffd0","text":"s a"},
{"color":"#55ffb6","text":" lo"},
{"color":"#55ff9d","text":"ng "},
{"color":"#55ff83","text":"tes"},
{"color":"#55ff6a","text":"t mess"},
{"color":"#55ff83","text":"age"}
],"text":""}
```
Code used ^:
```java
new DynamicJText().gradient("This is a long test message", CColor.AQUA, CColor.GREEN).finish().send(<player>);
```
<img width="280" alt="Screen_Shot_2022-01-25_at_3 06 16_PM" src="https://user-images.githubusercontent.com/25993701/151052660-63d8b801-d8f9-4fb0-a8be-b1d003f6a619.png">

So, as you can see there's a bit of a difference (but not in the text you read ;D)
