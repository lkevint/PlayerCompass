package io.github.liukevint2001.playercompass;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerCompass extends JavaPlugin {
	
	static HashMap<Player, Player> compassTracker = new HashMap<Player, Player>();
	
	@Override
	public void onEnable() {
		//performed when plugin is enabled
		getServer().getPluginManager().registerEvents(new CompassListener(), this);
		updateCompass();
		getLogger().info("onEnable has been invoked!");
	}
	
	@Override
	public void onDisable() {
		//performed when plugin is disabled
		getLogger().info("onDisable has been invoked!");
	}
	
	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("track")) {
			if (!(sender instanceof Player))
				return true;
			Player senderPlayer = Bukkit.getServer().getPlayer(sender.getName());
			Player targetPlayer;
	  	    if (args.length == 0 || ((targetPlayer = Bukkit.getServer().getPlayer(args[0])) == null)) {
			    sender.sendMessage("Player not found.");
			    return true;
		    }
	  	    if (compassTracker.containsKey(senderPlayer)) {
		    	compassTracker.replace(senderPlayer, targetPlayer);
		    	sender.sendMessage("Compass is now tracking " + targetPlayer.getName() + ".");
		    	return true;
		    } else {
		    	compassTracker.put(senderPlayer, targetPlayer);
		    	sender.sendMessage("Compass is now tracking " + targetPlayer.getName() + ".");
		    	return true;
		    }
	    } else if (cmd.getName().equalsIgnoreCase("untrack")) {
	    	Player senderPlayer = Bukkit.getServer().getPlayer(sender.getName());
	    	if (compassTracker.containsKey(sender)) {
	    		sender.sendMessage("Successfully untracked " + compassTracker.get(sender).getName() + ".");
	    		compassTracker.remove(senderPlayer);
	    		HashMap<Integer, ?> inventory = senderPlayer.getInventory().all(Material.COMPASS);
	    		CompassMeta compassMeta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
	    		for (Map.Entry compassItem : inventory.entrySet()) {
	    			ItemStack compass = (ItemStack) compassItem.getValue();
	    			compass.setItemMeta(compassMeta);
	    		}
	    		return true;
	    	} else {
	    		sender.sendMessage("You are currently not tracking a player.");
	    		return true;
	    	}
	    }
	    return false;
    }
  

	public void updateCompass() {
		new BukkitRunnable() {
			public void run() {
				for (Map.Entry mapElement : compassTracker.entrySet()){
					Player sender = (Player) mapElement.getKey();
					Player target = (Player) mapElement.getValue();
					if (sender.isOnline() && target.isOnline()) {
						if (sender.getInventory().contains(Material.COMPASS)) {
							Location targetLocation = target.getLocation();
							if (sender.getWorld().getName().equals("world") && target.getWorld().getName().equals("world_nether")) {
								targetLocation = target.getLocation().multiply(8);
								targetLocation.setWorld(sender.getWorld());
							}
							else if (sender.getWorld().getName().equals("world_nether") && target.getWorld().getName().equals("world")) {
								targetLocation = target.getLocation().multiply(1/8);
								targetLocation.setWorld(sender.getWorld());
							}
							HashMap<Integer, ?> inventory = sender.getInventory().all(Material.COMPASS);
							for (Map.Entry compassItem : inventory.entrySet()) {
								ItemStack compass = (ItemStack) compassItem.getValue();
								CompassMeta compassMeta = (CompassMeta) compass.getItemMeta();
								compassMeta.setLodestoneTracked(false);
								compassMeta.setLodestone(targetLocation);
								compass.setItemMeta(compassMeta);
							}
						}
					}
				}
			}		
		}.runTaskTimer(this,  20,  20);
	}

}
