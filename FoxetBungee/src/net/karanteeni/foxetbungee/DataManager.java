package net.karanteeni.foxetbungee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DataManager {
	private static String BAN_REASONS = "ban-reason-keys";
	private static String BAN_EMPTY_REASON = "ban-reason-other";
	private Foxet plugin;
	private String[] banReasons;
	
	
	public DataManager(Foxet plugin) {
		this.plugin = plugin;
		
		loadBanConfig();
	}
	
	
	/**
	 * Returns an array of ban reason keys
	 * @return array of reason keys for a ban
	 */
	public String[] getBanReasonKeys() {
		return this.banReasons;
	}
	
	
	/**
	 * Loads the ban configuration 
	 */
	private void loadBanConfig() {
		// register ban reasons
		if(!plugin.getConfig().contains(BAN_REASONS)) {
			List<Long> punishmentLengths = Arrays.asList(
					2592000000l,
					1209600000l,
					259200000l,
					1209600000l,
					259200000l,
					259200000l,
					1209600000l,
					1209600000l,
					2592000000l,
					864000000l,
					864000000l,
					1209600000l,
					432000000l,
					1209600000l);
			List<String> punishmentItems = Arrays.asList(
					"COMMAND_BLOCK",
					"DIAMOND_PICKAXE",
					"ACACIA_LEAVES",
					"MUSIC_DISC_11",
					"POISONOUS_POTATO",
					"IRON_AXE",
					"TNT",
					"REDSTONE",
					"OAK_SIGN",
					"SKELETON_SKULL",
					"CHEST",
					"DIAMOND_SWORD",
					"DEAD_BUSH",
					"BLAZE_POWDER");
			List<String> reasons = Arrays.asList(
				"hack",
				"xray",
				"lies",
				"disturbing",
				"bully",
				"aggression",
				"grief",
				"lag",
				"advertising",
				"player-kill",
				"stealing",
				"provocation",
				"bug-abuse",
				"discrimination");
			// add all of the ban reasons and lengths to the config
			for(int i = 0; i < reasons.size(); ++i) {
				plugin.getConfig().set(BAN_REASONS + "." + reasons.get(i) + ".icon", punishmentItems.get(i));
				plugin.getConfig().set(BAN_REASONS + "." + reasons.get(i) + ".default-length-ms", punishmentLengths.get(i));
			}
			
			plugin.saveConfig();
		}
		
		// register translation
		Foxet.getTranslator().registerTranslation(plugin, BAN_EMPTY_REASON, "Harmful behaviour");
		
		// register translation for all of the reasons in ban_reasons
		Collection<String> reasons = plugin.getConfig().getSection(BAN_REASONS).getKeys();
		this.banReasons = reasons.toArray(new String[reasons.size()]);
		
		// register translation for each of the ban reasons
		for(String reason : reasons)
			Foxet.getTranslator().registerTranslation(plugin, BAN_REASONS + "." + reason, reason.toUpperCase());
	}
	
	
	/**
	 * Returns a list of translated ban reasons using the reason keys
	 * @param reasonKeys reasons to translate
	 * @return list of translated reasons
	 */
	public List<String> getTranslatedReasons(List<String> reasonKeys, Locale locale) {
		List<String> reasons = new ArrayList<String>();
		for(String key : reasonKeys)
			reasons.add(Foxet.getTranslator().getTranslation(plugin, locale, BAN_REASONS + "." + key));
		return reasons;
	}
	
	
	/**
	 * Returns the icons for given punishment reasons in the same order
	 * @param reasonKeys reasons to which icons will be searched
	 * @return list of icons in the same order
	 */
	public List<String> getReasonIcons(List<String> reasonKeys) {
		List<String> icons = new ArrayList<String>();
		for(String key : reasonKeys)
			icons.add(plugin.getConfig().getString(BAN_REASONS + "." + key + ".icon"));
		return icons;
	}
	
	
	/**
	 * Returns a map of all icons for punishment reasons
	 * @return map of icons for punishment reason keys
	 */
	public HashMap<String, String> getReasonIcons() {
		HashMap<String, String> icons = new HashMap<String, String>();
		for(String key : this.banReasons)
			icons.put(key, plugin.getConfig().getString(BAN_REASONS + "." + key + ".icon"));
		return icons;
	}
	
	
	/**
	 * Returns the icons for given punishment reasons in the same order
	 * @param reasonKeys reasons to which icons will be searched
	 * @return list of icons in the same order
	 */
	public List<Long> getReasonLengths(List<String> reasonKeys) {
		List<Long> lengths = new ArrayList<Long>();
		for(String key : reasonKeys)
			lengths.add(plugin.getConfig().getLong(BAN_REASONS + "." + key + ".default-length-ms"));
		return lengths;
	}
	
	
	/**
	 * Returns a map of all icons for punishment reasons
	 * @return map of icons for punishment reason keys
	 */
	public HashMap<String, Long> getReasonLengths() {
		HashMap<String, Long> lengths = new HashMap<String, Long>();
		for(String key : this.banReasons)
			lengths.put(key, plugin.getConfig().getLong(BAN_REASONS + "." + key + ".default-length-ms"));
		return lengths;
	}
	
	
	/**
	 * Returns a map of all translated reasons with keys
	 * @param locale locale to translate to
	 * @return translations with keys
	 */
	public HashMap<String, String> getTranslatedReasons(Locale locale) {
		HashMap<String, String> translations = new HashMap<String, String>();
		for(String key : this.banReasons)
			translations.put(key, Foxet.getTranslator().getTranslation(plugin, locale, BAN_REASONS + "." + key));
		return translations;
	}
	
	
	/**
	 * Returns the default reason for ban when no reasons are given
	 * @param locale locale to get the ban reason with
	 * @return translated default ban reason
	 */
	public String getDefaultBanReason(Locale locale) {
		return Foxet.getTranslator().getTranslation(plugin, locale, DataManager.BAN_EMPTY_REASON);
	}
}
