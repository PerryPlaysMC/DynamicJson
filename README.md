# DynamicJsonText
DynamicJsonText is a simple API to create highly compressed JSON Strings


To use, either 
 - Download the src/main/java and copy those files and paste them UNDER YOUR PACKAGE NAME in your project
 - Download the plugin-plugin and add that to your classpath and put it in the plugins folder
 - Download the plugin-compile and compile the source files into your project
 - Use maven
 
Repository
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```
Dependency
```xml
<dependency>
    <groupId>com.github.PerryPlaysMC</groupId>
    <artifactId>DynamicJSONText</artifactId>
    <version>1.3.0-Compile</version>
    <scope>provided</scope>
</dependency>
```


To use this api

```java

CommandSender sender = //A CommandSender
Player player = //A Player

new DynamicJText("&6&lHello"/*Optional (can be empty) required if you don't do .add before events/colors*/)
 .onHover("&bHey")//When they hover they see a Aqua "Hey"
 .command("gamemode creative")//Runs whatever is in here as a command (/ is optional)
 .add(" Some new text")//Adds new text, can be colored
 .onHover(new ItemStack(Material.STONE))//When the hover they see the ItemStack tooltip
 .chat("I clicked the text!")//When the click they say this message
.send(player, sender);//Sends to both the Player and CommandSender, or just pass one of the two in
```
