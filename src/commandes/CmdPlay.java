package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPlay implements Commande {

	@Override
	public String getName() {
		return "mottoplay";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
	
		bot.loadAndPlay(e.getTextChannel(), arguments);
		return true;
	}

}
