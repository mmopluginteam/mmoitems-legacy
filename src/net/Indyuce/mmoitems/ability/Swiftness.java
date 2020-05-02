package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Swiftness extends Ability {
	public Swiftness() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 15);
		addModifier("duration", 4);
		addModifier("amplifier", 1);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double duration = data.getModifier("duration");
		int amplifier = (int) data.getModifier("amplifier");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ZOMBIE_PIG_ANGRY.getSound(), 1, 0);
		for (double y = 0; y <= 2; y += .2)
			for (double j = 0; j < Math.PI * 2; j += Math.PI / 16)
				if (random.nextDouble() <= .7) {
					Location loc = stats.getPlayer().getLocation();
					loc.add(Math.cos(j), y, Math.sin(j));
					ParticleEffect.SPELL_INSTANT.display(0, 0, 0, 0, 1, loc);
				}
		stats.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) (duration * 20), amplifier));
		stats.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, (int) (duration * 20), amplifier));
		return new AttackResult(true);
	}
}
