package net.Indyuce.mmoitems.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.Ability.CastingMode;
import net.Indyuce.mmoitems.api.AttackResult;
import net.Indyuce.mmoitems.api.PlayerData;
import net.Indyuce.mmoitems.api.PlayerStats;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.TypeSet;
import net.Indyuce.mmoitems.api.item.Consumable;
import net.Indyuce.mmoitems.api.item.GemStone;
import net.Indyuce.mmoitems.api.item.GemStone.ApplyResult;
import net.Indyuce.mmoitems.api.item.GemStone.ResultType;
import net.Indyuce.mmoitems.api.item.Tool;
import net.Indyuce.mmoitems.api.item.UseItem;
import net.Indyuce.mmoitems.api.item.weapon.Gauntlet;
import net.Indyuce.mmoitems.api.item.weapon.Weapon;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.Staff;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.UntargetedWeapon;
import net.Indyuce.mmoitems.api.item.weapon.untargeted.UntargetedWeapon.WeaponType;

@SuppressWarnings("deprecation")
public class ItemUse implements Listener {
	@EventHandler
	public void a(PlayerInteractEvent event) {
		if (!event.hasItem())
			return;

		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (!item.equals(player.getItemInHand()))
			return;

		Type type = Type.get(item);
		if (type == null)
			return;

		/*
		 * some consumables cannot be used by right clicking since they need to
		 * be eaten by waiting the vanilla eating animation in order to be
		 * successfully consumed
		 */
		UseItem useItem = UseItem.getItem(player, item, type);
		if (useItem instanceof Consumable && ((Consumable) useItem).hasVanillaEating())
			return;

		if (!useItem.canBeUsed())
			return;

		// commands
		if (event.getAction().name().contains("RIGHT_CLICK")) {
			useItem.executeCommands();

			if (useItem instanceof Consumable) {
				event.setCancelled(true);
				((Consumable) useItem).useWithoutItem(true);
			}
		}

		if (useItem instanceof UntargetedWeapon) {
			UntargetedWeapon weapon = (UntargetedWeapon) useItem;
			if ((event.getAction().name().contains("RIGHT_CLICK") && weapon.getWeaponType() == WeaponType.RIGHT_CLICK) || (event.getAction().name().contains("LEFT_CLICK") && weapon.getWeaponType() == WeaponType.LEFT_CLICK))
				weapon.untargetedAttack();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void b(EntityDamageByEntityEvent event) {

		// check for npc
		// safety checks
		if (event.getEntity().hasMetadata("NPC") || event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof LivingEntity) || event.getDamage() == 0 || event.getCause() != DamageCause.ENTITY_ATTACK)
			return;

		// custom damage check
		LivingEntity target = (LivingEntity) event.getEntity();
		if (MMOItems.plugin.getDamage().isCustomDamaged(target) || !MMOItems.getRPG().canBeDamaged(target))
			return;

		/*
		 * cast on-hit abilities and add the extra damage to the damage event
		 */
		Player player = (Player) event.getDamager();
		PlayerData playerData = PlayerData.get(player);
		AttackResult result = playerData.castAbilities(target, new AttackResult(true, event.getDamage()), CastingMode.ON_HIT);
		if (result.isDamageModified())
			event.setDamage(result.getDamage());

		ItemStack item = player.getItemInHand();
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

		weapon.targetedAttack(target, result.setSuccessful(true));
		if (!result.isSuccessful()) {
			event.setCancelled(true);
			return;
		}

		event.setDamage(result.getDamage());
	}

	@EventHandler
	public void c(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		ItemStack item = player.getItemInHand();
		Type type = Type.get(item);
		if (type == null)
			return;

		Tool tool = new Tool(player, item, type);
		if (!tool.canBeUsed()) {
			event.setCancelled(true);
			return;
		}

		if (tool.miningEffects(block))
			event.setCancelled(true);
	}

	@EventHandler
	public void d(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (!(event.getRightClicked() instanceof LivingEntity))
			return;

		ItemStack item = player.getItemInHand();
		Type type = Type.get(item);
		if (type == null)
			return;

		LivingEntity target = (LivingEntity) event.getRightClicked();
		if (!MMOUtils.canDamage(player, target))
			return;

		UseItem weapon = UseItem.getItem(player, item, type);
		if (!weapon.canBeUsed())
			return;

		// special staff attack
		if (weapon instanceof Staff)
			((Staff) weapon).specialAttack(target);

		// special gauntlet attack
		if (weapon instanceof Gauntlet)
			((Gauntlet) weapon).specialAttack(target);
	}

	@EventHandler
	public void e(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getAction() != InventoryAction.SWAP_WITH_CURSOR)
			return;

		ItemStack item = event.getCursor();
		Type itemType = Type.get(item);
		if (itemType == null)
			return;

		UseItem useItem = UseItem.getItem(player, item, itemType);
		if (!useItem.canBeUsed())
			return;

		if (useItem instanceof GemStone) {
			ItemStack picked = event.getCurrentItem();
			Type pickedType = Type.get(picked);
			if (pickedType == null)
				return;

			ApplyResult result = ((GemStone) useItem).applyOntoItem(picked, pickedType);
			if (result.getType() == ResultType.NONE)
				return;

			event.setCancelled(true);
			item.setAmount(item.getAmount() - 1);
			if (item.getAmount() == 0)
				event.setCursor(null);

			if (result.getType() == ResultType.FAILURE)
				return;

			event.setCurrentItem(result.getResult());
		}

		if (useItem instanceof Consumable && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR)
			((Consumable) useItem).useOnItem(event, event.getCurrentItem());
	}

	@EventHandler
	public void f(EntityShootBowEvent event) {
		if (!(event.getProjectile() instanceof Arrow) || !(event.getEntity() instanceof Player))
			return;

		ItemStack item = event.getBow();
		Type type = Type.get(item);
		if (type == null)
			return;

		PlayerData playerData = PlayerData.get((Player) event.getEntity());
		Weapon bow = new Weapon(playerData, item, type);
		if (!bow.canBeUsed()) {
			event.setCancelled(true);
			return;
		}

		Arrow arrow = (Arrow) event.getProjectile();
		PlayerStats stats = playerData.getStats(Stat.ATTACK_DAMAGE, Stat.CRITICAL_STRIKE_CHANCE, Stat.CRITICAL_STRIKE_POWER, Stat.UNDEAD_DAMAGE, Stat.WEAPON_DAMAGE, Stat.PVE_DAMAGE, Stat.PVP_DAMAGE);
		MMOItems.plugin.getEntities().registerCustomProjectile(item, stats, arrow, event.getForce());
	}

	@EventHandler
	public void g(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Type type = Type.get(item);
		if (type == null)
			return;

		Player player = event.getPlayer();
		UseItem useItem = UseItem.getItem(player, item, type);
		if (!useItem.canBeUsed()) {
			event.setCancelled(true);
			return;
		}

		if (useItem instanceof Consumable)
			if (!((Consumable) useItem).useWithoutItem(false))
				event.setCancelled(true);
	}
}
