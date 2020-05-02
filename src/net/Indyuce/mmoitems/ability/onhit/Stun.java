package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Stun extends Ability {
	public Stun() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 10);
		addModifier("duration", 2);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		target = target == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : target;
		if (target == null)
			return new AttackResult(false);

		target.getWorld().playSound(target.getLocation(), VersionSound.BLOCK_ANVIL_LAND.getSound(), 1, 2);
		target.getWorld().playEffect(target.getLocation(), Effect.STEP_SOUND, 42);
		target.getWorld().playEffect(target.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 42);
		target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) data.getModifier("duration") * 20, 254));
		return new AttackResult(true);
	}
}
