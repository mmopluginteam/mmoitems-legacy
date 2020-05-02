package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Wither extends Ability {
	public Wither() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 8);
		addModifier("duration", 3);
		addModifier("amplifier", 1);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity initialTarget, AbilityData data, double damage) {
		LivingEntity target = initialTarget == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : initialTarget;
		if (target == null)
			return new AttackResult(false);

		new BukkitRunnable() {
			final Location loc = target.getLocation();
			double y = 0;

			public void run() {
				for (int j1 = 0; j1 < 3; j1++) {
					y += .07;
					for (int j = 0; j < 3; j++) {
						double x = Math.cos(y * Math.PI + (j * Math.PI * 2 / 3)) * (3 - y) / 2.5;
						double z = Math.sin(y * Math.PI + (j * Math.PI * 2 / 3)) * (3 - y) / 2.5;
						ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.BLACK), loc.clone().add(x, y, z));
					}
				}
				if (y > 3) {
					cancel();
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 1, 2);
		target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, (int) data.getModifier("duration") * 20, (int) data.getModifier("amplifier")));
		return new AttackResult(true);
	}
}
