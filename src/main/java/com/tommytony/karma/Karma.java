package com.tommytony.karma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Karma extends JavaPlugin {
	
	public static PermissionHandler permissionHandler;

	private Map<String, KarmaPlayer> players;
	private Database db;
	private KarmaGroup startGroup;
	
	private Random random = new Random();
	
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);
		this.db.putAll();
		players.clear();
				
		this.getServer().getLogger().log(Level.INFO, "Karma> Disabled.");
	}

	public void onEnable() {
		// Init data
		this.players = new HashMap<String, KarmaPlayer>();
		this.db = new Database(this);
		this.db.initialize();
		this.setupPermissions();
		
		KarmaGroup greybeard = new KarmaGroup("greybeard", 2000, null, ChatColor.DARK_GREEN);
		KarmaGroup moderator = new KarmaGroup("moderator", 1000, greybeard, ChatColor.DARK_AQUA);
		KarmaGroup minimod = new KarmaGroup("minimod", 500, moderator, ChatColor.AQUA);
		KarmaGroup zonemaker = new KarmaGroup("zonemaker", 100, minimod, ChatColor.GOLD);
		KarmaGroup builder = new KarmaGroup("builder", 10, zonemaker, ChatColor.GRAY);
		KarmaGroup recruit = new KarmaGroup("recruit", 0, builder, ChatColor.LIGHT_PURPLE);
		
		this.startGroup = recruit;
				
        // Register events
        PluginManager manager = this.getServer().getPluginManager();
        
        KarmaWorldListener worldListener = new KarmaWorldListener(this);
        manager.registerEvents(worldListener, this);
        
        KarmaPlayerListener playerListener = new KarmaPlayerListener(this);
        manager.registerEvents(playerListener, this);
        
        // Load online players
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new LoadPlayers(this));
                
        // Launch karma party train!!        
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new KarmaParty(this), this.getNextRandomKarmaPartyDelay());
        
        this.getServer().getLogger().log(Level.INFO, "Karma> Enabled.");
	}
	
	public void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (Karma.permissionHandler == null) {
			if (permissionsPlugin != null) {
				Karma.permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			} else {
				this.getServer().getLogger().log(Level.INFO, "Permissions system not enabled.", Level.INFO);
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (command.getName().equals("karma") || command.getName().equals("k")) {
				if (args.length == 0 && sender instanceof Player) {
					KarmaPlayer karmaPlayer = this.players.get(((Player)sender).getName());
					if (karmaPlayer != null) {
						this.msg(sender, "You have " + ChatColor.GREEN + karmaPlayer.getKarmaPoints() + ChatColor.GRAY + " karma points. Current rank: " 
								+ this.getPlayerGroupString(karmaPlayer) + ". Next rank: " + this.getPlayerNextGroupString(karmaPlayer) + ".");
						return true;
					}
				} else if (args.length == 1 && (args[0].equals("ranks") || args[0].equals("rank") || args[0].equals("groups"))) {
					String ranksString = "Ranks: ";
					KarmaGroup group = this.startGroup;
					while (group != null) {
						ranksString += group.getGroupName() + "(" + ChatColor.GREEN + group.getKarmaPoints() + ChatColor.GRAY + ")";
						if (group.getNext() != null) ranksString += ChatColor.WHITE + " -> " + ChatColor.GRAY;
						group = group.getNext();
					}
					this.msg(sender, ranksString);
					return true;
				} else if (args.length == 1 && (args[0].equals("?") || args[0].equals("help"))) {
					return false;
				} else if (args.length == 1) {
					List<Player> matches = this.getServer().matchPlayer(args[0]);
					if (!matches.isEmpty()) {
						Player playerTarget = matches.get(0);
						KarmaPlayer karmaTarget = this.players.get(playerTarget.getName());
						if (karmaTarget != null) {
							this.msg(sender, ChatColor.WHITE + playerTarget.getName() + ChatColor.GRAY + " has " + ChatColor.GREEN 
								+ karmaTarget.getKarmaPoints() + ChatColor.GRAY + " karma points.");
							return true;
						} else {
							this.msg(sender, "Couldn't find target.");
							return true;
						}						
					} else {
						this.msg(sender, "Couldn't find player.");
						return true;
					}
				} else if (args.length == 2 && (args[0].equals("build") || args[0].equals("builder"))
						&& (!(sender instanceof Player) || Karma.permissionHandler.has((Player)sender, "karma.zonemaker"))) {
					List<Player> matches = this.getServer().matchPlayer(args[1]);
					KarmaGroup builder = this.startGroup.getNext();
					if (!matches.isEmpty()) {
						Player playerTarget = matches.get(0);
						KarmaPlayer karmaTarget = this.players.get(playerTarget.getName());
						if (karmaTarget != null && karmaTarget.getKarmaPoints() < builder.getKarmaPoints()) {
							karmaTarget.addKarma(builder.getKarmaPoints() - karmaTarget.getKarmaPoints());
							this.msg(playerTarget, "Promoted to builder.");
							return true;
						} else {
							this.msg(sender, "Couldn't find target or target already builder.");
							return true;
						}						
					} else {
						this.msg(sender, "Couldn't find player.");
						return true;
					}
				} else if (args.length == 2 && (args[0].equals("gift") || args[0].equals("give"))
						&& (!(sender instanceof Player) || Karma.permissionHandler.has((Player)sender, "karma.zonemaker"))) {
					String target = args[1];
					
					List<Player> matches = this.getServer().matchPlayer(target);
					KarmaPlayer karmaGiver = null;
					if (sender instanceof Player) {
						karmaGiver = this.players.get(((Player)sender).getName());
					}
					
					if (karmaGiver == null || karmaGiver.getKarmaPoints() > 0) {
						Player playerTarget = matches.get(0);
						KarmaPlayer karmaTarget = this.getPlayers().get(playerTarget.getName());
						
						if (karmaTarget != null && !sender.getName().equals(playerTarget.getName())) {
							
							String gifterName = "server";
							if (karmaGiver != null) {
								gifterName = ((Player)sender).getName();
								if (karmaGiver.canGift()) {
									karmaGiver.updateLastGiftTime();
									karmaGiver.removeKarma(1);
									this.msg(sender, "You gave " + ChatColor.WHITE + playerTarget.getName() + ChatColor.GREEN + " 1"+ ChatColor.GRAY
											+" karma point. How generous!");
								} else {
									long since = (System.currentTimeMillis() - karmaGiver.getLastGiftTime())/1000;
									this.msg(sender, "You need to wait " + ((3600 - since)/60) +  " minutes before gifting again.");
									return true;
								}
							}								
							
							karmaTarget.addKarma(1);
							this.msg(playerTarget, "You received " + ChatColor.GREEN + "1" + ChatColor.GRAY + " karma point from " + ChatColor.WHITE 
									+ gifterName + ChatColor.GRAY + ". How generous!");
							
							this.getServer().getLogger().log(Level.INFO, "Karma> " + gifterName + " gave a point to " + playerTarget.getName());
							
							return true;
						} else {
							this.getServer().getLogger().log(Level.WARNING, "Karma> Couldn't find target or targetted self.");
						}
					}
				} else if (args.length == 3) {
					String action = args[0];
					String target = args[1];
					
					if (action.equals("give") || action.equals("gift")) {
						this.msg(sender, "Use /karma " + action + " <person> to donate " + ChatColor.GREEN + "1" + ChatColor.GRAY + " karma point.");
						return true;
					}
					
					int amount = Integer.parseInt(args[2]);
					
					List<Player> matches = this.getServer().matchPlayer(target);
					if (action.equals("set") && !matches.isEmpty() && amount >= 0 
							&& (!(sender instanceof Player) || (Karma.permissionHandler.has((Player)sender, "karma.set")))) {
						return this.setAmount(matches, amount);
					} else {
						this.getServer().getLogger().log(Level.WARNING, "Karma> Command not recognized.");
					}
				}
				
			} else {
				this.getServer().getLogger().log(Level.WARNING, "Karma> Can't recognize command.");
			}
		} catch (Exception e) {
			this.getServer().getLogger().log(Level.WARNING, "Karma> Exception occured. " + e.toString());
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private boolean setAmount(List<Player> matches, int amount) {
		Player playerTarget = matches.get(0);
		return this.setAmount(playerTarget, amount);
	}
	
	private boolean setAmount(Player playerTarget, int amount) {
		KarmaPlayer karmaTarget = this.getPlayers().get(playerTarget.getName());
		if (karmaTarget != null && amount != karmaTarget.getKarmaPoints()) {
			int before = karmaTarget.getKarmaPoints();
			if (amount > karmaTarget.getKarmaPoints()) {
				karmaTarget.addKarma(amount - karmaTarget.getKarmaPoints());
			} else {
				karmaTarget.removeKarma(karmaTarget.getKarmaPoints() - amount);
			}
			this.msg(playerTarget, "Your karma was set to " + ChatColor.GREEN + karmaTarget.getKarmaPoints() + ChatColor.GRAY + ".");
			this.getServer().getLogger().log(Level.INFO, "Karma> " + playerTarget.getName() + " karma set to " + karmaTarget.getKarmaPoints() + " from " + before);
			
			return true;
		}
		return false;
		
	}

	public void loadOrCreateKarmaPlayer(Player player) {
		String playerName = player.getName();
		if (this.db.exists(playerName)) {
    		// existing player
			KarmaPlayer karmaPlayer = this.db.get(playerName); 
			if (karmaPlayer != null) {
	    		this.players.put(playerName, karmaPlayer);
	    		
	    		// check if player needs a promo, in case perms got wiped
	    		// BONUS: this lets you change perms systems without having
	    		// to migrate - ex: just change the GM commands to bPerms 
	    		KarmaGroup currentGroup = this.startGroup;
	    		while (currentGroup != null) {
		    		if (karmaPlayer.getKarmaPoints() >= currentGroup.getKarmaPoints() 
		    				&& !permissionHandler.has(player, "karma." + currentGroup.getGroupName())
		    				&& !(currentGroup.getNext() != null
		    						&& karmaPlayer.getKarmaPoints() >= currentGroup.getNext().getKarmaPoints())) {
		    			// either doesn't have a next rank or can't beat the next rank's k points, we found the right rank
		    			this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "manpromote " + playerName + " " + currentGroup.getGroupName());
						this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "mansave");
						for (Player playerOnline : this.getServer().getOnlinePlayers()) {
							this.msg(playerOnline, "Sweet karma! " + ChatColor.WHITE + playerName + ChatColor.GRAY 
									+ " promoted to " + currentGroup.getChatColor() + currentGroup.getGroupName() + ChatColor.GRAY + ".");
						}
		    		}
		    		
		    		currentGroup = currentGroup.getNext();
	    		}
	    		
	    		// check for last activity, remove one karma point per day off
	    		long gone = System.currentTimeMillis() - karmaPlayer.getLastActivityTime();
	    		int howManyDays = (int)Math.floor(gone/86400000L);
	    		
	    		if (howManyDays > 0) {
	    			int before = karmaPlayer.getKarmaPoints();
	    			karmaPlayer.removeKarmaAutomatic(howManyDays);
	    			this.getServer().getLogger().log(Level.INFO, "Karma> " + player.getName() + " lost " + (before - karmaPlayer.getKarmaPoints()) + " karma points");
	    		}    		
	    		
	    		// update last activity
	    		karmaPlayer.ping();
	    		this.db.put(karmaPlayer);
			}
    	} else {
    		// create player
    		int initialKarma = this.getInitialKarma(player);
    		KarmaPlayer karmaPlayer = new KarmaPlayer(this, player.getName(), initialKarma, System.currentTimeMillis(), 0);
    		this.players.put(player.getName(), karmaPlayer);
    		this.db.put(karmaPlayer);
    		
    		this.msg(player, "You begin your karmic journey with " + ChatColor.GREEN + initialKarma + ChatColor.GRAY + " karma points. Use "
    				+ ChatColor.GREEN + "/karma" + ChatColor.GRAY + " to check your karma. Gain enough karma points and you will be auto-promoted." );
    		this.getServer().getLogger().log(Level.INFO, "Karma> " + player.getName() + " created with " + initialKarma + " karma points");
    	}
	}

	private int getInitialKarma(Player player) {
		KarmaGroup group = this.startGroup;
		int initialKarma = 0;
		int karmaToNext = 0;
		while (group != null) {
			String perm = "karma." + group.getGroupName();
			if (Karma.permissionHandler.has(player, perm)) {
				initialKarma = group.getKarmaPoints();
				if (group.getNext() != null) {
					karmaToNext = group.getNext().getKarmaPoints() - group.getKarmaPoints(); 
				} else {
					// greybeards only initialize with 2020
					karmaToNext = 100;
				}
			} else {
				this.getServer().getLogger().log(Level.INFO, "Karma> Doesn't have " + perm);
				break;
			}
			group = group.getNext();
		}
		initialKarma += (int)(0.2 * karmaToNext);	// start bonus of 20% to next rank 
		return initialKarma;
	}

	public void checkForPromotion(String playerName, int before, int after) {
		KarmaGroup group = this.startGroup;
		Player playerForPromotion = this.findPlayer(playerName);
		while (group != null && playerForPromotion != null) {
			String perm = "karma." + group.getGroupName();
			if (before < group.getKarmaPoints() && after >= group.getKarmaPoints()
					&& !Karma.permissionHandler.has(playerForPromotion, perm)) {
				// promotion
				this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "manpromote " + playerName + " " + group.getGroupName());
				this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "mansave");
				for (Player player : this.getServer().getOnlinePlayers()) {
					this.msg(player, "Good karma! " + ChatColor.WHITE + playerName + ChatColor.GRAY + " promoted to " + group.getGroupName() + ".");
				}
				this.getServer().getLogger().log(Level.INFO, "Karma> " + playerName + " promoted to " + group.getGroupName());
			}
			group = group.getNext();
		}		
	}

	public void checkForDemotion(String playerName, int before, int after, boolean automatic) {
		KarmaGroup group = this.startGroup;
		Player playerForDemotion = this.findPlayer(playerName);
		while (group != null && playerForDemotion != null) {
			if (group.getNext() != null && before >= group.getNext().getKarmaPoints() && after < group.getNext().getKarmaPoints()) {
				String perm = "karma." + group.getNext().getGroupName();
				if (group.getGroupName().equals("recruit") && automatic) return; // Prevents players from being demoted to recruit
				if (Karma.permissionHandler.has(playerForDemotion, perm)) {
					// demotion
					this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "mandemote " + playerName + " " + group.getGroupName());
					this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "mansave");
					for (Player player : this.getServer().getOnlinePlayers()) {
						this.msg(player, "Bad karma! " + ChatColor.WHITE + playerName + ChatColor.GRAY + " demoted to " + group.getGroupName() + ".");
					}
					this.getServer().getLogger().log(Level.INFO, "Karma> " + playerName + " demoted to " + group.getGroupName());
					break;
				}
			}
			group = group.getNext();
		}	
	}
	
	public void msg(CommandSender destination, String message) {
		destination.sendMessage(ChatColor.DARK_PURPLE + "Karma> " + ChatColor.GRAY + replaceGroupNames(message));
	}	
	
	private String replaceGroupNames(String message) {
		KarmaGroup group = this.startGroup; 
		while (group != null) {
			message = message.replace(group.getGroupName(), group.getChatColor() + group.getGroupName() + ChatColor.GRAY);
			group = group.getNext();
		}
		return message;
	}

	private String getPlayerNextGroupString(KarmaPlayer karmaPlayer) {
		Player player = this.findPlayer(karmaPlayer.getName());
		KarmaGroup group = this.startGroup;
		while (group != null) {
			String perm = "karma." + group.getGroupName();
			if (!Karma.permissionHandler.has(player, perm)) {
				return group.getGroupName() + " ("  + ChatColor.GREEN +  group.getKarmaPoints() + ChatColor.GRAY + ")";
			}
			group = group.getNext();
		} 
		return "none";
	}

	private String getPlayerGroupString(KarmaPlayer karmaPlayer) {
		Player player = this.findPlayer(karmaPlayer.getName());
		KarmaGroup group = this.startGroup;
		KarmaGroup lastGroup = null; // first group is recruit
		while (group != null) {
			String perm = "karma." + group.getGroupName();
			if (!Karma.permissionHandler.has(player, perm)) {
				return lastGroup.getGroupName() + " (" + ChatColor.GREEN + lastGroup.getKarmaPoints() + ChatColor.GRAY + ")";
			}
			lastGroup = group;
			if (group.getNext() == null) {
				return group.getGroupName();
			}
			group = group.getNext();
		} 
		return "none";
	}
	
	public Player findPlayer(String playerName) {
		for (Player player : this.getServer().getOnlinePlayers()) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}
		return null;
	}
	
	public int getNextRandomKarmaPartyDelay() {
		// on average 20, between 10 min and 30 min 
		int minutes = 10 + this.random.nextInt(20);
		// 20 ticks/second, 60 seconds/min
		int ticks = minutes * 20 * 60;
		this.getServer().getLogger().log(Level.INFO, "Karma> Next karma party in " + minutes + " minutes or " + ticks + " ticks.");
		return ticks;
	}
	
	public Map<String, KarmaPlayer> getPlayers() {
		return this.players;
	}
	
	public Database getKarmaDatabase() {
		return this.db;
	}
}
