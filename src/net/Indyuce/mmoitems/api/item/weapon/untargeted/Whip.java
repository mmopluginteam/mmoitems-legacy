package net.Indyuce.mmoitems.api.item.weapon.untargeted;

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

public class Whip extends UntargetedWeapon {
	public Whip(Player player, ItemStack item, Type type) {
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

		player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_FIREWORK_BLAST.getSound(), 1, 2);
		Location loc = player.getEyeLocation().clone();
		Vector vec = player.getEyeLocation().getDirection().multiply(.4);
		for (double j = 0; j < range; j += .75) {
			loc.add(vec);
			if (loc.getBlock().getType().isSolid())
				break;

			ParticleEffect.CRIT.display(0, 0, 0, 0, 1, loc);
			for (Entity target : player.getWorld().getEntities())
				if (MMOUtils.canDamage(player, loc, target)) {
					new AttackResult(untargeted, attackDamage).applyEffectsAndDamage(stats, item, (LivingEntity) target);
					ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, loc);
					return;
				}
		}
	}
}
