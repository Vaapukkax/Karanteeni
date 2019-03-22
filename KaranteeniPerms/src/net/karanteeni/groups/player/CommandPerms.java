package net.karanteeni.groups.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.AbstractCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.groups.KaranteeniPerms;
import net.karanteeni.groups.player.PermissionPlayer.DATA_TYPE;
import net.md_5.bungee.api.ChatColor;

public class CommandPerms extends AbstractCommand implements TranslationContainer {

	private static KaranteeniPerms perms;
	private static String PATH_PLAYER_HAS_PERMISSION 			= "permissions.player.has-permission";
	private static String PATH_PLAYER_DOES_NOT_HAVE_PERMISSION 	= "permissions.player.does-not-have-permission";
	private static String PATH_GROUP_HAS_PERMISSION 			= "permissions.group.has-permission";
	private static String PATH_GROUP_DOES_NOT_HAVE_PERMISSION 	= "permissions.group.does-not-have-permission";
	
	private static String PATH_GROUP_ADDED_PERMISSION 			= "permissions.group.permission-added";
	private static String PATH_GROUP_REMOVED_PERMISSION 		= "permissions.group.permission-removed";
	private static String PATH_PLAYER_ADDED_PERMISSION 			= "permissions.player.permission-addded";
	private static String PATH_PLAYER_REMOVED_PERMISSION 		= "permissions.player.permission-removed";
	
	private static String PATH_GROUP_DOES_NOT_EXIST 			= "permissions.group.does-not-exist";
	
	private static String PATH_GROUP_RECOMMENDATION 			= "permissions.group.tag-recommended";
	
	private static String PATH_GROUP_SET_PREFIX 				= "permissions.group.set.prefix";
	private static String PATH_GROUP_SET_SUFFIX 				= "permissions.group.set.suffix";
	private static String PATH_PLAYER_SET_PREFIX 				= "permissions.player.set.prefix";
	private static String PATH_PLAYER_SET_SUFFIX 				= "permissions.player.set.suffix";
	
	private static String PATH_GROUP_SET_NAME_LONG 				= "permissions.group.set.rankname.long";
	private static String PATH_GROUP_SET_NAME_SHORT 			= "permissions.group.set.rankname.short";
	private static String PATH_PLAYER_SET_NAME_LONG 			= "permissions.player.set.rankname.long";
	private static String PATH_PLAYER_SET_NAME_SHORT 			= "permissions.player.set.rankname.short";
	private static String PATH_PLAYER_GROUP_SET					= "permissions.player.set.group";
	
	private static String PATH_PLAYER_RESET_PREFIX 				= "permissions.player.reset.prefix";
	private static String PATH_PLAYER_RESET_SUFFIX 				= "permissions.player.reset.suffix";
	private static String PATH_PLAYER_RESET_NAME_SHORT 			= "permissions.player.reset.name.short";
	private static String PATH_PLAYER_RESET_NAME_LONG 			= "permissions.player.reset.name.long";
			
	private static String PATH_SAVE_FAILED 						= "permissions.save-failed";
	
	private static String TAG_GROUP 		= "%group%";
	private static String TAG_PLAYER 		= "%player%";
	private static String TAG_PERMISSION 	= "%permission%";
	private static String TAG_PREFIX 		= "%prefix%";
	private static String TAG_SUFFIX 		= "%suffix%";
	private static String TAG_LANGUAGE 		= "%lang%";
	private static String TAG_NAME 			= "%name%";
	private static String PREFIX 			= "prefix";
	private static String SUFFIX 			= "suffix";
	private static String RANK_NAME 		= "rankname";
	private static String SET 				= "set";
	private static String ADD 				= "add";
	private static String REMOVE 			= "remove";
	private static String PLAYER 			= "player";
	private static String GROUP 			= "group";
	private static String LONG 				= "long";
	private static String SHORT 			= "short";
	private static String RESET 			= "reset";
	

