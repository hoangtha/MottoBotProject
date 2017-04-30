package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdUptime implements Commande {

	@Override
	public String getName() {
		return "mottouptime";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		e.getChannel().sendTyping().queue();
		e.getChannel().sendMessage("En ligne depuis " + bot.getUptime()).queue();
	}
}
