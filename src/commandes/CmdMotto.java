package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import main.MottoThread;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdMotto implements Commande {	
	public ArrayList<String> robinArmy;
	
	public CmdMotto() {
		this.robinArmy = new ArrayList<String>();
		this.robinArmy.add("269163044427268096"); // Freaking Potatoes
	}
	
	@Override
	public String getName() {
		return "motto";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		(new Thread(new MottoThread(e, arguments, this.robinArmy))).start();
		bot.addMsg(e.getMessage());
	}
}
