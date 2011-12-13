package bukkit.tommytony.karma;

public class KarmaPlayer {

	private final Karma karma;
	
	private final String name;
	private int karmaPoints;
	private long lastActivityTime;

	public KarmaPlayer(Karma karma, String name, int karmaPoints, long lastActivityTime) {
		this.karma = karma;
		this.name = name;
		this.karmaPoints = karmaPoints;
		this.lastActivityTime = lastActivityTime;
	}
	
	public void addKarma(int pointsToAdd) {
		if (pointsToAdd > 0) {
			int before = this.karmaPoints;
			this.karmaPoints += pointsToAdd;
			this.karma.checkForPromotion(name, before, this.karmaPoints);
			this.karma.getKarmaDatabase().put(this);
		}		
	}
	
	public void removeKarma(int pointsToRemove) {
		if (pointsToRemove > 0 && pointsToRemove <= this.karmaPoints) {
			int before = this.karmaPoints;
			this.karmaPoints -= pointsToRemove;
			this.karma.checkForDemotion(name, before, this.karmaPoints);
			this.karma.getKarmaDatabase().put(this);
		}		
	}

	public String getName() {
		return name;
	}

	public int getKarmaPoints() {
		return karmaPoints;
	}

	public long getLastActivityTime() {
		return lastActivityTime;
	}

	public void ping() {
		this.lastActivityTime = System.currentTimeMillis(); 
	}
	
}
