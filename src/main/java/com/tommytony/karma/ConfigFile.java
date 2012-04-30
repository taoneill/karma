package com.tommytony.karma;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.bukkit.ChatColor;

public class ConfigFile {

	private Karma karma;
	private char sep = File.separatorChar;
	private String[] names;
	private int[] amount;
	private String[] above;
	private ChatColor[] color;
	
	public ConfigFile(Karma karma) {
		//creates if not already created
		if(!new File("plugins" + sep + "karma" + sep + "config.dat").exists()) {
			try {
			this.create();
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		//now we have to read values
		try {
			this.loadValues();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//object construction is finished, very efficient constructor in that ITS BETTER THAN TOMS CONSTRUCTORS
	}
	
	public void create() throws FileNotFoundException {
		Formatter writer = new Formatter(new File("plugins" + sep + "karma" + sep + "config.dat"));
		writer.format("greybeard;%d;none;DarkGreen\n", 2000);
		writer.format("moderator;%d;greybeard;DarkAqua\n", 1000);
		writer.format("minimod;%d;moderator;Aqua\n", 500);
		writer.format("zonemaker;%d;minimod;Gold\n", 100);
		writer.format("builder;%d;zonemaker;Gray\n", 10);
		writer.format("recruit;%d;builder;LightPurple\n", 0);
		writer.close();
	}
	
	public void loadValues() throws FileNotFoundException {
		Scanner reader = new Scanner(new File("plugins" + sep + "karma" + sep + "config.dat"));
		int i = 0;
		while(reader.hasNextLine()) {
			String[] data = reader.nextLine().split(";");
			names[i] = data[0];
			amount[i] = Integer.parseInt(data[1]);
			try {
			above[i] = data[2];
			}catch(NullPointerException e) {
				//we don't want to mess with this
			}
			color[i] = this.convertStringToColor(data[3]);
			
			if(color[i] == null) {
				throw new RuntimeException();
			}
			i++;
		}
		reader.close();
	}
	
	public PriorityQueue<KarmaGroup> transferValuesIntoGroups() {
		PriorityQueue<KarmaGroup> groups = new PriorityQueue<KarmaGroup>();
		int i = 0;
		//Very Clean and efficient code for dealing with this... Tom can't say otherwise
		KarmaGroup last = null;
		do {
			if(i == 0) {
				KarmaGroup temp = new KarmaGroup(names[i], amount[i], null, color[i]);
				groups.offer(temp);
				last = temp;
			} else {
			    KarmaGroup temp = new KarmaGroup(names[i], amount[i], last, color[i]);
				groups.offer(temp);
			    last = temp;
			    //unfortunatly no way to get around a temporary variable, Don't want to create an object twice when it isn't needed...
			}
			i++;
		} while(i < names.length);
		return groups;
	}
	
	public ChatColor convertStringToColor(String string) {
		if(string.equals("DarkGreen")) {
			return ChatColor.DARK_GREEN;
		} else if(string.equals("DarkAqua")) {
			return ChatColor.DARK_AQUA;
		} else if(string.equals("Aqua")) {
			return ChatColor.AQUA;
		} else if(string.equals("Gold")) {
			return ChatColor.GOLD;
		} else if(string.equals("Gray")) {
			return ChatColor.GRAY;
		} else if(string.equals("LightPurple")) {
			return ChatColor.LIGHT_PURPLE;
		} else if(string.equals("Red")) {
			return ChatColor.RED;
		} else if(string.equals("Blue")) {
			return ChatColor.BLUE;
		} else if(string.equals("Black")) {
			return ChatColor.BLACK;
		} else if(string.equals("DarkBlue")) {
			return ChatColor.DARK_BLUE;
		} else if(string.equals("DarkGray")) {
			return ChatColor.DARK_GRAY;
		} else if(string.equals("DarkGreen")) {
			return ChatColor.DARK_GREEN;
		} else if(string.equals("DarkPurple")) {
			return ChatColor.DARK_PURPLE;
		}
		throw new RuntimeException();
	}
	
	
	
	public Karma getInstance() {
		return this.karma;
	}
	
	public String[] getNames() {
		return this.names;
	}
	
	public String[] getAbove() {
		return this.above;
	}
	
	public int[] getAmount() {
		return this.amount;
	}
	
	public ChatColor[] getColor() {
		return this.color;
	}
	
}
