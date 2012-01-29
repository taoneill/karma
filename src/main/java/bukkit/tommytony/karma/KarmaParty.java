package bukkit.tommytony.karma;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.tommytony.war.Team;
import com.tommytony.war.War;
import com.tommytony.war.Warzone;


public class KarmaParty implements Runnable {

	private final Karma karma;
	private final Random random = new Random();

	public KarmaParty(Karma karma) {
		this.karma = karma;
	}

	public void run() {
		for (Player player : this.karma.getServer().getOnlinePlayers()) {
			this.karma.msg(player, "It's a " + ChatColor.GREEN + "/karma"
					+ ChatColor.GRAY + " party!");
		}
		String playerList = "";
		for (String playerName : this.karma.getPlayers().keySet()) {
			KarmaPlayer player = this.karma.getPlayers().get(playerName);
			long activeInterval = System.currentTimeMillis() - player.getLastActivityTime();
			int minutesAfk = (int) Math.floor(activeInterval / (1000 * 60));
			Player p = this.karma.findPlayer(player.getName());
			if (minutesAfk < 10) {
				int warPlayBonus = getWarPlayingBonus(player, p);
				int warZonemakerBonus = getZonemakerBonus(player, p);
				int total = 1 + warPlayBonus + warZonemakerBonus;
				if (total > 1) {
					this.karma.msg(p, "You gain " + ChatColor.GREEN + total
							+ ChatColor.GRAY + " karma points.");
				} else {
					this.karma.msg(p, "You gain " + ChatColor.GREEN + "1"
							+ ChatColor.GRAY + " karma point.");
				}
				player.addKarma(total);
				playerList += playerName + ", ";
			} else {
				this.karma
						.msg(p, "You missed out on " + ChatColor.GREEN + "1"
								+ ChatColor.GRAY
								+ " karma point because you were afk.");
			}
		}
		if (!playerList.equals("")) {
			this.karma.getServer().getLogger()
					.log(Level.INFO, "Karma> " + playerList + "gained 1 karma");
		}

		// save
		this.karma.getKarmaDatabase().putAll();

		// schedule next karma party
		this.karma
				.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(this.karma,
						new KarmaParty(this.karma),
						this.karma.getNextRandomKarmaPartyDelay());
	}

	private int getWarPlayingBonus(KarmaPlayer karmaPlayer, Player player) {
		if (Warzone.getZoneByPlayerName(karmaPlayer.getName()) != null) {
			if (random.nextInt(3) == 2) {
				karma.msg(player, "Thanks for playing War!");
				return 1;
			}
		}
		return 0;
	}

	private int getZonemakerBonus(KarmaPlayer karmaPlayer, Player player) {
		for (Warzone zone : War.war.getWarzones()) {
			for (String author : zone.getAuthors()) {
				if (author.equals(karmaPlayer.getName()) && !zoneIsEmpty(zone)
						&& zone.isEnoughPlayers()) {
					if (random.nextInt(3) == 2) {
						karma.msg(player, "Thanks for making warzones!");
						return 1;
					}
				}
			}
		}
		return 0;
	}

	private boolean zoneIsEmpty(Warzone zone) {
		for (Team team : zone.getTeams()) {
			if (team.getPlayers().size() > 0) {
				return false;
			}
		}
		return true;
	}
}
