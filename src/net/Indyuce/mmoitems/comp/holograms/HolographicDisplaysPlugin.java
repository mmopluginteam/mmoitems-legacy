package net.Indyuce.mmoitems.comp.holograms;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.Indyuce.mmoitems.MMOItems;

public class HolographicDisplaysPlugin extends HologramSupport {
	public HolographicDisplaysPlugin() {
		super();
	}

	@Override
	public void displayIndicator(Location loc, String format, Player player) {
		Hologram hologram = HologramsAPI.createHologram(MMOItems.plugin, loc);
		hologram.appendTextLine(format);

		if (player != null)
			hologram.getVisibilityManager().hideTo(player);

		new BukkitRunnable() {
			int j = 0;

			public void run() {
				j++;
				if (j > 20) {
					hologram.delete();
					cancel();
					return;
				}
				hologram.teleport(hologram.getLocation().add(0, -.015, 0));
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
	}

}
