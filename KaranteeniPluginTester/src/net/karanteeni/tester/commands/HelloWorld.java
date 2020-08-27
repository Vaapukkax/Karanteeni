package net.karanteeni.tester.commands;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.block.BlockEffects.Effect;
import net.karanteeni.core.block.BlockManager;
import net.karanteeni.core.block.BlockType;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.DisplayFormat;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.bare.BareCommand;
import net.karanteeni.core.information.sounds.Sounds;
import net.karanteeni.core.information.text.Prefix;
import net.karanteeni.core.information.translation.TranslationContainer;
import net.karanteeni.tester.TesterMain;

public class HelloWorld extends BareCommand implements TranslationContainer {

	public HelloWorld(KaranteeniPlugin plugin) {
		super(plugin, 
				"hello", 
				"/hello", 
				"prints hello world to the player", 
				TesterMain.getDefaultMsgs().defaultNoPermission(), 
				Arrays.asList());
		registerTranslations();
	}

	
	@Override
	protected CommandResult runCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) 
			return CommandResult.NOT_FOR_CONSOLE;
		
		Player player = (Player)sender;
		player.sendMessage(Prefix.NEUTRAL + 
				TesterMain.getTranslator().getTranslation(plugin, player, "hello-world"));

		Block block = player.getTargetBlockExact(10);
		BlockManager bm = new BlockManager();
		
		// block cannot be thrown, invalid type
		if(block == null || block.getType().isSolid() || BlockType.GROWABLE.contains(block.getType())) {
			return new CommandResult(
					TesterMain.getTranslator().getTranslation(plugin, player, "not-on-block"),
					ResultType.OTHER, 
					Sounds.NO.get(),
					DisplayFormat.ACTIONBAR);
		}
		
		// throw the block
		bm.getBlockEffects().createEffect(block, Effect.CUBE, Particle.FLAME, Sounds.FIREWORK.get());
		bm.getBlockThrower().throwBlock(block, new Vector(Math.random()/2, Math.random(), Math.random()/2));
		
		// strike all nearby monsters with a lightning
		List<Monster> entities = TesterMain.getEntityManager().getNearbyMonsters(block.getLocation(), 10);
		for(Monster monster : entities) {
			monster.getWorld().strikeLightning(monster.getLocation());
		}
		
		return CommandResult.SUCCESS;
	}
	

	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String[] args) {
		return null;
	}


	@Override
	public void registerTranslations() {
		TesterMain.getTranslator().registerTranslation(plugin, "hello-world", "Hello world");
		TesterMain.getTranslator().registerTranslation(plugin, "not-on-block", "Only dirty blocks can be thrown");
	}
}
