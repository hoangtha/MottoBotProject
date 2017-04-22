package commandes;

import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import main.MottoBot;
import main.TallyCounter;
import main.UserProgress;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdStats implements Commande {

	@Override
	public String getName() {
		return "mottostats";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mstats");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		boolean err = false;
		e.getChannel().sendTyping();
		String guildId = e.getGuild().getId();
		TallyCounter tc = bot.getTallyCounter();
		Hashtable<String, UserProgress> guildStats = tc.getProgress().getOrDefault(guildId, new Hashtable<String, UserProgress>());
		
		EmbedBuilder eb = new EmbedBuilder();
		if(arguments==null || arguments.isEmpty()) {
			eb.setColor(new Color(50,50,200));
			eb.setTitle("Statistiques du serveur", null);
			int messages = 0;
			int commandes = 0;
			Duration tempsEnLigne = Duration.ZERO;
			Duration tempsEnVocal = Duration.ZERO;
			for(UserProgress up : guildStats.values()) {
				messages += up.messages;
				commandes += up.commands;
				tempsEnLigne = tempsEnLigne.plus(up.timeSpentOnline);
				tempsEnVocal = tempsEnVocal.plus(up.timeSpentVocal);
			}
			eb.addField("Messages", messages+"" , true);
			eb.addField("Commandes", commandes+"", true);
			eb.addField("Temps passé en ligne par tout les membres", formatDuration(tempsEnLigne), true);
			eb.addField("Temps passé en vocal par tout les membres", formatDuration(tempsEnVocal), true);
		}
		else {
			List<Member> targetList = e.getGuild().getMembersByEffectiveName(arguments, true);
			Member target = null;
			if(targetList.size()>=1) {
				target = targetList.get(0);
			}
			if(target!=null) {
				String userId = target.getUser().getId();
				String name = target.getUser().getName();
				String discriminator = target.getUser().getDiscriminator();
				String userName = name + "#" + discriminator;
				
				UserProgress up = guildStats.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
				
				eb.setTitle("Statistiques de "+target.getEffectiveName(), null);
				eb.setThumbnail(target.getUser().getAvatarUrl());
				if(up.titleOn) {
					eb.setColor(up.titleColor);
					eb.appendDescription(up.title);
				}
				eb.addField("Niveau", up.level+" ("+up.experience+"/"+UserProgress.requiredXP(up.level+(up.prestige*100))+")", true);
				eb.addField("Prestige", up.prestige+"★", true);
				eb.addBlankField(false);
				eb.addField("Messages", up.messages+"", true);
				eb.addField("Commandes", up.commands+"", true);
				eb.addField("Temps passé en ligne", formatDuration(up.timeSpentOnline), false);
				eb.addField("Temps passé en vocal", formatDuration(up.timeSpentVocal), false);
			}
			else {
				err = true;
			}
		}
		if(err) {
			e.getChannel().sendMessage("Personne sur ce serveur ne porte ce nom.");
		}
		else {
			e.getChannel().sendMessage(eb.build()).queue();
		}
	}

	private static String formatDuration(Duration d) {
		String res;
		
		long jours = d.toDays();
		long heures = d.minusDays(jours).toHours();
		long minutes = d.minusDays(jours).minusHours(heures).toMinutes();
		long secondes = d.minusDays(jours).minusHours(heures).minusMinutes(minutes).getSeconds();
		
		res = jours + " jours, " + heures + " heures, " + minutes + " minutes et " + secondes + " secondes ";
		
		return res;
	}

}
