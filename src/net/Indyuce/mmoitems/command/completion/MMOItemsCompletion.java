package net.Indyuce.mmoitems.command.completion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Ability;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.command.PluginHelp;

public class MMOItemsCompletion implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("mmoitems.op"))
			return null;

		List<String> list = new ArrayList<String>();

		if (args.length == 1) {
			list.add("edit");
			list.add("create");
			list.add("browse");
			list.add("load");
			list.add("copy");
			list.add("drop");
			list.add("itemlist");
			list.add("reload");
			list.add("list");
			list.add("help");
			list.add("delete");
			list.add("remove");
			list.add("heal");
			list.add("identify");
			list.add("unidentify");
			list.add("info");
			list.add("ability");
			list.add("allitems");

		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("help"))
				for (int j = 1; j <= PluginHelp.getMaxPage(); j++)
					list.add("" + j);

			else if (args[0].equalsIgnoreCase("ability"))
				list.addAll(MMOItems.plugin.getAbilities().getAbilityKeys());

			else if (args[0].equalsIgnoreCase("reload"))
				list.add("adv-recipes");

			else if (args[0].equalsIgnoreCase("list")) {
				list.add("ability");
				list.add("type");
				list.add("spirit");
			}

			else if (args[0].equalsIgnoreCase("itemlist") || args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("copy") || args[0].equalsIgnoreCase("load"))
				MMOItems.plugin.getTypes().getAll().forEach(type -> list.add(type.getId()));

			else {
				Type type = Type.safeValueOf(args[0]);
				if (type != null) {
					FileConfiguration config = type.getConfigFile();
					for (String s : config.getKeys(false))
						list.add(s.toUpperCase());
				}
			}
			
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("ability") || Type.safeValueOf(args[0]) != null)
				for (Player player : Bukkit.getOnlinePlayers())
					list.add(player.getName());

			else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("copy") || args[0].equalsIgnoreCase("drop")) {
				Type type = Type.safeValueOf(args[1]);
				if (type != null)
					for (String s : type.getConfigFile().getKeys(false))
						list.add(s.toUpperCase());
			}

		} else {
			if (args[0].equals("drop")) {
				if (args.length == 4)
					for (World world : Bukkit.getWorlds())
						list.add(world.getName());

				if (args.length == 5)
					list.add("" + (sender instanceof Player ? (int) ((Player) sender).getLocation().getX() : 0));

				if (args.length == 6)
					list.add("" + (sender instanceof Player ? (int) ((Player) sender).getLocation().getY() : 0));

				if (args.length == 7)
					list.add("" + (sender instanceof Player ? (int) ((Player) sender).getLocation().getZ() : 0));

				if (args.length == 8 || args.length == 10)
					for (int j = 0; j <= 100; j += 10)
						list.add("" + j);

				if (args.length == 9)
					for (int j = 0; j < 4; j++)
						for (int k = j; k < 4; k++)
							list.add(j + "-" + k);
			}

			if (args[0].equalsIgnoreCase("ability")) {
				String path = args[1].toUpperCase().replace("-", "_");
				if (MMOItems.plugin.getAbilities().hasAbility(path)) {
					Ability ability = MMOItems.plugin.getAbilities().getAbility(path);
					if (Math.floorMod(args.length, 2) == 0)
						list.addAll(ability.getModifiers());
					else
						for (int j = 0; j < 10; j++)
							list.add("" + j);
				}
			}

			if (Type.safeValueOf(args[0]) != null) {
				if (args.length == 4)
					for (String str : new String[] { "1", "16", "64", "1-5", "1-10", "4-16" })
						list.add(str);

				if (args.length == 5 || args.length == 6)
					for (int j : new int[] { 0, 10, 25, 50, 75, 100 })
						list.add("" + j);
			}
		}

		return args[args.length - 1].isEmpty() ? list : list.stream().filter(string -> string.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
	}
}
