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
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		e.getChannel().sendTyping();
		e.getChannel()
				.sendMessage(
						"```=motto [tag]: Affiche un lien vers l'image voulue\n"
						+ "=mottoclear : Nettoie un peu\n"
						+ "=mottohelp Affiche les commandes disponible\n="
						+ "mottovoice : Rejoint le channel vocal Taverne\n"
						+ "=mottoleave : Quitte le channel vocal```")
				.queue();
		return true;
	}

}
