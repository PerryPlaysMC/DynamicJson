package dev.perryplaysmc.dynamicjson;

import dev.perryplaysmc.dynamicjson.data.CColor;
import dev.perryplaysmc.dynamicjson.data.DynamicClickAction;
import dev.perryplaysmc.dynamicjson.data.DynamicHoverAction;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 01/2022
 **/

public class GradientBuilder {
  
  private List<DynamicJPart> gradients;
  private CColor[] colors;
  private DynamicJText jText;
  private CColor.GradientCenter gradientCenter = CColor.GradientCenter.CENTER;
  
  protected static GradientBuilder create(DynamicJText jText, CColor... colors) {
    return new GradientBuilder(jText, colors);
  }
  
  private GradientBuilder(DynamicJText jText, CColor[] colors) {
    this.jText = jText;
    this.colors = colors;
    this.gradients = new ArrayList<>();
  }
  
  public GradientBuilder center(CColor.GradientCenter gradientCenter) {
    this.gradientCenter = gradientCenter;
    return this;
  }
  
  public GradientBuilder add(String text) {
    gradients.add(new DynamicJPart(text));
    gradients.get(gradients.size()-1).isGradient = true;
    return this;
  }
  
  public GradientBuilder addDefault(String text) {
    gradients.add(new DynamicJPart(text));
    return this;
  }
  
  public GradientBuilder onHover(ItemStack item) {
    Validate.notNull(item);
    if(gradients.size() > 0) gradients.get(gradients.size()-1).onHover(item);
    return this;
  }
  
  public GradientBuilder onHover(Entity entity) {
    Validate.notNull(entity);
    if(gradients.size() > 0) gradients.get(gradients.size()-1).onHover(entity);
    return this;
  }
  
  public GradientBuilder onHover(String... text) {
    if(gradients.size() > 0) gradients.get(gradients.size()-1).onHover(text);
    return this;
  }
  
  public GradientBuilder onHoverPlain(String... text) {
    if(gradients.size() > 0) gradients.get(gradients.size()-1).onHoverPlain(text);
    return this;
  }
  
  public GradientBuilder onHover(DynamicHoverAction action, String... text) {
    if(gradients.size() > 0) gradients.get(gradients.size()-1).onHover(action, String.join("\n",text));
    return this;
  }
  
  
  public GradientBuilder chat(String text) {
    return onClick(DynamicClickAction.RUN_COMMAND, text);
  }
  
  
  public GradientBuilder command(String text) {
    return onClick(DynamicClickAction.RUN_COMMAND, text.startsWith("/") ? text : "/" + text);
  }
  
  public GradientBuilder suggest(String text) {
    return onClick(DynamicClickAction.SUGGEST_COMMAND, text);
  }
  
  public GradientBuilder insert(String text) {
    if(gradients.size() > 0) gradients.get(gradients.size()-1).insert(text);
    return this;
  }
  
  public GradientBuilder copy(String text) {
    return onClick(DynamicClickAction.COPY_TO_CLIPBOARD, text);
  }
  
  public GradientBuilder url(String text) {
    return onClick(DynamicClickAction.OPEN_URL, text);
  }
  
  public GradientBuilder onClick(DynamicClickAction action, String text) {
    if(gradients.size() > 0) gradients.get(gradients.size()-1).onClick(action,text);
    return this;
  }
  
  
  public DynamicJText finish() {
    int totalLength = gradients.stream().filter(DynamicJPart::isGradient).mapToInt((d)-> d.getText().length()).sum();
    int initialStep = 0;
    for(DynamicJPart dynamicJPart : gradients) {
      if(dynamicJPart.isGradient()){
        jText.findColors(CColor.translateGradient(dynamicJPart.getText(), gradientCenter, totalLength, initialStep, colors));
        initialStep+=dynamicJPart.getText().length();
      }else jText.findColors(CColor.translateAlternateColorCodes('&', CColor.translateHex('&', dynamicJPart.getText())));
      jText.onClick(dynamicJPart.getClickAction(), dynamicJPart.getClickActionData());
      jText.onHover(dynamicJPart.getHoverAction(), dynamicJPart.getHoverData());
      jText.insert(dynamicJPart.getInsertionData());
    }
    gradients.clear();
    return jText;
  }
  
}