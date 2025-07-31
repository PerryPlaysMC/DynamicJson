package io.dynamicstudios.json.data.util.packet;

import io.dynamicstudios.json.Version;

import java.util.UUID;

/**
 * Creator: PerryPlaysMC
 * Created: 01/2022
 **/
public class PacketSendChat {

 private static final UUID defaultUUID = UUID.randomUUID();

 private final String json;
 private final ChatMessageType type;
 private final UUID id;

 public PacketSendChat() {
	this("", ChatMessageType.DEFAULT_CHAT);
 }

 public PacketSendChat(String json, ChatMessageType type) {
	this(json, type, defaultUUID);
 }

 public PacketSendChat(String json, ChatMessageType type, UUID id) {
	this.json = json;
	this.type = type;
	this.id = id;
 }

 public void write(PacketDataSerializer data) {
	if(Version.isCurrentOrHigher(Version.v1_20)) {
	 data.writeString(json);
	 data.writeBoolean(type == ChatMessageType.ACTION_BAR);
	 return;
	}
	data.writeString(json);
	data.writeByte(type.getIndex());
	if(Version.isCurrentHigher(Version.v1_15)) data.writeUUID(id);
 }

 public enum ChatMessageType {
	DEFAULT_CHAT((byte) 0, false),
	FORCE_CHAT((byte) 1, true),
	ACTION_BAR((byte) 2, true);

	private final byte index;

	private final boolean interrupt;

	ChatMessageType(byte index, boolean interrupt) {
	 this.index = index;
	 this.interrupt = interrupt;
	}

	public byte getIndex() {
	 return this.index;
	}

	public static ChatMessageType getFromIndex(byte index) {
	 for(ChatMessageType type : values()) if(index == type.getIndex()) return type;
	 return DEFAULT_CHAT;
	}

	public boolean shouldInterrupt() {
	 return this.interrupt;
	}
 }


 private static final Class<?> asNMS = Version.Minecraft.getClass("network.protocol.game.PacketPlayOutChat") == null ?
		Version.Minecraft.getClass("network.protocol.game.ClientboundSystemChatPacket") : Version.Minecraft.getClass("network.protocol.game.PacketPlayOutChat");

 public Class<?> getNMSClass() {
	return asNMS;
 }
}
