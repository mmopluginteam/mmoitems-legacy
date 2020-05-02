package net.Indyuce.mmoitems.api.item.weapon.untargeted;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

public class Musket extends UntargetedWeapon {
	public Musket(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public WeaponType getWeaponType() {
		return WeaponType.RIGHT_CLICK;
	}

	@Override
	public void untargetedAttackEffects() {
		PlayerStats stats = playerData.getStats(Stat.ATTACK_DAMAGE, Stat.ATTACK_SPEED, Stat.RANGE, Stat.KNOCKBACK, Stat.RECOIL, Stat.CRITICAL_STRIKE_CHANCE, Stat.CRITICAL_STRIKE_POWER, Stat.WEAPON_DAMAGE, Stat.UNDEAD_DAMAGE, Stat.PVE_DAMAGE, Stat.PVP_DAMAGE);
		final double attackDamage = stats.getStat(Stat.ATTACK_DAMAGE);
		final double attackSpeed = 1 / getValue(stats.getStat(Stat.ATTACK_SPEED), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
		final double knockback = stats.getStat(Stat.KNOCKBACK);
		final double range = getValue(stats.getStat(Stat.RANGE), MMOItems.plugin.getConfig().getDouble("default.range"));
		final double recoil = getValue(stats.getStat(Stat.RECOIL), MMOItems.plugin.getConfig().getDouble("default.recoil"));

		if (!hasEnoughResources(attackSpeed, CooldownType.ATTACK, false))
			return;

		// knockback
		if (knockback > 0)
			player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().setY(0).normalize().multiply(-1 * knockback).setY(-.2)));

		player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 2, 2);
		Location loc = player.getEyeLocation().clone();
		loc.setPitch((float) (loc.getPitch() + (random.nextDouble() - .5) * 2 * recoil));
		loc.setYaw((float) (loc.getYaw() + (random.nextDouble() - .5) * 2 * recoil));
		Vector vec = loc.getDirection().multiply(.5);
		for (int j = 0; j < range; j++) {
			loc.add(vec);
			if (loc.getBlock().getType().isSolid())
				break;

			ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(Color.BLACK), loc);
			for (Entity target : player.getWorld().getEntitiesByClass(LivingEntity.class))
				if (MMOUtils.canDamage(player, loc, target)) {
					new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
					return;
				}
		}
	}
}
