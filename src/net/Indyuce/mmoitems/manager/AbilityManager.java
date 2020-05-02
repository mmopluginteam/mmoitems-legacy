package net.Indyuce.mmoitems.manager;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Ability;

public class AbilityManager extends URLClassLoader {
	private Map<String, Ability> abilities = new HashMap<String, Ability>();

	// ability class paths
	private List<String> classpaths = new ArrayList<String>();

	public AbilityManager() {
		super(((URLClassLoader) MMOItems.plugin.getClass().getClassLoader()).getURLs(), MMOItems.plugin.getClass().getClassLoader());
	}

	public Ability getAbility(String id) {
		return abilities.get(id);
	}

	public boolean hasAbility(String id) {
		return abilities.containsKey(id);
	}

	public Collection<Ability> getAll() {
		return abilities.values();
	}

	public Set<String> getAbilityKeys() {
		return abilities.keySet();
	}

	public void registerAbility(Ability ability) {
		if (ability.isEnabled()) {
			if (ability instanceof Listener)
				Bukkit.getPluginManager().registerEvents((Listener) ability, MMOItems.plugin);
			abilities.put(ability.getID(), ability);
		}
	}

	/*
	 * looks for default ability in the corresponding package and returns the
	 * number of extra addon abilities loaded in the addon/ability folder
	 */
	public int registerAbilities() {

		// mkdir the addon folder
		if (!new File(MMOItems.plugin.getDataFolder() + "/addon").exists())
			new File(MMOItems.plugin.getDataFolder() + "/addon").mkdir();

		// mkdir the ability folder
		if (!new File(MMOItems.plugin.getDataFolder() + "/addon/ability").exists())
			new File(MMOItems.plugin.getDataFolder() + "/addon/ability").mkdir();

		// register default abilities
		try {
			JarFile file = new JarFile(MMOItems.getJarFile());
			for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
				JarEntry jarEntry = entry.nextElement();
				String name = jarEntry.getName().replace("/", ".");

				// make sure this is a class
				// make sure there is no $
				if (!name.contains("$") && name.endsWith(".class") && name.startsWith("net.Indyuce.mmoitems.ability."))
					registerAbility((Ability) Class.forName(name.substring(0, name.length() - 6)).newInstance());
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// register extra abilities in the addon folder
		// path = MMOItems/addon/ability/
		for (File f : new File(MMOItems.plugin.getDataFolder() + "/addon/ability").listFiles()) {
			try {

				// add the url to the class loader
				addURL(f.toURI().toURL());

				// gets all entries in the jar
				// adds the names to the classpaths so they can be loaded
				JarFile jar = new JarFile(f.getPath());
				for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
					String name = entries.nextElement().getName().replace('/', '.');
					if (!name.endsWith(".class"))
						continue;

					classpaths.add(name.substring(0, name.length() - 6));
				}
				jar.close();
			} catch (IOException e) {
				MMOItems.plugin.getLogger().log(Level.INFO, "Couldn't read class path from " + f.getName());
			}
		}

		// amount of addon abilities loaded
		int n = 0;

		// now loads every class in the list
		try {
			for (String path : classpaths) {
				Class<?> clazz = loadClass(path);

				// if the class is not an inner class, load the class
				if (clazz.isAnonymousClass())
					continue;

				Object clazzInstance = clazz.newInstance();
				if (clazzInstance instanceof Ability) {
					registerAbility((Ability) clazzInstance);
					n++;
				}
			}
			close();
		} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException e) {
			MMOItems.plugin.getLogger().log(Level.WARNING, "An error occured while loading extra abilities");
			e.printStackTrace();
		}

		return n;
	}
}
