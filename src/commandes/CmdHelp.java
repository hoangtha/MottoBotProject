package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdHelp implements Commande {

	@Override
	public String getName() {
		return "mottohelp";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String[] arguments) {
		bot.addMsg(e.getMessage());
		e.getChannel().sendTyping();
		e.getChannel()
				.sendMessage(
						"```=motto [tag]: ??(default tag : nico_robin) \n=mottoclear : Supprime tout les motto de tout les channels (X)\n=mottohelp Affiche les commandes disponible```")
				.queue();
		return true;
	}

}
