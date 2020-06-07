package net.karanteeni.core.command.defaultcomponent;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;

/**
 * Loads List of players given in parameters and sets them into data with key "core.switch" and
 * the object is either 'true' or 'false'
 * @author Nuubles
 *
 */
public class BinaryLoader extends CommandLoader implements TranslationContainer {
	private final BINARY binary;
	public final static String BINARY_KEY = "core.binary";
	
	/**
	 * Initializes the binary loader class
	 * @param before should this be run before next component
	 * @param binary texts to use as the binary format
	 */
	public BinaryLoader(boolean before, BINARY binary) {
		super(before);
		this.binary = binary;
	}

	
	/**
	 * Initializes the true false component
	 * @param loader loader to load after this loader
	 * @param before load before the attached component
	 * @param binary
	 */
	public BinaryLoader(CommandLoader loader, boolean before, BINARY binary) {
		super(loader, before);
		this.binary = binary;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		// no truth has been requested
		if(args.length == 0) {
				return new CommandResult(KaranteeniCore.getTranslator().getTranslation(
						KaranteeniCore.getPlugin(KaranteeniCore.class), 
						sender, 
						"command.component-error.true-false")
						.replace("%true%", binary.TRUE)
						.replace("%false%", binary.FALSE), 
						ResultType.INVALID_ARGUMENTS, 
						Sounds.NO.get());
		}
			
		
		Boolean res = binary.asBoolean(args[0]);
		if(res == null)
			return new CommandResult(KaranteeniCore.getTranslator().getTranslation(
					KaranteeniCore.getPlugin(KaranteeniCore.class), 
					sender, 
					"command.component-error.true-false")
					.replace("%true%", binary.TRUE)
					.replace("%false%", binary.FALSE), 
					ResultType.INVALID_ARGUMENTS, 
					Sounds.NO.get());
		
		// set the result into memory
		this.chainer.setObject(BINARY_KEY, res.booleanValue());
		
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1)
			return null;
		
		// return autofill compatible names
		return this.filterByPrefix(Arrays.asList(binary.TRUE, binary.FALSE), args[0], false);
	}
	
	
	/**
	 * This enumerator represents booleans as a string in different formats
	 * @author Nuubles
	 *
	 */
	public static enum BINARY {
		ON_OFF("on", "off"),
		TRUE_FALSE("true", "false"),
		ENABLED_DISABLED("enabled", "disabled"),
		ENABLE_DISABLE("enable", "disable"),
		ALLOW_DENY("allow", "deny");
		
		private final String TRUE;
		private final String FALSE;
		
		private BINARY(String TRUE, String FALSE) {
			this.TRUE = TRUE;
			this.FALSE = FALSE;
		}
		
		
		/**
		 * Gets the true text of this binary
		 * @return true
		 */
		public String getTrue() {
			return this.TRUE;
		}
		
		
		/**
		 * Gets the false text of this binary
		 * @return false
		 */
		public String getFalse() {
			return this.FALSE;
		}
		
		
		/**
		 * Converts the given string to boolean using this state
		 * @param text string to try to convert to boolean
		 * @return true if converts to true, false if converts to false, null if no found match
		 */
		public Boolean asBoolean(String text) {
			if(text.toLowerCase().equals(TRUE)) return true;
			else if(text.toLowerCase().equals(FALSE)) return false;
			return null;
		}
	}
	
	
	/*@Override
	public void invalidArguments(CommandSender sender) {
		KaranteeniCore.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE +
				KaranteeniCore.getTranslator().getTranslation(
						KaranteeniCore.getPlugin(KaranteeniCore.class), 
						sender, 
						"command.component-error.true-false")
						.replace("%true%", binary.TRUE)
						.replace("%false%", binary.FALSE));
	}*/


	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"command.component-error.true-false", 
				"The given argument must be either %true% or %false%");
	}
}
