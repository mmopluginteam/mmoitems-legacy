package net.Indyuce.mmoitems.comp.mythicmobs;

import java.util.logging.Level;

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitItemStack;
import io.lumine.xikage.mythicmobs.drops.Drop;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.IMultiDrop;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import io.lumine.xikage.mythicmobs.drops.droppables.ItemDrop;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.DropItem;
import net.Indyuce.mmoitems.api.Type;

public class MMOItemsDrop extends Drop implements IMultiDrop {
	private Type type;
	private String id;
	private double unidentification = 0;

	public MMOItemsDrop(MythicLineConfig config) {
		super(config.getLine(), config);

		try {
			type = MMOItems.plugin.getTypes().get(config.getString("type").toUpperCase().replace("-", "_"));
		} catch (Exception e) {
			MMOItems.plugin.getLogger().log(Level.WARNING, "Wrong type name in a MM drop table at: " + config.getString("type"));
			return;
		}

		id = config.getString("id");
		unidentification = config.getDouble("unidentified", 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public LootBag get(DropMetadata metadata) {
		LootBag loot = new LootBag(metadata);
		loot.add(new ItemDrop(this.getLine(), (MythicLineConfig) this.getConfig(), new BukkitItemStack(new DropItem(type, id, unidentification).getItem((int) getAmount()))));
		return loot;
	}
}
