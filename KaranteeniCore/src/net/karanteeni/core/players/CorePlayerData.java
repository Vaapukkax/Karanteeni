package net.karanteeni.core.players;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.KaranteeniCore;

public enum CorePlayerData {
	JOINTIME("jointime");
	
	private final NamespacedKey key;
	private final Plugin plugin = KaranteeniCore.getPlugin(KaranteeniCore.class);
	
	CorePlayerData(String key)
	{
		this.key = new NamespacedKey(plugin, key);
	}
	
	public NamespacedKey getKey()
	{
		return key;
	}
}
