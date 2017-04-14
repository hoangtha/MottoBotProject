package main;

import java.awt.Color;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Hashtable;

public class UserProgress implements Serializable {
	private static final long serialVersionUID = 42L;

	private static final int COMMAND_VALUE = 50;
	private static final int ONLINE_TIME_MULT = 1;
	private static final int VOCAL_TIME_MULT = 2;
	private static final int MESSAGE_VALUE = 150;
	private static final int BASE_EXP = 500;
	
	public String guildId;
	public String userId;
	public String name;
	public String discriminator;
	
	public String title;
	public Color titleColor;
	public boolean titleOn;
	public boolean canRequestTitleChange;
	
	public int experience;
	public int level;
	public int prestige;
	
	public boolean canRequestFavor;
	public String currentFavor;
	public Instant favorStartTime;

	public Hashtable<String, Integer> commandsStats;
	public Hashtable<Achievement, Instant> achievements;
	
	public int messages;
	public int commands;
	public Duration timeSpentOnline;
	public Duration timeSpentVocal;
	public Instant onlineStart;
	public Instant vocalStart;
	public boolean isOnline;
	public boolean isVocal;

	public UserProgress(String guildId, String userId, String name, String discriminator) {
		this.guildId = guildId;
		this.userId = userId;
		this.name = name;
		this.discriminator = discriminator;
		
		this.title = null;
		this.titleColor = Color.BLUE;
		this.titleOn = false;
		this.canRequestTitleChange = false;
		
		this.experience = 0;
		this.level = 1;
		this.prestige = 0;
		
		this.canRequestFavor = false;
		this.currentFavor = null;
		this.favorStartTime = null;
		
		this.commandsStats = new Hashtable<String, Integer>();
		this.achievements = new Hashtable<Achievement, Instant>();
		
		this.messages = 0;
		this.commands = 0;
		this.timeSpentOnline = Duration.ZERO;
		this.timeSpentVocal = Duration.ZERO;
		this.onlineStart = null;
		this.vocalStart = null;
		this.isOnline = false;
		this.isVocal = false;
	}
	
	public void rewardExperience(int amount) {
		this.experience += amount;
	}
	
	public void rewardMessageExperience(int messageLength) {
		this.experience += MESSAGE_VALUE * Math.min(1.0, (float)messageLength/20.0);
	}
	
	public void rewardCommandExperience() {
		this.experience += COMMAND_VALUE;
	}
	
	public void rewardOnlineTimeExperience(long seconds) {
		this.experience += seconds*ONLINE_TIME_MULT;
	}

	public void rewardVocalTimeExperience(long seconds) {
		this.experience += seconds*VOCAL_TIME_MULT;
	}
	
	public void resetLevel() {
		this.experience = 0;
		this.level = 1;
		this.prestige = 0;
	}
	
	public int checkLevelUp() {
		int levelUps = 0;
		while(this.experience>=requiredXP(this.effectiveLevel()+levelUps)) {
			levelUps++;
		}
		
		return levelUps;
	}

	private int effectiveLevel() {
		return this.level + (this.prestige*100);
	}

	public static int requiredXP(int currentLevel) {
		int neededXP = 0;
		for(int i=1; i<=currentLevel; i++) {
			if(i<20)
				neededXP += i*BASE_EXP;
			else
				neededXP += 20*BASE_EXP;
		}
		return neededXP;
	}
}
