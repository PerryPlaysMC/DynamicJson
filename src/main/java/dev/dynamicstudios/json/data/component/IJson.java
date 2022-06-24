package dev.dynamicstudios.json.data.component;

import dev.dynamicstudios.json.JsonBuilder;

import java.io.IOException;

/**
 * Creator: PerryPlaysMC
 * Created: 03/2022
 **/
public interface IJson {

  void writeJson(JsonBuilder builder);

  String plainText();

  String json();

}
