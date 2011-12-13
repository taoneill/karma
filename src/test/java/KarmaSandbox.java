import static org.junit.Assert.*;

import java.util.Random;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.junit.Test;

import bukkit.tommytony.karma.KarmaGroup;
import bukkit.tommytony.karma.KarmaPlayer;



public class KarmaSandbox {

	@Test
	public void testDaysAgo() {
		long now = System.currentTimeMillis();
		long twelveHoursAgo = now - (12*3600*1000);
		long oneDayAgo = now - (24*3600*1000);
		long threeAndHalfDaysAgo = now - (84*3600*1000);
				
		long gone = System.currentTimeMillis() - now;
		int howManyDays = (int)Math.floor(gone/86400000L);
		
		assertEquals(0, howManyDays);
		
		gone = System.currentTimeMillis() - twelveHoursAgo;
		howManyDays = (int)Math.floor(gone/86400000L);
		
		assertEquals(0, howManyDays);
		
		gone = System.currentTimeMillis() - oneDayAgo;
		howManyDays = (int)Math.floor(gone/86400000L);
		
		assertEquals(1, howManyDays);
		
		gone = System.currentTimeMillis() - threeAndHalfDaysAgo;
		howManyDays = (int)Math.floor(gone/86400000L);
		
		assertEquals(3, howManyDays);
	}
	
	@Test
	public void testLastActive() {
		
		long now = System.currentTimeMillis();
		long twominutesago = now - 120*1000;
		long fifteenminutesago = now - 15*60*1000;
		
		long activeInterval = System.currentTimeMillis() - fifteenminutesago;
		int minutesAfk = (int) Math.floor(activeInterval/(1000*60));
			
		assertEquals(15, minutesAfk);
		assertFalse(minutesAfk < 15);
	}
	
	@Test
	public void testStartBonus() {
		KarmaGroup greybeard = new KarmaGroup("greybeard", 2000, null, ChatColor.AQUA);
		KarmaGroup moderator = new KarmaGroup("moderator", 1000, greybeard, ChatColor.AQUA);
		KarmaGroup minimod = new KarmaGroup("minimod", 500, moderator, ChatColor.AQUA);
		KarmaGroup zonemaker = new KarmaGroup("zonemaker", 100, minimod, ChatColor.AQUA);
		KarmaGroup builder = new KarmaGroup("builder", 10, zonemaker, ChatColor.AQUA);
		KarmaGroup recruit = new KarmaGroup("recruit", 0, builder, ChatColor.AQUA);
		
		KarmaGroup group = recruit;
		int initialKarma = 0;
		int karmaToNext = 0;
		while (group != null) {
			if (hasPermission("karma." + group.getGroupName())) {
				initialKarma = group.getKarmaPoints();
				if (group.getNext() != null) {
					karmaToNext = group.getNext().getKarmaPoints() - group.getKarmaPoints(); 
				} else {
					// greybeards only initialize with 2020
					karmaToNext = 100;
				}
			} else {
				break;
			}
			group = group.getNext();
		}
		initialKarma += (int)(0.2 * karmaToNext);	// start bonus of 20% to next rank 

		assertEquals(2020, initialKarma);
	}

	private boolean hasPermission(String string) {
		string = string.replace("karma.", "");
		if(string.equals("recruit")) {
			return true;
		} else if(string.equals("builder")) {
			return true;
		} else if(string.equals("zonemaker")) {
			return true;
		} else if(string.equals("minimod")) {
			return true;
		} else if(string.equals("moderator")) {
			return true;
		} else {
			return true;
		}
	}
	
	
}
