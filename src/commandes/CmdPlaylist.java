package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPlaylist implements Commande {

	@Override
	public String getName() {
		return "mottoplaylist";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		// TODO: Afficher la playlist.
		bot.getProperAudioManager().loadAndPlay(e.getTextChannel(), arguments, bot);
		return true;
	}
	
	

}
