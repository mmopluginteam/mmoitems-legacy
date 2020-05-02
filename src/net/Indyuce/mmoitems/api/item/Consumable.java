package net.Indyuce.mmoitems.api.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.DurabilityItem;
import net.Indyuce.mmoitems.api.Identification;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.comp.flags.FlagPlugin.CustomFlag;
import net.Indyuce.mmoitems.version.VersionSound;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class Consumable extends UseItem {
	public Consumable(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	@Override
	public boolean canBeUsed() {
		return MMOItems.plugin.getFlags().isFlagAllowed(player, CustomFlag.MI_CONSUMABLES) && profile.canUse(item, true);
	}

	@SuppressWarnings("deprecation")
	public void useOnItem(InventoryClickEvent event, ItemStack target) {

		/*
		 * this boolean is used to check if the consumable has applied at least
		 * once of its item options. if so, the consumable should be consumed
		 */
		boolean used = false;

		/*
		 * unidentified items do not have any type, so you must check if the
		 * item has a type first.
		 */
		Type targetType = Type.get(target);
		if (targetType == null) {
			String unidentifiedItemTag = MMOItems.plugin.getNMS().getStringTag(target, "MMOITEMS_UNIDENTIFIED_ITEM");
			if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_CAN_IDENTIFY") && !unidentifiedItemTag.equals("")) {
				event.setCurrentItem(new Identification(target).identify());
				Message.SUCCESSFULLY_IDENTIFIED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(event.getCurrentItem())).send(player);
				player.playSound(player.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
				used = true;
			}
		} else {

			/*
			 * deconstructing an item. usually consumables do not deconstruct
			 * and repair items at the same time to there's no pb with that
			 */
			String itemTierTag = MMOItems.plugin.getNMS().getStringTag(target, "MMOITEMS_TIER");
			if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_CAN_DECONSTRUCT") && !itemTierTag.equals("")) {
				ItemTier tier = new ItemTier(itemTierTag);
				if (tier.exists()) {
					List<ItemStack> deconstructed = tier.generateDeconstructedItem();
					if (!deconstructed.isEmpty()) {
						Message.SUCCESSFULLY_DECONSTRUCTED.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(event.getCurrentItem())).send(player);
						event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
						if (event.getCurrentItem().getAmount() < 1)
							event.setCurrentItem(null);
						for (ItemStack drop : player.getInventory().addItem(deconstructed.toArray(new ItemStack[deconstructed.size()])).values())
							player.getWorld().dropItem(player.getLocation(), drop);
						player.playSound(player.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
						used = true;
					}
				}
			}

			/*
			 * applying a soulbound onto an item. it does not work if the item
			 * already has a soulbound, and it has a chance to successfully
			 * apply.
			 */
			double soulbindingChance = MMOItems.plugin.getStats().getStat(item, Stat.SOULBINDING_CHANCE);
			if (soulbindingChance > 0) {
				if (target.getAmount() > 1) {
					Message.CANT_BIND_STACKED.format(ChatColor.RED).send(player, "soulbound");
					event.setCancelled(true);
					return;
				}

				String currentSoulbound = MMOItems.plugin.getNMS().getStringTag(target, "MMOITEMS_SOULBOUND");
				if (!currentSoulbound.equals("")) {
					Message.CANT_BIND_ITEM.format(ChatColor.RED, "#player#", Bukkit.getOfflinePlayer(UUID.fromString(currentSoulbound)).getName(), "#level#", MMOUtils.intToRoman((int) MMOItems.plugin.getStats().getStat(target, Stat.SOULBOUND_LEVEL))).send(player, "soulbound");
					event.setCancelled(true);
					return;
				}

				used = true;
				if (random.nextDouble() < soulbindingChance / 100) {
					int soulboundLevel = (int) Math.max(1, MMOItems.plugin.getStats().getStat(item, Stat.SOULBOUND_LEVEL));
					String formattedLoreTag = Message.SOULBOUND_ITEM_LORE.getUpdated().replace("#player#", player.getName()).replace("#level#", MMOUtils.intToRoman(soulboundLevel));
					ItemStack edited = MMOItems.plugin.getNMS().addTag(target, new ItemTag("MMOITEMS_SOULBOUND_INFO", player.getName() + "|infosep|" + formattedLoreTag), new ItemTag("MMOITEMS_SOULBOUND", player.getUniqueId().toString()), new ItemTag("MMOITEMS_SOULBOUND_LEVEL", soulboundLevel));

					ItemMeta meta = edited.getItemMeta();
					if (!formattedLoreTag.isEmpty()) {
						List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
						for (String string : formattedLoreTag.split(Pattern.quote("//")))
							lore.add(string);
						meta.setLore(lore);
					}

					target.setItemMeta(meta);
					Message.SUCCESSFULLY_BIND_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(target), "#level#", MMOUtils.intToRoman(soulboundLevel)).send(player, "soulbound");
					player.playSound(player.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
				} else {
					Message.UNSUCCESSFUL_SOULBOUND.format(ChatColor.RED).send(player, "soulbound");
					player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 1);
				}
			}

			/*
			 * breaking the item's current soulbound. it has a random factor
			 * determined by the soulbound break chance, and the consumable
			 * needs to have at least the soulbound's level to be able to break
			 * the item soulbound.
			 */
			double soulboundBreakChance = MMOItems.plugin.getStats().getStat(item, Stat.SOULBOUND_BREAK_CHANCE);
			if (soulboundBreakChance > 0) {
				String currentSoulbound = MMOItems.plugin.getNMS().getStringTag(target, "MMOITEMS_SOULBOUND");
				if (!currentSoulbound.equals("")) {

					// check for soulbound level
					int targetLevel = (int) MMOItems.plugin.getStats().getStat(target, Stat.SOULBOUND_LEVEL);
					if (Math.max(1, MMOItems.plugin.getStats().getStat(item, Stat.SOULBOUND_LEVEL)) < targetLevel) {
						Message.LOW_SOULBOUND_LEVEL.format(ChatColor.RED, "#level#", MMOUtils.intToRoman(targetLevel)).send(player, "soulbound");
						event.setCancelled(true);
						return;
					}

					used = true;
					if (random.nextDouble() < soulboundBreakChance / 100) {
						String[] loreTag = MMOItems.plugin.getNMS().getStringTag(target, "MMOITEMS_SOULBOUND_INFO").split(Pattern.quote("|infosep|"))[1].split(Pattern.quote("//"));
						ItemMeta meta = MMOItems.plugin.getNMS().removeTag(target, "MMOITEMS_SOULBOUND", "MMOITEMS_SOULBOUND_LEVEL", "MMOITEMS_SOULBOUND_INFO").getItemMeta();
						List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
						int n = -1;
						checkline: for (int j = 0; j <= lore.size() - loreTag.length; j++) {
							for (int k = 0; k < loreTag.length; k++)
								if (!lore.get(j + k).equals(loreTag[k]))
									continue checkline;
							n = j;
							break;
						}
						if (n > 0) {
							for (int j = 0; j < loreTag.length; j++)
								lore.remove(n);
							meta.setLore(lore);
						}

						target.setItemMeta(meta);
						Message.SUCCESSFULLY_BREAK_BIND.format(ChatColor.YELLOW, "#level#", MMOUtils.intToRoman(targetLevel)).send(player, "soulbound");
						player.playSound(player.getLocation(), VersionSound.BLOCK_ANVIL_LAND.getSound(), 1, 2);
					} else {
						Message.UNSUCCESSFUL_SOULBOUND_BREAK.format(ChatColor.RED).send(player, "soulbound");
						player.playSound(player.getLocation(), VersionSound.ENTITY_ITEM_BREAK.getSound(), 1, 0);
					}
				} else {
					Message.NO_SOULBOUND.format(ChatColor.RED).send(player, "soulbound");
					player.playSound(player.getLocation(), VersionSound.ENTITY_VILLAGER_NO.getSound(), 1, 1);
					event.setCancelled(true);
				}
			}

			/*
			 * item repairing, does not apply if there's no repair power or if
			 * the item still has all its uses left
			 */
			int repairPower = (int) MMOItems.plugin.getStats().getStat(item, Stat.REPAIR);
			if (repairPower > 0) {

				// custom durability
				if (MMOItems.plugin.getNMS().hasTag(target, "MMOITEMS_DURABILITY")) {
					int current = (int) MMOItems.plugin.getNMS().getDoubleTag(target, "MMOITEMS_DURABILITY");
					int max = (int) MMOItems.plugin.getNMS().getDoubleTag(target, "MMOITEMS_MAX_DURABILITY");

					if (current < max) {
						ItemStack editedDurability = MMOItems.plugin.getNMS().addTag(target, new ItemTag("MMOITEMS_DURABILITY", Math.min(max, current + repairPower)));
						editedDurability = new DurabilityItem(player, editedDurability).updateDurabilityState().getItem();
						target.setItemMeta(editedDurability.getItemMeta());
						Message.REPAIRED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(target), "#amount#", "" + repairPower).send(player);
						used = true;
					}
				}

				// vanilla durability
				else if (!MMOItems.plugin.getNMS().getBooleanTag(target, "Unbreakable") && target.getDurability() > 0 && target.getType().getMaxDurability() > 30) {
					target.setDurability((short) Math.max(target.getDurability() - repairPower, 0));
					Message.REPAIRED_ITEM.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(target), "#amount#", "" + repairPower).send(player);
					used = true;
				}
			}
		}

		if (used) {
			event.setCancelled(true);
			item.setAmount(item.getAmount() - 1);
			if (item.getAmount() < 1)
				event.setCursor(null);
		}
	}

	public boolean useWithoutItem(boolean consume) {
		if (MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_INEDIBLE"))
			return false;

		if (!playerData.canUseConsumable(id)) {
			Message.ITEM_ON_COOLDOWN.format(ChatColor.RED).send(player);
			return false;
		}

		double cooldown = MMOItems.plugin.getStats().getStat(item, Stat.CONSUME_COOLDOWN);
		if (cooldown > 0)
			playerData.applyConsumableCooldown(id, cooldown);

		// restore stats
		Map<String, Double> stats = MMOItems.plugin.getStats().requestStats(item, "RESTORE_HEALTH", "RESTORE_FOOD", "RESTORE_SATURATION", "RESTORE_MANA", "RESTORE_STAMINA");
		double health = stats.get("RESTORE_HEALTH");
		if (health > 0)
			MMOUtils.heal(player, health);

		double food = stats.get("RESTORE_FOOD");
		if (food > 0)
			MMOUtils.feed(player, (int) food);

		double saturation = stats.get("RESTORE_SATURATION");
		saturation = saturation == 0 ? 6 : saturation;
		if (saturation > 0)
			MMOUtils.saturate(player, (float) saturation);

		double mana = stats.get("RESTORE_MANA");
		if (mana > 0)
			MMOItems.getRPG().setMana(player, mana + MMOItems.getRPG().getMana(player));

		double stamina = stats.get("RESTORE_STAMINA");
		if (stamina > 0)
			MMOItems.getRPG().setStamina(player, stamina + MMOItems.getRPG().getStamina(player));

		// potion effects
		String stringEffectTag = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_EFFECTS");
		for (String effect : stringEffectTag.split("\\;")) {
			if (effect.equals(""))
				continue;

			String[] split = effect.split("\\:");
			double duration = Double.parseDouble(split[1]);
			int amplifier = (int) Double.parseDouble(split[2]) - 1;

			PotionEffectType type = PotionEffectType.getByName(split[0].toUpperCase().replace("-", "_"));
			player.removePotionEffect(type);
			player.addPotionEffect(new PotionEffect(type, (int) (duration * 20), amplifier));
		}

		// play sound
		String sound = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_CONSUME_SOUND");
		if (!sound.equals(""))
			try {
				player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
			} catch (Exception e1) {
			}
		else
			player.playSound(player.getLocation(), VersionSound.ENTITY_GENERIC_EAT.getSound(), 1, 1);

		if (consume)
			if (!MMOItems.plugin.getNMS().getBooleanTag(item, "MMOITEMS_DISABLE_RIGHT_CLICK_CONSUME"))
				item.setAmount(item.getAmount() - 1);
		return true;
	}

	public boolean hasVanillaEating() {
		return (item.getType().isEdible() || item.getType() == Material.POTION || item.getType() == Material.MILK_BUCKET) && MMOItems.plugin.getNMS().hasTag(item, "MMOITEMS_VANILLA_EATING");
	}
}
