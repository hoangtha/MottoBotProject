package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdClearPlaylist implements Commande {

	@Override
	public String getName() {
		return "mottoclearplaylist";
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
		e.getChannel().sendMessage("Playlist clean ! :ok_hand:");
		return true;
	}

}
