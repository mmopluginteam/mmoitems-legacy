package net.Indyuce.mmoitems.api.item.weapon.untargeted;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.ParticleEffect;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.VersionSound;

public class Lute extends UntargetedWeapon {
	public Lute(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public WeaponType getWeaponType() {
		return WeaponType.RIGHT_CLICK;
	}

	@Override
	public void untargetedAttackEffects() {
		PlayerStats stats = playerData.getStats(Stat.ATTACK_DAMAGE, Stat.ATTACK_SPEED, Stat.RANGE, Stat.CRITICAL_STRIKE_CHANCE, Stat.CRITICAL_STRIKE_POWER, Stat.WEAPON_DAMAGE, Stat.UNDEAD_DAMAGE, Stat.PVE_DAMAGE, Stat.PVP_DAMAGE);
		double attackDamage = getValue(stats.getStat(Stat.ATTACK_DAMAGE), 1);
		double attackSpeed = 1 / getValue(stats.getStat(Stat.ATTACK_DAMAGE), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
		double range = getValue(stats.getStat(Stat.RANGE), MMOItems.plugin.getConfig().getDouble("default.range"));

		if (!hasEnoughResources(attackSpeed, CooldownType.ATTACK, false))
			return;

		new BukkitRunnable() {
			Vector vec = player.getEyeLocation().getDirection().multiply(.4);
			Location loc = player.getEyeLocation();
			int ti = 0;

			public void run() {
				ti++;
				if (ti > range)
					cancel();

				for (int j = 0; j < 3; j++) {
					loc.add(vec.getX(), vec.getY(), vec.getZ());
					if (loc.getBlock().getType().isSolid()) {
						cancel();
						break;
					}

					ParticleEffect.NOTE.display(.5f, .5f, .5f, 1, 1, loc);
					loc.getWorld().playSound(loc, VersionSound.BLOCK_NOTE_HAT.getSound(), 2, (float) (1 + random.nextDouble()));

					for (Entity target : loc.getWorld().getEntities())
						if (MMOUtils.canDamage(player, target) && target.getLocation().distanceSquared(loc) <= 4) {
							new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
							cancel();
							return;
						}
				}
			}
		}.runTaskTimer(MMOItems.plugin, 0, 1);
	}
}
