package net.Indyuce.mmoitems.ability;

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
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Heavy_Charge extends Ability {
	public Heavy_Charge() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 6);
		addModifier("knockback", 1);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");
		double knockback = data.getModifier("knockback");

		new BukkitRunnable() {
			double ti = 0;
			Vector vec = getTargetDirection(stats.getPlayer(), target).setY(-1);

			public void run() {
				ti++;
				if (ti < 9) {
					stats.getPlayer().setVelocity(vec);
					ParticleEffect.EXPLOSION_NORMAL.display(.13f, .13f, .13f, 0, 3, stats.getPlayer().getLocation().add(0, 1, 0));
				}
				if (ti > 20)
					cancel();

				for (Entity target : stats.getPlayer().getNearbyEntities(1, 1, 1))
					if (MMOUtils.canDamage(stats.getPlayer(), target)) {
						stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 1, 1);
						ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, target.getLocation().add(0, 1, 0));
						target.setVelocity(stats.getPlayer().getVelocity().setY(0.3).multiply(1.7 * knockback));
						stats.getPlayer().setVelocity(stats.getPlayer().getVelocity().setX(0).setY(0).setZ(0));
						MMOItems.plugin.getDamage().damage(stats, (LivingEntity) target, damage1, DamageType.PHYSICAL);
						cancel();
						break;
					}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}
