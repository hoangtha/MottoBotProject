package commandes;

import java.awt.Color;
import java.util.List;


import main.MottoBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdFinalFlash implements Commande {
	
	public static final String FINALFLASH = "https://puu.sh/vWsxH/0c39eaf499.gif" ;
	public static final String FINALFAIL = "https://puu.sh/vWQaM/b3a72c682c.gif";
	public static final String FINALGIN = "https://puu.sh/vWQoz/698f0fc128.gif";
	public static final Color FLASH = new Color(255, 250, 10);

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
		EmbedBuilder gif = new EmbedBuilder();
		gif.setColor(FLASH);
		
		if(e.getAuthor().getId().equals("232603027641466882"))
		{
			gif.setImage(FINALFLASH);
			gif.appendDescription("Pour <@"+Momojean.getUser().getId()+"> FINAAAL FLAAAAAAAAAAASH!");
			e.getChannel().sendMessage(gif.build()).queue();
		}		
		else if (e.getAuthor().getId().equals("259789587432341506"))
		{
			gif.setImage(FINALGIN);
			gif.appendDescription("KA ME HA ME.....HA?");
			e.getChannel().sendMessage(gif.build()).queue();
		}
		else
		{
			gif.setImage(FINALFAIL);
			gif.appendDescription("Oula, tu crois faire koi toi?!");
			e.getChannel().sendMessage(gif.build()).queue();
			return;
		}	
	}
}
