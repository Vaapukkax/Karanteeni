package net.karanteeni.statmanager.level.execute;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.statmanager.StatManager;

public class BroadcastExecutor extends Executor<String> {
	private String translationKey;
	private static String BASE_KEY = "BROADCAST.";
	
	public BroadcastExecutor(ConfigurationSection config, String path, String key) {
		super(config, path, key); 
		
		translationKey = this.getString();
		StatManager.getTranslator().registerTranslation(StatManager.getPlugin(StatManager.class), BASE_KEY + translationKey, 
				"This is the default broadcast message for StatManager level translation key " + translationKey + 
				" " + Executor.PLAYER_NAME + " " + Executor.PLAYER_DISPLAY_NAME + " " + Executor.PLAYER_UUID);
	}
	

	@Override
	public void execute(Player player) {
		//PlayTime.getMessager().broadcastTranslatedMessage(Sounds.NONE.get(), translationKey, PlayTime.getPlugin(PlayTime.class));
		StatManager pl = StatManager.getPlugin(StatManager.class);
		
		// send a translated message to each player
		for(Player p : Bukkit.getOnlinePlayers()) {
			String message = StatManager.getTranslator().getTranslation(pl, p, BASE_KEY + translationKey);
			message = message.replace(Executor.PLAYER_DISPLAY_NAME, player.getDisplayName())
					.replace(Executor.PLAYER_NAME, player.getName())
					.replace(Executor.PLAYER_UUID, player.getUniqueId().toString());
			StatManager.getMessager().sendMessage(p, Sounds.NONE.get(), message);
		}
	}
	
	
	public static void register(String key) {
		BroadcastExecutor.register(key, BroadcastExecutor.class);
	}
}
