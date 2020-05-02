package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Poison extends Ability {
	public Poison() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 4);
		addModifier("cooldown", 10);
		addModifier("amplifier", 1);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		target = target == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : target;
		if (target == null)
			return new AttackResult(false);

		ParticleEffect.SLIME.display(1, 1, 1, 0, 32, target.getLocation().add(0, 1, 0));
		ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0, 24, target.getLocation().add(0, 1, 0));
		target.getWorld().playSound(target.getLocation(), VersionSound.BLOCK_BREWING_STAND_BREW.getSound(), 2, 2);
		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_SHEEP_DEATH.getSound(), 1, 2);
		target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int) data.getModifier("duration") * 20, (int) data.getModifier("amplifier")));
		return new AttackResult(true);
	}
}