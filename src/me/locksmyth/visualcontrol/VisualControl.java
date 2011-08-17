package me.locksmyth.visualcontrol;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.locksmyth.visualcontrol.WorldVisualData.IntegerOutOfBoundsException;

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

	private final VisualControlListener listener = new VisualControlListener(this);
	final VisualControlPlayerManager playerManager = new VisualControlPlayerManager(this);
	public ColouredConsoleSender console = new ColouredConsoleSender((CraftServer) Bukkit.getServer());
	String name = "VisualControl";

	String version = "0.1.2";

	public List<String> commandText = null;

	private World getWorldFromCommand(final CommandSender sender) {
		World world = null;
		final String worldName = commandText.get(0);
		if (getServer().getWorld(worldName) != null) {
			// world name provided
			world = getServer().getWorld(commandText.remove(0));
		} else {
			if ((KeyWords.fromString(worldName) != null) && (sender instanceof Player)) {
				// first word is valid command assume current world
				world = ((Player) sender).getWorld();
			}
		}
		return world;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
		commandText = Arrays.asList(args);
		if (cmd.getName().equalsIgnoreCase("vcset")) {
			final World world = getWorldFromCommand(sender);
			if ((commandText.get(0) == null) || (commandText.get(1) == null)) {
				return false;
			}
			final String worldName = world.getName();
			final WorldVisualData worldVisualData = VisualControlPlayerManager.worldData.get(worldName);
			if (world != null) {
				if (worldVisualData.doCommand(sender, commandText.get(0), commandText.get(1))) {
					VisualControlPlayerManager.config.save();
					try {
						playerManager.loadConfigWorld(world);
					} catch (final IntegerOutOfBoundsException e) {
						sender.sendMessage("The new configuration appears to have been saved.");
						sender.sendMessage("However the configuration could not beloaded.");
					}
					for (final Player player : world.getPlayers()) {
						playerManager.loadPlayerTexture(player, player.getWorld());
					}
					return true;
				}
			} else { // Cannot find the indicated world
				sender.sendMessage("Cannot find selected world.");
				return false;
			}
		}
		if (cmd.getName().equalsIgnoreCase("vcupdate")) {
			Player player;
			if (!commandText.isEmpty()) {
				player = getServer().getPlayer(commandText.get(0));
			} else if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				sender.sendMessage("You must select a player to update.");
				return false;
			}
			playerManager.loadPlayerTexture(player, player.getWorld());
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("vcreload")) {
			getServer().getPluginManager().disablePlugin(this);
			try {
				getServer().getPluginManager().loadPlugin(new File("plugins" + File.separator + "VisualControl.jar"));
			} catch (final Exception e) {
				sender.sendMessage("Visual Control could not reload itself after being disabled.\nSorry.");
			}
		}
		return false;
	}

	public void onDisable() {
		console.sendMessage("[" + name + "] " + name + " has been disabled.");
	}

	public void onEnable() {
		final Updater upd = new Updater(this, "http://dl.dropbox.com/u/21888917/VisualControl.jar");
		VisualControlPlayerManager.config = getConfiguration();
		playerManager.loadConfig();
		if (VisualControlPlayerManager.update) {
			console.sendMessage("Checking for updates...");
			upd.updateCheck();
		}

		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.CUSTOM_EVENT, listener.spout, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.WORLD_LOAD, listener.world, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, listener.player, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, listener.player, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, listener.player, Event.Priority.Normal, this);

		console.sendMessage("[" + name + "] " + name + " " + version + " enabled");
	}
}