package io.github.silicondev.customitemmanager;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomItemManager extends JavaPlugin {
	public static String pluginName = "CustomItemManager";
	public boolean debugMode = true;
	public static String version = "Beta 0.1.0";
	private FileConfiguration itemConfig;
	private FileConfiguration idConfig;
	public static YamlConfiguration langConfig;
	private File itemFile = new File(getDataFolder(), "items.yml");
	private File idFile = new File(getDataFolder(), "ids.yml");
	private static File langFile;
	
	CommandOut comOut = new CommandOut(this);
	static List<CommandCIM> commands = new ArrayList<CommandCIM>();
	
	static List<CustomItem> savedItems = new ArrayList<CustomItem>();
	
	@Override
	public void onEnable() {
		getLogger().info("Startup Initialized!");
		int errNum = 0;
		try {
			this.getCommand("customitem").setExecutor(new CommandExec(this));
			commands.add(new CommandCIM("customitem", 0, -1, false, true, false, 0, "default", "Base command for " + pluginName));
			//CHILDREN
			commands.add(new CommandCIM("test", 0, -1, false, false, true, 1, "customitemmanager.test", "Tests the plugin. Usage: /customitem test <testArg>"));
			commands.set(1, addParent(commands.get(1), commands.get(0)));
			commands.set(0, addChild(commands.get(0), commands.get(1)));
			
			commands.add(new CommandCIM("help", 0, 1, false, false, true, 2, "customitemmanager.help", "Displays the help text. Usage: /customitem help <command>"));
			commands.set(2, addParent(commands.get(2), commands.get(0)));
			commands.set(0, addChild(commands.get(0), commands.get(2)));
			
			commands.add(new CommandCIM("item", 0, -1, true, true, true, 3, "customitemmanager.item", "Base command for item management."));
			commands.set(3, addParent(commands.get(3), commands.get(0)));
			commands.set(0, addChild(commands.get(0), commands.get(3)));
			
			commands.add(new CommandCIM("set", 1, 0, true, false, true, 4, "customitemmanager.item.set", "Saves a custom item to the database. Usage: /customitem item set <id>"));
			commands.set(4, addParent(commands.get(4), commands.get(3)));
			commands.set(3, addChild(commands.get(3), commands.get(4)));
			
			commands.add(new CommandCIM("spawn", 1, 0, true, false, true, 5, "customitemmanager.item.spawn", "Spawns a custom item from the database. Usage: /customitem item spawn <id>"));
			commands.set(5, addParent(commands.get(5), commands.get(3)));
			commands.set(3, addChild(commands.get(3), commands.get(5)));
			
			commands.add(new CommandCIM("delete", 1, 0, true, false, true, 6, "customitemmanager.item.delete", "Deletes a custom item from the database. Usage: /customitem item delete <id>"));
			commands.set(6, addParent(commands.get(6), commands.get(3)));
			commands.set(3, addChild(commands.get(3), commands.get(6)));
			
			commands.add(new CommandCIM("list", 0, 0, false, false, true, 7, "customitemmanager.item.list", "Lists all custom items in the database. Usage: /customitem item list"));
			commands.set(7, addParent(commands.get(7), commands.get(3)));
			commands.set(3, addChild(commands.get(3), commands.get(7)));
			
			commands.add(new CommandCIM("save", 0, 0, false, false, true, 8, "customitemmanager.save", "Saves all data to configs."));
			commands.set(8, addParent(commands.get(8), commands.get(0)));
			commands.set(0, addChild(commands.get(0), commands.get(8)));
		} catch(NullPointerException e) {
			errNum++;
			getLogger().info("Error loading commands!");
		}
		
		errNum += load();
		loadLang();
		
		getLogger().info("Initialization complete with " + Integer.toString(errNum) + " errors.");
		
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Shutting Down!");
		getLogger().info("Saving items!");
		save();
	}
	
	public CommandCIM addChild(CommandCIM parent, CommandCIM child) {
		parent.children.add(child);
		return parent;
	}
	
	public CommandCIM addParent(CommandCIM child, CommandCIM parent) {
		child.parent = parent;
		return child;
	}
	
	public void save() {
		
		if (itemFile.exists()) {
			getLogger().info("Existing item file found! Deleting...");
			itemFile.delete();
			getLogger().info("Item file deleted! Saving blank file...");
			try {
				itemFile.createNewFile();
				getLogger().info("Blank file saved and ready for editing!");
			} catch (IOException e) {
				getLogger().info("ERR: Error creatine new file!");
				e.printStackTrace();
			}
			//saveResource("items.yml", false);
		}
		
		if (idFile.exists()) {
			getLogger().info("Existing id file found! Deleting...");
			idFile.delete();
			getLogger().info("Id file deleted! Saving blank file...");
			try {
				itemFile.createNewFile();
				getLogger().info("Blank file saved and ready for editing!");
			} catch (IOException e) {
				getLogger().info("ERR: Error creatine new file!");
				e.printStackTrace();
			}
			//saveResource("ids.yml", false);
		}
		
		Map<String, Object> itemConfigVals = itemConfig.getValues(false);
		Map<String, Object> idConfigVals = idConfig.getValues(false);
		
		for (Map.Entry<String, Object> entry : itemConfigVals.entrySet()) {
			itemConfig.set(entry.getKey(), null);
		}
		for (Map.Entry<String, Object> entry : idConfigVals.entrySet()) {
			idConfig.set(entry.getKey(), null);
		}
		
		for (int i = 0; i < savedItems.size(); i++) {
			getLogger().info("Saving id: (" + Integer.toString(i) + ") " + savedItems.get(i).id); 
			idConfig.set(Integer.toString(i), savedItems.get(i).id);
			getLogger().info("Id saved!");
		}
		
		try {
			idConfig.save(idFile);
			getLogger().info("Ids saved!");
		} catch (IOException e) {
			getLogger().info("Error saving file! STACKTRACE BELOW");
			e.printStackTrace();
		}
		
		
		for (int i = 0; i < savedItems.size(); i++) {
			getLogger().info("Saving item: (" + Integer.toString(i) + ") " + savedItems.get(i).id); 
			itemConfig.set(Integer.toString(i), savedItems.get(i).item);
			getLogger().info("Item saved!");
		}
		
		try {
			itemConfig.save(itemFile);
			getLogger().info("Items saved!");
		} catch (IOException e) {
			getLogger().info("Error saving file! STACKTRACE BELOW");
			e.printStackTrace();
		}
	}
	
	public int load() {
		int errNum = 0;
		
		if (!itemFile.exists()) {
			itemFile.getParentFile().mkdirs();
			saveResource("items.yml", false);
		}
		
		if (!idFile.exists()) {
			idFile.getParentFile().mkdirs();
			saveResource("ids.yml", false);
		}
		
		itemConfig = new YamlConfiguration();
		idConfig = new YamlConfiguration();
		try {
			itemConfig.load(itemFile);
			idConfig.load(idFile);
		} catch (InvalidConfigurationException inv) {
			inv.printStackTrace();
			errNum++;
		} catch (IOException io) {
			io.printStackTrace();
			errNum++;
		}
		
		savedItems.clear();
		try {
			boolean end = false;
			int i = 0;
			while (!end) {
				CustomItem ci = new CustomItem();
				
				if (!(idConfig.getString(Integer.toString(i)) == null)) {
					getLogger().info("Attempting to load id: " + Integer.toString(i));
					ci.setId(idConfig.getString(Integer.toString(i)));
					getLogger().info("Found id: " + ci.getId());
				} else {
					getLogger().info("Cannot find id, end of file.");
					end = true;
				}
				
				if (!(itemConfig.get(Integer.toString(i)) == null)) {
					getLogger().info("Attempting to load item: " + Integer.toString(i));
					ci.setItem((ItemStack)itemConfig.get(Integer.toString(i)));
					getLogger().info("Found item: " + ci.getId());
				} else {
					getLogger().info("Cannot find item, end of file.");
					end = true;
				}
				
				if (!end) {
					savedItems.add(ci);
					getLogger().info("Added item: " + ci.getId());
				}
				i++;
			}
		} catch (NullPointerException e) {
			getLogger().info("Error reading from file. Is it empty?");
		}
		
		return errNum;
	}
	
	public void loadLang() {
		
		File lang = new File(getDataFolder(), "lang.yml");
	    if (!lang.exists()) {
	        try {
	            getDataFolder().mkdir();
	            lang.createNewFile();
	            URL url = this.getClass().getResource("lang.yml");
	            if (url != null) {
	            	try {
	            		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new File(url.toURI()));
	            		defConfig.save(lang);
		                Lang.setFile(defConfig);
		                //return defConfig; //FIX THIS
	            	} catch (URISyntaxException u) {
	            		u.printStackTrace();
	            		getLogger().warning("Couldn't create language file.");
	    	            getLogger().warning("This is a fatal error. Now disabling.");
	    	            this.setEnabled(false); // Without it loaded, we can't send them messages
	            	}
	            }
	        } catch(IOException e) {
	            e.printStackTrace(); // So they notice
	            getLogger().warning("Couldn't create language file.");
	            getLogger().warning("This is a fatal error. Now disabling.");
	            this.setEnabled(false); // Without it loaded, we can't send them messages
	        }
	    }
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
	    for(Lang item:Lang.values()) {
	        if (conf.getString(item.getPath()) == null) {
	            conf.set(item.getPath(), item.getDefault());
	        }
	    }
	    Lang.setFile(conf);
	    CustomItemManager.langConfig = conf;
	    CustomItemManager.langFile = lang;
	    try {
	        conf.save(getLangFile());
	    } catch(IOException e) {
	    	getLogger().warning("Failed to save lang.yml.");
	    	getLogger().warning("Report this stack trace to <your name>.");
	        e.printStackTrace();
	    }
	}
	
	public YamlConfiguration getLang() {
	    return langConfig;
	}
	
	public File getLangFile() {
		return langFile;
	}
}
