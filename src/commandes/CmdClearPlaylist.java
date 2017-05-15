package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdClearPlaylist implements Commande {

	@Override
	public String getName() {
		return "mottoclearplaylist";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mottocp");
		alias.add("mottocplaylist");
		alias.add("mottoclearp");
		alias.add("mcp");
		alias.add("mcplaylist");
		alias.add("mclearp");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		if(e.getMember().getVoiceState().inVoiceChannel())
		{
			bot.addMsg(e.getMessage());
			e.getChannel().sendTyping().queue();
			bot.getProperAudioManager().clearQueue(e.getTextChannel(), bot);
			e.getChannel().sendMessage(":musical_note: Playlist clean ! :ok_hand:").queue();
		}
		else
		{
			e.getChannel().sendMessage("<@"+e.getAuthor().getId()+"> : entre dans un channel vocal pour effectuer cette commande").queue();

		}
	}

}
