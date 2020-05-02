package net.Indyuce.mmoitems.api.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.MMOUtils;
import net.Indyuce.mmoitems.api.ItemStat;
import net.Indyuce.mmoitems.api.Message;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.gui.GemStoneList;
import net.Indyuce.mmoitems.version.VersionSound;
import net.Indyuce.mmoitems.version.nms.Attribute;
import net.Indyuce.mmoitems.version.nms.ItemTag;

public class GemStone extends UseItem {
	public GemStone(Player player, ItemStack item, Type type) {
		super(player, item, type);
	}

	public ApplyResult applyOntoItem(ItemStack target, Type targetType) {
		int empty = getFirstSocket(target.getItemMeta().getLore());
		if (empty < 0)
			return new ApplyResult(ResultType.NONE);

		/*
		 * checks if the gem supports the item type, or the item set, or a
		 * weapon
		 */
		String appliableTypes = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_TYPE_RESTRICTION");
		if (!appliableTypes.equals(""))
			if ((!targetType.isWeapon() || !appliableTypes.contains("WEAPON")) && !appliableTypes.contains(targetType.getItemSet().name()) && !appliableTypes.contains(targetType.getId()))
				return new ApplyResult(ResultType.NONE);

		// check for success rate
		double successRate = MMOItems.plugin.getStats().getStat(item, Stat.SUCCESS_RATE);
		if (successRate != 0)
			if (random.nextDouble() < 1 - successRate / 100) {
				player.playSound(player.getLocation(), VersionSound.ENTITY_ITEM_BREAK.getSound(), 1, 1);
				Message.GEM_STONE_BROKE.format(ChatColor.RED, "#gem#", MMOUtils.getDisplayName(item), "#item#", MMOUtils.getDisplayName(target)).send(player);
				return new ApplyResult(ResultType.FAILURE);
			}

		player.playSound(player.getLocation(), VersionSound.ENTITY_PLAYER_LEVELUP.getSound(), 1, 2);
		Message.GEM_STONE_APPLIED.format(ChatColor.YELLOW, "#gem#", MMOUtils.getDisplayName(item), "#item#", MMOUtils.getDisplayName(target)).send(player);

		ItemMeta meta = target.getItemMeta();
		List<String> lore = meta.getLore();
		lore.set(empty, MMOItems.getLanguage().getStatFormat("filled-gem-socket").replace("#", MMOUtils.getDisplayName(item)));
		String tag = MMOItems.plugin.getNMS().getStringTag(target, "MMOITEMS_GEM_STONE");
		tag += (tag.isEmpty() ? "" : GemStoneList.gemSeparator) + MMOUtils.getDisplayName(item) + GemStoneList.nameSeparator;

		// attributes & tags to add
		List<Attribute> attributes = new ArrayList<>();
		List<ItemTag> tags = new ArrayList<>();

		for (Stat stat : MMOItems.plugin.getStats().getGemStoneStats()) {
			double gemValue = MMOItems.plugin.getStats().getStat(item, stat);
			if (gemValue == 0)
				continue;

			double weaponValue = MMOItems.plugin.getStats().getStat(target, stat);
			stat.c().applyOntoItem(targetType, stat, gemValue, weaponValue, attributes, tags);
			tag += (tag.charAt(tag.length() - 1) == '|' ? "" : GemStoneList.statSeparator) + stat.name() + "=" + gemValue;
		}

		// must apply tags after applying itemmeta
		// itemmeta saves all item tags and will erase them if applied before
		meta.setLore(lore);
		target.setItemMeta(meta);

		tags.add(new ItemTag("MMOITEMS_GEM_STONE", tag));
		target = MMOItems.plugin.getNMS().addTag(target, tags);
		if (!attributes.isEmpty())
			target = MMOItems.plugin.getNMS().addAttribute(target, attributes);
		return new ApplyResult(target);
	}

	public static int getFirstSocket(List<String> lore) {
		for (int j = 0; j < lore.size(); j++)
			if (lore.get(j).equals(ItemStat.translate("empty-gem-socket")))
				return j;
		return -1;
	}

	public class ApplyResult {
		private ResultType type;
		private ItemStack result;

		public ApplyResult(ResultType type) {
			this(null, type);
		}

		public ApplyResult(ItemStack result) {
			this(result, ResultType.SUCCESS);
		}

		public ApplyResult(ItemStack result, ResultType type) {
			this.type = type;
			this.result = result;
		}

		public ResultType getType() {
			return type;
		}

		public ItemStack getResult() {
			return result;
		}
	}

	public enum ResultType {

		/*
		 * when the gem stone is not successfully applied onto the item and when
		 * it needs to be destroyed
		 */
		FAILURE,

		/*
		 * when a gem stone, for some reason, cannot be applied onto an item (if
		 * it has no more empty gem socket), but when the gem must not be
		 * destroyed
		 */
		NONE,

		/*
		 * when a gem stone is successfully applied onto an item without any
		 * error
		 */
		SUCCESS;
	}
}
