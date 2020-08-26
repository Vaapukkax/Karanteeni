package net.karanteeni.tester;

import org.bukkit.entity.Damageable;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.command.defaultcomponent.EntityTypeLoader;
import net.karanteeni.core.command.defaultcomponent.IntegerLoader;
import net.karanteeni.core.command.defaultcomponent.UUIDLoader;
import net.karanteeni.tester.commands.HelloWorld;
import net.karanteeni.tester.commands.entity.EntityCommand;
import net.karanteeni.tester.commands.entity.components.EntityHeal;
import net.karanteeni.tester.commands.entity.components.EntityKill;
import net.karanteeni.tester.commands.entity.components.EntityTeleport;
import net.karanteeni.tester.commands.entity.components.HealNearest;
import net.karanteeni.tester.commands.entity.components.KillNearest;
import net.karanteeni.tester.commands.entity.components.TeleportNearest;
import net.karanteeni.tester.events.LeverSound;
import net.karanteeni.tester.events.RedstoneLampSound;

public class TesterMain extends KaranteeniPlugin {
	
	public TesterMain() {
		super(true);
	}

	
	@Override
	public void onLoad() {
		
	}
	
	
	@Override
	public void onEnable() {
		registerCommands();
		registerEvents();
	}
	
	
	@Override
	public void onDisable() {
		/*Player player = null;
		long systemTime = System.currentTimeMillis();
		KaranteeniCore.getTranslator();
		
		Prefix;
		TextUtil
		TimeData timeData = Time.parseTime("10min");
		TextUtil util = new TextUtil();
		ArrayFormat.join(list, divider)
		
		KaranteeniCore.getMessager().broadcastTranslatedMessage(sound, key, plugin);
		KaranteeniCore.getMessager().sendActionBar(receiver, sound, text);
		KaranteeniCore.getMessager().sendBossbar(players, sound, stay, updateFreq, animated, bar, texts);
		KaranteeniCore.getMessager().sendList(receiver, command, parameters, commandPrefix, commandSuffix);
		KaranteeniCore.getMessager().sendMessage(players, sound, component);
		KaranteeniCore.getMessager().sendTitle(fadein, fadeout, stay, receiver, title, subtitle, sound);
		
		KaranteeniCore.getDefaultMsgs().errorHappened(player);*/
	}
	
	
	private void registerCommands() {
		HelloWorld helloWorld = new HelloWorld(this);
		helloWorld.setPermission("hello.world");
		helloWorld.register();
		
		// root
		EntityCommand entityCommand = new EntityCommand(this);
		EntityTeleport teleportBranch = new EntityTeleport();
		EntityKill killBranch = new EntityKill();
		EntityHeal healBranch = new EntityHeal();
		
		entityCommand.addComponent("teleport", teleportBranch);
		entityCommand.addComponent("kill", killBranch);
		entityCommand.addComponent("heal", healBranch);
		teleportBranch.setPermission("tutorial.entity.teleport");
		killBranch.setPermission("tutorial.entity.kill");
		healBranch.setPermission("tutorial.entity.heal");
		
		UUIDLoader uuidLoader = new UUIDLoader(true, false);
		teleportBranch.setLoader(uuidLoader);
		killBranch.setLoader(uuidLoader);
		healBranch.setLoader(uuidLoader);
		
		TeleportNearest tpc = new TeleportNearest();
		KillNearest kn = new KillNearest();
		HealNearest hn = new HealNearest();
		
		teleportBranch.addComponent("nearest", tpc);
		killBranch.addComponent("nearest", kn);
		healBranch.addComponent("nearest", hn);
		
		IntegerLoader il = new IntegerLoader(EntityCommand.ENTITY_COUNT, true, 1, Integer.MAX_VALUE, false);
		EntityTypeLoader etl = new EntityTypeLoader(true, true, false, Damageable.class);
		il.setLoader(etl);
		
		// teleport
		tpc.setLoader(etl);
		
		// heal
		kn.setLoader(il);
		
		// kill
		hn.setLoader(il);
		
		// register
		entityCommand.register();
	}
	
	
	private void registerEvents() {
		getServer().getPluginManager().registerEvents(new LeverSound(), this);
		getServer().getPluginManager().registerEvents(new RedstoneLampSound(), this);
	}
}
