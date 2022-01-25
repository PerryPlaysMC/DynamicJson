# DynamicJson
Simple util for Json messages (Works on books too!)
 
  
 

 
 

# Usage
```java
DynamicJText test = new DynamicJText();//Create a new DynamicJText (optional args [String, DynamicJPart, nothing])

test.add("&cThis is an &eexample of &btranslated &dtext") //The text to display
  .onHover("You hovered over", "&cThis is an &eexample of &btranslated &dtext&r!") //Hover over the text to show this
  .suggest("You clicked on \"This is an example of translated text\"!") //Click this to set the text in your chatbar to this
  .insert(" You shift clicked \"This is an example of translated text\"!")//Shift + Click the text and it will append this to the end of your chatbar
  .addGradient("This text will have a gradient of Red to Orange", Color.RED, Color.ORANGE)
  .add("&3Displays an item on hover")//Append text as a new part, all previous click & hover events are cleared
  .onHover((ItemStack) null)//Item tooltip (if null defaults to Air)
  .addPlain("This text won't translate &bColor &cCodes"); //This will append plain text, meaning it won't translate &<code>'s
System.out.println(test); //Print it as JSON text to console


DynamicJText test2 = DynamicJText.fromJson(
 "{\"text\":\"You just leveled up\",\"clickEvent\":" +
 "{\"action\":\"suggest_command\",\"value\":\"/playerleveling stats\"},\"hoverEvent\":" +
 "{\"action\":\"show_text\",\"value\":" +
 "[{\"text\":\"You just leveled up to a new level\",\"color\":\"dark_red\"}]}}"
);//parse from Json


Player player = ...;
test.send(player);
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
new DynamicJText().addGradient("This is a long test message", CColor.AQUA, CColor.GREEN).send(<player>);
```
<img width="280" alt="Screen_Shot_2022-01-25_at_3 06 16_PM" src="https://user-images.githubusercontent.com/25993701/151052660-63d8b801-d8f9-4fb0-a8be-b1d003f6a619.png">

So, as you can see there's a bit of a difference (but not in the text you read ;D)
