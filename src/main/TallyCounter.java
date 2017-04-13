package main;

import java.time.Duration;
import java.time.Instant;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import commandes.Commande;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TallyCounter extends ListenerAdapter {
	private Hashtable<String, Hashtable<String, MemberStatistics>> perMemberStatistics;

	public TallyCounter() {
		this.perMemberStatistics = new Hashtable<String, Hashtable<String, MemberStatistics>>();
		/*Timer timeTimer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				updateTimeSpent();
			}
		};
		timeTimer.schedule(task, 60000, 60000);*/
	}
	
	/*protected void updateTimeSpent() {
		for(Hashtable<String, MemberStatistics> ms : guildTable.values()) {
		Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
		for(MemberStatistics ms : guildTable.values()) {
			messages += ms.messages;
			commandes += ms.commandes;
			tempsEnLigne = tempsEnLigne.plus(ms.tempsEnLigne);
			tempsEnVocal = tempsEnVocal.plus(ms.tempsEnVocal);
		}
	}*/

	public Hashtable<String, Hashtable<String, MemberStatistics>> getCounters() {
		return this.perMemberStatistics;
	}

	@Override
	public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getUser().getName() + "#" + event.getUser().getDiscriminator();
		OnlineStatus status = event.getGuild().getMember(event.getUser()).getOnlineStatus();

		if(this.perMemberStatistics.containsKey(guildId)==false) {
			Hashtable<String, MemberStatistics> ht = new Hashtable<String, MemberStatistics>();
			MemberStatistics ms = new MemberStatistics();
			if (event.getPreviousOnlineStatus()==OnlineStatus.OFFLINE) {
				ms.debutEnLigne = Instant.now();
			}
			ht.put(userName, ms);
			this.perMemberStatistics.put(guildId, ht);
		}
		else {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			if(guildTable.containsKey(userName)==false) {
				MemberStatistics ms = new MemberStatistics();
				if (event.getPreviousOnlineStatus()==OnlineStatus.OFFLINE) {
					ms.debutEnLigne = Instant.now();
				}
				guildTable.put(userName, ms);
			}
			else {
				MemberStatistics ms = guildTable.get(userName);
				if(status==OnlineStatus.OFFLINE) {
					if(ms.debutEnLigne!=null) { 
						ms.tempsEnLigne = ms.tempsEnLigne.plus(Duration.between(ms.debutEnLigne, Instant.now()));
					}
					ms.debutEnLigne = null;
				}
				else if (event.getPreviousOnlineStatus()==OnlineStatus.OFFLINE) {
					ms.debutEnLigne = Instant.now();
				}
			}
		}
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();
		
		if(this.perMemberStatistics.containsKey(guildId)==false) {
			Hashtable<String, MemberStatistics> ht = new Hashtable<String, MemberStatistics>();
			MemberStatistics ms = new MemberStatistics();
			ms.messages++;
			ht.put(userName, ms);
			this.perMemberStatistics.put(guildId, ht);
		}
		else {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			if(guildTable.containsKey(userName)==false) {
				MemberStatistics ms = new MemberStatistics();
				ms.messages++;
				guildTable.put(userName, ms);
			}
			else {
				MemberStatistics ms = guildTable.get(userName);
				ms.messages++;
			}
		}
	}
	
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();
		OnlineStatus status = event.getMember().getOnlineStatus();

		if(this.perMemberStatistics.containsKey(guildId)==false) {
			Hashtable<String, MemberStatistics> ht = new Hashtable<String, MemberStatistics>();
			MemberStatistics ms = new MemberStatistics();
			if(status!=OnlineStatus.OFFLINE) {
				ms.debutEnLigne = Instant.now();
			}
			ht.put(userName, ms);
			this.perMemberStatistics.put(guildId, ht);
		}
		else {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			MemberStatistics ms = new MemberStatistics();
			if(status!=OnlineStatus.OFFLINE) {
				ms.debutEnLigne = Instant.now();
			}
			guildTable.put(userName, ms);
		}
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

		if(this.perMemberStatistics.containsKey(guildId)==false) {
			Hashtable<String, MemberStatistics> ht = new Hashtable<String, MemberStatistics>();
			MemberStatistics ms = new MemberStatistics();
			ms.debutVocal = Instant.now();
			ht.put(userName, ms);
			this.perMemberStatistics.put(guildId, ht);
		}
		else {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			if(guildTable.containsKey(userName)==false) {
				MemberStatistics ms = new MemberStatistics();
				ms.debutVocal = Instant.now();
				guildTable.put(userName, ms);
			}
			else {
				MemberStatistics ms = guildTable.get(userName);
				ms.debutVocal = Instant.now();
			}
		}
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();

		if(this.perMemberStatistics.containsKey(guildId)==false) {
			Hashtable<String, MemberStatistics> ht = new Hashtable<String, MemberStatistics>();
			MemberStatistics ms = new MemberStatistics();
			ht.put(userName, ms);
			this.perMemberStatistics.put(guildId, ht);
		}
		else {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			if(guildTable.containsKey(userName)==false) {
				MemberStatistics ms = new MemberStatistics();
				guildTable.put(userName, ms);
			}
			else {
				MemberStatistics ms = guildTable.get(userName);
				if(ms.debutVocal!=null) {
					ms.tempsEnVocal = ms.tempsEnVocal.plus(Duration.between(ms.debutVocal, Instant.now()));
					ms.debutVocal = null;
				}
			}
		}
	}

	@Override
	public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
		// TODO: Similaire à 'onUserOnlineStatusUpdate'
	}

	public void onCommandUse(MessageReceivedEvent event, Commande commande) {
		String guildId = event.getGuild().getId();
		String userName = event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator();
		
		if(this.perMemberStatistics.containsKey(guildId)==false) {
			Hashtable<String, MemberStatistics> ht = new Hashtable<String, MemberStatistics>();
			MemberStatistics ms = new MemberStatistics();
			ms.commandes++;
			ht.put(userName, ms);
			this.perMemberStatistics.put(guildId, ht);
		}
		else {
			Hashtable<String, MemberStatistics> guildTable = this.perMemberStatistics.get(guildId);
			if(guildTable.containsKey(userName)==false) {
				MemberStatistics ms = new MemberStatistics();
				ms.commandes++;
				guildTable.put(userName, ms);
			}
			else {
				MemberStatistics ms = guildTable.get(userName);
				ms.commandes++;
			}
		}
	}
}
