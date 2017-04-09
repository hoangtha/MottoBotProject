package commandes;

import java.util.ArrayList;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdNinja implements Commande {

	@Override
	public String getName() {
		return "mottoninja";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		e.getMessage().delete().queue();
		SelfUser me = bot.getJda().getSelfUser();
		MessageHistory mh = e.getChannel().getHistory();
		List<String> mbot = new ArrayList<String>();
		List<Message> past;
		
		past = mh.retrievePast(50).complete();
		while(past!=null && past.isEmpty()==false) {
			mbot.clear();
			
			for(Message m:mh.getRetrievedHistory()) {
				if(m.getAuthor().equals(me)) {
					mbot.add(m.getId());
				}
			}
			if(mbot.size()>=2) {
				e.getTextChannel().deleteMessagesByIds(mbot).queue();
				System.out.println("J'ai supprim� "+mbot.size()+" messages.");
			}
			else if(mbot.size()==1) {
				e.getTextChannel().deleteMessageById(mbot.get(0)).queue();
				System.out.println("J'ai supprim� 1 message.");
			}
			else {
				System.out.println("Je n'ai rien supprim�.");
			}
			
			past = mh.retrievePast(50).complete();
		}
	}
}