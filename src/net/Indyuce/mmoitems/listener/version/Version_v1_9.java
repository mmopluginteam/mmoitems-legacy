package net.Indyuce.mmoitems.listener.version;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.DurabilityItem;

public class Version_v1_9 implements Listener {

	/*
	 * if the player switches his glide more than once a second, the old
	 * runnable needs to be cancelled so it doesn't consume twice as much
	 * durability
	 */
	private Map<UUID, BukkitRunnable> elytraDurabilityLoss = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGH)
	public void a(EntityToggleGlideEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.isGliding()))
			return;

		Player player = (Player) event.getEntity();
		if (elytraDurabilityLoss.containsKey(player.getUniqueId()))
			elytraDurabilityLoss.get(player.getUniqueId()).cancel();

		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				if (player == null || !player.isOnline() || !player.isGliding()) {
					cancel();
					elytraDurabilityLoss.remove(player.getUniqueId());
					return;
				}

				ItemStack elytra = player.getInventory().getChestplate();
				if (elytra != null && elytra.getType() == Material.ELYTRA) {
					DurabilityItem durabilityItem = new DurabilityItem(player, elytra);
					if (durabilityItem.isValid())
						player.getInventory().setChestplate(durabilityItem.decreaseDurability(1).getItem());
				}

			}
		};

		elytraDurabilityLoss.put(player.getUniqueId(), runnable);
		runnable.runTaskTimer(MMOItems.plugin, 10, 20);
	}

	/*
	 * registers arrows when they land only in 1.9 and above since
	 * ProjectileHitEvent does not support when arrows touch blocks below 1.9
	 */
	@EventHandler
	public void b(ProjectileHitEvent event) {
		if (event.getEntity().getType() != EntityType.ARROW || event.getHitEntity() != null)
			return;

		Arrow arrow = (Arrow) event.getEntity();
		if (MMOItems.plugin.getEntities().isCustomProjectile(arrow))
			new BukkitRunnable() {
				public void run() {
					MMOItems.plugin.getEntities().unregisterCustomProjectile(arrow);
				}
			}.runTaskLater(MMOItems.plugin, 1);
	}
}
