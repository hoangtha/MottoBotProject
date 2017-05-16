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
	public static final String FIREBALL_NULL = "https://puu.sh/vQ36t/5342a89e48.gif" ;
	public static final String FIREBALL_MADARA = "https://puu.sh/vQQ6S/ab9e979b4a.gif" ;
	public static final String FIREBALL_ACE = "https://puu.sh/vQQMb/2f84fbd386.gif" ;
	public static final String FIREBALL_FREEZER = "https://puu.sh/vQRMm/9917d3c76a.gif" ;
	public static final String FIREBALL_ROY = "https://puu.sh/vQT7G/2c1b091765.gif" ;
	public static final String FIREBALL_AVATAR = "https://puu.sh/vQU4s/d85c14c40f.gif" ;
	public static final String FIREBALL_CAT = "https://puu.sh/vRWji/1cc9e0d667.gif" ;
	
	
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
		String msg = "";
		Member Toraniso = e.getGuild().getMemberById("123860660487454720"); // ID Toraniso <@!123860660487454720> un point d'exclamation?
		
		if(arguments.startsWith("@"))
		{
			arguments = arguments.substring(1);
		}
		List<Member> targetList = e.getGuild().getMembersByEffectiveName(arguments, true);
		// --
		
		int random = CmdFireball.rand.nextInt(41);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(FIRE);
		
		if(targetList.size()>=1)
		{
			target = targetList.get(0);
			caster = e.getMember();
		}
		
		if(target != null)
		{
			msg = "<@"+target.getUser().getId()+"> a pris "+random+" de points de degats de :fire:";
			if(target.getEffectiveName().equals(caster.getEffectiveName()))
			{
				eb.setTitle("*"+e.getAuthor().getName()+" is casting Fireball on himself...*", null);
				eb.setImage(FIREBALL_SELF);
			}
			else
			{
				eb.setTitle("*"+e.getMember().getEffectiveName()+" is casting Fireball on "+target.getEffectiveName()+"...*", null);
				if (random == 0)
				{
					eb.setImage(FIREBALL_NULL);
					msg = "<@"+caster.getEffectiveName()+"> est à court de Magie et fait "+random+" dégats";
				}
				// la bonne blague
				else if (random == 6)
				{
					eb.setImage(FIREBALL_HS);
					msg = "<@"+target.getUser().getId()+"> a pris "+random+" de dégats des coussins de Jaina";
				}
				else if (random == 40)
				{
					eb.setImage(FIREBALL_FREEZER);
					msg = "<@"+target.getUser().getId()+"> est mort par le :fire:, RIP in peace";
				}
				else if (random < 5)
				{
					eb.setImage(FIREBALL_FAIL);
					msg = "<@"+caster.getEffectiveName()+">, le sous-mage, a seulement infligé "+random+" dégats de feu à <@"+target.getUser().getId()+">";
				}
				else if (random < 10)
				{
					eb.setImage(FIREBALL_AVATAR);
				}
				else if (random < 15)
				{
					eb.setImage(FIREBALL_FEUNARD);
					msg = "<@"+target.getUser().getId()+"> a perdu "+random+" PV ";
				}
				else if (random < 20)
				{
					eb.setImage(FIREBALL_MADARA);
				}
				else if (random < 25)
				{
					eb.setImage(FIREBALL_ROY);
				}
				else if (random < 30)
				{
					eb.setImage(FIREBALL_ACE);
				}
				else if(random > 35)
				{
					eb.setImage(FIREBALL_OP);
					msg = "EXPLOSIOOOOOOOOOOOOOOON!";
				}
				else
				{
					eb.setImage(FIREBALL_HIT);
				}
			}
			eb.appendDescription(msg);
			e.getChannel().sendMessage(eb.build()).queue();
		}
		else
		{
			msg = "Prend ça dans ta gueule <@"+Toraniso.getUser().getId()+">";
			eb.setImage(FIREBALL_CAT);
			eb.appendDescription(msg);
			e.getChannel().sendMessage(eb.build()).queue();
		}
	}
}
