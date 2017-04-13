package commandes;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import main.MemberStatistics;
import main.MottoBot;
import main.TallyCounter;
import net.dv8tion.jda.core.MessageBuilder;
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
			mb.append("Statistiques du serveur :\n");
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
			mb.append("Temps passé en ligne par tout les membres : "+tempsEnLigne+"\n");
			mb.append("Temps passé en vocal par tout les membres : "+tempsEnVocal+"\n");
		}
		else {
			MemberStatistics ms = guildStats.getOrDefault(e.getAuthor().getName()+"#"+e.getAuthor().getDiscriminator(), new MemberStatistics());
			mb.append("Statistiques de "+e.getMember().getEffectiveName()+" :\n");
			mb.append("Messages postés : "+ms.messages+"\n");
			mb.append("Commandes utilisées : "+ms.commandes+"\n");
			mb.append("Temps passé en ligne par tout les membres : "+ms.tempsEnLigne+"\n");
			mb.append("Temps passé en vocal par tout les membres : "+ms.tempsEnVocal+"\n");
		}
		mb.append("```");
		Message m = mb.build();
		e.getChannel().sendMessage(m).queue();
	}

}
