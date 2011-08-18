package me.locksmyth.visualcontrol;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Align;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;

public class VisualControlListener {

	public class sL extends ScreenListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onButtonClick(final ButtonClickEvent event) {
			if ((event.getButton() instanceof GenericButton) && event.getButton().getText().equals("Test")) {
				(event.getPlayer()).getMainScreen().closePopup();
				event.getScreen().setDirty(true);
				(event.getPlayer()).getMainScreen().attachWidget(
																	plugin,
																	((GenericLabel) new GenericLabel("I'm on the main screen!").setHexColor(0xFFFFFF).setAlignY(Align.FIRST))
																			.setAlignX(Align.FIRST).setX(0).setY(0).setHeight(240).setWidth(427));
				event.getPlayer().sendMessage("Button test successful!");
			}
		}

	}

	public class VisualControlPlayerListener extends PlayerListener {

		@Override
		public void onPlayerPortal(final PlayerPortalEvent event) {
			plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getTo().getWorld());
		}

		@Override
		public void onPlayerQuit(final PlayerQuitEvent event) {
			SpoutManager.getPlayer(event.getPlayer()).resetTexturePack();
			VisualControlPlayerManager.playerData.remove(event.getPlayer().getName());
		}

		@Override
		public void onPlayerRespawn(final PlayerRespawnEvent event) {
			plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getRespawnLocation().getWorld());
		}

		@Override
		public void onPlayerTeleport(final PlayerTeleportEvent event) {
			if (event.getFrom().getWorld() != event.getTo().getWorld()) {
				plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getTo().getWorld());
			}
		}

	}

	public class VisualControlSpoutListener extends SpoutListener {
		@Override
		public void onSpoutCraftEnable(final SpoutCraftEnableEvent event) {
			plugin.playerManager.initializePlayer(event.getPlayer());
			plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getPlayer().getWorld());
		}
	}

	public class VisualControlWorldListener extends WorldListener {
		@Override
		public void onWorldLoad(final WorldLoadEvent event) {
			try {
				plugin.playerManager.loadConfigWorld(event.getWorld());
			} catch (final Exception e) {
				plugin.console.sendMessage("Unexpected error loading world configuration.");
			}
		}

	}

	public static VisualControl plugin;
	public VisualControlWorldListener world;
	public VisualControlPlayerListener player;
	public VisualControlSpoutListener spout;

	public VisualControlListener(final VisualControl plugin) {
		VisualControlListener.plugin = plugin;
		world = new VisualControlWorldListener();
		spout = new VisualControlSpoutListener();
		player = new VisualControlPlayerListener();
	}
}