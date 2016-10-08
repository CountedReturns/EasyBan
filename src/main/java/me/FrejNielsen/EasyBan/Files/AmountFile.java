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

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class AmountFile {

	JavaPlugin plugin;

	File file;

	private HashMap<UUID, Integer> banAmount = new HashMap<UUID, Integer>();

	public AmountFile(JavaPlugin plugin, String fileName) throws IOException {
		this.plugin = plugin;

		this.file = new File(this.plugin.getDataFolder(), fileName);

		if (this.file.createNewFile()) {
			plugin.getLogger().log(Level.FINEST, "Amount file created.");
		} else {
			plugin.getLogger().log(Level.FINE, "Amount file already exists, loading it..");
			loadAmounts();
		}
	}

	private void loadAmounts() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains(";")) {
					String[] arr = line.split(";");

					UUID uuid = UUID.fromString(arr[0]);

					banAmount.put(uuid, Integer.parseInt(arr[1]));
				}
			}
		}
	}

	public int getAmount(OfflinePlayer player) {
		return banAmount.containsKey(player.getUniqueId()) ? banAmount.get(player.getUniqueId()) : 0;
	}

	public void add(OfflinePlayer player) {
		FileWriter w = null;
		BufferedWriter bw = null;

		try {
			w = new FileWriter(file, true);
			bw = new BufferedWriter(w);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (banAmount.containsKey(player.getUniqueId())) {
			int amount = banAmount.get(player.getUniqueId());
			banAmount.remove(player.getUniqueId());
			banAmount.put(player.getUniqueId(), amount + 1);

			if (bw != null) {
				try {
					bw.write(player.getUniqueId() + ";" + (amount + 1) + "\n");
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			banAmount.put(player.getUniqueId(), 1);
			if (bw != null) {
				try {
					bw.write(player.getUniqueId() + ";" + 1);
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void remove(OfflinePlayer player) {
		FileReader r = null;
		BufferedReader br = null;
		
		FileWriter w = null;
		BufferedWriter bw = null;

		try {
			r = new FileReader(file);
			br = new BufferedReader(r);
			
			w = new FileWriter(file);
			bw = new BufferedWriter(w);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String line;
		
		String output = "";
		
		String edit = "";
		
		try {
			while((line = br.readLine()) != null) {
				if(line.startsWith(player.getUniqueId().toString())) {
					edit = line;
				}
				output = output + line + "\n";
			}
			if(edit.equals(""))
				return;
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String[] amountstr = edit.split(";");

		if(amountstr[1].equals("1"))
			output = output.replace(edit, "");
		else {
			amountstr[1] = Integer.toString((Integer.parseInt(amountstr[1]) - 1));
			output = output.replace(edit, amountstr[0] + ";" + amountstr[1]);
		}
			
		try {
			bw.write(output);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		if(banAmount.get(player.getUniqueId()) == 1)
			banAmount.remove(player.getUniqueId());
		else {
			int amount = banAmount.get(player.getUniqueId()) - 1;
			banAmount.remove(player.getUniqueId());
			banAmount.put(player.getUniqueId(), amount);
		}
	}
}
