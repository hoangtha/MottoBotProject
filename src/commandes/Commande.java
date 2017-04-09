package commandes;

import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Commande {
    public String getName();
    public List<String> getAliases();
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments);
}
