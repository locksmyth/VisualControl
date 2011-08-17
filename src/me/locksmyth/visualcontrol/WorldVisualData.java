package me.locksmyth.visualcontrol;

import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class WorldVisualData {
	public class CloudVisuals extends SkyObject {
		private int level = 108;

		public CloudVisuals(final boolean visible, final int level) {
			try {
				setLevel(level);
			} catch (final IntegerOutOfBoundsException e) {
				plugin.console.sendMessage("Invalid Cloud height: " + level);
			}
			setVisibility(visible);
		}

		public int getLevel() {
			return level;
		}

		@Override
		public void setInt(final int value) throws IntegerOutOfBoundsException {
			setLevel(value);
		}

		public void setLevel(final int level) throws IntegerOutOfBoundsException {
			if ((level > 127) || (level < 0)) {
				throw new IntegerOutOfBoundsException();
			} else {
				this.level = level;
			}
		}
	}

	class IntegerOutOfBoundsException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	class InvalidCelestialOrbException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	public class SkyObject {
		private boolean visible = true;

		public boolean isVisible() {
			return visible;
		}

		public void setInt(final int state) throws IntegerOutOfBoundsException {
		}

		public void setString(final String value) {
		}

		public void setVisibility(final boolean state) {
			visible = state;
		}
	}

	public class SkyOrb extends SkyObject {
		private int size = 100;
		private String url = null;
		private String type = "";

		public SkyOrb(final String type, final boolean visible, final int size) {
			try {
				setType(type);
			} catch (final InvalidCelestialOrbException e) {
				plugin.console.sendMessage("Invalid Celestial Orb: " + type + "\n It must either be a sun or a moon.");
			}
			try {
				setSize(size);
			} catch (final IntegerOutOfBoundsException e) {
				plugin.console.sendMessage("Invalid " + type + " size: " + size + ". Must be between 0 and 200.");
			}
			setVisibility(visible);
		}

		public int getSize() {
			return size;
		}

		public String getType() {
			return type;
		}

		public String getURL() {
			return url;
		}

		public boolean hasURL() {
			return (url != null);
		}

		@Override
		public void setInt(final int value) throws IntegerOutOfBoundsException {
			setSize(value);
		}

		public void setSize(final int size) throws IntegerOutOfBoundsException {
			if ((size > 200) || (size < 0)) {
				throw new IntegerOutOfBoundsException();
			} else {
				this.size = size;
			}
		}

		@Override
		public void setString(final String value) {
			setURL(value);
		}

		public void setType(final String type) throws InvalidCelestialOrbException {
			if (type.equalsIgnoreCase("sun") || type.equalsIgnoreCase("moon")) {
				this.type = type;
			} else {
				throw new InvalidCelestialOrbException();
			}
		}

		public boolean setURL(final String file) {
			if ((file != null) && urlExists(file, "image/png")) {
				url = file;
				return true;
			} else {
				return false;
			}
		}
	}

	public class StarVisuals extends SkyObject {
		private int frequency = 1500;

		public StarVisuals(final boolean visible, final int frequency) {
			try {
				setFrequency(frequency);
			} catch (final IntegerOutOfBoundsException e) {
				plugin.console.sendMessage("Invalid star frequency: " + frequency + "\n It must either be between 0 and 2000.");
			}
			setVisibility(visible);
		}

		public int getFrequency() {
			return frequency;
		}

		public void setFrequency(final int frequency) throws IntegerOutOfBoundsException {
			if ((frequency > 2000) || (frequency < 0)) {
				throw new IntegerOutOfBoundsException();
			} else {
				this.frequency = frequency;
			}
		}
	}

	private World world;
	public static VisualControlPlayerManager playerManager;
	public static VisualControl plugin;
	public boolean defaultTexturePack = true;
	public String textureFile;
	public String textureTitle;
	public CloudVisuals clouds = new CloudVisuals(true, 108);
	public StarVisuals stars = new StarVisuals(true, 1500);
	public SkyOrb sun = new SkyOrb("sun", true, 100);
	public SkyOrb moon = new SkyOrb("moon", true, 100);

	public WorldVisualData(final VisualControlPlayerManager playerManager) {
		WorldVisualData.playerManager = playerManager;
		WorldVisualData.plugin = VisualControlPlayerManager.plugin;
	}

	public WorldVisualData(final World world) {
		this.world = world;
		switch (world.getEnvironment()) {
		case NORMAL:
		break;
		case SKYLANDS:
			try {
				clouds.setLevel(0);
			} catch (final IntegerOutOfBoundsException e) {
			}
		break;
		case NETHER:
			clouds.setVisibility(false);
			sun.setVisibility(false);
			moon.setVisibility(false);
			stars.setVisibility(false);
		break;
		}
	}

	public boolean doCommand(final CommandSender sender, final String entity, final String setting) {
		int value = 0;
		String intType = "";
		VisualControlPlayerManager.config.load();
		try {
			value = Integer.parseInt(setting);
			switch (VisualControl.KeyWords.fromString(entity)) {
			case MOON:
			case SUN:
				intType = "size";
			break;
			case CLOUDS:
				intType = "height";
			break;
			case STARS:
				intType = "frequency";
			break;
			default:
				sender.sendMessage("Not sure as to what " + entity + " refers.");
			}
			if (VisualControl.KeyWords.fromString(entity) != null) {
				VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + "." + entity.toLowerCase() + "." + intType, value);
				return true;
			}
		} catch (final NumberFormatException e) {
			if (setting.equalsIgnoreCase("on") || setting.equalsIgnoreCase("off")) {
				VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + "." + entity.toLowerCase() + ".visible", (setting.equalsIgnoreCase("on")));
				return true;
			} else {
				switch (VisualControl.KeyWords.fromString(entity)) {
				case MOON:
					if (urlExists(setting, "image/png")) {
						textureFile = setting;
						VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + "." + entity.toLowerCase() + ".file", setting);
						return true;
					} else {
						sender.sendMessage("URL " + setting + " does not resolve to a valid PNG.");
						return true;
					}
				case SUN:
					if (urlExists(setting, "image/png")) {
						textureFile = setting;
						VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + "." + entity.toLowerCase() + ".file", setting);
						return true;
					} else {
						sender.sendMessage("URL " + setting + " does not resolve to a valid PNG.");
						return true;
					}
				case USE:
					if (setting.equalsIgnoreCase("default")) {
						VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + ".texturePack.useDefaultTexture", true);
						return true;
					} else {
						if (urlExists(setting, "application/zip")) {
							textureFile = setting;
							VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + ".texturePack.file", setting);
							VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + ".texturePack.useDefaultTexture", false);
							return true;
						} else {
							sender.sendMessage("URL " + setting + " does not resolve to a valid ZIP.");
							return true;
						}
					}
				case TITLE:
					textureTitle = setting;
					VisualControlPlayerManager.config.setProperty("worlds." + world.getName() + ".texturePack.title", setting);
					return true;
				default:
					return false;
				}
			}
		} catch (final Throwable t) {
			sender.sendMessage(t.toString());
		}
		return false;
	}

	public void onPlayerTP(final SpoutPlayer player) {
		try {
			SpoutManager.getSkyManager().setCloudsVisible(player, clouds.isVisible());
			SpoutManager.getSkyManager().setStarsVisible(player, stars.isVisible());
			SpoutManager.getSkyManager().setMoonVisible(player, moon.isVisible());
			SpoutManager.getSkyManager().setSunVisible(player, sun.isVisible());

			if (defaultTexturePack == false) {
				if ((textureFile != null) && (playerManager.getPlayerData(player, "texture") != textureFile.toString())) {
					player.setTexturePack(textureFile.toString());
					playerManager.setPlayerData(player, "texture", textureFile.toString());
					if (VisualControlPlayerManager.announce) {
						player.sendNotification("Visual Control", textureTitle.toString() + " loaded.", Material.PAINTING);
					}
				}
			} else {
				if ((VisualControlPlayerManager.defaultFile != null) && (playerManager.getPlayerData(player, "texture") != VisualControlPlayerManager.defaultFile.toString())) {
					player.setTexturePack(VisualControlPlayerManager.defaultFile.toString());
					playerManager.setPlayerData(player, "texture", VisualControlPlayerManager.defaultFile.toString());
					if (VisualControlPlayerManager.announce) {
						player.sendNotification("Visual Control", VisualControlPlayerManager.defaultTitle.toString() + " loaded.", Material.PAINTING);
					}
				} else {
					playerManager.setPlayerData(player, "texture", "");
					// player.resetTextureControl();
				}
			}
			if (clouds.isVisible()) {
				SpoutManager.getSkyManager().setCloudHeight(player, clouds.getLevel());
			}
			if (stars.isVisible()) {
				SpoutManager.getSkyManager().setStarFrequency(player, stars.getFrequency());
			}
			if (sun.isVisible()) {
				SpoutManager.getSkyManager().setSunSizePercent(player, sun.getSize());
				if ((sun.hasURL()) && (playerManager.getPlayerData(player, "sun") != sun.getURL())) {
					SpoutManager.getSkyManager().setSunTextureUrl(player, sun.getURL());
					playerManager.setPlayerData(player, "sun", sun.getURL());
				} else if (!sun.hasURL()) {
					SpoutManager.getSkyManager().setSunTextureUrl(player, null);
					playerManager.setPlayerData(player, "sun", "");
				}
			}
			if (moon.isVisible()) {
				SpoutManager.getSkyManager().setMoonSizePercent(player, moon.getSize());
				if ((moon.hasURL()) && (playerManager.getPlayerData(player, "moon") != moon.getURL())) {
					SpoutManager.getSkyManager().setMoonTextureUrl(player, moon.getURL());
					playerManager.setPlayerData(player, "moon", moon.getURL());
				} else if (!moon.hasURL()) {
					SpoutManager.getSkyManager().setMoonTextureUrl(player, null);
					playerManager.setPlayerData(player, "moon", "");
				}
			}
		} catch (final UnsupportedOperationException e) {
			if (VisualControlPlayerManager.announce) {
				player.sendMessage(e.toString());
			}
			plugin.console.sendMessage("[" + plugin.name + "] " + e.toString());
		}
	}

	@Override
	public String toString() {
		String value = "Use Default Texture? " + (defaultTexturePack ? "Yes" : "No") + "\n";
		value = value.concat((defaultTexturePack ? "" : "Texture: " + textureTitle.toString() + "\n"));
		value = value.concat((defaultTexturePack ? "" : "File: " + textureFile.toString() + "\n"));
		value = value.concat("Show clouds? " + (clouds.isVisible() ? "Yes" : "No") + "\n");
		value = value.concat((clouds.isVisible() ? "Cloud layer: " + clouds.getLevel() + "\n" : ""));
		value = value.concat("Show moon? " + (moon.isVisible() ? "Yes" : "No") + "\n");
		value = value.concat((moon.isVisible() ? "Moon size (%): " + moon.getSize() + "\n" : ""));
		value = value.concat((moon.isVisible() ? "Moon texture: " + (moon.hasURL() ? moon.getURL() : "NONE") + "\n" : ""));
		value = value.concat("Show stars? " + (stars.isVisible() ? "Yes" : "No") + "\n");
		value = value.concat((stars.isVisible() ? "Star frequency: " + stars.getFrequency() + "\n" : ""));
		value = value.concat("Show sun? " + (sun.isVisible() ? "Yes" : "No") + "\n");
		value = value.concat((sun.isVisible() ? "Sun size (%): " + sun.getSize() + "\n" : ""));
		value = value.concat((sun.isVisible() ? "Sun texture: " + (sun.hasURL() ? sun.getURL() : "NONE") + "\n" : ""));
		return value;
	}

	private boolean urlExists(final String URLName, final String type) {
		if (URLName != null) {
			return true; // TODO figure out why URL validation is not working.
		}
		try {
			final HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			if ((con.getResponseCode() == HttpURLConnection.HTTP_OK) && con.getContentType().equalsIgnoreCase(type)) {
				return true;
			} else {
				plugin.console.sendMessage(con.getContentType());
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}
}
