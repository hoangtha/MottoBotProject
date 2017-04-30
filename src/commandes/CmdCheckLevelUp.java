package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdCheckLevelUp implements Commande {

	@Override
	public String getName() {
		return "checklevels";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("checklevelup");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		if(bot.admins().contains(e.getAuthor().getId())) {
			bot.getTallyCounter().checkLevelUpForEveryone();
		}
	}
}