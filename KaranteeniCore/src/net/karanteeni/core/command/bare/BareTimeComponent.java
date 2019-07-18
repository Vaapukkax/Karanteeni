package net.karanteeni.core.command.bare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.time.Time;
import net.karanteeni.core.information.time.TimeData;

public class BareTimeComponent implements BareComponent<TimeData>{
	private static final Pattern DIGITS = Pattern.compile("^\\d+");
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String arg) {
		// split the string into multiple parts
		String[] parts = arg.split(",");
		Character lastChar = (arg.length() != 0) ? arg.charAt(arg.length()-1) : null;
		String res = arg.toLowerCase();
			
		// get the possible time names
		Set<String> times = Time.getTimeNames();
		
		// default times
		List<String> defaultTimes = Arrays.asList(arg+"10s",
				arg+"10min",
				arg+"1h",
				arg+"10h",
				arg+"1d",
				arg+"1month",
				arg+"3months"); // no numbers given so return possible times
		
		String lastPart;
		Matcher m;
		
		// handle special case situations eg. first char after comma and first char is comma
		if(parts.length == 0) {
			return defaultTimes;
		} else {	// get the last part of times
			lastPart = parts[parts.length-1].toLowerCase();
			
			// check if any numbers has been entered to the last part
			m = DIGITS.matcher(lastPart);
			if(!m.find() || (lastChar != null && lastChar.charValue() == ',')) {
				return defaultTimes;
			}
		}
		
		// get the letters at the end of the string
		String letters = lastPart.substring(m.end(), lastPart.length());
		
		// replace the last letters with nothing from the result to allow gluing
		res = res.replaceFirst(Pattern.quote(letters) + "$", "");
		
		// create a result list where string are of format 1d | 55second || 1000
		List<String> formattedNumbers = new ArrayList<String>();
		
		// loop each fitting argument and add to autofill
		for(String format : times)
			if(format.startsWith(letters))
				formattedNumbers.add(res + format);
		
		// return autofill compatible names
		return formattedNumbers;
	}

	
	@Override
	public TimeData loadData(CommandSender sender, Command cmd, String label, String arg) {
		return Time.parseTime(arg);
	}
	
	
	/**
	 * Returns the error message for invalid arguments
	 * @param sender
	 */
	public CommandResult invalidArguments(CommandSender sender) {
		return new CommandResult(KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				sender, 
				"command.component-error.time-invalid"),
				ResultType.INVALID_ARGUMENTS,
				Sounds.NO.get());
	}
	
	
	/**
	 * Returns the error message for invalid arguments
	 * @param sender
	 */
	public CommandResult illegalArguments(CommandSender sender) {
		return new CommandResult(KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				sender, 
				"command.component-error.time-no-result"),
				ResultType.INVALID_ARGUMENTS,
				Sounds.NO.get());
	}
}
