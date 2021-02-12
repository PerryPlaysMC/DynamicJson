package dev.perryplaysmc.dynamicjsontext.data;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 01/2021-Now
 **/

public interface IJsonSerializable extends Serializable {

    void toJson(JsonWriter writer, boolean end) throws IOException;

}
