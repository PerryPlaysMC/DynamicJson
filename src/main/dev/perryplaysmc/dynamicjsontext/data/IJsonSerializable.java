package dev.perryplaysmc.dynamicjsontext.data;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 * Copy Right ©
 * This code is private
 * Owner: PerryPlaysMC
 * From: 01/2021-Now
 * <p>
 * Any attempts to use these program(s) may result in a penalty of up to $1,000 USD
 **/

public interface IJsonSerializable extends Serializable {

    void toJson(JsonWriter writer, boolean end) throws IOException;

}
