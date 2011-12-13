package bukkit.tommytony.karma;

import java.util.logging.Level;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KarmaPlayerListener extends PlayerListener {

	private final Karma karma;

	public KarmaPlayerListener(Karma karma) {
		this.karma = karma;
	}
	
    public void onPlayerJoin(PlayerJoinEvent event) 
    {
    	this.karma.loadOrCreateKarmaPlayer(event.getPlayer());
    }

    public void onPlayerQuit(PlayerQuitEvent event) 
    {
    	String playerName = event.getPlayer().getName();
    	KarmaPlayer player = this.karma.getPlayers().get(playerName);
    	if (player != null) {
    		this.karma.getKarmaDatabase().put(player);	// save latest changes
    		this.karma.getPlayers().remove(event.getPlayer().getName());	
    	} else {
    		this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Can't find " + playerName + " on quit event.");
    	}
    }
    
    public void onPlayerChat(PlayerChatEvent event) 
    {
    	String playerName = event.getPlayer().getName();
    	if (this.karma.getPlayers().containsKey(playerName)) {
    		this.karma.getPlayers().get(playerName).ping();
    	} else {
    		this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Can't find " + playerName + " on chat event.");
    	}    	
    }
    
    public void onPlayerMove(PlayerMoveEvent event) 
    {
    	String playerName = event.getPlayer().getName();
    	if (this.karma.getPlayers().containsKey(playerName)) {
    		this.karma.getPlayers().get(playerName).ping();
    	} else {
    		this.karma.getServer().getLogger().log(Level.WARNING, "Karma> Can't find " + playerName + " on move event.");
    	}   
    }


}
