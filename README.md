# DynamicJson
Simple util for Json messages (Works on books too!)
 
  
 

 
 

# Usage
```java
DynamicJText test = new DynamicJText();//Create a new DynamicJText (optional args [String, DynamicJPart, nothing])

test.addTranslated("&cHello there!!! &b&mHow are you???&r&b&l I'm good!!!") //The text to display
       .onHover("Hello there") //Hover over the text to show this
       .suggest("Hello there") //Click this to set the text in your chatbar to this
       .insert(" Hi")//Shift + Click the text and it will append this to the end of your chatbar
       .addTranslated("&bHehe, item")
       .onHover((ItemStack) null); //Show an item when hovering
System.out.println(test); //Print it as JSON text to console


DynamicJText test2 = DynamicJText.fromJson(
       "{\"text\":\"You just leveled up\",\"clickEvent\":" +
              "{\"action\":\"suggest_command\",\"value\":\"/playerleveling stats\"},\"hoverEvent\":" +
              "{\"action\":\"show_text\",\"value\":" +
              "[{\"text\":\"You just leveled up to a new level\",\"color\":\"dark_red\"}]}}"
);//parse from Json

System.out.println(test2);


if(Bukkit.getOnlinePlayers().isEmpty()) {
  test.send(Bukkit.getConsoleSender());
  return;
}


List<Player> online = new ArrayList<>(Bukkit.getOnlinePlayers());
Player player = online.get(new Random().nextInt(online.size()));

if(player == null) {
  test.send(Bukkit.getConsoleSender());
  return;
}

test2.send(player);
test.send(player);```
