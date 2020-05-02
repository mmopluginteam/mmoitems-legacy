package net.Indyuce.mmoitems.listener.version;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;

public class Version_v1_8 implements Listener {

	/*
	 * register custom projectiles when they hit the ground. forced to use
	 * reflection methods in 1.8 since ProjectileHitEvent does not support when
	 * arrows land on blocks yet.
	 */
	@EventHandler
	private void a(ProjectileHitEvent event) {
		if (event.getEntityType() != EntityType.ARROW)
			return;

		Arrow arrow = (Arrow) event.getEntity();
		if (MMOItems.plugin.getEntities().isCustomProjectile(arrow))
			new BukkitRunnable() {
				public void run() {
					try {
						Object entityArrow = arrow.getClass().getDeclaredMethod("getHandle").invoke(arrow);
						if (entityArrow.getClass().getField("inGround").getBoolean(entityArrow))
							MMOItems.plugin.getEntities().unregisterCustomProjectile(arrow);
					} catch (NoSuchFieldException | IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}.runTaskLater(MMOItems.plugin, 1);
	}
}
