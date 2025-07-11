package io.dynamicstudios.json.data.util.packet;

import io.dynamicstudios.json.Version;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public class JsonSender {

	private static final Class<?> packetDataSerializer = Version.Minecraft.getClass("network.PacketDataSerializer");
	private static final Class<?> playerConnect = Version.Minecraft.getClass("server.network.PlayerConnection");
	private static final Class<?> networkMan = Version.Minecraft.getClass("network.NetworkManager");
	private static final Class<?> craftPlayer = Version.CraftBukkit.getClass("entity.CraftPlayer");
	private static final Class<?> entityPlayer = Version.Minecraft.getClass("server.level.EntityPlayer");
	private static final Class<?> packetC = Version.Minecraft.getClass("network.protocol.Packet");
	private static final Class<?> craftMessage = Version.CraftBukkit.getClass("util.CraftChatMessage");
	private static final Class<?> component;

	static {
		Class<?> ichatcomponent = Version.Minecraft.getClass("network.chat.Component");
		if(ichatcomponent == null)
			ichatcomponent = Version.Minecraft.getClass("network.chat.IChatBaseComponent");
		component = ichatcomponent;
	}

	private static final Method readDataFromPDS = ReflectionUtils.getMethod(packetC, "a", packetDataSerializer);
	private static final Method sendPacket = ReflectionUtils.getMethod(networkMan, void.class, packetC);
	private static final Method getHandle = ReflectionUtils.getMethod(craftPlayer, "getHandle");
	private static final Method fromJSON = ReflectionUtils.getMethod(craftMessage, "fromJSON", String.class);
	private static final Field playerConnection = ReflectionUtils.getField(entityPlayer, playerConnect);
	private static final Field networkManager = ReflectionUtils.getField(playerConnect, networkMan);

	public static void sendJson(String json, Player... players) {
		sendJson(new PacketSendChat(json, PacketSendChat.ChatMessageType.DEFAULT_CHAT), players);
	}

	public static void sendJson(String json, UUID from, Player... players) {
		sendJson(new PacketSendChat(json, PacketSendChat.ChatMessageType.DEFAULT_CHAT, from), players);
	}

	public static void sendAction(String json, Player... players) {
		sendJson(new PacketSendChat(json, PacketSendChat.ChatMessageType.ACTION_BAR), players);
	}

	public static void sendJson(PacketSendChat json, Player... players) {
		for(Player player : players) {
			sendPacket(player, json);
		}
	}


	private static void sendPacket(Player player, PacketSendChat packet) {
		try {
			PacketDataSerializer pds = new PacketDataSerializer(Unpooled.buffer());
			if(packet.getNMSClass() == null) return;
			packet.write(pds);
			Object newPacket = createPacket(packet.getNMSClass(), pds);
			sendPacket(player, newPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Object createPacket(Class<?> packet, PacketDataSerializer data) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		Object nmsData = ReflectionUtils.getConstructor(packetDataSerializer, ByteBuf.class).newInstance(data.getData());
		Constructor<?> con = ReflectionUtils.getConstructor(packet, packetDataSerializer);
		if(con != null) return con.newInstance(nmsData);
		con = ReflectionUtils.getConstructor(packet);
		if(con == null) {
			con = ReflectionUtils.getConstructor(packet, component, boolean.class);
			if(con != null) return con.newInstance(fromJSON.invoke(null,data.toString(Short.MAX_VALUE)), data.readBoolean());
			return null;
		}
		Object newPacket = con.newInstance();
		readDataFromPDS.invoke(newPacket, nmsData);
		return newPacket;
	}

	private static void sendPacket(Player player, Object packet) {
		try {
			if(craftPlayer == null) return;
			sendPacket.invoke(networkManager.get(playerConnection.get(getHandle.invoke(player))), packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
