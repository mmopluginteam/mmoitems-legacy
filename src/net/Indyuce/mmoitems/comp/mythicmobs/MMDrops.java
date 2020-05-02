package net.Indyuce.mmoitems.comp.mythicmobs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent;

public class MMDrops implements Listener {
	@EventHandler
	public void onMythicDropLoad(MythicDropLoadEvent event) {
		if (event.getDropName().equalsIgnoreCase("mmoitems"))
			event.register(new MMOItemsDrop(event.getConfig()));
	}
}