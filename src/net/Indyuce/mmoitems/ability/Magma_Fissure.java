package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.manager.DamageManager.DamageType;
import net.Indyuce.mmoitems.version.VersionSound;

public class Magma_Fissure extends Ability {
	public Magma_Fissure() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
		addModifier("ignite", 4);
		addModifier("damage", 4);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		new BukkitRunnable() {
			int j = 0;
			Location loc = stats.getPlayer().getLocation().add(0, .2, 0);

			public void run() {
				j++;
				if (target.isDead() || !target.getWorld().equals(loc.getWorld()) || j > 200) {
					cancel();
					return;
				}

				Vector vec = target.getLocation().add(0, .2, 0).subtract(loc).toVector().normalize().multiply(.6);
				loc.add(vec);

				ParticleEffect.LAVA.display(.2f, 0, .2f, 0, 2, loc);
				ParticleEffect.FLAME.display(.2f, 0, .2f, .01f, 2, loc);
				ParticleEffect.SMOKE_NORMAL.display(.2f, 0, .2f, .01f, 1, loc);
				loc.getWorld().playSound(loc, VersionSound.BLOCK_NOTE_HAT.getSound(), 1, 1);

				if (target.getLocation().distanceSquared(loc) < 1) {
					loc.getWorld().playSound(loc, VersionSound.ENTITY_BLAZE_HURT.getSound(), 2, 1);
					target.setFireTicks((int) (target.getFireTicks() + data.getModifier("ignite") * 20));
					MMOItems.plugin.getDamage().damage(stats, target, data.getModifier("damage"), DamageType.MAGIC);
					cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}
}