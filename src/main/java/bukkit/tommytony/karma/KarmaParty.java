package bukkit.tommytony.karma;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KarmaParty implements Runnable {

	private final Karma karma;

	public KarmaParty(Karma karma) {
		this.karma = karma;
	}
	
	public void run() {
		for (Player player : this.karma.getServer().getOnlinePlayers()) {
			this.karma.msg(player, "It's a " + ChatColor.GREEN + "/karma" + ChatColor.GRAY + " party!");
		}
		for (String playerName : this.karma.getPlayers().keySet()) {
			KarmaPlayer player = this.karma.getPlayers().get(playerName);
			long activeInterval = System.currentTimeMillis() - player.getLastActivityTime();
			int minutesAfk = (int) Math.floor(activeInterval/(1000*60));
			if (minutesAfk < 15) {
				player.addKarma(1);
				Player p = this.karma.findPlayer(player.getName());
				this.karma.msg(p, "You gain " + ChatColor.GREEN + "1" + ChatColor.GRAY + " karma point.");
				this.karma.getServer().getLogger().log(Level.INFO, "Karma> " + playerName + " gained 1 karma");
			}
		}
		
		// save
		this.karma.getKarmaDatabase().putAll();
		
		// schedule next karma party
		this.karma.getServer().getScheduler().scheduleSyncDelayedTask(this.karma, new KarmaParty(this.karma), this.karma.getNextRandomKarmaPartyDelay());
	}

}
