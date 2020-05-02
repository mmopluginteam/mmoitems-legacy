package net.Indyuce.mmoitems.api;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;

public class DropItem {
	private Type type;
	private String id;
	private double unidentification, drop;
	private int min, max;

	private static final Random random = new Random();

	/*
	 * used in MythicDrops drop tables.
	 */
	public DropItem(Type type, String id, double unidentification) {
		this(type, id, 100, unidentification, 1, 1);
	}

	public DropItem(Type type, String id, double drop, double unidentification, int min, int max) {
		this.type = type;
		this.id = id;

		this.drop = drop;
		this.unidentification = unidentification;
		this.min = min;
		this.max = max;
	}

	/*
	 * used when loading drop tables from drops.yml
	 */
	public DropItem(Type type, String id, String info) throws Exception {
		this.type = type;
		this.id = id;

		String[] argSplit = info.split("\\,");
		drop = Double.parseDouble(argSplit[0]) / 100;

		String[] amountSplit = argSplit[1].split("\\-");
		min = Integer.parseInt(amountSplit[0]);
		max = amountSplit.length > 1 ? Integer.parseInt(amountSplit[1]) : min;

		unidentification = Double.parseDouble(argSplit[2]) / 100;
	}

	public boolean isDropped() {
		return random.nextDouble() < drop;
	}

	private boolean isUnidentified() {
		return random.nextDouble() < unidentification;
	}

	private int getRandomAmount() {
		return max > min ? min + random.nextInt(max - min + 1) : min;
	}

	public ItemStack getItem() {
		return getItem(getRandomAmount());
	}

	public ItemStack getItem(int amount) {
		ItemStack item = MMOItems.getItem(type, id);
		if (item == null || item.getType() == Material.AIR)
			return null;

		item.setAmount(amount);
		return isUnidentified() ? new Identification(item).unidentify() : item;
	}

	public Type getType() {
		return type;
	}

	public String getID() {
		return id;
	}
}
