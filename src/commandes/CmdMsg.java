package commandes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.MottoBot;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdMsg implements Commande {
	private static final Pattern PATTERN = Pattern.compile("^\"([^\"]+)\" \"([^\"]+)\" (.*)", Pattern.CASE_INSENSITIVE); // Guilde Channel Message
	
	@Override
	public String getName() {
		return "mottomsg";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("msg");
		alias.add("mottoyell");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		if(e.getChannelType()==ChannelType.PRIVATE && bot.admins().contains(e.getAuthor().getId())) {
			String guildName;
			String channelName;
			String msg;
			
			Matcher matcherMsg = PATTERN.matcher(arguments);
	        if (matcherMsg.matches()) {
	        	guildName = matcherMsg.group(1);
	        	channelName = matcherMsg.group(2);
	        	msg = matcherMsg.group(3).isEmpty() ? "Salut" : matcherMsg.group(3);
	        	
				List<Guild> guilds = bot.getJda().getGuildsByName(guildName, true);
				if(!guilds.isEmpty())
				{
					Guild g = guilds.get(0);
					List<TextChannel> channels = g.getTextChannelsByName(channelName, true);
					if(!channels.isEmpty())
					{
						TextChannel c = channels.get(0);
						c.sendMessage(msg).queue();
					}
					else {
						e.getPrivateChannel().sendMessage("Canal inconnu").queue();
					}
				}
				else {
					e.getPrivateChannel().sendMessage("Guilde inconnue").queue();
				}
			}
	        else {
	        	e.getPrivateChannel().sendMessage("=mottomsg \"nomGuilde\" \"nomChannel\" message").queue();
	        }
		}
		else {
			System.out.println("Not authorized to send msg !");
		}
	}
}