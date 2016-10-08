package me.FrejNielsen.EasyBan;

import java.util.logging.Level;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Listeners implements Listener {
	
	private JavaPlugin plugin;
	
	public Listeners(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
		
		if(EasyBan.banHandler.isBanned(player)) {
			if(EasyBan.banHandler.ticksLeft(player) > 0) {
				plugin.getLogger().log(Level.SEVERE, Long.toString(EasyBan.banHandler.ticksLeft(player)));
				event.setKickMessage(ChatColor.translateAlternateColorCodes('&', EasyBan.banHandler.getReason(player) + "\n\n\n&eYou will be unbanned in &4" + DurationFormatUtils.formatDurationWords(1000 * (EasyBan.banHandler.ticksLeft(player) / 20), true, true)));
				event.setLoginResult(Result.KICK_OTHER);
			}
			else if(EasyBan.banHandler.ticksLeft(player) < 0) {
				event.setKickMessage(ChatColor.translateAlternateColorCodes('&', EasyBan.banHandler.getReason(player) + "\n\n\n&eYou will &l&o&4NOT &ebe unbanned"));
				event.setLoginResult(Result.KICK_OTHER);
			}
			else
				EasyBan.banHandler.unban(player);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(EasyBan.banHandler.isBanned(player))
			event.setQuitMessage(null);
	}
}
