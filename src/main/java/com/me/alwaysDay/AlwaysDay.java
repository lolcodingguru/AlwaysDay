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
    private boolean noWeatherEnabled = false;
    private BukkitTask dayTask = null;
    private BukkitTask weatherTask = null;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Load alwaysDay state from config
        alwaysDayEnabled = getConfig().getBoolean("enabled", false);
        noWeatherEnabled = getConfig().getBoolean("noweather", false);

        // If it was enabled before server restart, start the day task
        if (alwaysDayEnabled) {
            startDayTask();
        }

        // If noweather was enabled before server restart, start the weather task
        if (noWeatherEnabled) {
            startWeatherTask();
        }

        getLogger().info("AlwaysDay plugin enabled. Use /alwaysday [on|off] to toggle day, or /alwaysday noweather [on|off] to toggle weather control.");
        getLogger().info("AlwaysDay is currently " + (alwaysDayEnabled ? "enabled" : "disabled") + ".");
        getLogger().info("NoWeather is currently " + (noWeatherEnabled ? "enabled" : "disabled") + ".");
    }

    @Override
    public void onDisable() {
        // Save current state to config
        getConfig().set("enabled", alwaysDayEnabled);
        getConfig().set("noweather", noWeatherEnabled);
        saveConfig();

        stopDayTask();
        stopWeatherTask();
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
                String dayStatus = alwaysDayEnabled ? "enabled" : "disabled";
                String weatherStatus = noWeatherEnabled ? "enabled" : "disabled";
                sender.sendMessage(ChatColor.GOLD + "AlwaysDay is currently " + dayStatus + ".");
                sender.sendMessage(ChatColor.GOLD + "NoWeather is currently " + weatherStatus + ".");
                sender.sendMessage(ChatColor.GOLD + "Use /alwaysday on|off to change day setting.");
                sender.sendMessage(ChatColor.GOLD + "Use /alwaysday noweather on|off to change weather setting.");
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

            if (args[0].equalsIgnoreCase("noweather")) {
                if (args.length < 2) {
                    String status = noWeatherEnabled ? "enabled" : "disabled";
                    sender.sendMessage(ChatColor.GOLD + "NoWeather is currently " + status + ".");
                    sender.sendMessage(ChatColor.GOLD + "Use /alwaysday noweather on|off to change.");
                    return true;
                }

                if (args[1].equalsIgnoreCase("on")) {
                    if (noWeatherEnabled) {
                        sender.sendMessage(ChatColor.YELLOW + "NoWeather is already enabled.");
                    } else {
                        noWeatherEnabled = true;
                        // Save state to config
                        getConfig().set("noweather", true);
                        saveConfig();
                        startWeatherTask();
                        sender.sendMessage(ChatColor.GREEN + "NoWeather enabled. Weather will always be clear now.");
                    }
                    return true;
                }

                if (args[1].equalsIgnoreCase("off")) {
                    if (!noWeatherEnabled) {
                        sender.sendMessage(ChatColor.YELLOW + "NoWeather is already disabled.");
                    } else {
                        noWeatherEnabled = false;
                        // Save state to config
                        getConfig().set("noweather", false);
                        saveConfig();
                        stopWeatherTask();
                        sender.sendMessage(ChatColor.GREEN + "NoWeather disabled. Weather will change normally.");
                    }
                    return true;
                }

                sender.sendMessage(ChatColor.RED + "Usage: /alwaysday noweather [on|off]");
                return true;
            }

            sender.sendMessage(ChatColor.RED + "Usage: /alwaysday [on|off] or /alwaysday noweather [on|off]");
            return true;
        }

        return false;
    }

    private void startDayTask() {
        stopDayTask();

        dayTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    world.setTime(1000);
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

    private void startWeatherTask() {
        stopWeatherTask();

        weatherTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    world.setStorm(false);
                    world.setThundering(false);
                    // Set weather duration to a high value to prevent natural weather changes
                    world.setWeatherDuration(Integer.MAX_VALUE);
                    world.setThunderDuration(0);
                }
            }
        }, 0L, 600L); // Check every 30 seconds (20 ticks * 30 = 600)
    }

    private void stopWeatherTask() {
        if (weatherTask != null) {
            weatherTask.cancel();
            weatherTask = null;
        }
    }
}
