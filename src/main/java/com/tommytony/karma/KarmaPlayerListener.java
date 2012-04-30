package com.tommytony.karma;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KarmaPlayerListener implements Listener {

	private final Karma karma;

	public KarmaPlayerListener(Karma karma) {
		this.karma = karma;
	}

	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
    	this.karma.loadOrCreateKarmaPlayer(event.getPlayer());
    }

	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) 
    {
    	KarmaPlayer player = this.karma.getPlayers().get(event.getPlayer().getName());
    	if (player != null) {
    		this.karma.getKarmaDatabase().put(player);	// save latest changes
    		this.karma.getPlayers().remove(player.getName());	
    	}
    }
    
	@EventHandler
    public void onPlayerChat(PlayerChatEvent event) 
    {
    	String playerName = event.getPlayer().getName();
    	if (this.karma.getPlayers().containsKey(playerName)) {
    		this.karma.getPlayers().get(playerName).ping();
    	}
    }
    
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) 
    {
    	String playerName = event.getPlayer().getName();
    	if (this.karma.getPlayers().containsKey(playerName)) {
    		this.karma.getPlayers().get(playerName).ping();
    	}
    }
}
