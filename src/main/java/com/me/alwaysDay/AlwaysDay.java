package com.me.alwaysDay;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class AlwaysDay extends JavaPlugin {

    private boolean alwaysDayEnabled = false;
    private BukkitTask dayTask = null;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load alwaysDay state from config
        alwaysDayEnabled = getConfig().getBoolean("enabled", false);

        // If it was enabled before server restart, start the day task
        if (alwaysDayEnabled) {
            startDayTask();
        }

        getLogger().info("AlwaysDay plugin enabled. Use /alwaysday [on|off] to toggle.");
        getLogger().info("AlwaysDay is currently " + (alwaysDayEnabled ? "enabled" : "disabled") + ".");
    }

    @Override
    public void onDisable() {
        // Save current state to config
        getConfig().set("enabled", alwaysDayEnabled);
        saveConfig();

        stopDayTask();
        getLogger().info("AlwaysDay plugin disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("alwaysday")) {
            if (!sender.hasPermission("alwaysday.use")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            if (args.length == 0) {
                String status = alwaysDayEnabled ? "enabled" : "disabled";
                sender.sendMessage(ChatColor.GOLD + "AlwaysDay is currently " + status + ".");
                sender.sendMessage(ChatColor.GOLD + "Use /alwaysday on|off to change.");
                return true;
            }

            if (args[0].equalsIgnoreCase("on")) {
                if (alwaysDayEnabled) {
                    sender.sendMessage(ChatColor.YELLOW + "AlwaysDay is already enabled.");
                } else {
                    alwaysDayEnabled = true;
                    // Save state to config
                    getConfig().set("enabled", true);
                    saveConfig();
                    startDayTask();
                    sender.sendMessage(ChatColor.GREEN + "AlwaysDay enabled. It will always be day now.");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("off")) {
                if (!alwaysDayEnabled) {
                    sender.sendMessage(ChatColor.YELLOW + "AlwaysDay is already disabled.");
                } else {
                    alwaysDayEnabled = false;
                    // Save state to config
                    getConfig().set("enabled", false);
                    saveConfig();
                    stopDayTask();
                    sender.sendMessage(ChatColor.GREEN + "AlwaysDay disabled. Time will flow normally.");
                }
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Usage: /alwaysday [on|off]");
            return true;
        }

        return false;
    }

    private void startDayTask() {
        stopDayTask();

        dayTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                if (alwaysDayEnabled) {
                    for (World world : Bukkit.getWorlds()) {
                        world.setTime(1000);
                    }
                }
            }
        }, 0L, 20L);
    }

    private void stopDayTask() {
        if (dayTask != null) {
            dayTask.cancel();
            dayTask = null;
        }
    }
}
