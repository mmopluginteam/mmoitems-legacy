package net.Indyuce.mmoitems.comp.inventory;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.Indyuce.mmoitems.api.PlayerData;
import ru.endlesscode.rpginventory.api.InventoryAPI;

public class RPGPlayerInventory implements PlayerInventory, Listener {
	public RPGPlayerInventory(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public List<ItemStack> getInventory(Player player) {
		List<ItemStack> list = InventoryAPI.getPassiveItems(player);
		list.addAll(InventoryAPI.getActiveItems(player));
		for (ItemStack armor : player.getInventory().getArmorContents())
			list.add(armor);
		return list;
	}

	@EventHandler
	public void a(InventoryCloseEvent event) {
		if (InventoryAPI.isRPGInventory(event.getInventory()))
			PlayerData.get((Player) event.getPlayer()).updateInventory();
	}
}
