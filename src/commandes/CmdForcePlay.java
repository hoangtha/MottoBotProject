package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdForcePlay implements Commande {

	@Override
	public String getName() {
		return "mottofplay";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		
		bot.getProperAudioManager().clearQueue(e.getTextChannel(), bot);
		bot.getProperAudioManager().loadAndPlay(e.getTextChannel(), arguments, bot);
		return true;
	}

}
