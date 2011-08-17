package me.locksmyth.visualcontrol;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;

public class VisualControlListener {
	public class VisualControlPlayerListener extends PlayerListener {

		@Override
		public void onPlayerPortal(final PlayerPortalEvent event) {
			plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getTo().getWorld());
		}

		@Override
		public void onPlayerRespawn(final PlayerRespawnEvent event) {
			plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getPlayer().getWorld());
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