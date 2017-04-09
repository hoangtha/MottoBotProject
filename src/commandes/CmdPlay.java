package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPlay implements Commande {

	@Override
	public String getName() {
		return "mottoplay";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		
		bot.getProperAudioManager().loadAndPlay(e.getTextChannel(), arguments, bot);
	}
}
