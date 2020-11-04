package net.karanteeni.missionnpcs.requirement;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import lombok.Getter;
import lombok.NonNull;
import net.karanteeni.missionnpcs.Saveable;
import net.karanteeni.missionnpcs.mission.Mission;
import net.karanteeni.missionnpcs.mission.MissionRequirement;
import net.karanteeni.missionnpcs.mission.MissionRequirement.RequirementType;

public class Requirements {
	@Getter private final HashMap<String, Class<? extends MissionRequirement>> requirements =
			new HashMap<String, Class<? extends MissionRequirement>>();
	
	public boolean registerRequirement(final String tagName, final Class<? extends MissionRequirement> clazz) {
		if(this.requirements.containsKey(tagName))
			return false;
		
		boolean valid = false;
		for (Constructor<?> constructor : clazz.getConstructors()) {
            Type[] parameterTypes = constructor.getGenericParameterTypes();
            if(parameterTypes.length != 3)
            	continue;
            if(parameterTypes[0].getClass().isAssignableFrom(String.class)
            		&& parameterTypes[1].getClass().isAssignableFrom(RequirementType.class)
            		&& parameterTypes[2].getClass().isAssignableFrom(Mission.class))
            	valid = true;
        }
		
		if(valid) {			
			this.requirements.put(tagName, clazz);
		} else {
			Bukkit.getLogger().log(Level.WARNING, String.format("Registered class %s does not have a valid constructor", clazz.getName()));
		}
		return valid;
	}
	
	
	public Set<String> getRequirementTags() {
		return requirements.keySet();
	}
	
	
	/**
	 * Returns the requirement tag for a given requirement class
	 * @param clazz
	 * @return
	 */
	public String getRequirementTag(Class<? extends MissionRequirement> clazz) {
		for(Entry<String, Class<? extends MissionRequirement>> entry : requirements.entrySet())
		if(entry.getValue().equals(clazz))
			return entry.getKey();
		return null;
	}
	
	
	/**
	 * Attaches the requirements to the given mission, loaded from the given configuration section
	 * that contains the requirements
	 * @param section section that contains the mission requirements
	 * @return mission 
	 */
	public Mission loadAndAttachRequirements(@NonNull Mission mission, @NonNull final ConfigurationSection section) {
		List<MissionRequirement> requirements = new LinkedList<MissionRequirement>();
		
		if(section.isConfigurationSection("start-requirements")) {			
			ConfigurationSection startSection = section.getConfigurationSection("start-requirements");
			requirements.addAll(parseRequirementSection(mission, RequirementType.START, startSection));
		}
		
		if(section.isConfigurationSection("continue-requirements")) {			
			ConfigurationSection continueSection = section.getConfigurationSection("continue-requirements");
			requirements.addAll(parseRequirementSection(mission, RequirementType.CONTINUE, continueSection));
		}

		if(section.isConfigurationSection("end-requirements")) {
			ConfigurationSection endSection = section.getConfigurationSection("end-requirements");
			requirements.addAll(parseRequirementSection(mission, RequirementType.END, endSection));
		}

		for(MissionRequirement requirement : requirements)
			mission.addRequirement(requirement);
		return mission;
	}
	
	
	/**
	 * Parses a section of requirements into a list of requirements
	 * @param mission
	 * @param requirementType
	 * @param section
	 * @return
	 */
	private List<? extends MissionRequirement> parseRequirementSection(
			@NonNull Mission mission,
			@NonNull RequirementType requirementType,
			@NonNull final ConfigurationSection section) {

		List<MissionRequirement> requirements = new LinkedList<MissionRequirement>();
		for(final String requirementKey : section.getKeys(false)) {
			MissionRequirement requirement = parseRequirement(requirementKey, requirementType, mission, section.getConfigurationSection(requirementKey));

			if(requirement != null) {				
				requirements.add(requirement);
			} else {
				Bukkit.getLogger().log(
						Level.WARNING,
						String.format("Could not parse a requirement called %s, "
								+ "either the requirement does not exist or the "
								+ "requirement data was invalid", requirementKey));
			}
		}
		return requirements;
	}
	
	
	/**
	 * Parses a requirement from section into an actual requirement object
	 * @param key
	 * @param requirementType
	 * @param mission
	 * @param section
	 * @return
	 */
	private MissionRequirement parseRequirement(
			@NonNull final String key,
			@NonNull RequirementType requirementType,
			@NonNull Mission mission,
			@NonNull final ConfigurationSection section) {
		if(!this.requirements.containsKey(key))
			return null;
		
		Class<? extends MissionRequirement> requirementClass = this.requirements.get(key);
		MissionRequirement requirement = null;
		try {
			Constructor<? extends MissionRequirement> ctr = requirementClass.getConstructor(String.class, RequirementType.class, Mission.class);
			requirement = ctr.newInstance(key, requirementType, mission);
			
			for(Field field : requirement.getClass().getDeclaredFields())
			if(field.isAnnotationPresent(Saveable.class))
				field.set(requirement, section.get(field.getName()));
		} catch (NoSuchMethodException
				| SecurityException
				| InstantiationException
				| IllegalAccessException				
				| InvocationTargetException
				| IllegalArgumentException e) {
			e.printStackTrace();
		}
		return requirement;
	}
}
