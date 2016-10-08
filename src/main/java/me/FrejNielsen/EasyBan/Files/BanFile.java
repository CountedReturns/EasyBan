package me.FrejNielsen.EasyBan.Files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.FrejNielsen.EasyBan.EasyBan;

public class BanFile {
	
	private JavaPlugin plugin;
	
	private File file;
	
	private HashMap<UUID, String> banned = new HashMap<UUID, String>();
	
	private HashMap<UUID, Long> endTime = new HashMap<UUID, Long>();
	
	public BanFile(JavaPlugin plugin, String fileName) throws IOException {
		this.plugin = plugin;
		
		this.file = new File(this.plugin.getDataFolder().getAbsolutePath() + "\\" + fileName);
		
		if(this.file.createNewFile()) {
			plugin.getLogger().log(Level.FINEST, "Ban file created.");
		} else {
			plugin.getLogger().log(Level.FINEST, "Ban file already exists, loading it..");
			loadBans();
		}
	}
	
	private void loadBans() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       if(line.contains(";")) {
		    	   String[] arr = line.split(";");
		    	   
		    	   UUID uuid = UUID.fromString(arr[0]);
		    	   
		    	   long time = Long.parseLong(arr[2]);
		    	   
		    	   if(time > 0L)
		    		   endTime.put(uuid, time);
		    	   
		    	   banned.put(uuid, arr[1]);
		       }
		    }
		}
	}
	
	public boolean isBanned(OfflinePlayer player) {
		return banned.containsKey(player.getUniqueId());
	}
	
	public String getReason(OfflinePlayer player) {
		if(!isBanned(player))
			return null;
		
		return banned.get(player.getUniqueId());
	}
	
	public void ban(OfflinePlayer player, String reason, long time, CommandSender sender) {
		if(isBanned(player)) {
			sender.sendMessage(ChatColor.RED + "I can't ban a banned player, like wtf mate.");
			return;
		}
		
		banned.put(player.getUniqueId(), reason);
		
		FileWriter w = null;
		BufferedWriter bw = null;
		
		if(time == 0 && player.isOnline())
			player.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', reason + "\n&6You will &l&o&4NOT &6be unbanned"));
		
		if(time > 0 && player.isOnline())
			player.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', reason + "\n" + "&eYou will be unbanned in " + DurationFormatUtils.formatDurationWords(20 * (time / 1000), true, true)));
		
		try {
			w = new FileWriter(file, true);
			bw = new BufferedWriter(w);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(bw != null) {
			try {
				bw.write(player.getUniqueId() + ";" + reason + ";" + (time == 0L ? "0" : System.currentTimeMillis() + time) + "\n");
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(time > 0L) {
			long endTim = System.currentTimeMillis() + time;
			endTime.put(player.getUniqueId(), endTim);
		}
		
		if(time > 0)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Successfully banned player &a" + player.getName() + " &9 in &6" + DurationFormatUtils.formatDurationWords(1000 * (time / 20), true, true) + " &9 for: &7\"&3"  + reason + "&7\"&9."));
		else
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9Successfully banned player &a" + player.getName() + " &6forever" + " &9 for: &7\"&3"  + reason + "&7\"&9."));
		
		EasyBan.amountHandler.add(player);
	}
	
	public void unban(OfflinePlayer player) {
		banned.remove(player.getUniqueId());
		
		FileReader r = null;
		BufferedReader br = null;
		
		FileWriter w = null;
		BufferedWriter bw = null;
		try {
			r = new FileReader(file);
			br  = new BufferedReader(r);
			
			w = new FileWriter(file);
			bw  = new BufferedWriter(w);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String line;
		
		String remove = "";
		
		String full = "";
		
		try {
			while((line = br.readLine()) != null) {
				 if(line.startsWith(player.getUniqueId().toString()))
					 remove = line;
				 
				 full = full + line + "\n";
			}
			
			full.replace(remove, "");
			
			bw.write(full);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		EasyBan.amountHandler.remove(player);
	}
	
	public long ticksLeft(OfflinePlayer player) {
		if(endTime.containsKey(player.getUniqueId())) {
			long time = endTime.get(player.getUniqueId());
			
			if(time - System.currentTimeMillis() < 0L)
				return 0L;
			
			return time - System.currentTimeMillis();
		}
		return -1L;
	}
}
