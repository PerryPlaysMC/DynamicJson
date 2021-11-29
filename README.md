# DynamicJson
Simple util for Json messages (Works on books too!)
 
  
 

 
 

# Usage
```java
DynamicJText test = new DynamicJText();//Create a new DynamicJText (optional args [String, DynamicJPart, nothing])

test.add("&cThis is an &eexample of &btranslated &dtext") //The text to display
  .onHover("You hovered over", "&cThis is an &eexample of &btranslated &dtext&r!") //Hover over the text to show this
  .suggest("You clicked on \"This is an example of translated text\"!") //Click this to set the text in your chatbar to this
  .insert(" You shift clicked \"This is an example of translated text\"!")//Shift + Click the text and it will append this to the end of your chatbar
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
<repositories>
 <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
 </repository>
</repositories>

<dependencies>
 <dependency>
  <groupId>com.github.PerryPlaysMC</groupId>
  <artifactId>DynamicJson</artifactId>
  <version>v1.0-RELEASE</version>
 </dependency>
</dependencies>
```
[![](https://jitpack.io/v/PerryPlaysMC/DynamicJson.svg)](https://jitpack.io/#PerryPlaysMC/DynamicJson)
