package me.locksmyth.visualcontrol;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.ColouredConsoleSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VisualControl extends JavaPlugin {
	public enum KeyWords {
		STARS, MOON, SUN, CLOUDS, USE, TITLE, UPDATE;

		public static KeyWords fromString(final String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (final Exception ex) {
				return null;
			}
		}
	}

	private final VisualControlPlayerListener playerListener = new VisualControlPlayerListener(this);
	final VisualControlPlayerManager playerManager = new VisualControlPlayerManager(this);
	public ColouredConsoleSender console = new ColouredConsoleSender((CraftServer) Bukkit.getServer());
	String name = "VisualControl";

	String version = "0.1.0";

	private List<String> commandText = null;

	private World getWorldFromCommand(final CommandSender sender) {
		World world = null;
		final String worldName = commandText.get(0);
		if (getServer().getWorld(worldName) != null) {
			world = getServer().getWorld(commandText.remove(0)); // world name
			// provided
		} else {
			if ((KeyWords.fromString(worldName) != null) && (sender instanceof Player)) {
				world = ((Player) sender).getWorld(); // first word is valid
				// command assume
				// current world
			}
		}
		return world;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
		commandText = Arrays.asList(args);
		if (cmd.getName().equalsIgnoreCase("vcset")) {
			final World world = getWorldFromCommand(sender);
			if (world != null) {
				final String worldName = world.getName();
				final WorldVisualData worldVisualData = VisualControlPlayerManager.worldData.get(worldName);
				if (worldVisualData.doCommand(sender, commandText)) {
					VisualControlPlayerManager.config.save();
					return true;
				}
			} else { // Cannot find the indicated world
				sender.sendMessage("Cannot find selected world.");
				return false;
			}
		}
		return false;
	}

	public void onDisable() {
		console.sendMessage("[" + name + "] " + name + " has been disabled.");
	}

	public void onEnable() {
		VisualControlPlayerManager.config = getConfiguration();
		playerManager.loadConfig();

		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener, Event.Priority.Normal, this);

		console.sendMessage("[" + name + "] " + name + " " + version + " enabled");
	}
}