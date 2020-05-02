package net.Indyuce.mmoitems.ability;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.version.VersionSound;

public class Shulker_Missile extends Ability implements Listener {
	public Shulker_Missile() {
		super(CastingMode.ON_HIT, CastingMode.WHEN_HIT, CastingMode.LEFT_CLICK, CastingMode.RIGHT_CLICK, CastingMode.SHIFT_LEFT_CLICK, CastingMode.SHIFT_RIGHT_CLICK);

		addModifier("cooldown", 12);
		addModifier("damage", 5);
		addModifier("effect-duration", 5);
		addModifier("duration", 5);
		addModifier("mana", 0);
		addModifier("stamina", 0);

		if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 8))
			disable();
	}

	@Override
	public AttackResult whenCast(PlayerStats stats, LivingEntity target, AbilityData data, double damage) {
		double effectDuration = data.getModifier("effect-duration");
		double duration = data.getModifier("duration");

		new BukkitRunnable() {
			double n = 0;
			Vector vec = getTargetDirection(stats.getPlayer(), target);

			public void run() {
				n++;
				if (n > 3) {
					cancel();
					return;
				}

				stats.getPlayer().getWorld().playSound(stats.getPlayer().getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 2, 0);
				ShulkerBullet shulkerBullet = (ShulkerBullet) stats.getPlayer().getWorld().spawnEntity(stats.getPlayer().getLocation().add(0, 1, 0), EntityType.SHULKER_BULLET);
				shulkerBullet.setShooter(stats.getPlayer());
				MMOItems.plugin.getEntities().registerCustomEntity(shulkerBullet, data.getModifier("damage"), effectDuration);
				new BukkitRunnable() {
					double ti = 0;

					public void run() {
						ti++;
						if (shulkerBullet.isDead() || ti >= duration * 20) {
							shulkerBullet.remove();
							cancel();
						}
						shulkerBullet.setVelocity(vec);
					}
				}.runTaskTimer(MMOItems.plugin, 0, 1);
			}
		}.runTaskTimer(MMOItems.plugin, 0, 3);
		return new AttackResult(true);
	}

	@EventHandler
	public void a(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof ShulkerBullet && event.getEntity() instanceof LivingEntity) {
			ShulkerBullet damager = (ShulkerBullet) event.getDamager();
			LivingEntity entity = (LivingEntity) event.getEntity();
			if (!MMOItems.plugin.getEntities().isCustomEntity(damager))
				return;
			if (!MMOUtils.canDamage(entity)) {
				event.setCancelled(true);
				return;
			}

			Object[] data = MMOItems.plugin.getEntities().getEntityData(damager);
			AttackResult result = new AttackResult(true, (double) data[0]);
			double duration = (double) data[1] * 20;

			// void spirit & not part of the ability itself
			// since only available for 1.8
			if (data.length > 2)
				if (data.length > 2)
					result.applyEffects((PlayerStats) data[4], (ItemStack) data[3], entity);

			event.setDamage(result.getDamage());

			new BukkitRunnable() {
				final Location loc = entity.getLocation();
				double y = 0;

				public void run() {
					/*
					 * the potion effect application must feature a nanodelay
					 * otherwise it does not override the vanilla levitation
					 */
					if (y == 0) {
						entity.removePotionEffect(PotionEffectType.LEVITATION);
						entity.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) duration, 0));
					}

					for (int j1 = 0; j1 < 3; j1++) {
						y += .04;
						for (int j = 0; j < 2; j++) {
							double xz = y * Math.PI * 1.3 + (j * Math.PI);
							Location loc1 = loc.clone().add(Math.cos(xz), y, Math.sin(xz));
							ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.MAROON), loc1);
						}
					}
					if (y >= 2)
						cancel();
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
		}
	}
}
