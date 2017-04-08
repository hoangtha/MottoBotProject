package commandes;

import java.util.List;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Commande {
    String getName();
    List<String> getAliases();
	public boolean run(Main bot, MessageReceivedEvent e, String[] arguments);
}
