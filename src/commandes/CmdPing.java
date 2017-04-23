package commandes;

import java.time.temporal.ChronoUnit;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdPing implements Commande {

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		e.getChannel().sendTyping().queue();
		e.getChannel().sendMessage("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va.").queue(m -> {
            m.editMessage("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va. ("+e.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS)+"ms)").queue();
        });
	}
}
