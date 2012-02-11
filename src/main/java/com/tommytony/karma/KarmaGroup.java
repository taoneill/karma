package com.tommytony.karma;

import org.bukkit.ChatColor;

public class KarmaGroup {
	private final String groupName;
	private final int karmaPoints;
	private final KarmaGroup next;
	private final ChatColor chatColor;

	public KarmaGroup(String groupName, int karmaPoints, KarmaGroup next, ChatColor color) {
		this.groupName = groupName;
		this.karmaPoints = karmaPoints;
		this.next = next;
		this.chatColor = color;
	}

	public int getKarmaPoints() {
		return karmaPoints;
	}

	public String getGroupName() {
		return groupName;
	}

	public KarmaGroup getNext() {
		return next;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}
}
