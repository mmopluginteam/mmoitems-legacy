 package net.Indyuce.mmoitems.comp.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInventory_v1_8 implements PlayerInventory {
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getInventory(Player player) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(player.getItemInHand());
		list.add(player.getEquipment().getHelmet());
		list.add(player.getEquipment().getChestplate());
		list.add(player.getEquipment().getLeggings());
		list.add(player.getEquipment().getBoots());
		return list;
	}
}
