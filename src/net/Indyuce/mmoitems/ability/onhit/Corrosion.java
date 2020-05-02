package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Corrosion extends Ability {
	public Corrosion() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT);

		addModifier("duration", 4);
		addModifier("amplifier", 1);
		addModifier("radius", 5);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		Location loc = getTargetLocation(stats.getPlayer(), target);
		if (loc == null)
			return new AttackResult(false);

		int duration = (int) (data.getModifier("duration") * 20);
		int amplifier = (int) data.getModifier("amplifier");
		double radiusSquared = Math.pow(data.getModifier("radius"), 2);

		ParticleEffect.SLIME.display(2, 2, 2, 0, 48, loc);
		ParticleEffect.VILLAGER_HAPPY.display(2, 2, 2, 0, 32, loc);
		loc.getWorld().playSound(loc, VersionSound.BLOCK_BREWING_STAND_BREW.getSound(), 2, 0);

		for (Entity entity : MMOUtils.getNearbyChunkEntities(loc))
			if (entity.getLocation().distanceSquared(loc) < radiusSquared && MMOUtils.canDamage(stats.getPlayer(), entity)) {
				((LivingEntity) entity).removePotionEffect(PotionEffectType.POISON);
				((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, duration, amplifier));
			}
		return new AttackResult(true);
	}
}
