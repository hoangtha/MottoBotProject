package commandes;

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
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		
		e.getChannel().sendMessage("byebye!").queue();
		bot.getProperAudioManager().clearQueue(e.getTextChannel(), bot);
		e.getGuild().getAudioManager().closeAudioConnection();
	}
}
