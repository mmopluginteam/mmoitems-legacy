package net.Indyuce.mmoitems.ability;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Corrupted_Fangs extends Ability implements Listener {
	public Corrupted_Fangs() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("damage", 5);
		addModifier("cooldown", 12);
		addModifier("mana", 0);
		addModifier("stamina", 0);

		if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 11))
			disable();
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double damage1 = data.getModifier("damage");

		stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 2, 0);
		new BukkitRunnable() {
			Vector vec = getTargetDirection(stats.getPlayer(), target).multiply(2);
			Location loc = stats.getPlayer().getLocation();
			double ti = 0;

			public void run() {
				ti += 2;
				loc.add(vec);

				MMOItems.plugin.getEntities().registerCustomEntity((EvokerFangs) stats.getPlayer().getWorld().spawnEntity(loc, EntityType.EVOKER_FANGS), damage1);

				if (ti > 12)
					cancel();
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
		return new AttackResult(true);
	}

	@EventHandler
	public void a(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof EvokerFangs && event.getEntity() instanceof LivingEntity) {
			EvokerFangs damager = (EvokerFangs) event.getDamager();
			if (!MMOItems.plugin.getEntities().isCustomEntity(damager))
				return;

			if (!MMOUtils.canDamage(event.getEntity())) {
				event.setCancelled(true);
				return;
			}

			event.setDamage((double) MMOItems.plugin.getEntities().getEntityData(damager)[0]);
		}
	}
}
