package net.Indyuce.mmoitems.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.AdvancedRecipe;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.listener.version.Version_v1_12;

public class RecipeManager {

	/*
	 * recipes are parsed into a string. they are saved both in a map for
	 * quicker access when checking for patterns - but another map also saves
	 * parsed recipes in a map to easily access items depending on their item
	 * type (for the recipe list)
	 */
	private Map<String, AdvancedRecipe> recipes = new HashMap<>();
	private Map<Type, List<AdvancedRecipe>> types = new HashMap<>();

	private List<AdvancedRecipe> recipeList = new ArrayList<>();
	private List<Type> availableTypes = new ArrayList<>();

	private final String argSeparator = ":";
	private final String itemSeparator = "|";

	public int[] recipeSlots = { 3, 4, 5, 12, 13, 14, 21, 22, 23 };

	@SuppressWarnings("deprecation")
	public RecipeManager() {
		loadRecipes();

		if (!MMOItems.plugin.getConfig().getBoolean("disable-craftings.vanilla"))
			for (Type type : MMOItems.plugin.getTypes().getAll()) {
				FileConfiguration config = type.getConfigFile();
				idLoop: for (String path : config.getKeys(false)) {

					// initialize item so it is not calculated twice
					ItemStack item = config.getConfigurationSection(path).contains("craft") || config.getConfigurationSection(path).contains("shapeless-craft") || config.getConfigurationSection(path).contains("furnace-craft") ? MMOItems.getItem(type, path) : null;

					// vanilla crafting recipe
					if (config.getConfigurationSection(path).contains("craft")) {
						if (item == null || item.getType() == Material.AIR) {
							MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (not a valid item)");
							continue;
						}

						ShapedRecipe recipe = MMOItems.plugin.getVersion().isBelowOrEqual(1, 11) ? new ShapedRecipe(item) : new ShapedRecipe(Version_v1_12.key("Shaped_" + path), item);
						recipe.shape("abc", "def", "ghi");
						char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i' };
						List<String> list = config.getStringList(path + ".craft");
						if (list.size() != 3) {
							MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (format error)");
							continue idLoop;
						}

						// prevent any empty crafting recipe to apply
						if (list.equals(Arrays.asList(new String[] { "AIR AIR AIR", "AIR AIR AIR", "AIR AIR AIR" })))
							continue;

						for (int j = 0; j < 9; j++) {
							char c = chars[j];
							List<String> line = Arrays.asList(list.get(j / 3).split("\\ "));
							if (line.size() < 3) {
								MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (format error)");
								continue idLoop;
							}
							String s = line.get(j % 3);
							Material material = null;
							try {
								material = Material.valueOf(s.split("\\:")[0].replace("-", "_").toUpperCase());
							} catch (Exception e1) {
								MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (can't read material from " + s + ")");
								continue idLoop;
							}

							if (material == Material.AIR)
								continue;

							if (s.contains(":")) {
								double durability = 0;
								try {
									durability = Double.parseDouble(s.split("\\:")[1]);
								} catch (Exception e1) {
									MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (can't read number from " + s + ")");
									continue idLoop;
								}
								recipe.setIngredient(c, material, (int) durability);
								continue;
							}
							recipe.setIngredient(c, material);
						}
						Bukkit.addRecipe(recipe);
					}

					// shapeless crafting recipe
					if (config.getConfigurationSection(path).contains("shapeless-craft")) {
						if (item == null || item.getType() == Material.AIR) {
							MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (not a valid item)");
							continue;
						}

						ShapelessRecipe recipe = MMOItems.plugin.getVersion().isBelowOrEqual(1, 11) ? new ShapelessRecipe(item) : new ShapelessRecipe(Version_v1_12.key("Shapeless_" + path), item);
						for (String ingredient : config.getStringList(path + ".shapeless-craft")) {
							String[] split = ingredient.split("\\:");
							Material material = null;
							try {
								material = Material.valueOf(split[0].toUpperCase().replace(" ", "_").replace("-", "_"));
							} catch (Exception e1) {
								MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (can't read material from " + split[0] + ")");
								continue idLoop;
							}

							if (material == Material.AIR)
								continue;

							if (split.length > 1) {
								int durability = 0;
								try {
									durability = Integer.parseInt(split[1]);
								} catch (Exception e1) {
									MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (can't read number from " + split[1] + ")");
									continue idLoop;
								}
								recipe.addIngredient(new MaterialData(material, (byte) durability));
								continue;
							}
							recipe.addIngredient(material);
						}
						Bukkit.addRecipe(recipe);
					}

					// furnace crafting recipe
					if (config.getConfigurationSection(path).contains("furnace-craft")) {
						if (item == null || item.getType() == Material.AIR) {
							MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (not a valid item)");
							continue;
						}

						Material material = null;
						String format = config.getString(path + ".furnace-craft.input");
						if (format == null)
							continue;

						format = format.toUpperCase().replace(" ", "_").replace("-", "_");
						try {
							material = Material.valueOf(format);
						} catch (Exception e1) {
							MMOItems.plugin.getLogger().log(Level.WARNING, "Could not register the recipe of " + path + " (can't read material from " + format + ")");
							continue;
						}

						if (material != Material.AIR)
							Bukkit.addRecipe(new FurnaceRecipe(item, material));
					}
				}
			}
	}

	/*
	 * clear everything in the maps and reload the advanced recipes. it then
	 * returns the amount of errors or 0 if the advanced recipes are disabled
	 */
	public int loadRecipes() {
		if (MMOItems.plugin.getConfig().getBoolean("disable-craftings.advanced"))
			return 0;

		recipeList.clear();
		types.clear();
		recipes.clear();

		int errors = 0;

		for (Type type : MMOItems.plugin.getTypes().getAll()) {
			FileConfiguration config = type.getConfigFile();
			List<AdvancedRecipe> recipeList = new ArrayList<>();

			itemLoop: for (String id : config.getKeys(false)) {
				if (config.getConfigurationSection(id).contains("advanced-craft")) {

					AdvancedRecipe advancedRecipe = new AdvancedRecipe(type, id);
					String parsedRecipe = "";

					for (int j = 0; j < 9; j++) {
						if (!config.getConfigurationSection(id + ".advanced-craft").contains("" + j)) {
							MMOItems.plugin.getLogger().log(Level.WARNING, id.toUpperCase() + " (" + type.getName() + ") is missing ingredient n" + (j + 1));
							errors++;
							continue itemLoop;
						}

						// type, id
						if (config.getConfigurationSection(id + ".advanced-craft." + j).contains("id")) {
							String type1 = config.getString(id + ".advanced-craft." + j + ".type");
							String id1 = config.getString(id + ".advanced-craft." + j + ".id");
							try {
								String itemFormat = MMOItems.plugin.getTypes().get(type1).getId() + argSeparator + id1;
								parsedRecipe += (parsedRecipe.length() > 0 ? itemSeparator : "") + itemFormat;
								advancedRecipe.setAmount(j, config.getInt(id + ".advanced-craft." + j + ".amount"));
							} catch (Exception e) {
								MMOItems.plugin.getLogger().log(Level.WARNING, id.toUpperCase() + " (" + type.getName() + ") - " + type1 + " is not a valid item type");
								errors++;
								continue itemLoop;
							}
							continue;
						}

						// material, durability, name
						String materialParse = config.getString(id + ".advanced-craft." + j + ".material").toUpperCase().replace(" ", "_").replace("-", "_");
						try {
							String itemFormat = Material.valueOf(materialParse).name() + argSeparator + config.getInt(id + ".advanced-craft." + j + ".durability") + argSeparator + config.getString(id + ".advanced-craft." + j + ".name");
							if (itemFormat.startsWith("AIR:")) {
								parsedRecipe += (parsedRecipe.length() > 0 ? itemSeparator : "") + "AIR";
								advancedRecipe.setAmount(j, 0);
								continue;
							}
							parsedRecipe += (parsedRecipe.length() > 0 ? itemSeparator : "") + itemFormat;
							advancedRecipe.setAmount(j, config.getInt(id + ".advanced-craft." + j + ".amount"));
						} catch (Exception e) {
							MMOItems.plugin.getLogger().log(Level.WARNING, materialParse + " is not a valid material");
							errors++;
							continue itemLoop;
						}
					}

					ItemStack preview = MMOItems.getItem(type, id);
					if (preview == null || preview.getType() == Material.AIR) {
						errors++;
						continue;
					}

					// recipe is now successfully added.
					advancedRecipe.setPreviewItem(preview);
					advancedRecipe.setPermission(config.getString(id + ".advanced-craft-permission"));
					advancedRecipe.setParsed(parsedRecipe);

					recipes.put(type.getId() + "." + id, advancedRecipe);
					recipeList.add(advancedRecipe);
				}
			}

			if (!recipeList.isEmpty())
				types.put(type, recipeList);
		}

		recipeList = new ArrayList<AdvancedRecipe>(recipes.values());
		availableTypes = new ArrayList<Type>(types.keySet());
		return errors;
	}

	public AdvancedRecipe getData(Type type, String id) {
		return recipes.get(type.getId() + "." + id);
	}

	public List<AdvancedRecipe> getRecipes() {
		return recipeList;
	}

	public List<Type> getAvailableTypes() {
		return availableTypes;
	}

	public List<AdvancedRecipe> getTypeRecipes(Type type) {
		return types.containsKey(type) ? types.get(type) : new ArrayList<>();
	}

	/*
	 * filter the list of recipes from a specific type, only showing the recipes
	 * the player has permissions for
	 */
	public List<AdvancedRecipe> getRecipesAvailableForPlayer(Type type, Player player) {
		return getTypeRecipes(type).stream().filter(recipe -> recipe.hasPermission(player)).collect(Collectors.toList());
	}

	/*
	 * returns the current recipe of the opened adv workbench. it can return
	 * null either if the player does not have enough permissions to see the
	 * crafting recipe, or if there is simply no corresponding adv recipe
	 */
	public AdvancedRecipe getCurrentRecipe(Player player, Inventory inv) {

		// check for valid pattern
		String parsedRecipe = getRecipeFormat(inv);
		AdvancedRecipe currentRecipe = null;

		for (AdvancedRecipe recipe : getRecipes())
			if (recipe.isParsed(parsedRecipe)) {
				currentRecipe = recipe;
				break;
			}

		// check for null
		if (currentRecipe == null)
			return null;

		// check for permission
		if (!currentRecipe.hasPermission(player))
			return null;

		// check for amounts
		for (int j = 0; j < 9; j++) {
			ItemStack currentItem = inv.getItem(recipeSlots[j]);
			int current = currentItem == null ? 0 : currentItem.getAmount();
			int needed = currentRecipe.getAmount(j);
			if (current < needed)
				return null;
		}

		return currentRecipe;
	}

	private String getRecipeFormat(Inventory inv) {
		String recipeFormat = "";
		for (int j : new int[] { 3, 4, 5, 12, 13, 14, 21, 22, 23 }) {
			if (inv.getItem(j) == null || inv.getItem(j).getType() == Material.AIR) {
				recipeFormat += (recipeFormat.length() < 1 ? "" : itemSeparator) + "AIR";
				continue;
			}

			ItemStack item = inv.getItem(j);

			// type, id
			String id1 = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_ID");
			if (id1 != null && !id1.equals("")) {
				String type1 = MMOItems.plugin.getNMS().getStringTag(item, "MMOITEMS_ITEM_TYPE");
				String itemFormat = type1 + argSeparator + id1;
				recipeFormat += (recipeFormat.length() < 1 ? "" : itemSeparator) + itemFormat;
				continue;
			}

			// material, durability, name
			String name = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";
			String itemFormat = item.getType().name() + argSeparator + item.getDurability() + argSeparator + name;
			recipeFormat += (recipeFormat.length() < 1 ? "" : itemSeparator) + itemFormat;
		}
		return recipeFormat;
	}
}
