package me.locksmyth.visualcontrol;

/**
 * 
 */

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author mgsmith
 */
public class VisualControlPlayerListener extends PlayerListener {
	public static VisualControl plugin;

	public VisualControlPlayerListener(final VisualControl instance) {
		plugin = instance;
	}

	@Override
	public void onPlayerJoin(final PlayerJoinEvent event) {
		plugin.playerManager.initializePlayer(event.getPlayer());
		plugin.playerManager.loadPlayerTexture(event.getPlayer(), event.getPlayer().getWorld());
	}

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
