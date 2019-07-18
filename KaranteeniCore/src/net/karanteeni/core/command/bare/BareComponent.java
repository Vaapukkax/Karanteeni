package net.karanteeni.core.command.bare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface BareComponent<T> {
	public List<String> autofill(CommandSender sender, Command cmd, String label, String arg);
	public T loadData(CommandSender sender, Command cmd, String label, String arg);
	
    public static List<String> filterByPrefix(Collection<String> list, String prefix, boolean caseSensitive) {
    	if(prefix == null)
    		return new ArrayList<String>();
    	if(caseSensitive)
    		return list.stream().filter(param -> param.startsWith(prefix)).collect(Collectors.toList());	
    	return list.stream().filter(param -> param.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
    }
}
