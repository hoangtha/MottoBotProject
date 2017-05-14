package main;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import commandes.CmdMotto;
import commandes.Commande;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TallyCounter extends ListenerAdapter {
	private Hashtable<String, Hashtable<String, UserProgress>> userProgress;
	private String pathProgress;
	private ArrayList<String> activeGuilds;
	private MottoBot bot;
	private Hashtable<String, OffsetDateTime> lastMessage;
	
	@SuppressWarnings("unchecked")
	public TallyCounter(MottoBot bot, String pathProgress) {
		this.pathProgress = pathProgress;
		this.lastMessage = new Hashtable<String, OffsetDateTime>();
		this.activeGuilds = new ArrayList<String>();
		this.activeGuilds.add("269163044427268096");// FP
		this.activeGuilds.add("228161553986355212");// Ehreon
		this.bot = bot;
		
		try {
			FileInputStream fileIn = new FileInputStream(this.pathProgress);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			this.userProgress = (Hashtable<String, Hashtable<String, UserProgress>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Erreur lors du chargement de "+this.pathProgress+", création d'une nouvelle table.");
			this.userProgress = new Hashtable<String, Hashtable<String, UserProgress>>();
		}
		this.fixExp();
		this.saveToFile();
		
		Timer timeTimer = new Timer(true);
		TimerTask timeTask = new TimerTask() {
			@Override
			public void run() {
				updateTimeSpent();
			}
		};
		timeTimer.schedule(timeTask, 10000, 10000); //10s
		TimerTask saveTask = new TimerTask() {
			@Override
			public void run() {
				saveToFile();
			}
		};
		timeTimer.schedule(saveTask, 15000, 60000); // 1min
		TimerTask levelUp = new TimerTask() {
			@Override
			public void run() {
				checkLevelUpForEveryone();
			}
		};
		timeTimer.schedule(levelUp, 5000, 43200000); // 12h
	}
	
	private void checkLevelUp(UserProgress up, String guildId) {
		if(this.activeGuilds.contains(guildId) && up.checkLevelUp()>0) {
			EmbedBuilder mb = new EmbedBuilder();
			mb.setColor(new Color(200,50,50));
			mb.setTitle("LEVEL UP !", null);
			String effName = this.bot.getJda().getGuildById(up.guildId).getMemberById(up.userId).getEffectiveName();
			int newLevel = up.level+up.checkLevelUp();
			int newPrestige = up.prestige;
			while (newLevel>100) {
				newPrestige++;
				newLevel -= 100;
			}
			String text = "Niveau "+up.level+" -> "+newLevel+" ! ";
			up.level = newLevel;
			if(up.prestige!=newPrestige) {
				text += "\nPrestige "+up.prestige+"★ -> "+newPrestige+"★ !";
				up.prestige = newPrestige;
				up.canRequestFavor = true;
				up.canRequestTitleChange = true;
			}
			mb.addField(effName, text, false);
			List<TextChannel> channels = this.bot.getJda().getGuildById(guildId).getTextChannelsByName("annonces", true);
			if(channels!=null && channels.isEmpty()==false) {
				channels.get(0).sendMessage(mb.build()).queue();
			}
			else {
				System.out.println("Pas de canal annonces pour les level up sur la guilde "+guildId);
			}
		}
	}

	public void checkLevelUpForEveryone() {
		ArrayList<UserProgress> events = new ArrayList<UserProgress>();
		for(String guildId : this.activeGuilds) {
			if(this.bot.getJda().getGuildById(guildId)==null) {
				continue;
			}
			Hashtable<String, UserProgress> guildTable = this.userProgress.get(guildId);
			if(guildTable==null)
				continue;
			for(UserProgress up : guildTable.values()) {
				if(this.bot.getJda().getGuildById(guildId).getMemberById(up.userId)==null) {
					continue;
				}
				int lvlUps = up.checkLevelUp();
				if(lvlUps>0)
					events.add(up);
			}
			
			if(events.size()>0) {
				if(this.activeGuilds.contains(guildId)) {
					EmbedBuilder mb = new EmbedBuilder();
					mb.setColor(new Color(200,50,50));
					mb.setTitle("LEVEL UP !", null);
					for(UserProgress up:events) {
						String effName = this.bot.getJda().getGuildById(up.guildId).getMemberById(up.userId).getEffectiveName();
						int newLevel = up.level+up.checkLevelUp();
						int newPrestige = up.prestige;
						while (newLevel>100) {
							newPrestige++;
							newLevel -= 100;
						}
						String text = "Niveau "+up.level+" -> "+newLevel+" !";
						up.level = newLevel;
						if(up.prestige!=newPrestige) {
							text += "\nPrestige "+up.prestige+"★ -> "+newPrestige+"★ !";
							up.prestige = newPrestige;
							up.canRequestFavor = true;
							up.canRequestTitleChange = true;
						}
						mb.addField(effName, text, false);
					}
					List<TextChannel> channels = this.bot.getJda().getGuildById(guildId).getTextChannelsByName("annonces", true);
					if(channels!=null && channels.isEmpty()==false) {
						channels.get(0).sendMessage(mb.build()).queue();
					}
					else {
						System.out.println("Pas de canal annonces pour les level up sur la guilde "+guildId);
					}
				}
				events.clear();
			}
		}
	}
	
	protected void updateTimeSpent() {
		Instant now = Instant.now();
		for(Hashtable<String, UserProgress> guildTable : this.userProgress.values()) {
			for(UserProgress up : guildTable.values()) {
				if(up.isOnline) {
					up.timeSpentOnline = up.timeSpentOnline.plus(Duration.between(up.onlineStart, now));
					up.rewardOnlineTimeExperience(Duration.between(up.onlineStart, now).getSeconds());
					up.onlineStart = now;
				}
				if(up.isVocal) {
					up.timeSpentVocal = up.timeSpentVocal.plus(Duration.between(up.vocalStart, now));
					up.rewardVocalTimeExperience(Duration.between(up.vocalStart, now).getSeconds());
					up.vocalStart = now;
				}
			}
		}
	}
	
	public boolean saveToFile() {
		try {
			FileOutputStream fileOut = new FileOutputStream(this.pathProgress);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this.userProgress);
			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Hashtable<String, Hashtable<String, UserProgress>> getProgress() {
		return this.userProgress;
	}

	public void statsInit() {
		for(Guild g:this.bot.getJda().getGuilds()) {
			String guildId = g.getId();
			Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
			for(Member m:g.getMembers()) {
				if(m.getUser().isBot())
					continue;
				String userId = m.getUser().getId();
				String name = m.getUser().getName();
				String discriminator = m.getUser().getDiscriminator();
				String userName = name + "#" + discriminator;
				OnlineStatus status = m.getOnlineStatus();
				
				UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
				if(status==OnlineStatus.OFFLINE || status==OnlineStatus.INVISIBLE) {
					up.onlineStart = null;
					up.isOnline = false;
				}
				else {
					up.onlineStart = Instant.now();
					up.isOnline = true;
				}
				if(m.getVoiceState().inVoiceChannel()==false) {
					up.vocalStart = null;
					up.isVocal = false;
				}
				else {
					up.vocalStart = Instant.now();
					up.isVocal = true;
				}
				guildTable.putIfAbsent(userName, up);
			}
			this.userProgress.putIfAbsent(guildId, guildTable);
		}
	}

	@Override
	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		if(event.getUser().isBot())
			return;
		String guildId = event.getGuild().getId();
		String userId = event.getUser().getId();
		String name = event.getUser().getName();
		String discriminator = event.getUser().getDiscriminator();
		String userName = name + "#" + discriminator;
		OnlineStatus status = event.getGuild().getMember(event.getUser()).getOnlineStatus();

		Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
		UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
		if (event.getPreviousOnlineStatus()==OnlineStatus.OFFLINE || event.getPreviousOnlineStatus()==OnlineStatus.INVISIBLE) {
			up.onlineStart = Instant.now();
			up.isOnline = true;
		}
		else if(status==OnlineStatus.OFFLINE || status==OnlineStatus.INVISIBLE) {
			up.onlineStart = null;
			up.isOnline = false;
		}
		guildTable.putIfAbsent(userName, up);
		this.userProgress.putIfAbsent(guildId, guildTable);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot() || event.getMember()==null || event.getMember().getUser()==null || 
				event.getMember().getUser().getName()==null || event.getMember().getUser().getDiscriminator()==null)
			return;
		String guildId = event.getGuild().getId();
		String userId = event.getMember().getUser().getId();
		String name = event.getMember().getUser().getName();
		String discriminator = event.getMember().getUser().getDiscriminator();
		String userName = name + "#" + discriminator;
		
		Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
		UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
		if(this.lastMessage.get(guildId+":"+userId)==null || OffsetDateTime.now().isAfter(this.lastMessage.get(guildId+":"+userId).plusSeconds(1))) {
			up.messages++;
			this.lastMessage.put(guildId+":"+userId, OffsetDateTime.now());
			up.rewardMessageExperience(event.getMessage().getContent().length());
		}
		checkLevelUp(up, guildId);
		guildTable.putIfAbsent(userName, up);
		this.userProgress.putIfAbsent(guildId, guildTable);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if(event.getMember().getUser().isBot())
			return;
		String guildId = event.getGuild().getId();
		String userId = event.getMember().getUser().getId();
		String name = event.getMember().getUser().getName();
		String discriminator = event.getMember().getUser().getDiscriminator();
		String userName = name + "#" + discriminator;
		OnlineStatus status = event.getMember().getOnlineStatus();
		
		Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
		UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
		if(status!=OnlineStatus.OFFLINE && status!=OnlineStatus.INVISIBLE) {
			up.onlineStart = Instant.now();
			up.isOnline = true;
		}
		guildTable.putIfAbsent(userName, up);
		this.userProgress.putIfAbsent(guildId, guildTable);
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if(event.getMember().getUser().isBot())
			return;
		String guildId = event.getGuild().getId();
		String userId = event.getMember().getUser().getId();
		String name = event.getMember().getUser().getName();
		String discriminator = event.getMember().getUser().getDiscriminator();
		String userName = name + "#" + discriminator;

		Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
		UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
		up.vocalStart = Instant.now();
		up.isVocal = true;
		guildTable.putIfAbsent(userName, up);
		this.userProgress.putIfAbsent(guildId, guildTable);
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		if(event.getMember().getUser().isBot())
			return;
		String guildId = event.getGuild().getId();
		String userId = event.getMember().getUser().getId();
		String name = event.getMember().getUser().getName();
		String discriminator = event.getMember().getUser().getDiscriminator();
		String userName = name + "#" + discriminator;

		Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
		UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
		up.vocalStart = null;
		up.isVocal = false;
		guildTable.putIfAbsent(userName, up);
		this.userProgress.putIfAbsent(guildId, guildTable);
	}

	public void onCommandUse(MessageReceivedEvent event, Commande commande, String arguments) {
		if(event.getMember().getUser().isBot())
			return;
		String guildId = event.getGuild().getId();
		String userId = event.getMember().getUser().getId();
		String name = event.getMember().getUser().getName();
		String discriminator = event.getMember().getUser().getDiscriminator();
		String userName = name + "#" + discriminator;
		
		Hashtable<String, UserProgress> guildTable = this.userProgress.getOrDefault(guildId, new Hashtable<String, UserProgress>());
		UserProgress up = guildTable.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
		up.commands++;
		int n = up.commandsStats.getOrDefault(commande.getName(), 0) + 1;
		up.commandsStats.put(commande.getName(), n);
		if(commande.getClass().equals(CmdMotto.class)) {
			if(up.mottoTagStats==null) {
				up.mottoTagStats = new Hashtable<String, Integer>();
			}
			CmdMotto motto = (CmdMotto) commande;
			if(arguments!=null && arguments.isEmpty()==false) {
				String[] tags = arguments.split("\\s+");
				for(int i=0; i<tags.length; i++) {
					String t = tags[i].toLowerCase();
					int tagCount = up.mottoTagStats.getOrDefault(t, 0) + 1;
					up.mottoTagStats.put(t, tagCount);
				}
			}
			else {
				if(motto.robinArmy.contains(guildId)) {
					String[] tags = MottoBot.DEFAULT_SEARCH.split("\\s+");
					for(int i=0; i<tags.length; i++) {
						String t = tags[i].toLowerCase();
						int tagCount = up.mottoTagStats.getOrDefault(t, 0) + 1;
						up.mottoTagStats.put(t, tagCount);
					}
				}
			}
		}
		up.rewardCommandExperience();
		checkLevelUp(up, guildId);
		guildTable.putIfAbsent(userName, up);
		this.userProgress.putIfAbsent(guildId, guildTable);
	}

	public void fixExp() {
		for(Hashtable<String, UserProgress> guildTable : this.userProgress.values()) {
			for(UserProgress up : guildTable.values()) {
				// Experience fix
				//up.rewardExperience(0);
				// Favors & Title fix
				if(up.prestige>0) {
					up.canRequestFavor = true;
					up.canRequestTitleChange = true;
				}
			}
		}
	}
}
