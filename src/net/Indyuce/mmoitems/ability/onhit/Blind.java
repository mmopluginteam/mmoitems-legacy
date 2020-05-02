package net.Indyuce.mmoitems.ability.onhit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.ArrayCast;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Blind extends Ability {
	public Blind() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("duration", 5);
		addModifier("cooldown", 9);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		target = target == null ? new ArrayCast(stats.getPlayer(), 50).getHitEntity() : target;
		if (target == null)
			return new AttackResult(false);

		target.getWorld().playSound(target.getLocation(), VersionSound.ENTITY_ENDERMEN_HURT.getSound(), 1, 2);
		for (double i = 0; i < Math.PI * 2; i += Math.PI / 24)
			for (double j = 0; j < 2; j++) {
				Location loc = target.getLocation();
				Vector vec = MMOUtils.rotateFunc(new Vector(Math.cos(i), 1 + Math.cos(i + (Math.PI * j)) * .5, Math.sin(i)), stats.getPlayer().getLocation());
				ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.BLACK), loc.add(vec));
			}
		target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) data.getModifier("duration") * 20, 0));
		return new AttackResult(true);
	}
}
