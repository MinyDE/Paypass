package me.Miny.Paypassage;

import Permissions.PermissionsUtility;
import me.Miny.Paypassage.Report.ReportToHost;
import me.Miny.Paypassage.config.ConfigurationHandler;
import me.Miny.Paypassage.logger.LoggerUtility;
import me.Miny.Paypassage.update.Update;
import me.Miny.Paypassage.update.Utilities;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Miny
 */
public class Paypassage extends JavaPlugin {

    //Modules
    private LoggerUtility logger;
    private ConfigurationHandler config;
    private ReportToHost report;
    private PermissionsUtility permissions;
    private Privacy privacy;
    private Update update;
    private Utilities pluginmanager;
    private String[] commands = {
        "help",
        "debugfile",
        "internet",
        "version",
        "update",
        "reload",
        "statuschange",
        "language",
        "report",
        "denytracking",
        "allowtracking"
    };

    public String[] getCommands() {
        return commands;
    }

    /**
     * returns an Error-Reporting-API
     *
     * @return ReportToHost
     */
    public ReportToHost getReportHandler() {
        if (report == null) {
            report = new ReportToHost(this);
        }
        return report;
    }

    public Utilities getPluginManager() {
        if (pluginmanager == null) {
            pluginmanager = new Utilities(this);
        }
        return pluginmanager;
    }

    public Privacy getPrivacy() {
        if (privacy == null) {
            privacy = new Privacy(this);
        }
        return privacy;
    }

    public Update getUpdate() {
        if (update == null) {
            update = new Update(this);
        }
        return update;
    }

    /**
     * Returns a permissions API (GroupManager, PermissionsEx, bPermissions,
     * BukkitPermissions)
     *
     * @return PermissionsUtility
     */
    public PermissionsUtility getPermissions() {
        if (permissions == null) {
            permissions = new PermissionsUtility(this);
        }
        return permissions;
    }

    /**
     * Returns a handler that manages all config files
     *
     * @return ConfigurationHandler
     */
    public ConfigurationHandler getConfigHandler() {
        if (config == null) {
            config = new ConfigurationHandler(this);
        }
        return config;
    }

    /**
     * returns a custom loggertool
     *
     * @return LoggerUtility
     */
    public LoggerUtility getLoggerUtility() {
        if (logger == null) {
            logger = new LoggerUtility(this);
        }
        return logger;
    }

    /**
     * returns the same as isEnabled()
     *
     * @return boolean isEnabled
     */
    public boolean isStarted() {
        return this.isEnabled();
    }

    /**
     * Called by ConfigurationHandler if loading of files failed Disables the
     * plugin
     */
    @Override
    public void onDisable() {
        setEnabled(false);
        privacy.savePrivacyFiles();
        long time = System.nanoTime();
        System.out.println("Paypassage disabled in " + ((System.nanoTime() - time) / 1000000) + " ms");
    }

    /**
     * Enables the plugin
     */
    @Override
    public void onEnable() {
        long time = System.nanoTime();
        getConfigHandler().onStart();
        getLoggerUtility();
        getLoggerUtility().log("creating config!", LoggerUtility.Level.DEBUG);
        getLoggerUtility().log("init logger!", LoggerUtility.Level.DEBUG);
        getReportHandler();
        getLoggerUtility().log("init report!", LoggerUtility.Level.DEBUG);
        getPermissions();
        getLoggerUtility().log("init permissions!", LoggerUtility.Level.DEBUG);
        getPrivacy().autoSave();
        getLoggerUtility().log("init privacy control!", LoggerUtility.Level.DEBUG);
        getUpdate().startUpdateTimer();
        getLoggerUtility().log("init update control!", LoggerUtility.Level.DEBUG);
        getLoggerUtility().log("Paypassage enabled in " + ((System.nanoTime() - time) / 1000000) + " ms", LoggerUtility.Level.INFO);
        setEnabled(true);
    }

    public float getVersion() {
        try {
            return Float.parseFloat(getDescription().getVersion());
        } catch (Exception e) {
            getLoggerUtility().log("Could not parse version in float", LoggerUtility.Level.INFO);
            getLoggerUtility().log("Error getting version of " + this.getName() + "! Message: " + e.getMessage(), LoggerUtility.Level.ERROR);
            this.report.report(3310, "Error getting version of " + this.getName() + "!", e.getMessage(), "Paypassage", e);
            getLoggerUtility().log("Uncatched Exeption!", LoggerUtility.Level.ERROR);
        }
        return 0;
    }

    /**
     * Handles commands
     *
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isEnabled()) {
            getLoggerUtility().log("Paypassage plugin is NOT enabled!", LoggerUtility.Level.ERROR);
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            /**
             * /pp create command
             */
            if (command.getName().equalsIgnoreCase("pp")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("create")) {
                        //create Command
                        player.sendMessage(ChatColor.GRAY + "[Paypassage]" + ChatColor.DARK_AQUA + "Paypassage created");
                        return true;
                    } else if (args[0].equalsIgnoreCase("delete")) {
                        player.sendMessage(ChatColor.GRAY + "[Paypassage]" + ChatColor.DARK_AQUA + "Paypassage deleted");
                        return true;
                    } else if (args[0].equalsIgnoreCase("info")) {
                        player.sendMessage(ChatColor.GRAY + "[Paypassage]" + ChatColor.DARK_AQUA + "Paypassage Status:" + ChatColor.GREEN + "Working!");
                        return true;
                    } else if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.denytracking.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.denytracking.permission"))) {
                            if (getPrivacy().getConfig().containsKey(player.getName())) {
                                getPrivacy().getConfig().remove(player.getName());

                            }
                            getPrivacy().getConfig().put(player.getName(), Boolean.FALSE);
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("privacy.notification.denied"), LoggerUtility.Level.INFO);
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase(getConfigHandler().getLanguage_config().getString("commands.allowtracking.name"))) {
                        if (getPermissions().checkpermissions(player, getConfigHandler().getLanguage_config().getString("commands.allowtracking.permission"))) {
                            if (getPrivacy().getConfig().containsKey(player.getName())) {
                                getPrivacy().getConfig().remove(player.getName());
                            }
                            getPrivacy().getConfig().put(player.getName(), Boolean.TRUE);
                            getLoggerUtility().log(player, getConfigHandler().getLanguage_config().getString("privacy.notification.allowed"), LoggerUtility.Level.INFO);
                        }
                        return true;
                    } else {
                        player.sendMessage(ChatColor.GRAY + "[Paypassage]" + ChatColor.RED + "Du hast irgendetwas falsch gemacht!");
                        return false;
                    }
                }
            }
        }
        return false;
    }
}