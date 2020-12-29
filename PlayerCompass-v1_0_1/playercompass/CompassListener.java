package io.github.lkevint.playercompass;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;


public class CompassListener implements Listener {

	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		for (Map.Entry mapElement : PlayerCompass.compassTracker.entrySet()){
			Player sender = (Player) mapElement.getKey();
			Player target = (Player) mapElement.getValue();
			if (event.getPlayer() == target) {
				switch (target.getWorld().getName()) {
					case "world":
						sender.sendMessage(target.getName() + " has entered the overworld.");
						break;
					case "world_nether":
						sender.sendMessage(target.getName() + " has entered the nether.");
						break;
					case "world_the_end":
						sender.sendMessage(target.getName() + " has entered the end.");
						
				}
			}
		}
	}
}
