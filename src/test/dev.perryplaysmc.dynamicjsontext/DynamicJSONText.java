package dev.perryplaysmc.dynamicjsontext;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 01/2021-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/

public class DynamicJSONText extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        DynamicJText test = new DynamicJText();
        test.addTranslated("&cHello there!!! &b&mHow are you???&r&b&l I'm good!!!") //The text to display
                .onHover("Hello there") //Hover over the text to show this
                .suggest("Hello there") //Click this to set the text in your chatbar to this
                .insert(" Hi");//Shift + Click the text and it will append this to the end of your chatbar
        System.out.println(test); //Print it as JSON text to console
        Player player = Bukkit.getPlayer("PerryPlaysMC");
        if(player == null) return;
        test.send(player);
    }
}
