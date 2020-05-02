package net.Indyuce.mmoitems.comp.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInventory_v1_9 implements PlayerInventory {
	@Override
	public List<ItemStack> getInventory(Player player) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(player.getInventory().getItemInMainHand());
		list.add(player.getInventory().getItemInOffHand());
		list.add(player.getInventory().getHelmet());
		list.add(player.getInventory().getChestplate());
		list.add(player.getInventory().getLeggings());
		list.add(player.getInventory().getBoots());
		return list;
	}
}
