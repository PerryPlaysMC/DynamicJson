package dev.perryplaysmc.dynamicjson.plugin;

import dev.perryplaysmc.dynamicjson.DynamicJText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Owner: PerryPlaysMC
 * From: 01/2021-Now
 **/

public class DynamicPlugin extends JavaPlugin implements Listener {
  
  @Override
  public void onEnable() {
    DynamicJText test = new DynamicJText();
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
    System.out.println("{\"text\":\"You just leveled up\",\"clickEvent\":" +
           "{\"action\":\"suggest_command\",\"value\":\"/playerleveling stats\"},\"hoverEvent\":" +
           "{\"action\":\"show_text\",\"value\":" +
           "[{\"text\":\"You just leveled up to a new level\",\"color\":\"dark_red\"}]}}");
    System.out.println(test2);
    if(Bukkit.getOnlinePlayers().size() < 1) {
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
    test.send(player);
  }
}
