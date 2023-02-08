package me.skirmish;

import me.skirmish.handlers.Config;
import me.skirmish.handlers.GUIHandler;
import me.skirmish.handlers.WorldHandler;
import me.skirmish.objects.TC;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkirmishTC extends JavaPlugin {
    public static SkirmishTC plugin;
    public static List<TC> allTCs;
    public Config testingConfig;

    @Override
    public void onEnable() {
        plugin = this;
        ConfigurationSerialization.registerClass(TC.class);

        loadTcs();

        getServer().getPluginManager().registerEvents(new WorldHandler(), this);
        getServer().getPluginManager().registerEvents(new GUIHandler(), this);

    }

    public void onDisable(){
        saveTcs();
    }

    static public void saveTcs(){
        File playerDataFile = new File("./plugins/ToolCupboards/"+"allTcs"+".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(playerDataFile);
        data.set("toolCupboards", allTCs);
        try { data.save(playerDataFile); } catch (IOException ex) { throw new RuntimeException(ex); }
    }

    static void createTcFile(){
        File playerDataFile = new File("./plugins/ToolCupboards/"+"allTcs"+".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(playerDataFile);
        try { data.save(playerDataFile); } catch (IOException ex) { throw new RuntimeException(ex); }
    }


    void loadTcs(){
        testingConfig = new Config("allTcs.yml");
        allTCs = (List<TC>) testingConfig.getList("toolCupboards");
        if(allTCs == null) allTCs = new ArrayList<>(); // You only need this when the file is still empty
    }
}
