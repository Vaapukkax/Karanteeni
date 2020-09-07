package net.karanteeni.core.information;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatColor {
	public static final char COLOR_CHAR = '&';
	public static final char FORMATTED_COLOR_CHAR = org.bukkit.ChatColor.COLOR_CHAR;
	private static String[] COLORS = {
			COLOR_CHAR + "0",
			COLOR_CHAR + "1",
			COLOR_CHAR + "2",
			COLOR_CHAR + "3",
			COLOR_CHAR + "4",
			COLOR_CHAR + "5",
			COLOR_CHAR + "6",
			COLOR_CHAR + "7",
			COLOR_CHAR + "8",
			COLOR_CHAR + "9",
			COLOR_CHAR + "a",
			COLOR_CHAR + "b",
			COLOR_CHAR + "c",
			COLOR_CHAR + "d",
			COLOR_CHAR + "e",
			COLOR_CHAR + "f"
		};
	private static String[] REAL_COLORS = {
			"§0",
			"§1",
			"§2",
			"§3",
			"§4",
			"§5",
			"§6",
			"§7",
			"§8",
			"§9",
			"§a",
			"§b",
			"§c",
			"§d",
			"§e",
			"§f"
		};
	private static String[] FORMATS = {
			COLOR_CHAR + "o",
			COLOR_CHAR + "n",
			COLOR_CHAR + "m",
			COLOR_CHAR + "r",
			COLOR_CHAR + "l"
		};
	private static String[] REAL_FORMATS = {
			"§o",
			"§n",
			"§m",
			"§r",
			"§l"
		};
	private static String RANDOM = COLOR_CHAR + "k";
	private static String REAL_RANDOM = "§k";
	
	
	public static String translateColor(String message) {
		for(int i = 0; i < COLORS.length; ++i) {
			message = message.replace(COLORS[i], REAL_COLORS[i]);
		}
		return message;
	}
	
	
	public static String translateFormat(String message) {
		for(int i = 0; i < FORMATS.length; ++i) {
			message = message.replace(FORMATS[i], REAL_FORMATS[i]);
		}
		return message;
	}
	
	
	public static String translateMagic(String message) {
		return message.replace(RANDOM, REAL_RANDOM);
	}
	
	
    public static String translateHexColorCodes(String message) {
        final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find())
        {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, FORMATTED_COLOR_CHAR + "x"
                    + FORMATTED_COLOR_CHAR + group.charAt(0) + FORMATTED_COLOR_CHAR + group.charAt(1)
                    + FORMATTED_COLOR_CHAR + group.charAt(2) + FORMATTED_COLOR_CHAR + group.charAt(3)
                    + FORMATTED_COLOR_CHAR + group.charAt(4) + FORMATTED_COLOR_CHAR + group.charAt(5)
                    );
        }
        return matcher.appendTail(buffer).toString();
    }
    
    
    public static String stripColors(String message) {
    	StringBuilder builder = new StringBuilder(message);
    	int index = 0;
    	while((index = builder.indexOf(org.bukkit.ChatColor.COLOR_CHAR + "", index)) != -1) {
    		if(index + 1 > builder.length()) break;
    		
    		builder.replace(index, index + 2, "");
    	}
    	return builder.toString();
    }
    
    
    public static String translateAll(String message) {
    	message = translateHexColorCodes(message);
    	message = translateColor(message);
    	message = translateFormat(message);
    	message = translateMagic(message);
    	return message;
    }
}
