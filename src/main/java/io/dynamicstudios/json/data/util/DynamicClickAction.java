package io.dynamicstudios.json.data.util;

import java.io.Serializable;

public enum DynamicClickAction implements Serializable {

 RUN_COMMAND, CHAT("run_command"), OPEN_URL, SUGGEST_COMMAND, COPY_TO_CLIPBOARD, CHANGE_PAGE, NONE;

 private final String id;

 DynamicClickAction() {
	id = name().toLowerCase();
 }

 DynamicClickAction(String id) {
	this.id = id;
 }

 public String id() {
	return id;
 }

 public static DynamicClickAction fromName(String name) {
	for(DynamicClickAction value : values())
	 if(value.name().equalsIgnoreCase(name)) return value;
	switch(name.toLowerCase()) {
	 case "url":
		return OPEN_URL;
	 case "command":
		return RUN_COMMAND;
	 case "suggest":
		return SUGGEST_COMMAND;
	 case "copy":
		return COPY_TO_CLIPBOARD;
	}
	return NONE;
 }


}