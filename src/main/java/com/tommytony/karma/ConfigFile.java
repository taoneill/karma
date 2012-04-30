package com.tommytony.karma;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

public class ConfigFile {

	private Karma karma;
	private char sep = File.separatorChar;
	
	public ConfigFile(Karma karma) {
		//creates if not already created
		if(new File("plugins" + sep + "karma" + sep + "config.dat").exists()) {
			//read values here
		} else {
			try {
			this.create();
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
			//create file
			//now can assume values
		}
	}
	
	public void create() throws FileNotFoundException {
		Formatter a = new Formatter(new File("plugins" + sep + "karma" + sep + "config.dat"));
		a.format("greybeard;%d;none;DarkGreen\n", 2000);
		a.format("moderator;%d;greybeard;DarkAqua", 1000);
		a.format("minimod;%d;moderator;Aqua", 500);
		a.format("zonemaker;%d;minimod;Gold", 100);
		a.format("builder;%d;zonemaker;Gray", 10);
		a.format("recruit;%d;builder;LightPurple", 0);
	}
	
	
	
	public Karma getInstance() {
		return this.karma;
	}
}
