package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdShufflePlaylist implements Commande {

	@Override
	public String getName() {
		return "mottoshuffleplaylist";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mottoshuffle");
		alias.add("msp");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		
		e.getChannel().sendTyping().queue();
		bot.getProperAudioManager().shufflePlaylist(e.getTextChannel(), bot);
		e.getChannel().sendMessage(bot.getProperAudioManager().showPlaylist(e.getTextChannel(), bot, 1)).queue();
		
	}

}


