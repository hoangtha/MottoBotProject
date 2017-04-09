package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdHelp implements Commande {

	@Override
	public String getName() {
		return "mottohelp";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		e.getChannel().sendTyping();
		e.getChannel()
				.sendMessage(
						"```Liste des commandes :\n"
						+ "=motto [tag]:     Affiche un lien vers l'image voulue\n"
						+ "=mottoclear :      Nettoie un peu\n"
						+ "=mottohelp :       Affiche les commandes disponible\n\n"
						+ "PLAYER : \n"
						+ "=mottoplay (url) : Rejoint le channel vocal Taverne et lance la musique si possible\n"
						+ "=mottoleave :      Quitte le channel vocal et vide la playlist\n"
						+ "=mottoskip :       Passe à la prochaine musique dans la playlist\n"
						+ "=mottofplay (url): Force le lancement de la musique\n\n\n"
						+ "Liste des sources pour le player : YouTube, SoundCloud, Bandcamp, Vimeo, Twitch stream, HTTP URLs, (local)```")
				.queue();
	}

}
