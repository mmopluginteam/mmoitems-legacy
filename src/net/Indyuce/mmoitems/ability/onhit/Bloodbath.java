package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;

import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Bloodbath extends Ability {
	public Bloodbath() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("amount", 2);
		addModifier("cooldown", 8);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		target = target == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : target;
		if (target == null)
			return new AttackResult(false);

		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_COW_HURT.getSound(), 1, 2);
		target.getWorld().playEffect(target.getLocation().add(0, 1, 0), Effect.STEP_SOUND, 152);
		stats.getPlayer().setFoodLevel((int) Math.min(20, stats.getPlayer().getFoodLevel() + data.getModifier("amount")));
		return new AttackResult(true);
	}
}
