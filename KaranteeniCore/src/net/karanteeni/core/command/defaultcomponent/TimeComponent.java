package net.karanteeni.core.command.defaultcomponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.time.Time;
import net.karanteeni.core.information.time.TimeData;
import net.karanteeni.core.information.translation.TranslationContainer;

/**
 * Loads List of players given in parameters and sets them into data with key "core.switch" and
 * the object is either 'true' or 'false'
 * @author Nuubles
 *
 */
public class TimeComponent extends CommandLoader implements TranslationContainer {
	private static final Pattern DIGITS = Pattern.compile("^\\d+");
	public final static String TIME_KEY = "core.time";
	// allow malformed input
	private boolean strict;
	
	/**
	 * Initializes the binary loader class
	 * @param before should this be run before next component
	 * @param strict can the code continue even if no valid time format given
	 */
	public TimeComponent(boolean before, boolean strict) {
		super(before);
		this.strict = strict;
	}

	
	/**
	 * Initializes the true false component
	 * @param loader loader to load after this loader
	 * @param before load before the attached component
	 * @param strict can the code continue even if no valid time format given
	 */
	public TimeComponent(CommandLoader loader, boolean before, boolean strict) {
		super(loader, before);
		this.strict = strict;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		// no time has been set
		if(args.length < 1) {
			if(strict)
				return new CommandResult(invalidArguments(sender), ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
			else
				return CommandResult.SUCCESS;
		}
		
		TimeData time = Time.parseTime(args[0]);
		if(time == null)
			return new CommandResult(invalidArguments(sender), ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		else if(time.isInvalid()) {
			return new CommandResult(illegalArguments(sender), ResultType.INVALID_ARGUMENTS, Sounds.NO.get());
		}
		
		// time is set and valid
		this.chainer.setObject(TIME_KEY, time);
		
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 1)
			return null;

		// split the string into multiple parts
		String[] parts = args[0].split(",");
		Character lastChar = (args[0].length() != 0) ? args[0].charAt(args[0].length()-1) : null;
		String res = args[0].toLowerCase();
			
		// get the possible time names
		Set<String> times = Time.getTimeNames();
		
		// default times
		List<String> defaultTimes = Arrays.asList(args[0]+"10s",
				args[0]+"10min",
				args[0]+"1h",
				args[0]+"10h",
				args[0]+"1d",
				args[0]+"1month",
				args[0]+"3months"); // no numbers given so return possible times
		
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
	
	
	/**
	 * Returns the error message for invalid arguments
	 * @param sender
	 */
	public String invalidArguments(CommandSender sender) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				sender, 
				"command.component-error.time-invalid");
	}
	
	
	/**
	 * Returns the error message for invalid arguments
	 * @param sender
	 */
	public String illegalArguments(CommandSender sender) {
		return KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				sender, 
				"command.component-error.time-no-result");
	}


	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.time-invalid", 
				"The time format given is invalid");
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.time-no-result", 
				"Could not get a result from given time parameter");
	}
}
