package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdSkip implements Commande {

	@Override
	public String getName() {
		return "mottoskip";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		int nbOfSkips = arguments.equals("") ? 1 : Integer.parseInt(arguments) ;
		bot.getProperAudioManager().skipTrack(e.getTextChannel(), bot, nbOfSkips);
	}
}
