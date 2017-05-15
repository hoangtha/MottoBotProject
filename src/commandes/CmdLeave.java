package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdLeave implements Commande {

	@Override
	public String getName() {
		return "mottoleave";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mottol");
		alias.add("ml");
		alias.add("mleave");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		if(e.getMember().getVoiceState().inVoiceChannel())
		{
			bot.addMsg(e.getMessage());
		
			e.getChannel().sendMessage("byebye!").queue();
			bot.getProperAudioManager().clearQueue(e.getTextChannel(), bot);
			e.getGuild().getAudioManager().closeAudioConnection();
		}
		else
		{
			e.getChannel().sendMessage("<@"+e.getAuthor().getId()+"> : entre dans un channel vocal pour effectuer cette commande").queue();

		}
	}
}
