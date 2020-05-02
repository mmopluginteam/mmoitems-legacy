package net.Indyuce.mmoitems.listener;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.DurabilityItem;
import net.Indyuce.mmoitems.api.InteractItem;

@SuppressWarnings("deprecation")
public class CustomDurability implements Listener {
	private final List<DamageCause> applyDamageCauses = Arrays.asList(DamageCause.ENTITY_ATTACK, DamageCause.ENTITY_EXPLOSION, DamageCause.BLOCK_EXPLOSION, DamageCause.THORNS, DamageCause.THORNS);
	private final List<String> hoeableBlocks = Arrays.asList("GRASS_PATH", "GRASS", "DIRT");
	private final List<PlayerFishEvent.State> applyFishStates = Arrays.asList(State.IN_GROUND, State.CAUGHT_ENTITY, State.CAUGHT_FISH);

	/*
	 * when breaking a block, ANY item will lose one durability point even if
	 * the block can be broken instantly.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void a(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		DurabilityItem durItem = new DurabilityItem(player, item);
		if (!durItem.isValid())
			return;

		player.setItemInHand(durItem.decreaseDurability(1).getItem());
	}

	/*
	 * when hitting an entity, ANY item will lose one durability point (shears
	 * are supposed to lose 2, etc)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void b(EntityDamageByEntityEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof LivingEntity) || !(event.getDamager() instanceof Player) || MMOItems.plugin.getDamage().isCustomDamaged(event.getEntity()))
			return;

		Player player = (Player) event.getDamager();
		ItemStack item = player.getItemInHand();
		DurabilityItem durItem = new DurabilityItem(player, item);
		if (!durItem.isValid())
			return;

		player.setItemInHand(durItem.decreaseDurability(1).getItem());
	}

	/*
	 * when getting hit, any armor piece will lose 1 durability point
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void c(EntityDamageEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player))
			return;

		if (!applyDamageCauses.contains(event.getCause()))
			return;

		Player player = (Player) event.getEntity();
		ItemStack helmet = player.getInventory().getHelmet();
		if (helmet != null) {
			DurabilityItem durabilityItem = new DurabilityItem(player, helmet);
			if (durabilityItem.isValid())
				player.getInventory().setHelmet(durabilityItem.decreaseDurability(1).getItem());
		}

		ItemStack chestplate = player.getInventory().getChestplate();
		if (chestplate != null) {
			DurabilityItem durabilityItem = new DurabilityItem(player, chestplate);
			if (durabilityItem.isValid())
				player.getInventory().setChestplate(durabilityItem.decreaseDurability(1).getItem());
		}

		ItemStack leggings = player.getInventory().getLeggings();
		if (leggings != null) {
			DurabilityItem durabilityItem = new DurabilityItem(player, leggings);
			if (durabilityItem.isValid())
				player.getInventory().setLeggings(durabilityItem.decreaseDurability(1).getItem());
		}

		ItemStack boots = player.getInventory().getBoots();
		if (boots != null) {
			DurabilityItem durabilityItem = new DurabilityItem(player, boots);
			if (durabilityItem.isValid())
				player.getInventory().setBoots(durabilityItem.decreaseDurability(1).getItem());
		}

		if (MMOItems.plugin.getVersion().isStrictlyHigher(1, 8)) {
			InteractItem intItem = new InteractItem(player, Material.SHIELD);
			if (intItem.hasItem() && player.isBlocking()) {
				DurabilityItem durabilityItem = new DurabilityItem(player, intItem.getItem());
				if (durabilityItem.isValid())
					intItem.setItem(durabilityItem.decreaseDurability(1).getItem());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void d(EntityShootBowEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		InteractItem intItem = new InteractItem(player, Material.BOW);
		DurabilityItem durItem = new DurabilityItem(player, event.getBow());
		if (!durItem.isValid())
			return;

		intItem.setItem(durItem.decreaseDurability(1).getItem());
	}

	/*
	 * make shears lose durability when used on a sheep
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void e(PlayerShearEntityEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		InteractItem intItem = new InteractItem(player, Material.SHEARS);
		if (!intItem.hasItem())
			return;

		DurabilityItem durItem = new DurabilityItem(player, intItem.getItem());
		if (!durItem.isValid())
			return;

		intItem.setItem(durItem.decreaseDurability(1).getItem());
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void f(PlayerInteractEvent event) {
		if (event.isCancelled() || !event.hasItem() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.hasBlock())
			return;

		Player player = event.getPlayer();
		InteractItem intItem;

		/*
		 * making hoe lose durability when hoeing grass/dirt/coarse
		 */
		if (hoeableBlocks.contains(event.getClickedBlock().getType().name()) && (intItem = new InteractItem(player, "_HOE")).hasItem()) {
			DurabilityItem durItem = new DurabilityItem(player, intItem.getItem());
			if (durItem.isValid())
				intItem.setItem(durItem.decreaseDurability(1).getItem());
			return;
		}

		/*
		 * grass path creation (only for 1.9)
		 */
		if (event.getClickedBlock().getType() == Material.GRASS && (intItem = new InteractItem(player, "_SPADE")).hasItem()) {
			DurabilityItem durItem = new DurabilityItem(player, intItem.getItem());
			if (durItem.isValid())
				intItem.setItem(durItem.decreaseDurability(1).getItem());
			return;
		}
	}

	/*
	 * flint and steel consuming
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void g(BlockIgniteEvent event) {
		if (event.isCancelled() || !(event.getIgnitingEntity() instanceof Player))
			return;

		Player player = (Player) event.getIgnitingEntity();
		InteractItem intItem = new InteractItem(player, Material.FLINT_AND_STEEL);
		if (!intItem.hasItem())
			return;

		DurabilityItem durItem = new DurabilityItem(player, intItem.getItem());
		if (!durItem.isValid())
			return;

		intItem.setItem(durItem.decreaseDurability(1).getItem());
	}

	/*
	 * fishing rod durability loss - loses 1 if it catches a fish successfully.
	 * if it catches an item, uses 3 durability, 5 if it is any other entity,
	 * and 0 durability loss if it does not catch anything ; a delay is needed
	 * otherwise it does not update the item held by the player
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void h(PlayerFishEvent event) {
		if (event.isCancelled() || !applyFishStates.contains(event.getState()))
			return;

		Player player = event.getPlayer();
		InteractItem intItem = new InteractItem(player, Material.FISHING_ROD);
		if (!intItem.hasItem())
			return;

		DurabilityItem durItem = new DurabilityItem(player, intItem.getItem());
		if (!durItem.isValid())
			return;

		int loss = event.getState() == State.CAUGHT_FISH ? 1 : (event.getState() == State.CAUGHT_ENTITY ? (event.getCaught() instanceof Item ? 3 : 5) : 2);
		new BukkitRunnable() {
			public void run() {
				intItem.setItem(durItem.decreaseDurability(loss).getItem());
			}
		}.runTaskLater(MMOItems.plugin, 0);
	}
}
