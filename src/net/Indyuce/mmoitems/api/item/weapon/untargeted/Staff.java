package net.Indyuce.mmoitems.api.item.weapon.untargeted;

import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.StaffSpirit;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.VersionSound;

public class Staff extends UntargetedWeapon {
	public Staff(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public WeaponType getWeaponType() {
		return WeaponType.LEFT_CLICK;
	}

	@Override
	public void untargetedAttackEffects() {
		PlayerStats stats = playerData.getStats(Stat.ATTACK_DAMAGE, Stat.ATTACK_SPEED, Stat.RANGE, Stat.CRITICAL_STRIKE_CHANCE, Stat.CRITICAL_STRIKE_POWER, Stat.WEAPON_DAMAGE, Stat.UNDEAD_DAMAGE, Stat.PVP_DAMAGE, Stat.PVE_DAMAGE);
		double attackDamage = getValue(stats.getStat(Stat.ATTACK_DAMAGE), 1);
		double attackSpeed = 1 / getValue(stats.getStat(Stat.ATTACK_SPEED), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
		double range = getValue(stats.getStat(Stat.RANGE), MMOItems.plugin.getConfig().getDouble("default.range"));

		if (!hasEnoughResources(attackSpeed, CooldownType.ATTACK, false))
			return;

		StaffSpirit ss = StaffSpirit.get(item);
		if (ss == null) {
			player.getWorld().playSound(player.getLocation(), VersionSound.BLOCK_FIRE_EXTINGUISH.getSound(), 2, 2);
			Location loc = player.getEyeLocation().clone();
			Vector vec = player.getEyeLocation().getDirection().multiply(.3);
			loop1: for (int j = 0; j < range; j++) {
				loc.add(vec);
				if (loc.getBlock().getType().isSolid())
					break;

				ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, 0, 1, loc);
				for (Entity target : MMOUtils.getNearbyChunkEntities(loc))
					if (MMOUtils.canDamage(player, loc, target)) {
						new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
						ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
						break loop1;
					}
			}
			return;
		}

		switch (ss) {
		case NETHER_SPIRIT:
			new BukkitRunnable() {
				Vector vec = player.getEyeLocation().getDirection().multiply(.4);
				Location loc = player.getEyeLocation();
				int ti = 0;

				public void run() {
					ti++;
					if (ti % 2 == 0)
						loc.getWorld().playSound(loc, VersionSound.BLOCK_FIRE_AMBIENT.getSound(), 2, 2);
					List<Entity> targets = MMOUtils.getNearbyChunkEntities(loc);
					for (int j = 0; j < 3; j++) {
						loc.add(vec);
						if (loc.getBlock().getType().isSolid()) {
							cancel();
							break;
						}

						ParticleEffect.FLAME.display(.07f, .07f, .07f, 0, 2, loc);
						ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0, 1, loc);
						for (Entity target : targets)
							if (MMOUtils.canDamage(player, loc, target)) {
								new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
								ParticleEffect.FLAME.display(.07f, .07f, .07f, 0, 2, loc);
								cancel();
								return;
							}
					}
					if (ti >= range)
						cancel();
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
			break;

		case SUNFIRE_SPIRIT:
			new BukkitRunnable() {
				Location target = getGround(player.getTargetBlock((Set<Material>) null, (int) range * 2).getLocation()).add(0, 1.2, 0);
				double a = random.nextDouble() * Math.PI * 2;
				Location loc = target.clone().add(Math.cos(a) * 4, 10, Math.sin(a) * 4);
				Vector vec = target.toVector().subtract(loc.toVector()).multiply(.015);
				double ti = 0;

				public void run() {
					loc.getWorld().playSound(loc, VersionSound.BLOCK_FIRE_AMBIENT.getSound(), 2, 2);
					for (int j = 0; j < 4; j++) {
						ti += .015;
						loc.add(vec);
						ParticleEffect.FLAME.display(.03f, 0, .03f, 0, 1, loc);
						if (ti >= 1) {
							ParticleEffect.FLAME.display(0, 0, 0, .12f, 24, loc);
							ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, .12f, 24, loc);
							ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
							loc.getWorld().playSound(loc, VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 2, 2);
							for (Entity target : MMOUtils.getNearbyChunkEntities(loc))
								if (MMOUtils.canDamage(player, target) && target.getLocation().distanceSquared(loc) <= 9)
									new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
							cancel();
							break;
						}
					}
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 2, 2);
			break;

		case MANA_SPIRIT:
			new BukkitRunnable() {
				Vector vec = player.getEyeLocation().getDirection().multiply(.4);
				Location loc = player.getEyeLocation();
				int ti = 0;
				double r = .2;

				public void run() {
					ti++;
					if (ti > range)
						cancel();

					if (ti % 2 == 0)
						loc.getWorld().playSound(loc, VersionSound.BLOCK_SNOW_BREAK.getSound(), 2, 2);
					List<Entity> targets = MMOUtils.getNearbyChunkEntities(loc);
					for (int j = 0; j < 3; j++) {
						loc.add(vec);
						if (loc.getBlock().getType().isSolid()) {
							cancel();
							break;
						}

						for (double item = 0; item < Math.PI * 2; item += Math.PI / 3.5) {
							Vector vec = MMOUtils.rotateFunc(new Vector(r * Math.cos(item), r * Math.sin(item), 0), loc);
							if (random.nextDouble() <= .6)
								ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.AQUA), loc.clone().add(vec));
						}
						for (Entity target : targets)
							if (MMOUtils.canDamage(player, loc, target)) {
								new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
								ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
								cancel();
								return;
							}
					}
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
			break;

		case VOID_SPIRIT:
			if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 8)) {
				player.sendMessage(ChatColor.RED + "This staff spirit is not supported by your current server version.");
				return;
			}

			Vector vec = player.getEyeLocation().getDirection();
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 2, 2);
			ShulkerBullet shulkerBullet = (ShulkerBullet) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.valueOf("SHULKER_BULLET"));
			shulkerBullet.setShooter(player);
			new BukkitRunnable() {
				double ti = 0;

				public void run() {
					ti += .1;
					if (shulkerBullet.isDead() || ti >= range / 4) {
						shulkerBullet.remove();
						cancel();
					}
					shulkerBullet.setVelocity(vec);
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
			MMOItems.plugin.getEntities().registerCustomEntity(shulkerBullet, attackDamage, 0., player, item, stats);
			break;

		case LIGHTNING_SPIRIT:
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 2, 2);
			Location loc = player.getEyeLocation().clone();
			vec = player.getEyeLocation().getDirection().multiply(.75);
			for (int j = 0; j < range; j++) {
				loc.add(vec);
				if (loc.getBlock().getType().isSolid())
					break;

				ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, loc);
				for (Entity target : MMOUtils.getNearbyChunkEntities(loc))
					if (MMOUtils.canDamage(player, loc, target)) {
						new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
						ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .1f, 16, loc);
						return;
					}
			}
			break;

		case XRAY_SPIRIT:
			player.getWorld().playSound(player.getLocation(), VersionSound.BLOCK_FIRE_EXTINGUISH.getSound(), 2, 2);
			loc = player.getEyeLocation().clone();
			vec = player.getEyeLocation().getDirection().multiply(.75);
			for (int j = 0; j < range; j++) {
				loc.add(vec);
				if (loc.getBlock().getType().isSolid())
					break;

				ParticleEffect.REDSTONE.display(0, 0, 0, 0, 1, loc);
				for (Entity target : MMOUtils.getNearbyChunkEntities(loc))
					if (MMOUtils.canDamage(player, loc, target))
						new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
			}
			break;

		case THUNDER_SPIRIT:
			new BukkitRunnable() {
				Location target = getGround(player.getTargetBlock((Set<Material>) null, (int) range * 2).getLocation()).add(0, 1.2, 0);
				double a = random.nextDouble() * Math.PI * 2;
				Location loc = target.clone().add(Math.cos(a) * 4, 10, Math.sin(a) * 4);
				Vector vec = target.toVector().subtract(loc.toVector()).multiply(.015);
				double ti = 0;

				public void run() {
					loc.getWorld().playSound(loc, VersionSound.BLOCK_NOTE_HAT.getSound(), 2, 2);
					for (int j = 0; j < 4; j++) {
						ti += .015;
						loc.add(vec);
						ParticleEffect.FIREWORKS_SPARK.display(.03f, 0, .03f, 0, 1, loc);
						if (ti >= 1) {
							ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, .12f, 24, loc);
							loc.getWorld().playSound(loc, VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 2, 2);
							for (Entity target : MMOUtils.getNearbyChunkEntities(loc))
								if (MMOUtils.canDamage(player, target) && target.getLocation().distanceSquared(loc) <= 9)
									new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
							cancel();
						}
					}
				}
			}.runTaskTimer(MMOItems.plugin, 0, 1);
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_WITHER_SHOOT.getSound(), 2, 2);
			break;
		}
	}

	public void specialAttack(LivingEntity target) {
		if (!MMOItems.plugin.getConfig().getBoolean("item-ability.staff.enabled"))
			return;

		if (!hasEnoughResources(MMOItems.plugin.getConfig().getDouble("item-ability.staff.cooldown"), CooldownType.SPECIAL_ATTACK, false))
			return;

		double power = MMOItems.plugin.getConfig().getDouble("item-ability.staff.power");
		Vector vec = target.getLocation().toVector().subtract(player.getLocation().toVector()).setY(0).normalize().multiply(1.75 * power).setY(.65 * power);
		ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, .1f, 16, target.getLocation().add(0, 1, 0));
		ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, target.getLocation().add(0, 1, 0));
		target.setVelocity(vec);
		target.playEffect(EntityEffect.HURT);
		target.getWorld().playSound(target.getLocation(), VersionSound.BLOCK_ANVIL_LAND.getSound(), 1, 2);
	}
}
