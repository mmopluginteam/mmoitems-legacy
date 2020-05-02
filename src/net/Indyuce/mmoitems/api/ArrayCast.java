package net.Indyuce.mmoitems.api;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOUtils;

public class ArrayCast {

	// the hit player, null if no player hit
	private LivingEntity target;

	// represents the radius in which the entity has to be in
	private static final double radiusSquared = 1 * 1;

	public ArrayCast(Player player, int length) {
		Vector direction = player.getEyeLocation().getDirection().multiply(.5);

		Location loc = player.getLocation().add(0, 1.3, 0).clone();
		for (int j = 0; j < length; j++) {
			loc.add(direction);
			for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
				if (MMOUtils.canDamage(player, entity))
					if (entity.getLocation().add(0, MMOUtils.getHeight(entity) / 2, 0).distanceSquared(loc) < radiusSquared) {
						target = (LivingEntity) entity;
						return;
					}
		}
	}

	public LivingEntity getHitEntity() {
		return target;
	}
}
