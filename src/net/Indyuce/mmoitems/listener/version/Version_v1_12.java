package net.Indyuce.mmoitems.listener.version;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;

import net.Indyuce.mmoitems.MMOItems;

public class Version_v1_12 implements Listener {

	/*
	 * fixes a deprecated method in 1.12 that would send a console error when
	 * the server boots. since 1.12 recipes need a namespacedKey to be
	 * registered
	 */
	public static NamespacedKey key(String path) {
		return new NamespacedKey(MMOItems.plugin, "MMOItems_" + path);
	}
}
