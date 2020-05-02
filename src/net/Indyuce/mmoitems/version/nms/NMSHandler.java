package net.Indyuce.mmoitems.version.nms;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface NMSHandler {
	public ItemStack addTag(ItemStack item, List<ItemTag> tags);

	public ItemStack removeTag(ItemStack item, String... paths);

	public ItemStack saveAttributes(ItemStack item);

	public ItemStack loadAttributes(ItemStack item);

	public Set<String> getTags(ItemStack item);

	public boolean hasTag(ItemStack item, String path);

	public double getDoubleTag(ItemStack item, String path);

	public String getStringTag(ItemStack item, String path);

	public boolean getBooleanTag(ItemStack item, String path);

	public Map<String, Double> requestDoubleTags(ItemStack item, Map<String, Double> map);

	public ItemStack addAttribute(ItemStack i, List<Attribute> attributes);

	public void sendTitle(Player player, String title, String subtitle, int fadeIn, int ticks, int fadeOut);

	public void sendActionBar(Player player, String message);

	public void sendJson(Player player, String message);

	public void damageEntity(Player player, LivingEntity entity, double value);

	public double[] getBoundingBox(Entity entity, double offset);

	public default ItemStack addTag(ItemStack i, ItemTag... tags) {
		return addTag(i, Arrays.asList(tags));
	}

	int getNextContainerId(Player player);

	void handleInventoryCloseEvent(Player player);

	void sendPacketOpenWindow(Player player, int containerId);

	void sendPacketCloseWindow(Player player, int containerId);

	void setActiveContainerDefault(Player player);

	void setActiveContainer(Player player, Object container);

	void setActiveContainerId(Object container, int containerId);

	void addActiveContainerSlotListener(Object container, Player player);

	Inventory toBukkitInventory(Object container);

	Object newContainerAnvil(Player player);
}
