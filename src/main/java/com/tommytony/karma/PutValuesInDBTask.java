package com.tommytony.karma;

import java.util.ArrayDeque;

public class PutValuesInDBTask implements Runnable {

	
	private final ArrayDeque<KarmaPlayer> players;
	private final Karma karma;
	
	public PutValuesInDBTask(ArrayDeque<KarmaPlayer> players, Karma karma) {
		this.players = players;
		this.karma = karma;
	}
	
	public void run() {
		while(!players.isEmpty()) {;
			this.karma.getKarmaDatabase().put(players.remove());
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //sleep for 5 Seconds
		}
	}
}
