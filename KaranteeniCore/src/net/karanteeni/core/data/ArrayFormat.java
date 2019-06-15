package net.karanteeni.core.data;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniCore;

/**
 * Used to format arrays of text etc. to text
 * @author Nuubles
 *
 */
public class ArrayFormat {
	private static KaranteeniCore plugin = null;
	private static List<Character> defColors = null;
	
	/**
	 * Initializes the array formatter
	 */
	public static void initialize() {
		plugin = KaranteeniCore.getPlugin(KaranteeniCore.class);
		if(!plugin.getConfig().isSet("array-colors")) {
			plugin.getConfig().set("array-colors", Arrays.asList('a', 'e'));
			plugin.saveConfig();
		}
		
		defColors = plugin.getConfig().getCharacterList("array-colors");
	}
	
	/**
	 * Joins the given array elements with divider into a string
	 * @param array Array to be converted to string
	 * @param divider divider for array
	 * @return array as a string
	 */
	public static String join(String[] array, String divider) {
		StringBuffer output = new StringBuffer();
		
		// loop each array element
		for(int i = 0; i < array.length; ++i) {
			// if last element
			if(array.length-1 == i) {
				output.append("§" + defColors.get(i % defColors.size()) + array[i]);
			} else {
				output.append("§" + defColors.get(i % defColors.size()) + array[i] + divider);
			}
		}
		
		return output.toString();
	}

	
	/**
	 * Joins the given array elements with divider into a string
	 * @param array Array to be converted to string
	 * @param divider divider for array
	 * @return array as a string
	 */
	public static String joinSort(String[] array, String divider) {
		StringBuffer output = new StringBuffer();
		
		Arrays.sort(array);
		
		// loop each array element
		for(int i = 0; i < array.length; ++i) {
			// if last element
			if(array.length-1 == i) {
				output.append("§" + defColors.get(i % defColors.size()) + array[i]);
			} else {
				output.append("§" + defColors.get(i % defColors.size()) + array[i] + divider);
			}
		}
		
		return output.toString();
	}
	
	
	/**
	 * Joins the given array elements with divider into a string
	 * @param array
	 * @param divider
	 * @param colors
	 * @return
	 */
	public static String join(String[] array, String divider, char[] colors) {
		StringBuffer output = new StringBuffer();
		
		// loop each array element
		for(int i = 0; i < array.length; ++i) {
			// if last element
			if(array.length-1 == i) {
				output.append("§" + colors[i % colors.length] + array[i]);
			} else {
				output.append("§" + colors[i % colors.length] + array[i] + divider);
			}
		}
		
		return output.toString();
	}
	
	
	/**
	 * Joins the given array elements with divider into a string
	 * @param array
	 * @param divider
	 * @param colors
	 * @return
	 */
	public static String joinSort(String[] array, String divider, char[] colors) {
		StringBuffer output = new StringBuffer();
		
		Arrays.sort(array);
		
		// loop each array element
		for(int i = 0; i < array.length; ++i) {
			// if last element
			if(array.length-1 == i) {
				output.append("§" + colors[i % colors.length] + array[i]);
			} else {
				output.append("§" + colors[i % colors.length] + array[i] + divider);
			}
		}
		
		return output.toString();
	}
	
	
	/**
	 * Converts list of players to string array
	 * @param players players to be made into an array
	 * @return array of playernames
	 */
	public static String[] playersToArray(List<Player> players) {
		String[] names = new String[players.size()];
		
		// add each player to array
		for(int i = 0; i < players.size(); ++i) {
			names[i] = players.get(i).getName();
		}
		
		return names;
	}
	
	/**
	 * Converts list of players to string array
	 * @param players players to be made into an array
	 * @return array of playernames
	 */
	public static String[] playersToArray(Player[] players) {
		String[] names = new String[players.length];
		
		// add each player to array
		for(int i = 0; i < players.length; ++i) {
			names[i] = players[i].getName();
		}
		
		return names;
	}	
}
