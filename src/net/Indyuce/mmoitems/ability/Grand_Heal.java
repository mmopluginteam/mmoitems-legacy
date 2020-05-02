package net.Indyuce.mmoitems.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Grand_Heal extends Ability {
	public Grand_Heal() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("heal", 5);
		addModifier("radius", 5);
		addModifier("cooldown", 15);
		addModifier("mana", 0);
		addModifier("stamina", 0);
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double heal = data.getModifier("heal");
		double radius = data.getModifier("radius");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
		ParticleEffect.HEART.display(1, 1, 1, 0, 16, stats.getPlayer().getLocation().add(0, .75, 0));
		ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0, 16, stats.getPlayer().getLocation().add(0, .75, 0));

		MMOUtils.heal(stats.getPlayer(), heal);
		for (Entity entity : stats.getPlayer().getNearbyEntities(radius, radius, radius))
			if (entity instanceof Player)
				MMOUtils.heal((Player) entity, heal);
		
		return new AttackResult(true);
	}
}
