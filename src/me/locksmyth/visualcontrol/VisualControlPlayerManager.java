/**
 * 
 */
package me.locksmyth.visualcontrol;

import java.util.HashMap;
import java.util.List;

import me.locksmyth.visualcontrol.WorldVisualData.IntegerOutOfBoundsException;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mgsmith
 */
public class VisualControlPlayerManager {

	public static Configuration config;

	public static Boolean announce = new Boolean(null);
	public static Boolean update = new Boolean(null);
	public static String defaultTitle = new String();
	public static String defaultFile = new String();
	public static HashMap<String, WorldVisualData> worldData = new HashMap<String, WorldVisualData>();
	public static HashMap<String, HashMap<String, String>> playerData = new HashMap<String, HashMap<String, String>>();
	public static VisualControl plugin;

	public VisualControlPlayerManager(final VisualControl visualControl) {
		plugin = visualControl;
		WorldVisualData.playerManager = this;
	}

	public String getPlayerData(final Player player, final String key) {
		String value;
		final String name = player.getName();
		if (playerData.containsKey(name) && playerData.get(name).containsKey(key)) {
			value = playerData.get(name).get(key);
		} else {
			value = "";
		}
		return value;
	}

	public void initializePlayer(final Player player) {
		setPlayerData(player, "texture", "");
		setPlayerData(player, "moon", "");
		setPlayerData(player, "sun", "");
	}

	public void loadConfig() {
		plugin.console.sendMessage("[" + plugin.name + "] Loading...");
		final List<World> worlds = plugin.getServer().getWorlds();
		config.load();
		if (config.getHeader() == null) {
			config.setHeader("# " + plugin.name + " " + plugin.version, "# locksmyth", "#", "#", "# Configuration options ",
								"# announce: <true/false> // is the player notified of the loading of a new texture pack",
								"# defaultFile : <URL> // the URL to a direct download of the texture pack",
								"# defaultTitle : <STRING> // the title of the default texturepack to load", "# worlds:", "#     <world_name>:", "#         texturePack:",
								"#             file: http://url.to.texturepack", "#             title: Texture Title", "#             useDefaultTexture: true", "#         sun:",
								"#             file: http://url.to.sun/image.png", "#             size: 0 - 100", "#             visible: true/false", "#         moon:",
								"#             file: http://url.to.moon/image.png", "#             size: 0 - 100", "#             visible: true/false", "#         stars:",
								"#             frequency: 1500  // the default value is 1500", "#             visible: true/false", "#         clouds:",
								"#             height: 0 - 127", "#             visible: true/false", "################");
			config.save();
		}
		if (config.getProperty("announce") == null) {
			config.setProperty("announce", true);
			announce = true;
		} else {
			announce = config.getBoolean("announce", false);
		}
		if (config.getProperty("update") == null) {
			config.setProperty("update", false);
			update = false;
		} else {
			update = config.getBoolean("update", false);
		}
		if (config.getProperty("defaultTitle") == null) {
			config.setProperty("defaultTitle", "AlchemyX");
			defaultTitle = "AlchemyX";
		} else {
			defaultTitle = config.getString("defaultTitle");
		}
		if (config.getProperty("defaultFile") == null) {
			config.setProperty("defaultFile", "http://dl.dropbox.com/u/21888917/AXSteam0.2.zip");
			defaultFile = "http://dl.dropbox.com/u/21888917/AXSteam0.2.zip";
		} else {
			defaultFile = config.getString("defaultFile");
		}
		for (final World world : worlds) {
			try {
				loadConfigWorld(world);
			} catch (final IntegerOutOfBoundsException e) {
				plugin.console.sendMessage("[" + plugin.name + "] encountered errors loading the configuration file.");
			}
		}
		config.save();
	}

	public void loadConfigWorld(final World world2) throws IntegerOutOfBoundsException {
		final String world = world2.getName();
		final WorldVisualData worldConf = new WorldVisualData(world2);
		plugin.console.sendMessage("[" + plugin.name + "] " + world);
		if (config.getProperty("worlds." + world + ".texturePack") == null) {
			config.setProperty("worlds." + world + ".texturePack" + ".useDefaultTexture", new Boolean(true));
			worldConf.defaultTexturePack = true;
		} else {
			worldConf.defaultTexturePack = config.getBoolean("worlds." + world + ".texturePack" + ".useDefaultTexture", true);
			if (worldConf.defaultTexturePack == false) {
				worldConf.textureFile = config.getString("worlds." + world + ".texturePack" + ".file");
				worldConf.textureTitle = config.getString("worlds." + world + ".texturePack" + ".title");
			}
		}
		if (config.getProperty("worlds." + world + ".sun") != null) {
			worldConf.sun.setVisibility(config.getBoolean("worlds." + world + ".sun" + ".visible", worldConf.sun.isVisible()));
			worldConf.sun.setURL(config.getString("worlds." + world + ".sun" + ".file"));
			worldConf.sun.setSize(config.getInt("worlds." + world + ".sun" + ".size", worldConf.sun.getSize()));
		}
		if (config.getProperty("worlds." + world + ".moon") != null) {
			worldConf.moon.setVisibility(config.getBoolean("worlds." + world + ".moon" + ".visible", worldConf.moon.isVisible()));
			worldConf.moon.setURL(config.getString("worlds." + world + ".moon" + ".file"));
			worldConf.moon.setSize(config.getInt("worlds." + world + ".moon" + ".size", worldConf.moon.getSize()));
		}
		if (config.getProperty("worlds." + world + ".stars") != null) {
			worldConf.stars.setVisibility(config.getBoolean("worlds." + world + ".stars" + ".visible", worldConf.stars.isVisible()));
			worldConf.stars.setFrequency(config.getInt("worlds." + world + ".stars" + ".frequency", worldConf.stars.getFrequency()));
		}
		if (config.getProperty("worlds." + world + ".clouds") != null) {
			worldConf.clouds.setVisibility(config.getBoolean("worlds." + world + ".clouds" + ".visible", worldConf.clouds.isVisible()));
			worldConf.clouds.setLevel(config.getInt("worlds." + world + ".clouds" + ".height", worldConf.clouds.getLevel()));
		}
		plugin.console.sendMessage(worldConf.toString());
		worldData.put(world, worldConf);
	}

	public void loadPlayerTexture(final Player player, final World world) {
		final SpoutPlayer splayer = SpoutManager.getPlayer(player);
		plugin.console.sendMessage("[" + plugin.name + "] " + player.getName() + " " + (splayer.isSpoutCraftEnabled() ? "Spout Player" : "Not Spout PLayer"));
		if (splayer.isSpoutCraftEnabled() && (worldData.get(world.getName()) != null)) {
			plugin.console.sendMessage("[" + plugin.name + "] " + world.getName() + " attempting to reject " + player.getName() + "'s reality and substitute my own.");
			worldData.get(world.getName()).onPlayerTP(splayer);
		}
	}

	public void setPlayerData(final Player player, final String key, final String value) {
		final String name = player.getName();
		HashMap<String, String> individual;
		if (playerData.containsKey(name)) {
			individual = playerData.get(name);
		} else {
			individual = new HashMap<String, String>();
		}
		individual.put(key, value);
		playerData.put(name, individual);
	}

}
