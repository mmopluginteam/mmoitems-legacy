package net.Indyuce.mmoitems.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Shockwave extends Ability {
	public Shockwave() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 7.5);
		addModifier("knock-up", 1);
		addModifier("length", 20);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double knockUp = data.getModifier("knock-up");
		double length = data.getModifier("length");

		new BukkitRunnable() {
			Vector vec = stats.getPlayer().getEyeLocation().getDirection().setY(0).multiply(.6);
			Location loc = stats.getPlayer().getLocation();
			double ti = 0;
			List<Integer> hit = new ArrayList<>();

			public void run() {
				ti++;
				loc.add(vec);

				loc.getWorld().playSound(loc, VersionSound.BLOCK_GRAVEL_BREAK.getSound(), 2, 1);
				ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.DIRT), .5f, 0, .5f, 0, 12, loc);

				for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
					if (entity.getLocation().distance(loc) < 1.1 && !entity.equals(stats.getPlayer()) && !hit.contains(entity.getEntityId()))
						if (MMOUtils.canDamage(stats.getPlayer(), entity)) {
							hit.add(entity.getEntityId());
							entity.playEffect(EntityEffect.HURT);
							entity.setVelocity(entity.getVelocity().setY(.4 * knockUp));
						}
				if (ti >= Math.min(300, length))
					cancel();
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
