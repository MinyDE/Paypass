package me.Miny.Paypassage;

import java.io.File;
import java.util.HashMap;
import me.Miny.Paypassage.logger.LoggerUtility;

/**
 *
 * @author ibhh
 */
public class Privacy {

    private HashMap<String, Boolean> config = new HashMap<>();
    private Paypassage plugin;

    public Privacy(Paypassage plugin) {
        this.plugin = plugin;
        loadData();
    }

    public void loadData(){
        File dir = new File(plugin.getDataFolder() + File.separator + "privacy");
        dir.mkdirs();
        if(!new File(plugin.getDataFolder() + File.separator + "privacy" + File.separator + "User.privacy").exists()){
            plugin.getLoggerUtility().log("Cannot load privacy files statistics: no data found", LoggerUtility.Level.DEBUG);
            return;
        }
        try {
            config = ObjectManager.load(plugin.getDataFolder() + File.separator + "privacy" + File.separator + "User.privacy");
        } catch (Exception e) {
            plugin.getLoggerUtility().log("Cannot load privacy files statistics!", LoggerUtility.Level.ERROR);
            if (plugin.getConfigHandler().getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
        }
    }
    
    public void autoSave() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin,
                new Runnable() {
                    @Override
                    public void run() {
                        savePrivacyFiles();
                        plugin.getLoggerUtility().log("Saving privacy files!", LoggerUtility.Level.DEBUG);
                    }
                }, 0, 20 * 60 * 5);
    }

    public HashMap<String, Boolean> getConfig() {
        return config;
    }

    public void savePrivacyFiles() {
        File dir = new File(plugin.getDataFolder() + File.separator + "privacy");
        dir.mkdirs();
        try {
            ObjectManager.save(config, plugin.getDataFolder() + File.separator + "privacy" + File.separator + "User.privacy");
        } catch (Exception e) {
            plugin.getLoggerUtility().log("Cannot save Privacy files statistics!", LoggerUtility.Level.ERROR);
            if (plugin.getConfigHandler().getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
        }
    }
}
