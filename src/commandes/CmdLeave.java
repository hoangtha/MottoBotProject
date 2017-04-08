package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class CmdLeave implements Commande {

	@Override
	public String getName() {
		return "mottoleave";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		e.getChannel().sendMessage("byebye!").queue();
		AudioManager manager = bot.getJda().getGuilds().get(0).getAudioManager();
		manager.closeAudioConnection();
		return true;
	}

}
