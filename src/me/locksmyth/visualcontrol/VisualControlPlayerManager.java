/**
 * 
 */
package me.locksmyth.visualcontrol;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mgsmith
 */
public class VisualControlPlayerManager {

	private class WorldData {
		public boolean defaultTexturePack = true;
		public Object textureFile = defaultFile;
		public Object textureTitle = defaultTitle;
		public boolean cloudsVisible = true;
		public boolean starsVisible = true;
		public boolean moonVisible = true;
		public boolean sunVisible = true;
		public int cloudHeight = 108;
		public int sunSize = 100;
		public Object sunFile;
		public int moonSize = 100;
		public Object moonFile;
		public int starFrequency = 1500;

		@SuppressWarnings("unused")
		public WorldData() {

		}

		public WorldData(final World world) {
			switch (world.getEnvironment()) {
			case NORMAL:
			break;
			case SKYLANDS:
				cloudHeight = 0;
			break;
			case NETHER:
				cloudsVisible = false;
				sunVisible = false;
				moonVisible = false;
				starsVisible = false;
			break;
			}
		}

		public void onPlayerTP(final SpoutPlayer player) {
			plugin.console.sendMessage("[" + plugin.name + "] " + player.getName());

			SpoutManager.getSkyManager().setCloudsVisible(player, cloudsVisible);
			SpoutManager.getSkyManager().setStarsVisible(player, starsVisible);
			SpoutManager.getSkyManager().setMoonVisible(player, moonVisible);
			SpoutManager.getSkyManager().setSunVisible(player, sunVisible);

			if (defaultTexturePack == false) {
				if ((textureFile != null) && (getPlayerData(player, "texture") != textureFile.toString())) {
					player.setTexturePack(textureFile.toString());
					setPlayerData(player, "texture", textureFile.toString());
					if (announce) {
						player.sendNotification("Visual Control", textureTitle.toString() + " loaded.", Material.PAINTING);
					}
				}
			} else {
				if ((defaultFile != null) && (getPlayerData(player, "texture") != defaultFile.toString())) {
					player.setTexturePack(defaultFile.toString());
					setPlayerData(player, "texture", defaultFile.toString());
					if (announce) {
						player.sendNotification("Visual Control", defaultTitle.toString() + " loaded.", Material.PAINTING);
					}
				} else {
					setPlayerData(player, "texture", "");
					// player.resetTextureControl();
				}
			}
			if (cloudsVisible == true) {
				SpoutManager.getSkyManager().setCloudHeight(player, cloudHeight);
			}
			if (starsVisible == true) {
				SpoutManager.getSkyManager().setStarFrequency(player, starFrequency);
			} else {
				SpoutManager.getSkyManager().setStarFrequency(player, starFrequency);
			}
			if (sunVisible == true) {
				SpoutManager.getSkyManager().setSunSizePercent(player, sunSize);
				if ((sunFile != null) && (getPlayerData(player, "sun") != sunFile.toString())) {
					SpoutManager.getSkyManager().setSunTextureUrl(player, sunFile.toString());
					setPlayerData(player, "sun", sunFile.toString());
				} else {
					setPlayerData(player, "sun", "");
				}
			}
			if (moonVisible == true) {
				SpoutManager.getSkyManager().setMoonSizePercent(player, moonSize);
				if ((moonFile != null) && (getPlayerData(player, "moon") != moonFile.toString())) {
					SpoutManager.getSkyManager().setMoonTextureUrl(player, moonFile.toString());
					setPlayerData(player, "moon", moonFile.toString());
				} else {
					SpoutManager.getSkyManager().setMoonTextureUrl(player, null);
					setPlayerData(player, "moon", "");
				}
			}
		}

		@Override
		public String toString() {
			String value = "Use Default Texture? " + (defaultTexturePack ? "Yes" : "No") + "\n";
			value = value.concat((defaultTexturePack ? "" : "Texture: " + textureTitle.toString() + "\n"));
			value = value.concat((defaultTexturePack ? "" : "File: " + textureFile.toString() + "\n"));
			value = value.concat("Show clouds? " + (cloudsVisible ? "Yes" : "No") + "\n");
			value = value.concat((cloudsVisible ? "Cloud layer: " + cloudHeight + "\n" : ""));
			value = value.concat("Show moon? " + (moonVisible ? "Yes" : "No") + "\n");
			value = value.concat((moonVisible ? "Moon size (%): " + moonSize + "\n" : ""));
			value = value.concat((moonVisible ? "Moon texture: " + (moonFile == null ? "NONE" : moonFile.toString()) + "\n" : ""));
			value = value.concat("Show stars? " + (starsVisible ? "Yes" : "No") + "\n");
			value = value.concat((starsVisible ? "Star frequency: " + starFrequency + "\n" : ""));
			value = value.concat("Show sun? " + (sunVisible ? "Yes" : "No") + "\n");
			value = value.concat((sunVisible ? "Sun size (%): " + sunSize + "\n" : ""));
			value = value.concat((sunVisible ? "Sun texture: " + (sunFile == null ? "NONE" : sunFile.toString()) + "\n" : ""));
			return value;
		}
	}

