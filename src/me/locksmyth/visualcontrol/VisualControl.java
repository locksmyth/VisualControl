package me.locksmyth.visualcontrol;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VisualControl extends JavaPlugin {
	private final VisualControlPlayerListener playerListener = new VisualControlPlayerListener(this);
	final VisualControlPlayerManager playerManager = new VisualControlPlayerManager(this);
	public ColouredConsoleSender console = new ColouredConsoleSender((CraftServer) Bukkit.getServer());
	String name = "VisualControl";
	String version = "0.0.2";

	public void onDisable() {
		console.sendMessage("[" + name + "] " + name + " has been disabled.");
	}

	public void onEnable() {
		playerManager.config = getConfiguration();
		playerManager.loadConfig();

		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Event.Priority.Normal, this);

		console.sendMessage("[" + name + "] " + name + " " + version + " enabled");
	}
}