package net.karanteeni.nature;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.nature.block.events.AutoGrass;
import net.karanteeni.nature.block.events.BlowDandelion;
import net.karanteeni.nature.block.events.ChopTree;
import net.karanteeni.nature.block.events.SpawnerSpawn;
import net.karanteeni.nature.block.events.TreeGrow;
import net.karanteeni.nature.entity.events.EntityGrief;
import net.karanteeni.nature.entity.events.EntitySpawn;
import net.karanteeni.nature.entity.events.ExplosionEffects;
import net.karanteeni.nature.entity.events.FeedSheep;
import net.karanteeni.nature.entity.events.OnLightning;
import net.karanteeni.nature.entity.events.SpreadCreeperExplosion;

public class Katura extends KaranteeniPlugin {

	public Katura() {
		super(true);
	}
	
	
	@Override
	public void onEnable()
	{
		registerEvents();
		registerCommands();
	}
	
	private void registerEvents()
	{
		getServer().getPluginManager().registerEvents(new FeedSheep(), this);
		getServer().getPluginManager().registerEvents(new BlowDandelion(), this);
		getServer().getPluginManager().registerEvents(new AutoGrass(), this);
		getServer().getPluginManager().registerEvents(new ChopTree(), this);
		getServer().getPluginManager().registerEvents(new TreeGrow(), this);
		getServer().getPluginManager().registerEvents(new OnLightning(), this);
		getServer().getPluginManager().registerEvents(new SpreadCreeperExplosion(), this);
		
		//Long config lists, create last
		getServer().getPluginManager().registerEvents(new EntityGrief(), this);
		getServer().getPluginManager().registerEvents(new ExplosionEffects(), this);
		getServer().getPluginManager().registerEvents(new EntitySpawn(), this);
		getServer().getPluginManager().registerEvents(new SpawnerSpawn(), this);
	}
	
	private void registerCommands()
	{
		
	}

	@Override
	public void onDisable()
	{
		
	}
}

