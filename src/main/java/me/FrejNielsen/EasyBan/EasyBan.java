package me.FrejNielsen.EasyBan;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.FrejNielsen.EasyBan.Files.AmountFile;
import me.FrejNielsen.EasyBan.Files.BanFile;

public class EasyBan extends JavaPlugin {
	
	public static BanFile banHandler;
	public static AmountFile amountHandler;
	
	private PluginManager pm = getServer().getPluginManager();
	
	private PluginCommand banCmd;
	private PluginCommand unbanCmd;
	
	private Commands cmd;
	
	public void onEnable() {
		cmd = new Commands();
		banCmd = getCommand("ban");
		unbanCmd = getCommand("unban");
		
		if(!this.getDataFolder().exists())
			this.getDataFolder().mkdir();
		
		try {
			banHandler = new BanFile(this, "bannedPlayers.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			amountHandler = new AmountFile(this, "banAmounts.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pm.registerEvents(new Listeners(this), this);

		banCmd.setExecutor(cmd);
		banCmd.setPermission("easyban.ban");
		banCmd.setPermissionMessage(ChatColor.RED + "No.. Just no.");
		
		unbanCmd.setExecutor(cmd);
		unbanCmd.setPermission("easyban.unban");
		unbanCmd.setPermissionMessage(ChatColor.RED + "No.. Just no.");
	}
	
	public void onDisable() {
		
	}
	
}
