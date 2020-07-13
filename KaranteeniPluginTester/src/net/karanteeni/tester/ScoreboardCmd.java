import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player))
        	return false;
        CakeEvent cakeEvent = TesterMain.getPlugin(TesterMain.class).getCakeEvent();
        Player p = (Player) sender;

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("kakku", "dummy", "§eKAKUNSYÖNTI");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        for (UUID key : cakeEvent.getPointsMap().keySet()) {
            Bukkit.broadcastMessage(key.toString());
            Score score = objective.getScore("1. " + Bukkit.getPlayer(key).getName());
            score.setScore(cakeEvent.getPointsMap().get(p.getUniqueId()));
        }

        if (p.isOp()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setScoreboard(scoreboard);
            }
        }

        return true;
    }
}