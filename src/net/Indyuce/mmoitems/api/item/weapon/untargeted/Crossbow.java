package net.Indyuce.mmoitems.api.item.weapon.untargeted;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.version.VersionSound;

public class Crossbow extends UntargetedWeapon {
	public Crossbow(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public WeaponType getWeaponType() {
		return WeaponType.RIGHT_CLICK;
	}

	@Override
	public void untargetedAttackEffects() {

		// check for arrow
		if (player.getGameMode() != GameMode.CREATIVE && !player.getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1))
			return;

		PlayerStats stats = playerData.getStats(Stat.ATTACK_DAMAGE, Stat.ATTACK_SPEED, Stat.CRITICAL_STRIKE_CHANCE, Stat.CRITICAL_STRIKE_POWER, Stat.ARROW_VELOCITY, Stat.WEAPON_DAMAGE, Stat.UNDEAD_DAMAGE, Stat.PVE_DAMAGE, Stat.PVP_DAMAGE);
		double attackDamage = getValue(stats.getStat(Stat.ATTACK_DAMAGE), 1);
		double attackSpeed = 1 / getValue(stats.getStat(Stat.ATTACK_SPEED), MMOItems.plugin.getConfig().getDouble("default.attack-speed"));
		double arrowVelocity = getValue(stats.getStat(Stat.ARROW_VELOCITY), 1);

		// cooldown
		if (!hasEnoughResources(attackSpeed, CooldownType.ATTACK, false))
			return;

		// consume arrow
		// has to be after the CD check
		if (player.getGameMode() != GameMode.CREATIVE)
			player.getInventory().removeItem(new ItemStack(Material.ARROW));

		// shoot arrow
		player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ARROW_SHOOT.getSound(), 1, 1);
		player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR.getSound(), 1, 2);
		Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setVelocity(player.getEyeLocation().getDirection().multiply(3 * arrowVelocity));
		player.setVelocity(player.getVelocity().setX(0).setZ(0));

		MMOItems.plugin.getEntities().registerCustomProjectile(item, stats, arrow, attackDamage);
	}
}
