package commandes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import main.MemberStatistics;
import main.MottoBot;
import main.TallyCounter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
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
		e.getChannel().sendTyping();
		String guildId = e.getGuild().getId();
		TallyCounter tc = bot.getTallyCounter();
		Hashtable<String, MemberStatistics> guildStats = tc.getCounters().getOrDefault(guildId, new Hashtable<String, MemberStatistics>());
		
		MessageBuilder mb = new MessageBuilder();
		mb.append("```");
		if(arguments==null || arguments.isEmpty()) {
			mb.append("Statistiques du serveur :\n\n");
			int messages = 0;
			int commandes = 0;
			Duration tempsEnLigne = Duration.ZERO;
			Duration tempsEnVocal = Duration.ZERO;
			for(MemberStatistics ms : guildStats.values()) {
				messages += ms.messages;
				commandes += ms.commandes;
				tempsEnLigne = tempsEnLigne.plus(ms.tempsEnLigne);
				tempsEnVocal = tempsEnVocal.plus(ms.tempsEnVocal);
			}
			mb.append("Messages postés : "+messages+"\n");
			mb.append("Commandes utilisées : "+commandes+"\n");
			mb.append("Temps passé en ligne par tout les membres : "+formatDuration(tempsEnLigne)+"\n");
			mb.append("Temps passé en vocal par tout les membres : "+formatDuration(tempsEnVocal)+"\n");
		}
		else {
			List<Member> targetList = e.getGuild().getMembersByEffectiveName(arguments, true);
			Member target = null;
			if(targetList.size()>=1) {
				target = targetList.get(0);
			}
			if(target!=null) {
				MemberStatistics ms = guildStats.getOrDefault(target.getUser().getName()+"#"+target.getUser().getDiscriminator(), new MemberStatistics());
				mb.append("Statistiques de "+target.getEffectiveName()+" :\n\n");
				mb.append("Messages postés : "+ms.messages+"\n");
				mb.append("Commandes utilisées : "+ms.commandes+"\n");
				mb.append("Temps passé en ligne : "+formatDuration(ms.tempsEnLigne)+"\n");
				mb.append("Temps passé en vocal : "+formatDuration(ms.tempsEnVocal)+"\n");
			}
			else {
				mb.append("Personne sur ce serveur ne porte ce nom\n");
			}
		}
		mb.append("```");
		Message m = mb.build();
		e.getChannel().sendMessage(m).queue();
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
