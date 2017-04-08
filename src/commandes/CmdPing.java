package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPing implements Commande {

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		e.getChannel().sendTyping();
		e.getChannel().sendMessage("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va.").queue();
		return true;
	}

}
