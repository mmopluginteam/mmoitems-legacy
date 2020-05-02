package net.Indyuce.mmoitems.comp.inventory;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlayerInventory {
	public List<ItemStack> getInventory(Player player);
}
