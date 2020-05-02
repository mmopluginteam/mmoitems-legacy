package net.Indyuce.mmoitems.ability.onhit;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Blink extends Ability {
	public Blink() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("range", 8);
		addModifier("cooldown", 10);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, stats.getPlayer().getLocation().add(0, 1, 0));
		ParticleEffect.SPELL_INSTANT.display(0, 0, 0, .1f, 32, stats.getPlayer().getLocation().add(0, 1, 0));
		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_ENDERMEN_TELEPORT.getSound(), 1, 1);
		Location loc = stats.getPlayer().getTargetBlock((Set<Material>) null, (int) data.getModifier("range")).getLocation().add(0, 1, 0);
		loc.setYaw(stats.getPlayer().getLocation().getYaw());
		loc.setPitch(stats.getPlayer().getLocation().getPitch());
		stats.getPlayer().teleport(loc);
		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, stats.getPlayer().getLocation().add(0, 1, 0));
		ParticleEffect.SPELL_INSTANT.display(0, 0, 0, .1f, 32, stats.getPlayer().getLocation().add(0, 1, 0));
		return new AttackResult(true);
	}
}
