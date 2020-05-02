package net.Indyuce.mmoitems.api.item.weapon.untargeted;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.api.DurabilityItem;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.weapon.Weapon;

public abstract class UntargetedWeapon extends Weapon {

	/*
	 * this final field is used in the AttackResult constructor to be able to
	 * cast on-hit abilities since the weapon is untargeted.
	 */
	protected final UntargetedWeapon untargeted = this;

	public UntargetedWeapon(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	/*
	 * called first when the player clicks his item and allows to apply
	 * durability onto a weapon that is not targeted
	 */
	@SuppressWarnings("deprecation")
	public final void untargetedAttack() {
		DurabilityItem durItem = new DurabilityItem(player, item);
		if (durItem.isValid())
			player.setItemInHand(durItem.decreaseDurability(1).getItem());

		untargetedAttackEffects();
	}

	public abstract WeaponType getWeaponType();

	public abstract void untargetedAttackEffects();

	public enum WeaponType {
		RIGHT_CLICK,
		LEFT_CLICK;

		public boolean corresponds(Action action) {
			return (this == RIGHT_CLICK && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) || (this == LEFT_CLICK && (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK));
		}
	}
}
