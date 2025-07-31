package io.dynamicstudios.json.data.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Creator: PerryPlaysMC
 * Created: 04/2022
 **/
public class SelectEntities {

 private static final Pattern RANGE = Pattern.compile("(?<min>\\d*(?:\\.\\d*)?)?(\\.\\.(?<max>\\d+(?:\\.\\d*)?))?");
 private static final Pattern NUMBER = Pattern.compile("\\d+");
 private static final HashMap<String, EntityType> entityTypes = new HashMap<>();

 static {
	for(EntityType value : EntityType.values()) {
	 if(value.getName() != null)
		entityTypes.put(value.getName().toLowerCase(), value);
	 entityTypes.put(value.name().toLowerCase(), value);
	}
 }

 public static List<Entity> parseSelector(CommandSender sender, String selector) {
	if(!selector.startsWith("@")) return new ArrayList<>();
	if(selector.startsWith("@p") || selector.startsWith("@s"))
	 return sender instanceof Entity ? Collections.singletonList((Entity) sender) : null;
	if(selector.startsWith("@e") || selector.startsWith("@r")) {
	 boolean playerOnly = selector.startsWith("@r");
	 Location center = sender instanceof Player ? ((Player) sender).getLocation() : Bukkit.getWorlds().get(0).getSpawnLocation();
	 String init = selector.contains("[") ? selector.split("\\[")[1] : "";
	 String[] spl = init.isEmpty() ? new String[0] : init.split("]")[init.split("]").length - 1].split(",");
	 Sort sort = Sort.ARBITRARY;
	 EntityType type = null;
	 GameMode gameMode = null;
	 List<EntityType> doesntEqual = new ArrayList<>();
	 List<Entity> collected = new ArrayList<>();
	 float min = -1;
	 float max = -1;
	 float xRotMax = -91;
	 float xRotMin = -91;
	 float yRotMax = -181;
	 float yRotMin = -181;
	 int x = center.getBlockX();
	 int y = center.getBlockY();
	 int z = center.getBlockZ();
	 int limit = -1;
	 for(String s : spl) {
		if(s.startsWith("sort=")) {
		 sort = Sort.valueOf(s.substring("sort=".length()).toUpperCase());
		} else if(s.startsWith("distance=")) {
		 Matcher matcher = RANGE.matcher(s.substring("distance=".length()));
		 if(matcher.find()) {
			String mi = matcher.group("min") == null ? "0" : matcher.group("min");
			String ma = matcher.group("max") == null ? "0" : matcher.group("max");
			min = Float.parseFloat(mi);
			max = Float.parseFloat(ma);
		 }
		} else if(s.startsWith("x_rotation=")) {
		 Matcher matcher = RANGE.matcher(s.substring("x_rotation=".length()));
		 if(matcher.find()) {
			String mi = matcher.group("min") == null ? "0" : matcher.group("min");
			String ma = matcher.group("max") == null ? "0" : matcher.group("max");
			xRotMin = Float.parseFloat(mi);
			xRotMax = Float.parseFloat(ma);
			if(matcher.group("min") != null) {
			 if(xRotMin > 90) xRotMin = (xRotMin % 90) - 90;
			 if(xRotMin < -90) xRotMin = (xRotMin % 90) + 90;
			}
			if(matcher.group("max") != null) {
			 if(xRotMax > 90) xRotMax = (xRotMax % 90) - 90;
			 if(xRotMax < -90) xRotMax = (xRotMax % 90) + 90;
			}
		 }
		} else if(s.startsWith("y_rotation=")) {
		 Matcher matcher = RANGE.matcher(s.substring("y_rotation=".length()));
		 if(matcher.find()) {
			String mi = matcher.group("min") == null ? "-181" : matcher.group("min");
			String ma = matcher.group("max") == null ? "-181" : matcher.group("max");
			yRotMin = Float.parseFloat(mi);
			yRotMax = Float.parseFloat(ma);
			if(matcher.group("min") != null) {
			 if(yRotMin > 180) yRotMin = (yRotMin % 180) - 180;
			 if(yRotMin < -180) yRotMin = (yRotMin % 180) + 180;
			}
			if(matcher.group("max") != null) {
			 if(yRotMax > 180) yRotMax = (yRotMax % 180) - 180;
			 if(yRotMax < -180) yRotMax = (yRotMax % 180) + 180;
			}
		 }
		} else if(s.startsWith("type=")) {
		 String entity = s.substring("type=".length()).toLowerCase();
		 boolean mustMatchType = !entity.startsWith("!");
		 if(mustMatchType && type != null) {
			System.out.println("Can't set type multiple times in a selector. Got: " + s);
			continue;
		 }
		 if(!mustMatchType) entity = entity.substring(1);
		 if(entity.startsWith("minecraft:")) entity = entity.substring(10);
		 if(!mustMatchType) doesntEqual.add(entityTypes.get(entity));
		 else type = entityTypes.get(entity);
		} else if(s.startsWith("limit=")) {
		 Matcher matcher = NUMBER.matcher(s.substring("limit=".length()));
		 if(matcher.find()) {
			String mi = matcher.group() == null ? "-1" : matcher.group();
			limit = Integer.parseInt(mi);
		 }
		} else if(s.startsWith("x=")) {
		 Matcher matcher = NUMBER.matcher(s.substring("x=".length()));
		 if(matcher.find()) {
			String mi = matcher.group() == null ? "-1" : matcher.group();
			x = Integer.parseInt(mi);
		 }
		} else if(s.startsWith("y=")) {
		 Matcher matcher = NUMBER.matcher(s.substring("y=".length()));
		 if(matcher.find()) {
			String mi = matcher.group() == null ? "-1" : matcher.group();
			y = Integer.parseInt(mi);
		 }
		} else if(s.startsWith("z=")) {
		 Matcher matcher = NUMBER.matcher(s.substring("z=".length()));
		 if(matcher.find()) {
			String mi = matcher.group() == null ? "-1" : matcher.group();
			z = Integer.parseInt(mi);
		 }
		} else if(s.startsWith("gamemode=")) {
		 String gamemode = s.substring("gamemode=".length()).toLowerCase();
		 gameMode = GameMode.valueOf(gamemode);
		}
	 }
	 center = new Location(center.getWorld(), x, y, z);
	 EntityType fType = type;
	 GameMode fGamemode = gameMode;
	 float xRotMn = xRotMin;
	 float xRotMx = xRotMax;
	 float yRotMn = yRotMin;
	 float yRotMx = yRotMax;
	 Predicate<Entity> isValid = init.isEmpty() ? null : (entity) -> {
		Location eLoc = entity.getLocation();
		if(xRotMx > -181) {
		 if(xRotMn == -181)
			if(eLoc.getPitch() < xRotMn) return false;
			else if(!(eLoc.getPitch() >= xRotMn && eLoc.getPitch() <= xRotMx)) return false;
		} else if(xRotMn > -181)
		 if(eLoc.getPitch() != xRotMn) return false;
		if(yRotMx > -181) {
		 if(yRotMn == -181)
			if(eLoc.getYaw() < yRotMx) return false;
			else if(!(eLoc.getYaw() >= yRotMn && eLoc.getYaw() <= yRotMx)) return false;
		} else if(yRotMn > -181)
		 if(eLoc.getYaw() != yRotMn) return false;
		if(playerOnly) {
		 if(entity instanceof Player) return fGamemode == null || ((Player) entity).getGameMode() == fGamemode;
		 return false;
		}
		return (fType == null && !doesntEqual.contains(entity.getType())) || entity.getType() == fType;
	 };
	 if(max > -1 && min > -1) {
		Location finalCenter1 = center.clone();
		float finalMax = max;
		List<Entity> entities = Bukkit.getWorlds().stream().flatMap(w -> {
		 finalCenter1.setWorld(w);
		 return w.getNearbyEntities(finalCenter1, finalMax, finalMax, finalMax).stream();
		}).collect(Collectors.toList());
		for(Entity entity : entities) {
		 if(isValid.test(entity) && dist(entity.getLocation(), center) >= min && dist(entity.getLocation(), center) <= max) {
			if(gameMode == null || (entity instanceof Player && ((Player) entity).getGameMode() == gameMode))
			 collected.add(entity);
			if(collected.size() == limit) break;
		 }
		}
	 } else collected =
			Bukkit.getWorlds().stream().flatMap(w -> w.getEntities().stream()).filter((e) -> init.isEmpty() || isValid.test(e)).collect(Collectors.toList());

	 BiPredicate<Double, Location> dist = null;
	 double smallest = Double.MAX_VALUE;
	 Location finalCenter = center;
	 switch(sort) {
		case NEAREST:
		 dist = (m, l) -> dist(l, finalCenter) < m;
		 break;
		case FURTHEST:
		 smallest = Double.MIN_VALUE;
		 dist = (m, l) -> dist(l, finalCenter) > m;
		 break;
		case RANDOM:
		 Collections.shuffle(collected);
		 break;
		default:
		 break;
	 }
	 if(dist != null) {
		List<Entity> newList = new ArrayList<>();
		while(!collected.isEmpty()) {
		 Entity found = null;
		 for(Entity entity : collected) {
			if(dist.test(smallest, entity.getLocation())) {
			 smallest = dist(entity.getLocation(), center);
			 found = entity;
			}
		 }
		 if(found == null) break;
		 collected.remove(found);
		 newList.add(found);
		}
		collected = newList;
	 }
	 if(limit > -1 && collected.size() > limit) collected = collected.subList(0, limit);
	 return collected;
	}
	return new ArrayList<>();
 }

 private static double dist(Location l1, Location l2) {
	return Math.sqrt(distanceSquared(l1, l2));
 }

 private static double distanceSquared(Location l1, Location l2) {
	return NumberConversions.square(l1.getBlockX() - l2.getBlockX()) + NumberConversions.square(l1.getBlockY() - l2.getBlockY()) + NumberConversions.square(l1.getBlockZ() - l2.getBlockZ());
 }

 private enum Sort {
	NEAREST, FURTHEST, ARBITRARY, RANDOM
 }

}
