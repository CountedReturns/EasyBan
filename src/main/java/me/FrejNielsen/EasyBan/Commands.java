package me.FrejNielsen.EasyBan;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ban")) {
			switch(args.length) {
			case 0:
				sender.sendMessage(ChatColor.RED + "I can't read minds, who am I supposed to ban?");
				break;
			case 1:
				sender.sendMessage(ChatColor.RED + "Come on man, I ain't banning for no reason.");
				break;
			case 2:
				switch(args[1]) {
				case "hacking":
					if(EasyBan.amountHandler.getAmount(Bukkit.getOfflinePlayer(args[0])) > 1) {
						EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Hacking &6is not allowed.", 0L, sender);
					}
					//2 weeks
					EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Hacking &6is not allowed.", 1209600 * 20, sender);
					break;
				case "spamming":
					if(EasyBan.amountHandler.getAmount(Bukkit.getOfflinePlayer(args[0])) > 1) {
						EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Spamming &6is not allowed.", 0L, sender);
					}
					//2 days
					EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Spamming &6is not allowed.", 172800 * 20, sender);
					break;
				case "advertising":
					if(EasyBan.amountHandler.getAmount(Bukkit.getOfflinePlayer(args[0])) > 0) {
						EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Advertising &6is not allowed.", 0L, sender);
					}
					//1 month
					EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Advertising &6is not allowed.", 2629743 * 20, sender);
					break;
				case "offense":
					//Permanent
					EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Offending &aother people &6is not tolerated in any way.", 0L, sender);
					break;
				case "abuse":
					//Permanent
					EasyBan.banHandler.ban(Bukkit.getOfflinePlayer(args[0]), "&4Admin abusing &6is not tolerated in any way.", 0L, sender);
					break;
				}
				break;
			default:
				sender.sendMessage(ChatColor.RED + "What's this nonsense? I can't use all of those arguments for anything");
				break;
			}
		} else if(cmd.getName().equalsIgnoreCase("unban")) {
			switch(args.length) {
			case 0:
				sender.sendMessage(ChatColor.RED + "I can't unban noone, that's just not how it works");
				break;
			case 1:
				if(EasyBan.banHandler.isBanned(Bukkit.getOfflinePlayer(args[0]))) {
					EasyBan.banHandler.unban(Bukkit.getOfflinePlayer(args[0]));
					sender.sendMessage(ChatColor.BLUE + args[0] + ChatColor.GREEN + " has been unbanned");
				}
				else
					sender.sendMessage(ChatColor.RED + "I'm having a hard time here, and you're really not helping it, why do you think I can unban someone who isn't banned?");
			}
			
			if(args.length > 1) {
				sender.sendMessage(ChatColor.RED + "Holy! That's a lot of arguments, I can't really use them for anything though.");
			}
		}
		return false;
	}

}
