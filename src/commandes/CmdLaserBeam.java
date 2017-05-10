package commandes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import main.MottoBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdLaserBeam implements Commande {
	
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
		Member caster = null;
		if(arguments.startsWith("@"))
		{
			arguments = arguments.substring(1);
		}
		List<Member> targetList = e.getGuild().getMembersByEffectiveName(arguments, true);
		List<Member> casterList = e.getGuild().getMembersByEffectiveName(e.getAuthor().getName(), true);
		// --
		
		
		File hey = null;
		if(targetList.size()>=1)
		{
			target = targetList.get(0);
			caster = casterList.get(0);
		}
		
		hey = new File("src/ressources/laserbeam.gif");
		if(arguments != "")
		{
			e.getChannel().sendMessage("*Nakarkos is casting Laser Beam on <@"+target.getUser().getId()+">...*").queue();
		}
		else
		{
			e.getChannel().sendMessage("*Nakarkos is casting Laser Beam on <@"+e.getAuthor().getId()+">...*").queue();
		}

		try {
			e.getChannel().sendFile(hey, null).queue();
			if(arguments != "")
			{
				e.getChannel().sendMessage("<@"+target.getUser().getId()+"> a pris 666 de points de dégats, ripped").queue();
			}
			else
			{
				e.getChannel().sendMessage("<@"+e.getAuthor().getId()+"> a pris 666 de points de dégats, ripped").queue();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
