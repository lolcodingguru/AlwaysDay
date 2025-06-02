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
        getLogger().info("AlwaysDay plugin enabled. Use /alwaysday [on|off] to toggle.");
    }

    @Override
    public void onDisable() {
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
