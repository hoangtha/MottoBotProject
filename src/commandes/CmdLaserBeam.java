package commandes;

import java.awt.Color;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdLaserBeam implements Commande {
	
	public static final String LASERBEAM = "http://puu.sh/vPfLD/946b739026.gif";
	public static final Color LASER = new Color(255, 255, 102);
	
	@Override
	public String getName() {
		return "laserbeam";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		e.getChannel().sendTyping().queue();
		e.getMessage().delete().queue();
		e.getAuthor();
		
		//cette partie la sert à géré un systeme de points de vie TODO
		Member target = null;
		if(arguments.startsWith("@"))
		{
			arguments = arguments.substring(1);
		}
		List<Member> targetList = e.getGuild().getMembersByEffectiveName(arguments, true);
		// --
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(LASER);
		
		if(targetList.size()>=1)
		{
			target = targetList.get(0);
		}
		
		if(arguments != "")
		{
			eb.setTitle("*Nakarkos is casting Laser Beam on <@"+target.getUser().getId()+">...*", null);
		}
		else
		{
			eb.setTitle("*Nakarkos is casting Laser Beam on <@"+e.getAuthor().getId()+">...*", null);
		}

		eb.setImage(LASERBEAM);
		if(arguments != "")
		{
			eb.appendDescription("<@"+target.getUser().getId()+"> a pris 666 de points de dégats, ripped");
		}
		else
		{
			eb.appendDescription("<@"+e.getAuthor().getId()+"> a pris 666 de points de dégats, ripped");
		}
		e.getChannel().sendMessage(eb.build()).queue();

	}

}
