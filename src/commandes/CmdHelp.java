package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
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
		MessageBuilder mb = new MessageBuilder();
		mb.append("```");
		mb.append("Liste des commandes :\n");
		mb.append("=motto [tag]:      Affiche un lien vers l'image voulue\n");
		mb.append("=mottoclear :      Nettoie un peu\n");
		mb.append("=mottohelp :       Affiche les commandes disponible\n");
		mb.append("\nPlayer :\n");
		mb.append("=mottoplay (url) : Rejoint le channel vocal Taverne et lance la musique si possible\n");
		mb.append("=mottoplaylist :   Affiche les premiers éléments de la playlist\n");
		mb.append("=mottoleave :      Quitte le channel vocal et vide la playlist\n");
		mb.append("=mottoskip :       Passe à la prochaine musique dans la playlist\n");
		mb.append("=mottofplay (url): Force le lancement de la musique\n");
		mb.append("\nListe des sources pour le player : YouTube, SoundCloud, Bandcamp, Vimeo, Twitch stream, HTTP URLs, (local)");
		mb.append("```");
		Message m = mb.build();
		e.getChannel().sendMessage(m).queue();
	}

}
