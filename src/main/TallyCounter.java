package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import commandes.Commande;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TallyCounter extends ListenerAdapter {
	private Hashtable<String, Hashtable<String, MemberStatistics>> perMemberStatistics;
	private String path;

	public TallyCounter() {
		this.perMemberStatistics = new Hashtable<String, Hashtable<String, MemberStatistics>>();
		this.path = "./usersStatistics.ser";
		Timer timeTimer = new Timer(true);
		TimerTask timeTask = new TimerTask() {
			@Override
			public void run() {
				updateTimeSpent();
			}
		};
		timeTimer.schedule(timeTask, 10000, 10000);
		TimerTask saveTask = new TimerTask() {
			@Override
			public void run() {
				updateTimeSpent();
			}
		};
		timeTimer.schedule(saveTask, 15000, 60000);
	}
	
	@SuppressWarnings("unchecked")
	public TallyCounter(String path) {
		this.perMemberStatistics = null;
		this.path = path;
		
		try {
			FileInputStream fileIn = new FileInputStream(this.path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			this.perMemberStatistics = (Hashtable<String, Hashtable<String, MemberStatistics>>) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			this.perMemberStatistics = new Hashtable<String, Hashtable<String, MemberStatistics>>();
		}
		
		Timer timeTimer = new Timer(true);
		TimerTask timeTask = new TimerTask() {
			@Override
			public void run() {
				updateTimeSpent();
			}
		};
		timeTimer.schedule(timeTask, 10000, 10000);
		TimerTask saveTask = new TimerTask() {
			@Override
			public void run() {
				saveToFile();
			}
		};
		timeTimer.schedule(saveTask, 15000, 60000);
	}
	
	protected void updateTimeSpent() {
		for(Hashtable<String, MemberStatistics> guildTable : this.perMemberStatistics.values()) {
			for(MemberStatistics ms : guildTable.values()) {
				if(ms.enLigne) {
					ms.tempsEnLigne = ms.tempsEnLigne.plus(Duration.between(ms.debutEnLigne, Instant.now()));
					ms.debutEnLigne = Instant.now();
				}
				if(ms.enVocal) {
					ms.tempsEnVocal = ms.tempsEnVocal.plus(Duration.between(ms.debutVocal, Instant.now()));
					ms.debutVocal = Instant.now();
				}
			}
		}
	}
	
	public boolean saveToFile() {
		try {
			FileOutputStream fileOut = new FileOutputStream(this.path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(this.perMemberStatistics);
			out.close();
			fileOut.close();
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Hashtable<String, Hashtable<String, MemberStatistics>> getCounters() {
		return this.perMemberStatistics;
	}

	public void statsInit(MottoBot bot) {
		for(Guild g:bot.getJda().getGuilds()) {
			String guildId = g.getId();
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
			for(Member m:g.getMembers()) {
				String userName = m.getUser().getName() + "#" + m.getUser().getDiscriminator();
				OnlineStatus status = m.getOnlineStatus();
				
				
				MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
				if(status==OnlineStatus.OFFLINE || status==OnlineStatus.INVISIBLE) {
					ms.debutEnLigne = null;
					ms.enLigne = false;
				}
				else {
					ms.debutEnLigne = Instant.now();
					ms.enLigne = true;
				}
				if(m.getVoiceState().inVoiceChannel()==false) {
					ms.debutVocal = null;
					ms.enVocal = false;
				}
				else {
					ms.debutVocal = Instant.now();
					ms.enVocal = true;
				}
				guildTable.putIfAbsent(userName, ms);
			}
			this.perMemberStatistics.putIfAbsent(guildId, guildTable);
		}
	}

	@Override
	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getUser().getName() + "#" + event.getUser().getDiscriminator();
		OnlineStatus status = event.getGuild().getMember(event.getUser()).getOnlineStatus();

		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
		if (event.getPreviousOnlineStatus()==OnlineStatus.OFFLINE || event.getPreviousOnlineStatus()==OnlineStatus.INVISIBLE) {
			ms.debutEnLigne = Instant.now();
			ms.enLigne = true;
		}
		else if(status==OnlineStatus.OFFLINE || status==OnlineStatus.INVISIBLE) {
			ms.debutEnLigne = null;
			ms.enLigne = false;
		}
		guildTable.putIfAbsent(userName, ms);
		this.perMemberStatistics.putIfAbsent(guildId, guildTable);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String guildId = event.getGuild().getId();
		if(event.getMember()==null || event.getMember().getUser()==null || event.getMember().getUser().getName()==null || event.getMember().getUser().getDiscriminator()==null) {
			return;
		}
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();
		
		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
		ms.messages++;
		guildTable.putIfAbsent(userName, ms);
		this.perMemberStatistics.putIfAbsent(guildId, guildTable);
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();
		OnlineStatus status = event.getMember().getOnlineStatus();
		
		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
		if(status!=OnlineStatus.OFFLINE && status!=OnlineStatus.INVISIBLE) {
			ms.debutEnLigne = Instant.now();
			ms.enLigne = true;
		}
		guildTable.putIfAbsent(userName, ms);
		this.perMemberStatistics.putIfAbsent(guildId, guildTable);
	}

	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();

		if(this.perMemberStatistics.containsKey(guildId)==true) {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			guildTable.remove(userName);
		}
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();

		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
		ms.debutVocal = Instant.now();
		ms.enVocal = true;
		guildTable.putIfAbsent(userName, ms);
		this.perMemberStatistics.putIfAbsent(guildId, guildTable);
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();

		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
		ms.debutVocal = null;
		ms.enVocal = false;
		guildTable.putIfAbsent(userName, ms);
		this.perMemberStatistics.putIfAbsent(guildId, guildTable);
	}

	public void onCommandUse(MessageReceivedEvent event, Commande commande) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();
		
		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		MemberStatistics ms = guildTable.getOrDefault(userName, new MemberStatistics());
		ms.commandes++;
		guildTable.putIfAbsent(userName, ms);
		this.perMemberStatistics.putIfAbsent(guildId, guildTable);
	}
}
