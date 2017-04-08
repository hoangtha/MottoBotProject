package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

public class CmdVoice implements Commande {

	@Override
	public String getName() {
		return "mottovoice";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		AudioManager manager = bot.getJda().getGuilds().get(0).getAudioManager();
		e.getChannel().sendMessage("J'arrive dans la taverne").queue();
		manager.openAudioConnection(e.getAuthor().getJDA().getVoiceChannelByName("Taverne", true).get(0));
		return true;
	}

}
