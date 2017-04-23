package commandes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import main.MottoBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdFireball implements Commande {
	private static Random rand = new Random();
	
	@Override
	public String getName() {
		return "fireball";
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
		if(targetList.size()>=1) {
			target = targetList.get(0);
			caster = casterList.get(0);
		}	
		if(target != null && target.getEffectiveName().equals(caster.getEffectiveName()))
		{
			e.getChannel().sendMessage("*<@"+e.getAuthor().getId()+"> is casting Fireball on himself...*").queue();
			hey = new File("src/ressources/fireball_self.gif");
		}
		else
		{
			e.getChannel().sendMessage("*<@"+e.getAuthor().getId()+"> is casting Fireball on <@"+target.getUser().getId()+">...*").queue();
			hey = new File("src/ressources/fireball_hit.gif");
		}
		try {
			int random = CmdFireball.rand.nextInt(6);
			e.getChannel().sendFile(hey, null).queue();
			e.getChannel().sendMessage("<@"+target.getUser().getId()+"> a pris "+random+" de points de dégats de :fire:").queue();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
