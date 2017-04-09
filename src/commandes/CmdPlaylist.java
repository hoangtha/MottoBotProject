package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPlaylist implements Commande {

	@Override
	public String getName() {
		return "mottoplaylist";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		int index = arguments.equals("") ? 1 : Integer.parseInt(arguments);
		e.getChannel().sendMessage(bot.getProperAudioManager().showPlaylist(e.getTextChannel(), bot, index)).queue();
	}
}
