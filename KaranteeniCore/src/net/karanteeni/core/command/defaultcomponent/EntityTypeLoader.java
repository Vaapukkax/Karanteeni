package net.karanteeni.core.command.defaultcomponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandLoader;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.translation.TranslationContainer;

/**
 * Loads List of players given in parameters and sets them into data with key "core.players" and if
 * only one players is requested it is set to "core.player". if no data is set then no players has been
 * searched.
 * @author Nuubles
 *
 */
public class EntityTypeLoader extends CommandLoader implements TranslationContainer {
	private boolean singular;
	private boolean mandatory;
	private Class<? extends Entity> entityClass;
	public final static String ENTITY_KEY_MULTIPLE = "core.entities";
	public final static String ENTITY_KEY_SINGLE = "core.entity";
	
	/**
	 * Initializes the player loader class
	 * @param before load before the attached component
	 * @param showBukkit show players also on other servers
	 * @param singular do we require only 1 player
	 * @param is it mandatory to type the requested player names
	 */
	public EntityTypeLoader(boolean before, boolean singular, boolean mandatory) {
		super(before);
		this.singular = singular;
		this.mandatory = mandatory;
	}
	
	
	public EntityTypeLoader(boolean before, boolean singular, boolean mandatory, Class<? extends Entity> entityClass) {
		super(before);
		this.singular = singular;
		this.mandatory = mandatory;
		this.entityClass = entityClass;
	}

	
	/**
	 * Initializes the player loader class
	 * @param loader loader to load after this loader
	 * @param before load before the attached component
	 * @param showBukkit show players also on other servers
	 * @param singular do we require only 1 player
	 * @param is it mandatory to type the requested player names
	 */
	public EntityTypeLoader(CommandLoader loader, boolean before, boolean singular, boolean mandatory) {
		super(loader, before);
		this.singular = singular;
		this.mandatory = mandatory;
	}

	
	@Override
	protected void onRegister() {
		registerTranslations();
	}
	

	@Override
	protected CommandResult runComponent(CommandSender sender, Command cmd, String label, String[] args) {
		// no players has been requested
		if(args.length == 0) {
			if(mandatory)
				return CommandResult.INVALID_ARGUMENTS;
			return CommandResult.SUCCESS;
		}
		
		// get requested players
		List<EntityType> materials = new ArrayList<EntityType>();
		String[] args_ = args[0].split(",");
		
		for(String arg : args_) {
			try {
				EntityType type = EntityType.valueOf(arg.toUpperCase());
				if(entityClass != null && (type.getEntityClass() == null || !entityClass.isAssignableFrom(type.getEntityClass())))
					return CommandResult.INVALID_ARGUMENTS;
				materials.add(type);
			} catch(Exception e) {
				
			}
		}
		
		
		// if there are more players than requested, give error
		if(singular && materials.size() > 1) {
			return new CommandResult(
					KaranteeniCore.getTranslator().getTranslation(
							KaranteeniCore.getPlugin(KaranteeniCore.class), 
							sender, 
							"entities.too-many"),
					ResultType.INVALID_ARGUMENTS,
					Sounds.NO.get());
		} else if(singular && materials.size() == 0) {
			if(mandatory) {
				// no entities found with given arguments
				return CommandResult.INVALID_ARGUMENTS;
			} else {
				return CommandResult.SUCCESS;
			}
		}
		
		// set the data of found players
		if(singular)
			this.chainer.setObject(ENTITY_KEY_SINGLE, materials.get(0));
		else
			this.chainer.setObject(ENTITY_KEY_MULTIPLE, materials);
		
		return CommandResult.SUCCESS;
	}
	
	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1 || args.length > 1)
			return null;
		
		// split the string into multiple parts
		String[] parts = args[0].split(",");
		Character lastChar = (args[0].length() != 0) ? args[0].charAt(args[0].length()-1) : null;
		String res = args[0];
		
		// get all player names
		EntityType[] materials = EntityType.values();
		Set<String> playerNames = new HashSet<String>();
		for(EntityType material : materials) {
			if(entityClass == null || (material.getEntityClass() != null && entityClass.isAssignableFrom(material.getEntityClass())))
				playerNames.add(material.name().toLowerCase());
		}
		
		// at the beginning of arg or comma so requires special handling
		if(lastChar == null || lastChar.charValue() == ',') {
			List<String> results = new ArrayList<String>();
			// add all compatible names to the results array
			for(String name : playerNames)
				results.add(res + name);
			return results;
		}
		
		// get the last part of players
		String lastPart = parts[parts.length-1].toLowerCase();
		
		// replace the last letters with nothing from the result to allow gluing
		res = res.substring(0, res.length()-lastPart.length());
		
		List<String> results = new ArrayList<String>();
		
		// add all compatible names to the results array
		for(String name : playerNames)
			if(name.toLowerCase().startsWith(lastPart))
				results.add(res + name);
		
		return results;
	}


	@Override
	public void registerTranslations() {
		KaranteeniCore.getTranslator().registerTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				"entities.too-many", 
				"Too many entities found, please specify your search parameters");
	}
}
