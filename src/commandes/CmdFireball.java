package commandes;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import main.MottoBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdFireball implements Commande {
	
	public static final String FIREBALL_HIT = "http://puu.sh/vPfy8/4d48d518dc.gif";
	public static final String FIREBALL_FAIL = "http://puu.sh/vPfud/a8e46f0c89.gif";
	public static final String FIREBALL_OP = "http://puu.sh/vPfpD/9acf1292da.gif";
	public static final String FIREBALL_SELF = "http://puu.sh/vPfzS/ee260de294.gif";
	public static final String FIREBALL_HS = "https://puu.sh/vPZmI/d065aa31ac.gif";
	public static final String FIREBALL_FEUNARD = "https://puu.sh/vQ0wr/446ab8e248.gif";
	public static final Color FIRE = new Color(227, 140, 45);
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
		// --
		
		int random = CmdFireball.rand.nextInt(40);
		
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(FIRE);
		
		
		if(targetList.size()>=1) {
			target = targetList.get(0);
			caster = e.getMember();
		}
		if(target != null && target.getEffectiveName().equals(caster.getEffectiveName()))
		{
			eb.setTitle("*"+e.getAuthor().getName()+" is casting Fireball on himself...*", null);
			eb.setImage(FIREBALL_SELF);
		}
		
		else
		{
			eb.setTitle("*"+e.getMember().getEffectiveName()+" is casting Fireball on "+target.getEffectiveName()+"...*", null);
			if(random >35)
			{
				eb.setImage(FIREBALL_OP);
			}
			// la bonne blague
			else if (random == 6)
			{
				eb.setImage(FIREBALL_HS);
			}
			else if (random < 5)
			{
				eb.setImage(FIREBALL_FAIL);
			}
			else
			{
				eb.setImage(FIREBALL_HIT);
			}
		}
		eb.appendDescription("<@"+target.getUser().getId()+"> a pris "+random+" de points de dégats de :fire:");
		e.getChannel().sendMessage(eb.build()).queue();

	}

}
