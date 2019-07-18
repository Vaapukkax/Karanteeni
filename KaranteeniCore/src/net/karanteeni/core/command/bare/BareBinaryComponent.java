package net.karanteeni.core.command.bare;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.karanteeni.core.KaranteeniCore;
import net.karanteeni.core.command.CommandResult;
import net.karanteeni.core.command.CommandResult.ResultType;
import net.karanteeni.core.command.defaultcomponent.BinaryComponent.BINARY;
import net.karanteeni.core.information.sounds.Sounds;

public class BareBinaryComponent implements BareComponent<Boolean> {
	private final BINARY binary;
	
	
	public BareBinaryComponent(BINARY binary) {
		this.binary = binary;
	}
	
	
	public CommandResult getInvalidArgumentResult(CommandSender sender) {
		return new CommandResult(KaranteeniCore.getTranslator().getTranslation(
				KaranteeniCore.getPlugin(KaranteeniCore.class), 
				sender, 
				"command.component-error.true-false")
				.replace("%true%", binary.getTrue())
				.replace("%false%", binary.getFalse()), 
				ResultType.INVALID_ARGUMENTS, 
				Sounds.NO.get());
	}

	
	@Override
	public List<String> autofill(CommandSender sender, Command cmd, String label, String arg) {
		return BareComponent.filterByPrefix(Arrays.asList(binary.getTrue(), binary.getFalse()), arg, false);
	}

	
	@Override
	public Boolean loadData(CommandSender sender, Command cmd, String label, String arg) {
		return binary.asBoolean(arg);
	}
}
