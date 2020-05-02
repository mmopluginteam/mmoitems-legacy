package net.Indyuce.mmoitems.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemStat.StatType;
import net.Indyuce.mmoitems.api.Stat;
import net.Indyuce.mmoitems.api.Type;

public class StatManager {
	private List<Stat> gemStats = new ArrayList<>();

	public StatManager() {
		for (Stat stat : Stat.values())
			if (Type.GEM_STONE.canHaveStat(stat) && stat != Stat.REQUIRED_LEVEL && stat != Stat.DURABILITY && stat != Stat.MAX_CUSTOM_DURABILITY && stat != Stat.SUCCESS_RATE
			&& stat.c().getStatType() == StatType.DOUBLE)
				gemStats.add(stat);
	}

	public List<Stat> getGemStoneStats() {
		return gemStats;
	}

	public Map<String, Double> requestStats(ItemStack item, Stat... stats) {
		LinkedHashMap<String, Double> map = new LinkedHashMap<>();
		for (Stat stat : stats)
			map.put(stat.name(), 0d);
		return MMOItems.plugin.getNMS().requestDoubleTags(item, map);
	}

	public Map<String, Double> requestStats(ItemStack item, String... stats) {
		LinkedHashMap<String, Double> map = new LinkedHashMap<>();
		for (String stat : stats)
			map.put(stat, 0d);
		return MMOItems.plugin.getNMS().requestDoubleTags(item, map);
	}

	public double getStat(ItemStack item, Stat stat) {
		return MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_" + stat.name());
	}

	public double getStat(ItemStack item, String stat) {
		return MMOItems.plugin.getNMS().getDoubleTag(item, "MMOITEMS_" + stat);
	}
}
