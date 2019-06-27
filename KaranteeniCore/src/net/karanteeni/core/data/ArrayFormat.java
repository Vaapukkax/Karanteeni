package net.karanteeni.core.data;

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.karanteeni.core.KaranteeniCore;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

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
	 * Combines multiple components into one basecomponent
	 * @param components components to be combined
	 * @return combined components
	 */
	public static BaseComponent combine(BaseComponent[] components) {
		BaseComponent component = new TextComponent();
		for(BaseComponent bc : components)
			component.addExtra(bc);
		return component;
	}
	
	
	/**
	 * Joins the given array elements with divider into a BaseComponent
	 * @param list Array to be converted to string
	 * @param divider divider for array
	 * @return array as a string
	 */
	public static BaseComponent join(List<BaseComponent> list, String divider) {
		BaseComponent component = new TextComponent(); // component to add the items to
		int counter = 0; // counter used to get the divider
		BaseComponent div = combine(TextComponent.fromLegacyText(divider));
		
		// loop each list item
		for(BaseComponent c : list) {
			// if last element
			if(list.size()-1 == counter) {
				c.setColor(ChatColor.getByChar(defColors.get(counter % defColors.size())).asBungee());
				component.addExtra( c );
				//Bukkit.broadcastMessage(component.toLegacyText());
			} else {
				c.setColor(ChatColor.getByChar(defColors.get(counter % defColors.size())).asBungee());
				component.addExtra( c );
				component.addExtra(div);
			}
			
			++counter;
		}
		
		return component;
	}
	
	
	/**
	 * Combines the given basecomponents into a one basecomponent with colors formatting
	 * @param list list of components to combine
	 * @param divider divider between components
	 * @param colors colors for list
	 * @return combined basecomponent
	 */
	public static BaseComponent join(List<BaseComponent> list, String divider, char[] colors) {
		BaseComponent component = new TextComponent(); // component to add the items to
		int counter = 0; // counter used to get the divider
		BaseComponent div = combine(TextComponent.fromLegacyText(divider));
		
		// loop each list item
		for(BaseComponent c : list) {
			// if last element
			if(list.size()-1 == counter) {
				c.setColor(ChatColor.getByChar(colors[counter % colors.length]).asBungee());
				component.addExtra( c );
			} else {
				c.setColor(ChatColor.getByChar(colors[counter % colors.length]).asBungee());
				component.addExtra( c );
				component.addExtra(div);
			}
			
			++counter;
		}
		
		return component;
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
