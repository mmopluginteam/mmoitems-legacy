package net.Indyuce.mmoitems.api;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.event.ItemBreakEvent;
import net.Indyuce.mmoitems.api.event.ItemLoseDurabilityEvent;
import net.Indyuce.mmoitems.version.VersionSound;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class DurabilityItem {
	private ItemStack item;
	private int unbreakingLevel = -1, durability, maxDurability = 0;
	private Player player;

	private static final Random random = new Random();

	/*
	 * durability loss is not perfect and thus should only be used with weapons
	 * and not with tools which could lose durability more than often e.g when
	 * breaking a block with shears
	 */
	public DurabilityItem(Player player, ItemStack item) {
		this.player = player;
		this.item = item;
		this.durability = (int) MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_DURABILITY");
	}

	public int getMaxDurability() {
		return maxDurability == 0 ? maxDurability = (int) MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_MAX_DURABILITY") : maxDurability;
	}

	public int getUnbreakingLevel() {
		return unbreakingLevel < 0 ? item.getItemMeta().getEnchantLevel(Enchantment.DURABILITY) : unbreakingLevel;
	}

	public DurabilityItem decreaseDurability(int loss) {

		/*
		 * calculate the chance of the item not losing any durability because of
		 * the vanilla unbreaking enchantment ; an item with unbreaking X has 1
		 * 1 chance out of (X + 1) to lose a durability point, that's 50% chance
		 * -> 33% chance -> 25% chance -> 20% chance...
		 */
		if (getUnbreakingLevel() > 0)
			if (random.nextInt(getUnbreakingLevel()) > 0)
				return this;

		ItemLoseDurabilityEvent event = new ItemLoseDurabilityEvent(player, item, durability, loss);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return this;

		durability -= loss;

		// when the item breaks
		if (durability < 0) {
			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ITEM_BREAK.getSound(), 1, 1);
			ItemBreakEvent breakEvent = new ItemBreakEvent(player, item, MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_WILL_BREAK"));
			Bukkit.getPluginManager().callEvent(breakEvent);

			/*
			 * when the item breaks and gets really removed from the player
			 * inventory since the corresponding item option is toggled on
			 */
			if (breakEvent.doesItemBreak()) {
				String name = MMOUtils.getDisplayName(item);
				item = null;

				Message.ITEM_BROKE.format(ChatColor.RED, "#item#", name).send(player, "item-break");
				return this;
			}

			/*
			 * when the item is unusable, it gets removed from the player
			 * inventory but gets added again in a different slot to make sure
			 * the player unequips it and won't be able to equip it again
			 * without repairing it first
			 */
			ItemStack drop = MMOItems.plugin.getNMS().addTag(item, new ItemTag("MMOITEMS_DURABILITY", durability));
			item = null;

			player.getWorld().playSound(player.getLocation(), VersionSound.ENTITY_ITEM_BREAK.getSound(), 1, 1);
			Message.ZERO_DURABILITY.format(ChatColor.RED).send(player, "item-break");

			// add item to inventory in a different position and schedule an
			// inventory update
			for (int j = 0; j < 36; j++) {
				ItemStack invItem = player.getInventory().getItem(j);
				if (player.getInventory().getHeldItemSlot() != j && (invItem == null || invItem.getType() == Material.AIR)) {
					player.getInventory().setItem(j, drop);
					drop = null;
					break;
				}
			}
			if (drop != null)
				player.getWorld().dropItem(player.getLocation(), drop);
			PlayerData.get(player).updateInventory();
			return this;
		}

		/*
		 * this method is made public so when repairing an item with the repair
		 * power, you can then create an instance of DurabilityItem and update
		 * the item durability state
		 */
		updateDurabilityState();

		item = MMOItems.plugin.getNMS().addTag(item, new ItemTag("MMOITEMS_DURABILITY", durability));
		return this;
	}

	public DurabilityItem updateDurabilityState() {
		/*
		 * calculate the new durability state and update it in the item lore. if
		 * the durability state is null, it either means the durability state is
		 * out of the lore format or the state display was changed, thus in both
		 * cases it shall no be updated
		 */
		DurabilityState state = getDurabilityState();
		if (state != null) {

			/*
			 * if the item does not have the correct durability state, update it
			 */
			DurabilityState expected = getExpectedDurabilityState();
			if (!expected.equals(state)) {
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.getLore();

				for (int j = 0; j < lore.size(); j++)
					if (lore.get(j).equals(state.getDisplay())) {
						lore.set(j, expected.getDisplay());
						break;
					}

				meta.setLore(lore);
				item.setItemMeta(meta);
				item = MMOItems.plugin.getNMS().addTag(item, new ItemTag("MMOITEMS_DURABILITY_STATE", expected.getID()));
			}
		}
		return this;
	}

	public ItemStack getItem() {
		return item;
	}

	public boolean isBreaking() {
		return durability <= 0;
	}

	public boolean isValid() {
		return Type.get(item) != null && MMOItems.plugin.getNMS().hasTag(item, "MMOITEMS_DURABILITY") && player.getGameMode() == GameMode.SURVIVAL;
	}

	private DurabilityState getDurabilityState() {
		String tag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_DURABILITY_STATE");
		return MMOItems.getLanguage().hasDurabilityState(tag) ? MMOItems.getLanguage().getDurabilityState(tag) : null;
	}

	private DurabilityState getExpectedDurabilityState() {
		return getExpectedDurabilityState(durability, getMaxDurability());
	}

	// used during item generation to determine the item first state
	public static DurabilityState getExpectedDurabilityState(int durability, int max) {
		for (DurabilityState state : MMOItems.getLanguage().getDurabilityStates())
			if (state.isInState(durability, max))
				return state;

		return null;
	}
}
