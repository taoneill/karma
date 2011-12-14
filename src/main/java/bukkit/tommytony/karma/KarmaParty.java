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
		String playerList = "";
		for (String playerName : this.karma.getPlayers().keySet()) {
			KarmaPlayer player = this.karma.getPlayers().get(playerName);
			long activeInterval = System.currentTimeMillis() - player.getLastActivityTime();
			int minutesAfk = (int) Math.floor(activeInterval/(1000*60));
			Player p = this.karma.findPlayer(player.getName());
			if (minutesAfk < 10) {
				player.addKarma(1);
				this.karma.msg(p, "You gain " + ChatColor.GREEN + "1" + ChatColor.GRAY + " karma point.");
				playerList += playerName + ", ";
			} else {
				this.karma.msg(p, "You missed out on " + ChatColor.GREEN + "1" + ChatColor.GRAY + " karma point because you were afk.");
			}
		}
		if (!playerList.equals("")) {
			this.karma.getServer().getLogger().log(Level.INFO, "Karma> " + playerList + "gained 1 karma");
		}
		
		// save
		this.karma.getKarmaDatabase().putAll();
		
		// schedule next karma party
		this.karma.getServer().getScheduler().scheduleSyncDelayedTask(this.karma, new KarmaParty(this.karma), this.karma.getNextRandomKarmaPartyDelay());
	}

}
