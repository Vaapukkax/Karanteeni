package net.karanteeni.songwriter;

import java.util.HashMap;
import org.bukkit.Bukkit;
import com.xxmicloxx.NoteBlockAPI.model.Layer;
import net.karanteeni.core.KaranteeniPlugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SongWriter extends JavaPlugin {

    @Override
    public void onEnable() {
        File file = this.getDataFolder();
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        }
        this.getCommand("record").setExecutor(new RecordCommand(this));
    }

}