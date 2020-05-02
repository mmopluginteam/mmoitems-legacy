package net.Indyuce.mmoitems.comp;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.evill4mer.RealDualWield.Api.PlayerDamageEntityWithOffhandEvent;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.TypeSet;
import net.Indyuce.mmoitems.api.item.weapon.Weapon;

public class RealDualWieldHook implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void a(PlayerDamageEntityWithOffhandEvent event) {

		// check for npc
		// safety checks
		if (event.getEntity().hasMetadata("NPC") || event.isCancelled() || !(event.getEntity() instanceof LivingEntity))
			return;

		// custom damage check
		LivingEntity target = (LivingEntity) event.getEntity();
		if (MMOItems.plugin.getDamage().isCustomDamaged(target) || !MMOItems.getRPG().canBeDamaged(target))
			return;

		/*
		 * cast on-hit abilities and add the extra damage to the damage event
		 */
		Player player = event.getPlayer();
		PlayerData playerData = PlayerData.get(player);
		AttackResult result = playerData.castAbilities(target, new AttackResult(true, event.getDamage()), CastingMode.ON_HIT);
		if (result.isDamageModified())
			event.setDamage(result.getDamage());

		ItemStack item = player.getInventory().getItemInOffHand();
		Type type = Type.get(item);
		if (type == null)
			return;

		Weapon weapon = new Weapon(playerData, item, type);

		// can't attack melee
		if (type.getItemSet() == TypeSet.RANGE) {
			event.setCancelled(true);
			return;
		}

		if (!weapon.canBeUsed()) {
			event.setCancelled(true);
			return;
		}

		weapon.targetedAttack(target, result);
		if (!result.isSuccessful()) {
			event.setCancelled(true);
			return;
		}

		event.setDamage(result.getDamage());
	}
}
