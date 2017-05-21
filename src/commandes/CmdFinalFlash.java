package commandes;

import java.awt.Color;
import java.util.List;


import main.MottoBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdFinalFlash implements Commande {
	
	public static final String FINALFLASH = "https://puu.sh/vWsxH/0c39eaf499.gif" ;
	public static final Color FLASH = new Color(227, 140, 45);

	@Override
	public String getName() {
		return "finalflash";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		e.getChannel().sendTyping().queue();
		e.getMessage().delete().queue();
		
		//ID Momojean <@262610896545644554>
		//ID Ademage <@232603027641466882>
		Member Momojean = e.getGuild().getMemberById("262610896545644554");
		
		
		if(e.getAuthor().getId().equals("232603027641466882"))
		{
			EmbedBuilder gif = new EmbedBuilder();
			gif.setColor(FLASH);
			gif.setImage(FINALFLASH);
			gif.appendDescription("Pour "+Momojean.getEffectiveName()+" FINAAAL FLAAAAAAAAAAASH!");
			e.getChannel().sendMessage(gif.build()).queue();
		}
		
		else 
		{
			e.getChannel().sendMessage("nope, tu ne sais pas faire ça toi").queue();
			return;
		}

		
	}

}
