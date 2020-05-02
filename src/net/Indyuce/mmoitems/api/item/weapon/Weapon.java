package net.Indyuce.mmoitems.api.item.weapon;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.DurabilityItem;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.PlayerData.CooldownType;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.UseItem;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;

@SuppressWarnings("deprecation")
public class Weapon extends UseItem {
	public Weapon(PlayerData playerData, ItemStack item, Type type) {
		super(playerData, item, type);
	}

	public Weapon(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public boolean canBeUsed() {
		if (playerData.areHandsFull()) {
			Message.HANDS_TOO_CHARGED.format(ChatColor.RED).send(player, "two-handed");
			return false;
		}

		return MMOItems.plugin.getFlags().isFlagAllowed(player, CustomFlag.MI_WEAPONS) && profile.canUse(item, true);
	}

	/*
	 * applies the cooldown, mana & stamina cost and returns a true boolean if
	 * the player does have all the resource requirements
	 */
	public boolean hasEnoughResources(double attackSpeed, CooldownType cooldown, boolean isSwing) {

		if (MMOItems.plugin.getVersion().isBelowOrEqual(1, 8) || !isSwing)
			if (playerData.isOnCooldown(cooldown))
				return false;

		double manaCost = MMOItems.plugin.getStats().getStat(item, Stat.MANA_COST), staminaCost = MMOItems.plugin.getStats().getStat(item, Stat.STAMINA_COST), mana = 0, stamina = 0;

		if (manaCost > 0)
			if ((mana = MMOItems.getRPG().getMana(player)) < manaCost) {
				Message.NOT_ENOUGH_MANA.format(ChatColor.RED).send(player, "not-enough-mana");
				return false;
			}

		if (staminaCost > 0)
			if ((stamina = MMOItems.getRPG().getStamina(player)) < staminaCost) {
				Message.NOT_ENOUGH_STAMINA.format(ChatColor.RED).send(player, "not-enough-stamina");
				return false;
			}

		if (manaCost > 0)
			MMOItems.getRPG().setMana(player, mana - manaCost);

		if (staminaCost > 0)
			MMOItems.getRPG().setStamina(player, stamina - staminaCost);

		playerData.applyCooldown(cooldown, attackSpeed);
		return true;
	}

	public AttackResult targetedAttack(LivingEntity target, AttackResult result) {
		PlayerStats stats = playerData.getStats(Stat.ATTACK_DAMAGE, Stat.ATTACK_SPEED, Stat.CRITICAL_STRIKE_CHANCE, Stat.CRITICAL_STRIKE_POWER, Stat.WEAPON_DAMAGE, Stat.UNDEAD_DAMAGE, Stat.PVP_DAMAGE, Stat.PVE_DAMAGE);

		// custom durability
		DurabilityItem durItem = new DurabilityItem(player, item);
		if (durItem.isValid())
			player.setItemInHand(durItem.decreaseDurability(1).getItem());

		// cooldown
		double attackSpeed = MMOItems.plugin.getStats().getStat(item, Stat.ATTACK_SPEED);
		attackSpeed = attackSpeed == 0 ? 1.493 : 1 / attackSpeed;
		if (!hasEnoughResources(attackSpeed, CooldownType.ATTACK, true))
			return result.setSuccessful(false);

		// apply weapon effects
		result.applyEffects(stats, getItem(), target);

		if (!MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_ATTACK_PASSIVE"))
			type.getItemSet().applyAttackEffect(stats, target, this, result);

		return result;
	}

	protected Location getGround(Location loc) {
		for (int j = 0; j < 20; j++) {
			if (loc.getBlock().getType().isSolid())
				return loc;
			loc.add(0, -1, 0);
		}
		return loc;
	}

	// returns default getValue if stat equals 0
	public double getValue(double a, double def) {
		return a <= 0 ? def : a;
	}
}