	public CommandPerms(KaranteeniPlugin plugin, String command, String usage, String description,
			String permissionMessage) 
	{
		super(plugin, command, usage, description, permissionMessage, 
				Arrays.asList(PREFIX,SUFFIX,RANK_NAME,ADD,SET,REMOVE,PLAYER,GROUP,LONG,SHORT,RESET));
		this.registerTranslations();
		CommandPerms.perms = (KaranteeniPerms)plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		
		ParamChecker checker = new ParamChecker(args);
		
		//If invalid parameters then cancel
		if(checker.invalidParameters()) {
			this.sendError(sender);
			return true;
		}
		Group group = null;
		UUID uuid = null;
		String playername = "";
		
		if(checker.isGroup()) {
			group = CommandPerms.perms.getLocalGroup(checker.getPlayerGroupName());
			if(group == null) //Group was not found 
			{
				this.sendInvalidGroup(sender, checker.getPlayerGroupName());
				return true;
			}
		}
		else {
			uuid = KaranteeniPlugin.getPlayerHandler().getUUID(checker.getPlayerGroupName());
			if(uuid == null) //Player was not found 
			{
				KaranteeniPlugin.getDefaultMsgs().playerNotFound(sender, checker.getPlayerGroupName());
				return true;
			}
			playername = KaranteeniPlugin.getPlayerHandler().getOfflineName(uuid);
			
			//Case for when setting player group /permissions set player group <player> <group>
			if(checker.isGroupChange())
			{
				group = CommandPerms.perms.getLocalGroup(checker.getNewName());
				if(group == null) //Group was not found 
				{
					this.sendInvalidGroup(sender, checker.getPlayerGroupName());
					return true;
				}
			}
		}
		
		if(checker.isAdd()) {
			if(checker.isPlayer())
				this.addPlayerPermission(sender, uuid, playername, checker.getPermissions());
			else
				this.addGroupPermission(sender, group, checker.getPermissions());
		}
		else if(checker.isRemove()) {
			if(checker.isPlayer())
				this.removePlayerPermission(sender, uuid, playername, checker.getPermissions());
			else
				this.removeGroupPermission(sender, group, checker.getPermissions());
		} 
		else if(checker.isSet()) {
			if(checker.isPrefix())
				if(checker.isPlayer())
					this.setPlayerPrefix(sender, uuid, playername, checker.getNewName());
				else
					this.setGroupPrefix(sender, group, checker.getNewName());
			else if(checker.isSuffix())
				if(checker.isPlayer())
					this.setPlayerSuffix(sender, uuid, playername, checker.getNewName());
				else
					this.setGroupSuffix(sender, group, checker.getNewName());
			else if(checker.isRankName())
				if(checker.isPlayer())
					this.setPlayerRankName(sender, uuid, playername, checker.getNewName(), checker.isLong());
				else
					this.setGroupRankName(sender, group, checker.getNewName(), checker.getLanguage(), checker.isLong());
			else if(checker.isGroupChange())
				if(checker.isPlayer())
					this.setPlayerGroup(sender, uuid, checker.getPlayerGroupName(), group);
		}
		else if(checker.isReset()) {
			if(checker.isPrefix()) {
				this.resetPlayerPrefix(sender, uuid, playername);
			}
			else if(checker.isSuffix()) {
				this.resetPlayerSuffix(sender, uuid, playername);
			}
			else if(checker.isRankName()) {
				this.resetPlayerRankName(sender, uuid, playername, checker.isLong());
			}
		}
		else
			this.sendError(sender);
		
		return true;
	}
	
	private void setPlayerGroup(CommandSender sender, UUID uuid, String playerName, Group group)
	{
		if(!sender.hasPermission("karanteeniperms.player.group")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		
		if(!perms.getPlayerModel().setLocalGroup(uuid, group))
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), Prefix.NEGATIVE.toString(), 
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
			return;
		}
		
		KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
				Prefix.NEUTRAL+
				KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_GROUP_SET)
				.replace(TAG_GROUP, group.getID())
				.replace(TAG_PLAYER, playerName));
	}
	
	private void addPlayerPermission(CommandSender sender, UUID uuid, String playername, List<String> permission)
	{
		if(!sender.hasPermission("karanteeniperms.player.permission.add")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		
		for(int i = 0; i < permission.size(); ++i) //Loop all permissions and add them to the player
		{
			if(player.hasPermission(DATA_TYPE.PLAYER, permission.get(i))) { //Does player already have permission
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
						Prefix.NEGATIVE + 
						KaranteeniPlugin.getTranslator().getTranslation(this.plugin, sender, PATH_PLAYER_HAS_PERMISSION)
						.replace(TAG_PERMISSION, permission.get(i))
						.replace(TAG_PLAYER, playername));
				continue;
			}
			
			player.addPermission(permission.get(i));
			
			//perms.getPlayerModel().addAndSavePlayerPermission(uuid, permission.get(i), i+1 == permission.size());
			
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), //Send message that a permission has been added
					Prefix.NEUTRAL + 
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_ADDED_PERMISSION)
					.replace(TAG_PERMISSION, permission.get(i))
					.replace(TAG_PLAYER, playername));
		}
	}
	
	/**
	 * Adds a given permission to group
	 * @param sender
	 * @param groupID
	 * @param permission
	 */
	private void addGroupPermission(CommandSender sender, Group group, List<String> permission)
	{
		if(!sender.hasPermission("karanteeniperms.group.permission.add")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		boolean modified = false;;
		for(String perm : permission) //Loop all permissions
		{
			if(!group.addPermission(perm, true)) { //Group already has this permission! Don't add it!
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
						Prefix.NEGATIVE +
						KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_HAS_PERMISSION)
						.replace(TAG_GROUP, group.getID())
						.replace(TAG_PERMISSION, perm));
				continue;
			}
			
			modified = true;
			
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), //Permission added to group!
					Prefix.NEUTRAL +
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_ADDED_PERMISSION)
					.replace(TAG_PERMISSION, perm)
					.replace(TAG_GROUP, group.getID()));
		}
		
		if(modified) //Save group if it has been modified
			group.saveGroup();
	}
	
	/**
	 * Removes a given permission from player
	 * @param sender
	 * @param uuid
	 * @param playername
	 * @param permission
	 */
	private void removePlayerPermission(CommandSender sender, UUID uuid, String playername, List<String> permission)
	{
		if(!sender.hasPermission("karanteeniperms.player.permission.remove")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		
		for(int i = 0; i < permission.size(); ++i) //Loop all permissions and add them to the player
		{
			if(!player.hasPermission(DATA_TYPE.PLAYER, permission.get(i))) { //Does player already have permission
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
						Prefix.NEGATIVE + 
						KaranteeniPlugin.getTranslator().getTranslation(this.plugin, sender, PATH_PLAYER_DOES_NOT_HAVE_PERMISSION)
						.replace(TAG_PERMISSION, permission.get(i))
						.replace(TAG_PLAYER, playername));
				continue;
			}
			player.removePermission(permission.get(i));
			
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), //Send message that a permission has been added
					Prefix.NEUTRAL + 
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_REMOVED_PERMISSION)
					.replace(TAG_PERMISSION, permission.get(i))
					.replace(TAG_PLAYER, playername));
		}
	}
	
	/**
	 * Removes a given permission from group
	 * @param sender
	 * @param group
	 * @param permission
	 */
	private void removeGroupPermission(CommandSender sender, Group group, List<String> permission)
	{
		if(!sender.hasPermission("karanteeniperms.group.permission.remove")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		boolean modified = false;;
		for(String perm : permission) //Loop all permissions
		{
			if(!group.removePermission(perm, true)) { //Group does not have this permission?
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
						Prefix.NEGATIVE +
						KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_DOES_NOT_HAVE_PERMISSION)
						.replace(TAG_GROUP, group.getID())
						.replace(TAG_PERMISSION, perm));
				continue;
			}
			
			modified = true;
			
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), //Permission added to group!
					Prefix.NEUTRAL +
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_REMOVED_PERMISSION)
					.replace(TAG_PERMISSION, perm)
					.replace(TAG_GROUP, group.getID()));
		}
		
		if(modified) //Save group if it has been modified
			group.saveGroup();
	}
	
	/**
	 * Sets the prefix of given group
	 * @param sender
	 * @param group
	 * @param prefix
	 */
	private void setGroupPrefix(CommandSender sender, Group group, String prefix)
	{
		if(!sender.hasPermission("karanteeniperms.group.prefix")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		if(group.setPrefix(prefix))
		{
			if(!prefix.contains(TAG_GROUP)) {
				KaranteeniPlugin.getMessager().sendMessage(
						sender, Sounds.SETTINGS.get(), 
						Prefix.NEUTRAL +
						KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_RECOMMENDATION));
			}
			KaranteeniPlugin.getMessager().sendMessage(
					sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL +
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_SET_PREFIX)
					.replace(TAG_GROUP, group.getID())
					.replace(TAG_PREFIX, prefix));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), Prefix.NEGATIVE.toString(), 
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	/**
	 * Sets the prefix of given player
	 * @param sender
	 * @param uuid
	 * @param playerName
	 * @param prefix
	 */
	private void setPlayerPrefix(CommandSender sender, UUID uuid, String playerName, String prefix)
	{
		if(!sender.hasPermission("karanteeniperms.player.prefix")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		
		if(player.setPrefix(prefix))
		{
			if(!prefix.contains(TAG_GROUP)) {
				KaranteeniPlugin.getMessager().sendMessage(
						sender, Sounds.SETTINGS.get(), 
						Prefix.NEUTRAL +
						KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_RECOMMENDATION));
			}
			KaranteeniPlugin.getMessager().sendMessage(
					sender, Sounds.NONE.get(), 
					Prefix.NEUTRAL +
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_SET_PREFIX)
					.replace(TAG_PLAYER, playerName)
					.replace(TAG_PREFIX, prefix));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(
					sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	/**
	 * Sets the suffix of given group
	 * @param sender
	 * @param group
	 * @param suffix
	 */
	private void setGroupSuffix(CommandSender sender, Group group, String suffix)
	{
		if(!sender.hasPermission("karanteeniperms.group.suffix")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		if(group.setSuffix(suffix))
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_SET_SUFFIX)
					.replace(TAG_SUFFIX, suffix)
					.replace(TAG_GROUP, group.getID()));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	/**
	 * Sets the suffix of given player to given value
	 * @param sender
	 * @param uuid
	 * @param playerName
	 * @param suffix
	 */
	private void setPlayerSuffix(CommandSender sender, UUID uuid, String playerName, String suffix)
	{
		if(!sender.hasPermission("karanteeniperms.player.suffix")) {
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		
		if(player.setSuffix(suffix))
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NONE.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_SET_SUFFIX)
					.replace(TAG_SUFFIX, suffix)
					.replace(TAG_PLAYER, playerName));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	/**
	 * Sets the rankname of group to given locale, value and long or short format
	 * @param sender
	 * @param group
	 * @param newName
	 * @param locale
	 * @param isLong
	 */
	private void setGroupRankName(CommandSender sender, Group group, String newName, Locale locale, boolean isLong)
	{
		if((isLong && !sender.hasPermission("karanteeniperms.group.rankname.long")) || 
				(!isLong && !sender.hasPermission("karanteeniperms.group.rankname.short"))) {
			this.sendNoPermissions(sender);
			return;
		}
		
		group.setName(locale, newName, isLong);
		
		if(isLong)
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_SET_NAME_LONG)
					.replace(TAG_GROUP, group.getID())
					.replace(TAG_LANGUAGE, locale.toLanguageTag())
					.replace(TAG_NAME, newName));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_GROUP_SET_NAME_SHORT)
					.replace(TAG_GROUP, group.getID())
					.replace(TAG_LANGUAGE, locale.toLanguageTag())
					.replace(TAG_NAME, newName));
		}
	}
	
	/**
	 * Sets the rankname of given player to given value and short or long format
	 * @param sender
	 * @param uuid
	 * @param playerName
	 * @param newName
	 * @param isLong
	 */
	private void setPlayerRankName(CommandSender sender, UUID uuid, String playerName, String newName, boolean isLong)
	{
		if((isLong && !sender.hasPermission("karanteeniperms.player.rankname.long")) || 
				(!isLong && !sender.hasPermission("karanteeniperms.player.rankname.short"))) {
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		
		if(isLong)
		{
			if(player.setLocalGroupName(newName))
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_SET_NAME_LONG)
					.replace(TAG_PLAYER, playerName)
					.replace(TAG_NAME, newName));
			else
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
		else
		{
			if(player.setLocalGroupShortName(newName))
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_SET_NAME_SHORT)
					.replace(TAG_PLAYER, playerName)
					.replace(TAG_NAME, newName));
			else
				KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
						Prefix.NEGATIVE+
						KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	private void resetPlayerPrefix(CommandSender sender, UUID uuid, String playername)
	{
		if(!sender.hasPermission("karanteeniperms.player.prefix"))
		{
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		if(player.resetPrefix())
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_RESET_PREFIX)
					.replace(TAG_PLAYER, playername));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	private void resetPlayerSuffix(CommandSender sender, UUID uuid, String playername)
	{
		if(!sender.hasPermission("karanteeniperms.player.suffix"))
		{
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		if(player.resetPrefix())
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_RESET_SUFFIX)
					.replace(TAG_PLAYER, playername));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	private void resetPlayerRankName(CommandSender sender, UUID uuid, String playername, boolean isLong)
	{
		if((isLong && !sender.hasPermission("karanteeniperms.player.rankname.long")) || 
				(!isLong && !sender.hasPermission("karanteeniperms.player.rankname.short")))
		{
			this.sendNoPermissions(sender);
			return;
		}
		
		PermissionPlayer player = perms.getPlayerModel().getPermissionPlayer(uuid);
		if(isLong && player.resetLocalGroupName())
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_RESET_NAME_LONG)
					.replace(TAG_PLAYER, playername));
		}
		else if(!isLong && player.resetLocalGroupShortName())
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.SETTINGS.get(), 
					Prefix.NEUTRAL+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_PLAYER_RESET_NAME_SHORT)
					.replace(TAG_PLAYER, playername));
		}
		else
		{
			KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.ERROR.get(), 
					Prefix.NEGATIVE+
					KaranteeniPlugin.getTranslator().getTranslation(perms, sender, PATH_SAVE_FAILED));
		}
	}
	
	/**
	 * Sends the invalid parameters message to player
	 * @param sender
	 */
	private void sendError(CommandSender sender)
	{
		KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE.toString() + KaranteeniPlugin.getDefaultMsgs().incorrectParameters(sender));
	}
	
	/**
	 * Sends the no permission message to player
	 * @param sender
	 */
	private void sendNoPermissions(CommandSender sender)
	{
		KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE.toString() + KaranteeniPlugin.getDefaultMsgs().noPermission(sender));
	}
	
	private void sendInvalidGroup(CommandSender sender, String groupName)
	{
		KaranteeniPlugin.getMessager().sendMessage(sender, Sounds.NO.get(), 
				Prefix.NEGATIVE.toString() + KaranteeniPlugin.getTranslator().getTranslation(
						this.plugin, sender, PATH_GROUP_DOES_NOT_EXIST).replace(TAG_GROUP, groupName));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerTranslations() {
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_HAS_PERMISSION, 
				"Player "+TAG_PLAYER+" already has private permission "+TAG_PERMISSION+"!");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_DOES_NOT_HAVE_PERMISSION, 
				"Player "+TAG_PLAYER+" does not have private permission "+TAG_PERMISSION+"!");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_HAS_PERMISSION, 
				"Group "+TAG_GROUP+" already has permission "+TAG_PERMISSION+"!");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_DOES_NOT_HAVE_PERMISSION, 
				"Group "+TAG_GROUP+" does not have permission "+TAG_PERMISSION+"!");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_DOES_NOT_HAVE_PERMISSION, 
				"Group "+TAG_GROUP+" does not have permission "+TAG_PERMISSION+"!");
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_DOES_NOT_EXIST, 
				"No group with ID "+TAG_GROUP+" could be found!");
		
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_ADDED_PERMISSION, 
				"Added permission "+TAG_PERMISSION+" to group " + TAG_GROUP);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_REMOVED_PERMISSION, 
				"Removed permission "+TAG_PERMISSION+" to group " + TAG_GROUP);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_ADDED_PERMISSION, 
				"Added private permission "+TAG_PERMISSION+" to player " + TAG_PLAYER);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_REMOVED_PERMISSION, 
				"Removed private permission "+TAG_PERMISSION+" to player " + TAG_PLAYER);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_RECOMMENDATION,
				"It is recommended to use tag "+TAG_GROUP+" in prefix to allow "
						+ "translation engine input the translated group name into prefix!");
		
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_SET_PREFIX, 
				"Prefix of group "+TAG_GROUP+" was set to §r"+TAG_PREFIX);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_SET_SUFFIX, 
				"Suffix of group "+TAG_GROUP+" was set to §r"+TAG_SUFFIX);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_SET_PREFIX, 
				"Prefix of player "+TAG_PLAYER+" was set to §r"+TAG_PREFIX);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_SET_SUFFIX, 
				"Prefix of group "+TAG_PLAYER+" was set to §r"+TAG_SUFFIX);
		
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_SET_NAME_LONG, 
				"Set the long name of group "+TAG_GROUP+" in language "+TAG_LANGUAGE+" to §r"+TAG_NAME);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_GROUP_SET_NAME_SHORT, 
				"Set the short name of group "+TAG_GROUP+" in language "+TAG_LANGUAGE+" to §r"+TAG_NAME);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_SET_NAME_LONG, 
				"Set the long name of player "+TAG_PLAYER+" to §r"+TAG_NAME);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_SET_NAME_SHORT, 
				"Set the short name of player "+TAG_PLAYER+" to §r"+TAG_NAME);
		
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_RESET_NAME_LONG, 
				"Resetted the long group name of player "+TAG_PLAYER);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_RESET_NAME_SHORT, 
				"Resetted the short group name of player "+TAG_PLAYER);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_RESET_PREFIX, 
				"Resetted the prefix of player "+TAG_PLAYER);
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_RESET_SUFFIX, 
				"Resetted the suffix of player "+TAG_PLAYER);
		
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_PLAYER_GROUP_SET, 
				"Set the group of "+TAG_PLAYER+" to " + TAG_GROUP);
		
		KaranteeniPlugin.getTranslator().registerTranslation(
				this.plugin, PATH_SAVE_FAILED, "Failed to save modifications!");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args)
	{
		if(args.length == 1)
			return filterByPrefix(Arrays.asList(GROUP, PLAYER), args[0]);
		
		if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase(GROUP))
				return filterByPrefix(Arrays.asList(SET,ADD,REMOVE), args[1]);
			else if(args[0].equalsIgnoreCase(PLAYER))
				return filterByPrefix(Arrays.asList(SET,ADD,REMOVE,RESET), args[1]);
		}
		
		if(args.length == 3)
		{
			if(args[0].equalsIgnoreCase(GROUP))
			{
				if(args[1].equalsIgnoreCase(SET))
					return filterByPrefix(Arrays.asList(PREFIX,SUFFIX,RANK_NAME), args[2]);
				else if(args[1].equalsIgnoreCase(ADD))
					return filterByPrefix(getGroupNames(), args[2]);
				else if(args[1].equalsIgnoreCase(REMOVE))
					return filterByPrefix(getGroupNames(), args[2]);
			}
			else if(args[0].equalsIgnoreCase(PLAYER))
			{
				if(args[1].equalsIgnoreCase(SET))
					return filterByPrefix(Arrays.asList(PREFIX,SUFFIX,RANK_NAME,GROUP), args[2]);
				else if(args[1].equalsIgnoreCase(ADD))
					return filterByPrefix(getPlayerNames(), args[2]);
				else if(args[1].equalsIgnoreCase(REMOVE))
					return filterByPrefix(getPlayerNames(), args[2]);
				else if(args[1].equalsIgnoreCase(RESET))
					return filterByPrefix(Arrays.asList(PREFIX,SUFFIX,RANK_NAME), args[2]);
			}
		}
		
		if(args.length == 4)
		{
			if(args[0].equalsIgnoreCase(GROUP))
			{
				if(args[1].equalsIgnoreCase(SET))
				{
					if(args[2].equalsIgnoreCase(PREFIX))
						return filterByPrefix(getGroupNames(), args[3]);
					else if(args[2].equalsIgnoreCase(SUFFIX))
						return filterByPrefix(getGroupNames(), args[3]);
					else if(args[2].equalsIgnoreCase(RANK_NAME))
						return filterByPrefix(Arrays.asList(LONG,SHORT), args[3]);
				}
			}
			else if(args[0].equalsIgnoreCase(PLAYER))
			{
				if(args[1].equalsIgnoreCase(SET))
				{
					if(args[2].equalsIgnoreCase(PREFIX))
						return filterByPrefix(getPlayerNames(), args[3]);
					else if(args[2].equalsIgnoreCase(SUFFIX))
						return filterByPrefix(getPlayerNames(), args[3]);
					else if(args[2].equalsIgnoreCase(GROUP))
						return filterByPrefix(getPlayerNames(), args[3]);
					else if(args[2].equalsIgnoreCase(RANK_NAME))
						return filterByPrefix(Arrays.asList(LONG,SHORT), args[3]);
				}
				else if(args[1].equalsIgnoreCase(RESET))
				{
					if(args[2].equalsIgnoreCase(RANK_NAME))
						return filterByPrefix(Arrays.asList(LONG,SHORT), args[3]);
					return filterByPrefix(getPlayerNames(), args[3]);
				}
			}
		}
		
		if(args.length == 5)
		{
			if(args[0].equalsIgnoreCase(GROUP))
			{
				if(args[1].equalsIgnoreCase(SET))
				{
					if(args[2].equalsIgnoreCase(RANK_NAME))
						if(args[3].equalsIgnoreCase(LONG) || args[3].equalsIgnoreCase(SHORT))
						{
							List<String> locales = new ArrayList<String>();
							for(Locale loc : KaranteeniPerms.getTranslator().getLocales())
								locales.add(loc.toLanguageTag());
							return filterByPrefix(locales, args[4]);
						}
				}
			}
			else if(args[0].equalsIgnoreCase(PLAYER))
				if(args[1].equalsIgnoreCase(SET))
				{
					if(args[2].equalsIgnoreCase(RANK_NAME))
						return filterByPrefix(getPlayerNames(), args[4]);
					else if(args[2].equalsIgnoreCase(GROUP))
						return filterByPrefix(getGroupNames(), args[4]);
				}
		}
		
		if(args.length == 6)
			if(args[0].equalsIgnoreCase(GROUP))
				if(args[1].equalsIgnoreCase(SET))
					if(args[2].equalsIgnoreCase(RANK_NAME))
						if(args[3].equalsIgnoreCase(LONG) || args[3].equalsIgnoreCase(SHORT))
							return filterByPrefix(getGroupNames(), args[5]);
		
		return null;
	}
	
	/**
	 * Returns the group names as string list
	 * @return
	 */
	private List<String> getGroupNames()
	{
		List<String> groups = new ArrayList<String>();
		for(Group group : perms.getGroupModel().getLocalGroupList().getGroups())
			groups.add(group.getID());
		return groups;
	}
	
	/**
	 * Returns the playernames as string list
	 * @return
	 */
	private List<String> getPlayerNames()
	{
		List<String> players = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers())
			players.add(player.getName());
		return players;
	}
	
	/**
	 * Class which is used to convey information about
	 * the params in command.
	 * Yes, this code is horrible
	 * @author Nuubles
	 *
	 */
	private class ParamChecker
	{
		private boolean invalidParameters = false;
		private boolean playerOrGroup = false;
		private byte addRemoveSet = -1;
		private byte prefixSuffixRankName = -1;
		private boolean longRank = false;
		private Locale lang = null;
		private String playerGroupName;
		private String newName = "";
		private List<String> permissions = new ArrayList<String>();
		
		ParamChecker(String[] params)
		{
			if(params == null || params.length == 0) {
				this.invalidParameters = true;
				return;
			}
			
			//Manage player or group
			parsePlayerGroup(CommandPerms.this.getRealParam(params[0]));
			
			//Is the param add or remove or set
			if(params.length > 1)
				parseAddRemoveSet(CommandPerms.this.getRealParam(params[1]));
			else {
				this.invalidParameters = true;
				return;
			}
			
			if(this.invalidParameters)
				return;
			
			if(this.addRemoveSet == 2 || this.addRemoveSet == 3) //Param was "SET" or "RESET"
			{
				if(this.addRemoveSet == 3 && this.isGroup()) { //If was reset and was group, invalid params
					this.invalidParameters = true;
					return;
				}
				
				
				if(params.length < 3)
				{
					this.invalidParameters = true;
					return;
				}
				parsePrefixSuffixRankName(CommandPerms.this.getRealParam(params[2]));
				
				if(this.prefixSuffixRankName == 2) //Was RANKNAME
				{
					if((!this.isReset() && this.isGroup() && params.length < 7) || 
							(!this.isReset() && this.isPlayer() && params.length < 5) || 
							this.invalidParameters) {
						this.invalidParameters = true;
						return;
					}
					
					if(params.length < 4)
					{
						this.invalidParameters = true;
						return;
					}
					// /permission group set rankname long fi-FI myGroup oofington
					parseLongShort(CommandPerms.this.getRealParam(params[3]));
					
					if(this.isGroup())
					{
						parseLang(params[4]);
						this.playerGroupName = params[5];
						
						for(int i = 6; i < params.length; ++i) { //Loop all the rest params to get the correct name
							this.newName += params[i];
							if(i+1 != params.length)
								this.newName += " "; //Add a space since there's more coming
						}
					}
					else if(this.isPlayer())
					{
						if(this.addRemoveSet == 3) //Is Reset
						{
							if(params.length != 5) {
								this.invalidParameters = true;
								return;
							}
							this.parseLongShort(params[3]);
							if(this.invalidParameters)
								return;
							
							this.playerGroupName = params[4];
						}
						else
						{
							// /permissions player set rankname long <nimi>
							for(int i = 4; i < params.length; ++i) { //Loop all the rest params to get the correct name
								this.newName += params[i];
								if(i+1 != params.length)
									this.newName += " "; //Add a space since there's more coming
							}
						}
					}
					else
						this.invalidParameters = true;
					// Color the new name correctly
					this.newName = ChatColor.translateAlternateColorCodes('&', this.newName);
				}
				else if(this.prefixSuffixRankName == 3) //WAS new group set
				{
					if(this.isGroup() || params.length < 5) {
						this.invalidParameters = true;
						return;
					}
					
					this.playerGroupName = params[3];
					this.newName = params[4];
				}
				else //WAS prefix or suffix
				{
					if((!this.isReset() && params.length < 5) || this.invalidParameters) {
						this.invalidParameters = true;
						return;
					}
					// /permission group set prefix <groupname> <prefix>
					this.playerGroupName = params[3];
					
					for(int i = 4; i < params.length; ++i) { //Loop all the rest params to get the correct name
						this.newName += params[i];
						if(i+1 != params.length)
							this.newName += " "; //Add a space since there's more coming
					}
					// Color the new name correctly
					this.newName = ChatColor.translateAlternateColorCodes('&', this.newName);
				}
			}
			else if(this.addRemoveSet < 2) //Param was ADD or REMOVE
			{
				// /permissions player add <name> <permission>
				if(params.length < 4) {
					this.invalidParameters = true;
					return;
				}

				playerGroupName = params[2]; //Get the name of the player/group
				
				for(int i = 3; i < params.length; ++i) //Get all the permissions
					this.permissions.add(params[i]);
			}
		}
		
		public String getNewName()
		{ return this.newName; }
		
		public List<String> getPermissions()
		{ return this.permissions; }
		
		public String getPlayerGroupName()
		{ return this.playerGroupName; }
		
		public Locale getLanguage()
		{ return this.lang; }
		
		public boolean isPlayer()
		{ return !this.playerOrGroup; }
		
		public boolean isGroup()
		{ return this.playerOrGroup; }
		
		public boolean isAdd()
		{ return this.addRemoveSet == 0; }
		
		public boolean isRemove()
		{ return this.addRemoveSet == 1; }
		
		public boolean isSet()
		{ return this.addRemoveSet == 2; }
		
		public boolean isPrefix()
		{ return this.prefixSuffixRankName == 0; }
		
		public boolean isSuffix()
		{ return this.prefixSuffixRankName == 1; }
		
		public boolean isRankName()
		{ return this.prefixSuffixRankName == 2; }
		
		public boolean isGroupChange()
		{ return this.prefixSuffixRankName == 3; }
		
		@SuppressWarnings("unused")
		public boolean isShort()
		{ return !this.longRank; }
		
		public boolean isReset()
		{ return this.addRemoveSet == 3; }
		
		public boolean isLong()
		{ return this.longRank; }
		
		private void parsePlayerGroup(String param)
		{
			if(param.equals(CommandPerms.GROUP))
				playerOrGroup = true;
			else if(param.equals(CommandPerms.PLAYER))
				playerOrGroup = false;
			else
				this.invalidParameters = true;
		}
		
		private void parseAddRemoveSet(String param)
		{
			if(param.equals(CommandPerms.ADD))
				addRemoveSet = 0;
			else if(param.equals(CommandPerms.REMOVE))
				addRemoveSet = 1;
			else if(param.equals(CommandPerms.SET))
				addRemoveSet = 2;
			else if(param.equals(CommandPerms.RESET))
				addRemoveSet = 3;
			else
				this.invalidParameters = true;
		}
		
		private void parsePrefixSuffixRankName(String param)
		{
			if(param.equals(CommandPerms.PREFIX))
				prefixSuffixRankName = 0;
			else if(param.equals(CommandPerms.SUFFIX))
				prefixSuffixRankName = 1;
			else if(param.equals(CommandPerms.RANK_NAME))
				prefixSuffixRankName = 2;
			else if(param.equals(CommandPerms.GROUP))
				prefixSuffixRankName = 3;
			else
				this.invalidParameters = true;
		}
		
		private void parseLongShort(String param)
		{
			if(param.equals(CommandPerms.LONG))
				this.longRank = true;
			else if(param.equals(CommandPerms.SHORT))
				this.longRank = false;
			else
				this.invalidParameters = true;
		}
		
		private void parseLang(String param)
		{
			String[] parameters = param.split("-");
			
			if(parameters.length != 2)
			{
				this.invalidParameters = true;
				return;
			}
			this.lang = new Locale(parameters[0], parameters[1]);
			
			if(!KaranteeniPlugin.getTranslator().getLocales().contains(lang))
				this.invalidParameters = true;
		}
		
		/**
		 * Was the parameters invalid
		 * @return
		 */
		public boolean invalidParameters()
		{ return this.invalidParameters; }
	}
}
