package net.karanteeni.nature.entity.events;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.Plugin;

import net.karanteeni.core.block.BlockType;
import net.karanteeni.nature.Katura;

public class EntityGrief implements Listener{
	private static String griefPrefix = "block.grief.";
	private static String formPrefix = "block.form.";
	private static String spreadPrefix = "block.spread.";
	
	private static String creeperExplosion 	= griefPrefix + "creeper";
	private static String villagerFarm 		= griefPrefix + "villager";
	private static String wither	 		= griefPrefix + "wither.move";
	private static String witherExplode		= griefPrefix + "wither.explode";
	private static String witherShoot		= griefPrefix + "wither.shoot";
	private static String endermanGrief 	= griefPrefix + "enderman";
	private static String blockBurn 		= griefPrefix + "block-burn";
	private static String iceMelt 			= griefPrefix + "ice-melt";
	private static String snowMelt 			= griefPrefix + "snow-melt";
	private static String zombieGrief 		= griefPrefix + "zombie";
	private static String ghastGrief 		= griefPrefix + "ghast";
	private static String fireballGrief		= griefPrefix + "fireball";
	private static String dragonGrief		= griefPrefix + "dragon";
	private static String dragonBallGrief	= griefPrefix + "dragon-fireball";
	private static String sheepGrief		= griefPrefix + "sheep";
	private static String lavaMove			= griefPrefix + "water";
	private static String waterMove			= griefPrefix + "lava";
	
	private static String iceForm 			= formPrefix + "ice";
	private static String snowForm 			= formPrefix + "snow";
	private static String obsidianForm		= formPrefix + "obsidian";
	private static String cobblestoneForm	= formPrefix + "cobblestone";
	private static String concreteForm		= formPrefix + "concrete";
	private static String stoneForm			= formPrefix + "stone";
	private static String fireForm			= formPrefix + "fire.";
	private static String snowmanSnowForm	= formPrefix + "entity.snowman-walk";
	private static String frostForm			= formPrefix + "entity.frost-walker-ice";
	
	private static String mushroomSpread	= spreadPrefix + "mushroom";
	private static String vineSpread		= spreadPrefix + "vine";
	/*private static String grassSpread		= spreadPrefix + "grass";
	private static String myceliumSpread	= spreadPrefix + "mycelium";
	private static String podzolSpread		= spreadPrefix + "podzol";*/
	
	private static Plugin pl = Katura.getPlugin(Katura.class);
	
