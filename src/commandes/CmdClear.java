package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdClear implements Commande {

	@Override
	public String getName() {
		return "mottoclear";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mc");
		alias.add("mclear");
		alias.add("mottoc");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());
		
		List<Message> temp = new ArrayList<Message>();
		int nbMessageInitial = 0;
		while (bot.getMsgTab().size() != 0)
		{
			Message messageToDelete = bot.getMsgTab().get(bot.getMsgTab().size() - 1);
			if(messageToDelete.getChannel().equals(e.getMessage().getChannel()))
			{
				messageToDelete.delete().queue();
				nbMessageInitial++;
			}
			else
			{
				temp.add(messageToDelete);
			}
			bot.getMsgTab().remove(bot.getMsgTab().size() - 1); 
		}
		bot.setMsgTab(temp);
		
		if (nbMessageInitial > 1)
		{
			e.getChannel().sendMessage(nbMessageInitial + " messages effacés.").queue();
		}
	}

}
