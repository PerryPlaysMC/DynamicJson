package dev.perryplaysmc.dynamicjsontext.data;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

/**
 * Owner: PerryPlaysMC
 * Created: 2/21
 **/

public interface IJsonSerializable extends Serializable {

    void toJson(JsonWriter writer, boolean end) throws IOException;

}
