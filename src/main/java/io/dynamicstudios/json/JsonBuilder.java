package io.dynamicstudios.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public class JsonBuilder {
 private static final String[] REPLACEMENT_CHARS = new String[128];

 private final StringBuilder stringBuilder;
 private final List<ObjectType> jsonTop = new ArrayList<>();
 private final List<ObjectType> openType = new ArrayList<>();
 private boolean newOpen = false;
 private String name;

 public JsonBuilder(StringBuilder builder) {
	this.stringBuilder = builder;
 }

 public ObjectType topObject() {
	return topObject(jsonTop.size() - 1);
 }

 public List<ObjectType> topObjects() {
	return jsonTop;
 }

 public ObjectType topObject(int index) {
	if(index >= jsonTop.size()) index = jsonTop.size() - 1;
	else if(index < 0) return null;
	return jsonTop.isEmpty() ? null : jsonTop.get(index);
 }

 public JsonBuilder beginObject() {
	return begin(ObjectType.OBJECT);
 }

 public JsonBuilder beginArray() {
	return begin(ObjectType.ARRAY);
 }

 public JsonBuilder begin(ObjectType type) {
	check();
	writeName();
	if(topObject() != null) before(BeforeType.VALUE);
	newOpen = true;
	open(type);
	return this;
 }

 public JsonBuilder name(String text) {
	check();
	if(topObject() == ObjectType.ARRAY) throw new IllegalStateException("Cannot add a name element to an array");
	this.name = (text);
	return this;
 }

 public JsonBuilder value(Object value) {
	check();
	writeName();
	before(BeforeType.VALUE);
	string(value.toString());
	newOpen = false;
	return this;
 }

 public JsonBuilder jsonValue(String text) {
	check();
	writeName();
	before(BeforeType.VALUE);
	newOpen = false;
	stringBuilder.append(text);
	return this;
 }

 public boolean isClosed() {
	return jsonTop.isEmpty() && stringBuilder.length() != 0;
 }

 public void close() {
	if(!jsonTop.isEmpty() && stringBuilder.length() > 0)
	 throw new IllegalStateException("Incomplete json\n" + stringBuilder);
 }

 private void check() {
	if(jsonTop.isEmpty() && stringBuilder.length() != 0)
	 throw new IllegalStateException("JsonBuilder is closed, all objects have ended");
 }


 public JsonBuilder end() {
	check();
	newOpen = false;
	stringBuilder.append(jsonTop.remove(jsonTop.size() - 1).closeChar());
	if(openType.size() > 0) {
	 openType.remove(openType.size() - 1);
	 if(!jsonTop.isEmpty()) openType.add(jsonTop.get(jsonTop.size() - 1));
	}
	return this;
 }


 private void open(ObjectType type) {
	stringBuilder.append(type.openChar());
	jsonTop.add(type);
 }

 private void before(BeforeType type) {
	if(type == BeforeType.NAME && !openType.isEmpty() && topObject() == openType.get(openType.size() - 1))
	 stringBuilder.append(newOpen ? "" : ",");
	else if(type == BeforeType.VALUE && (!openType.isEmpty() && topObject() == openType.get(openType.size() - 1)))
	 stringBuilder.append(topObject() == ObjectType.ARRAY ? "," : ":");
	else if(type == BeforeType.NAME || (topObject() == ObjectType.ARRAY))
	 openType.add(topObject());
 }

 private void writeName() {
	if(this.name == null) return;
	check();
	before(BeforeType.NAME);
	string(name);
	this.name = null;
 }

 public String currentJson() {
	return stringBuilder.toString();
 }

 @Override
 public String toString() {
	close();
	return stringBuilder.toString();
 }

 public enum ObjectType {
	OBJECT('{', '}'), ARRAY('[', ']');

	private final Character open, close;

	ObjectType(Character open, Character close) {
	 this.open = open;
	 this.close = close;
	}

	public Character openChar() {
	 return open;
	}

	public Character closeChar() {
	 return close;
	}
 }

 enum BeforeType {
	NAME, VALUE
 }


 private void string(String value) {
	prepareString(this.stringBuilder, value);
 }

 public static void prepareString(StringBuilder sb, String value) {
	if(!value.equals("true") && !value.equals("false")) sb.append('"');
	int last = 0;
	int length = value.length();
	char[] chars = value.toCharArray();
	for(int i = 0; i < length; ++i) {
	 char c = value.charAt(i);
	 String replacement;
	 if(c < 128) {
		replacement = REPLACEMENT_CHARS[c];
		if(replacement == null) continue;
	 } else if(c == '\u2028') replacement = "\\u2028";
	 else {
		if(c != '\u2029') continue;
		replacement = "\\u2029";
	 }
	 if(last < i) sb.append(chars, last, i - last);
	 sb.append(replacement);
	 last = i + 1;
	}
	if(last < length) sb.append(chars, last, length - last);
	if(!value.equals("true") && !value.equals("false")) sb.append('"');
 }


 static {
	for(int i = 0; i <= 31; ++i) {
	 REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
	}

	REPLACEMENT_CHARS[34] = "\\\"";
	REPLACEMENT_CHARS[92] = "\\\\";
	REPLACEMENT_CHARS[9] = "\\t";
	REPLACEMENT_CHARS[8] = "\\b";
	REPLACEMENT_CHARS[10] = "\\n";
	REPLACEMENT_CHARS[13] = "\\r";
	REPLACEMENT_CHARS[12] = "\\f";
 }

}