	public Configuration config;

	public static Boolean announce = new Boolean(null);
	public static String defaultTitle = new String();
	public static String defaultFile = new String();
	public static HashMap<String, WorldData> worldData = new HashMap<String, WorldData>();
	public static HashMap<String, HashMap<String, String>> playerData = new HashMap<String, HashMap<String, String>>();
	public static VisualControl plugin;

	public VisualControlPlayerManager(final VisualControl visualControl) {
		plugin = visualControl;
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
			config.setHeader("/****************************************", " * " + plugin.name + " " + plugin.version, " * locksmyth", " *", "*", " * Configuration options ",
								" * announce: <true/false> // is the player notified of the loading of a new texture pack",
								" * defaultFile : <URL> // the URL to a direct download of the texture pack",
								" * defaultTitle : <STRING> // the title of the default texturepack to load", " * worlds:", " *     AlchemyX_nether:", " *         texturePack:",
								" *             file: http://url.to.texturepack", " *             title: Texture Title", " *             useDefaultTexture: true",
								" *         sun:", " *             file: http://url.to.sun/image.png", " *             size: 0 - 100", " *             visible: true/false",
								" *         moon:", " *             file: http://url.to.moon/image.png", " *             size: 0 - 100", " *             visible: true/false",
								" *         stars:", " *             frequency: 1500  // the default value is 1500", " *             visible: true/false", " *         clouds:",
								" *             height: 0 - 127", " *             visible: true/false", " *", " ****************************************/");
		}
		if (config.getProperty("announce") == null) {
			config.setProperty("announce", true);
			announce = true;
		} else {
			announce = config.getBoolean("announce", false);
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
			loadConfigWorld(world);
		}
		config.save();
	}

	public void loadConfigWorld(final World world2) {
		final String world = world2.getName();
		final WorldData worldConf = new WorldData(world2);
		plugin.console.sendMessage("[" + plugin.name + "] " + world);
		if (config.getProperty("worlds." + world + ".texturePack") == null) {
			config.setProperty("worlds." + world + ".texturePack" + ".useDefaultTexture", new Boolean(true));
			worldConf.defaultTexturePack = true;
		} else {
			worldConf.defaultTexturePack = config.getBoolean("worlds." + world + ".texturePack" + ".useDefaultTexture", true);
			if (worldConf.defaultTexturePack == false) {
				worldConf.textureFile = config.getProperty("worlds." + world + ".texturePack" + ".file");
				worldConf.textureTitle = config.getProperty("worlds." + world + ".texturePack" + ".title");
			}
		}
		if (config.getProperty("worlds." + world + ".sun") != null) {
			worldConf.sunVisible = config.getBoolean("worlds." + world + ".sun" + ".visible", worldConf.sunVisible);
			worldConf.sunFile = config.getProperty("worlds." + world + ".sun" + ".file");
			worldConf.sunSize = config.getInt("worlds." + world + ".sun" + ".size", worldConf.sunSize);
		}
		if (config.getProperty("worlds." + world + ".moon") != null) {
			worldConf.moonVisible = config.getBoolean("worlds." + world + ".moon" + ".visible", worldConf.moonVisible);
			worldConf.moonFile = config.getProperty("worlds." + world + ".moon" + ".file");
			worldConf.moonSize = config.getInt("worlds." + world + ".moon" + ".size", worldConf.moonSize);
		}
		if (config.getProperty("worlds." + world + ".stars") != null) {
			worldConf.starsVisible = config.getBoolean("worlds." + world + ".stars" + ".visible", worldConf.starsVisible);
			worldConf.starFrequency = config.getInt("worlds." + world + ".stars" + ".frequency", worldConf.starFrequency);
		}
		if (config.getProperty("worlds." + world + ".clouds") != null) {
			worldConf.cloudsVisible = config.getBoolean("worlds." + world + ".clouds" + ".visible", worldConf.cloudsVisible);
			worldConf.cloudHeight = config.getInt("worlds." + world + ".clouds" + ".height", worldConf.cloudHeight);
		}
		plugin.console.sendMessage(worldConf.toString());
		worldData.put(world, worldConf);
	}

	public void loadPlayerTexture(final Player player, final World world) {
		final SpoutPlayer splayer = SpoutManager.getPlayer(player);
		if (splayer.isSpoutCraftEnabled() && (worldData.get(world.getName()) != null)) {
			worldData.get(world.getName()).onPlayerTP(splayer);
			plugin.console.sendMessage("[" + plugin.name + "] " + world.getName() + " attempting to reject " + player.getName() + "'s reality and substitute my own.");
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
