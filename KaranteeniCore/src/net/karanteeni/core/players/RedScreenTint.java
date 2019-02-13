package net.karanteeni.core.players;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RedScreenTint {
	private static Class<?> getClass(String prefix, String name) throws Exception {
		return Class.forName(new StringBuilder().append(prefix + ".").append(Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1)).append(".").append(name).toString());
	}
	
	private static Method handle, sendPacket;
	private static Method center, distance, time, movement;
	private static Field player_connection;
	private static Constructor<?> constructor, border_constructor;
	private static Object constant;
	static {
		try {			
			handle = getClass("org.bukkit.craftbukkit", "entity.CraftPlayer").getMethod("getHandle");
			player_connection = getClass("net.minecraft.server", "EntityPlayer").getField("playerConnection");
			for (Method m : getClass("net.minecraft.server", "PlayerConnection").getMethods()) {
				if (m.getName().equals("sendPacket")) {
					sendPacket = m;
					break;
				}
			}
			Class<?> enumclass;
			try {
				enumclass = getClass("net.minecraft.server", "EnumWorldBorderAction");
			} catch(ClassNotFoundException x) {
				enumclass = getClass("net.minecraft.server", "PacketPlayOutWorldBorder$EnumWorldBorderAction");
			}
			constructor = getClass("net.minecraft.server", "PacketPlayOutWorldBorder").getConstructor(getClass("net.minecraft.server", "WorldBorder"), enumclass);
			border_constructor = getClass("net.minecraft.server", "WorldBorder").getConstructor();
			
			String setCenter = "setCenter";
			String setWarningDistance = "setWarningDistance";
			String setWarningTime = "setWarningTime";
			String transitionSizeBetween = "transitionSizeBetween";
			
			center = getClass("net.minecraft.server", "WorldBorder").getMethod(setCenter, double.class, double.class);
			distance = getClass("net.minecraft.server", "WorldBorder").getMethod(setWarningDistance, int.class);
			time = getClass("net.minecraft.server", "WorldBorder").getMethod(setWarningTime, int.class);
			movement = getClass("net.minecraft.server", "WorldBorder").getMethod(transitionSizeBetween, double.class, double.class, long.class);
			
			for (Object o: enumclass.getEnumConstants()) {
				if (o.toString().equals("INITIALIZE")) {
					constant = o;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void sendWorldBorderPacket(Player p, int dist, double oldradius, double newradius, long delay) {
		//border_constructor = Main.class.getClass("net.minecraft.server", "WorldBorder").getConstructor();
		
		try {
			Object wb = border_constructor.newInstance();
			
			// Thanks Sashie for this
			Method worldServer = getClass("org.bukkit.craftbukkit", "CraftWorld").getMethod("getHandle", (Class<?>[]) new Class[0]);
			Field world = getClass("net.minecraft.server", "WorldBorder").getField("world");
			world.set(wb, worldServer.invoke(p.getWorld()));
			
			center.invoke(wb, p.getLocation().getX(), p.getLocation().getY());
			distance.invoke(wb, dist);
			time.invoke(wb, 15);
			movement.invoke(wb, oldradius, newradius, delay);
			
			Object packet = constructor.newInstance(wb, constant);
			sendPacket.invoke(player_connection.get(handle.invoke(p)), packet);
		} catch(Exception x) {
			x.printStackTrace();
		}
    }
	
	/**
	 * Fade out a red border from player
	 * @param p
	 * @param percentage
	 * @param time
	 */
	public void fadeOutBorder(Player p, int percentage, long time){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, 0, 200000D, (double) dist, (long) 1000 * time + 4000); //Add 4000 to make sure the "security" zone does not count in the fade time
	}
	
	/**
	 * Fade in a red border to player
	 * @param p
	 * @param percentage
	 * @param time
	 */
	public void fadeInBorder(Player p, int percentage, long time){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, 0, (double)dist, 0D, (long) 1000 * time + 4000); //Add 4000 to make sure the "security" zone does not count in the fade time
	}
	
	/**
	 * Send the border data to player
	 * @param p
	 * @param percentage
	 */
	public void sendBorder(Player p, int percentage){
		percentage = Math.round(percentage / 2);
		setBorder(p, percentage);
	}
	
	/**
	 * Set red screen tint to player
	 * @param p
	 * @param percentage
	 */
	public void setBorder(Player p, int percentage){
		int dist = -10000 * percentage + 1300000;
		sendWorldBorderPacket(p, dist, 200000D, 200000D, 0);
	}
}
