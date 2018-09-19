package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdReboot implements Commande {

	@Override
	public String getName() {
		return "mottoreboot";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mottorestart");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		if(bot.admins().contains(e.getAuthor().getId())) {
			try {
				ProcessBuilder pb = new ProcessBuilder("java","-jar","MottoBot.jar",bot.getToken(),"10");
				pb.inheritIO();
				pb.start();
				bot.setStop(true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else {
			System.out.println("Not authorized to ask for reboot !");
		}
	}
}