	public EntityGrief()
	{
		pl = Katura.getPlugin(Katura.class);
		//--- GRIEF
		if(!pl.getConfig().isSet(lavaMove)) {
			pl.getConfig().set(lavaMove, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(waterMove)) {
			pl.getConfig().set(waterMove, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(sheepGrief)) {
			pl.getConfig().set(sheepGrief, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(dragonGrief)) {
			pl.getConfig().set(dragonGrief, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(dragonBallGrief)) {
			pl.getConfig().set(dragonBallGrief, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(ghastGrief)) {
			pl.getConfig().set(ghastGrief, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(zombieGrief)) {
			pl.getConfig().set(zombieGrief, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(creeperExplosion)) {
			pl.getConfig().set(creeperExplosion, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(villagerFarm)) {
			pl.getConfig().set(villagerFarm, true);
			pl.saveConfig();
		}
		/*if(!pl.getConfig().isSet(witherExplosion)) {
			pl.getConfig().set(witherExplosion, true);
			pl.saveConfig();
		}*/
		if(!pl.getConfig().isSet(wither)) {
			pl.getConfig().set(wither, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(witherExplode)) {
			pl.getConfig().set(witherExplode, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(witherShoot)) {
			pl.getConfig().set(witherShoot, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(endermanGrief)) {
			pl.getConfig().set(endermanGrief, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(blockBurn)) {
			pl.getConfig().set(blockBurn, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(iceMelt)) {
			pl.getConfig().set(iceMelt, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(snowMelt)) {
			pl.getConfig().set(snowMelt, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(fireballGrief)) {
			pl.getConfig().set(fireballGrief, true);
			pl.saveConfig();
		}
		//----- FORM
		if(!pl.getConfig().isSet(iceForm)) {
			pl.getConfig().set(iceForm, true);
			pl.saveConfig();
		}
		
		for(IgniteCause cause : IgniteCause.values())
		{
			if(!pl.getConfig().isSet(fireForm+cause.toString())) {
				pl.getConfig().set(fireForm+cause.toString(), true);
				pl.saveConfig();
			}
		}
		if(!pl.getConfig().isSet(snowForm)) {
			pl.getConfig().set(snowForm, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(obsidianForm)) {
			pl.getConfig().set(obsidianForm, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(cobblestoneForm)) {
			pl.getConfig().set(cobblestoneForm, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(concreteForm)) {
			pl.getConfig().set(concreteForm, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(stoneForm)) {
			pl.getConfig().set(stoneForm, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(snowmanSnowForm)) {
			pl.getConfig().set(snowmanSnowForm, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(frostForm)) {
			pl.getConfig().set(frostForm, true);
			pl.saveConfig();
		}
		//----- SPREAD
		if(!pl.getConfig().isSet(mushroomSpread)) {
			pl.getConfig().set(mushroomSpread, true);
			pl.saveConfig();
		}
		if(!pl.getConfig().isSet(vineSpread)) {
			pl.getConfig().set(vineSpread, true);
			pl.saveConfig();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void blockIgnite(BlockIgniteEvent event)
	{
		if(!pl.getConfig().getBoolean(fireForm+event.getCause().toString()))
			{ event.setCancelled(true); return ;}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void entityChangeBlock(EntityChangeBlockEvent event)
	{
		if(event.getEntityType().equals(EntityType.CREEPER))
			if(!pl.getConfig().getBoolean(creeperExplosion))
			{ event.setCancelled(true); return ;}
		
		if(event.getEntityType().equals(EntityType.SHEEP))
			if(!pl.getConfig().getBoolean(sheepGrief))
			{ event.setCancelled(true); return ;}
		
		if(event.getEntityType().equals(EntityType.VILLAGER))
			if(!pl.getConfig().getBoolean(villagerFarm))
			{ event.setCancelled(true); return ;}
		
		if(event.getEntityType().equals(EntityType.ZOMBIE) ||
				event.getEntityType().equals(EntityType.ZOMBIE_VILLAGER))
			if(!pl.getConfig().getBoolean(zombieGrief))
			{ event.setCancelled(true); return ;}
		
		if(event.getEntityType().equals(EntityType.ENDERMAN))
			if(!pl.getConfig().getBoolean(endermanGrief))
			{ event.setCancelled(true); return ;}
		
		if(event.getEntityType().equals(EntityType.WITHER))
			if(!pl.getConfig().getBoolean(wither))
			{ event.setCancelled(true); return ;}
		
		if(event.getEntityType().equals(EntityType.ENDER_DRAGON))
			if(!pl.getConfig().getBoolean(dragonGrief))
			{ event.setCancelled(true); return ;}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void entityExplodeEvent(EntityExplodeEvent event)
	{
		if(event.getEntityType().equals(EntityType.FIREBALL))
		{
			Fireball ball = (Fireball)event.getEntity();
			
			if(!pl.getConfig().getBoolean(fireballGrief) && 
					!(ball.getShooter() instanceof Ghast))
				{ event.setCancelled(true); return ;}
			
			if(!pl.getConfig().getBoolean(ghastGrief) && 
					(ball.getShooter() instanceof Ghast))
				{ event.setCancelled(true); return ;}
		}
		else if(event.getEntityType().equals(EntityType.DRAGON_FIREBALL))
		{
			if(!pl.getConfig().getBoolean(dragonBallGrief))
				{ event.setCancelled(true); return ;}
		}
		else if(event.getEntityType().equals(EntityType.WITHER_SKULL))
		{
			if(!pl.getConfig().getBoolean(witherShoot))
				{ event.setCancelled(true); return ;}
		}
		else if(event.getEntityType().equals(EntityType.WITHER))
		{
			if(!pl.getConfig().getBoolean(witherExplode))
				{ event.setCancelled(true); return ;}
		}
		else if(event.getEntityType().equals(EntityType.ENDER_DRAGON))
		{
			if(!pl.getConfig().getBoolean(dragonGrief))
				{ event.setCancelled(true); return ;}
		}
	}
	
	/**
	 * Handles the block spreading
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void blockSpreadEvent(BlockSpreadEvent event)
	{
		if(event.getSource().getType().equals(Material.BROWN_MUSHROOM) ||
				event.getSource().getType().equals(Material.RED_MUSHROOM))
			if(!pl.getConfig().getBoolean(mushroomSpread))
			{ event.setCancelled(true); return ;}
		
		if(event.getSource().getType().equals(Material.VINE))
			if(!pl.getConfig().getBoolean(vineSpread))
			{ event.setCancelled(true); return ;}
	}
	
	/**
	 * Handles the frost walker and snowmen
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	private void entityBlockFormEvent(EntityBlockFormEvent event)
	{
		if(event.getNewState().getType().equals(Material.SNOW))
			if(!pl.getConfig().getBoolean(snowmanSnowForm))
				{ event.setCancelled(true); return ;}
			else
				{ event.setCancelled(false); return ;}
		if(event.getNewState().getType().equals(Material.FROSTED_ICE))
			if(!pl.getConfig().getBoolean(frostForm))
				{ event.setCancelled(true); return ;}
			else
				{ event.setCancelled(false); return ;}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void blockFormToEvent(BlockFromToEvent event)
	{
		if(event.getToBlock().getType().equals(Material.LAVA))
			if(!pl.getConfig().getBoolean(lavaMove))
			{ event.setCancelled(true); return ;}
		if(event.getToBlock().getType().equals(Material.WATER))
			if(!pl.getConfig().getBoolean(waterMove))
			{ event.setCancelled(true); return ;}
		if(event.getToBlock().getType().equals(Material.STONE))
			if(!pl.getConfig().getBoolean(stoneForm))
			{ event.setCancelled(true); return ;}
		if(event.getToBlock().getType().equals(Material.OBSIDIAN))
			if(!pl.getConfig().getBoolean(obsidianForm))
			{ event.setCancelled(true); return ;}
		if(event.getToBlock().getType().equals(Material.COBBLESTONE))
			if(!pl.getConfig().getBoolean(cobblestoneForm))
			{ event.setCancelled(true); return ;}
	}
	
	/**
	 * Defines block form events
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void blockFormEvent(BlockFormEvent event)
	{
		if(event.getNewState().getBlock().getType().equals(Material.SNOW))
			if(!pl.getConfig().getBoolean(snowForm))
			{ event.setCancelled(true); return ;}
		if(event.getNewState().getBlock().getType().equals(Material.ICE))
			if(!pl.getConfig().getBoolean(iceForm))
			{ event.setCancelled(true); return ;}
		if(BlockType.CONCRETE_POWDER.contains(event.getNewState().getBlock().getType()))
			if(!pl.getConfig().getBoolean(concreteForm))
			{ event.setCancelled(true); return ;}
	}
	
	/**
	 * Allow ice and snow to melt
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void blockSmeltEvent(BlockFadeEvent event)
	{
		if(event.getBlock().getType().equals(Material.ICE))
			if(!pl.getConfig().getBoolean(iceMelt))
			{ event.setCancelled(true); return ;}
		
		if(event.getBlock().getType().equals(Material.SNOW_BLOCK) || 
				event.getBlock().getType().equals(Material.SNOW))
			if(!pl.getConfig().getBoolean(snowMelt))
			{ event.setCancelled(true); return ;}
	}
	
	/**
	 * Allow blocks to burn
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void blockBurnEvent(BlockBurnEvent event)
	{
		if(pl.getConfig().getBoolean(blockBurn))
			return;
		
		event.setCancelled(true);
	}
}
