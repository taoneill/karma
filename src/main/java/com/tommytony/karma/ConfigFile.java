package com.tommytony.karma;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.Scanner;

import org.bukkit.ChatColor;

public class ConfigFile {

	private Karma karma;
	private char sep = File.separatorChar;
	private String[] names = new String[6];
	private int[] amount = new int[6];
	private String[] above = new String[6];
	private ChatColor[] color = new ChatColor[6];
	
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
		//object construction is finished
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
			above[i] = data[2];
			color[i] = this.convertStringToColor(data[3]);
			
			if(color[i] == null) {
				throw new RuntimeException();
			}
			i++;
		}
		reader.close();
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
		return null;
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
