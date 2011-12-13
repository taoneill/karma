package bukkit.tommytony.karma;

import org.bukkit.entity.Player;

public class LoadPlayers implements Runnable {

	private final Karma karma;

	public LoadPlayers(Karma karma) {
		this.karma = karma;
	}

	public void run() {
		for (Player player : this.karma.getServer().getOnlinePlayers()) {
        	this.karma.loadOrCreateKarmaPlayer(player);
        }
	}

}
