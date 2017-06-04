package commandes;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import main.MottoBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdWaterball implements Commande {
		
	public static final String WATERBALL_SELF = "https://puu.sh/wa1bE/69ea4adf37.gif";//
	public static final String WATERBALL_NULL = "https://puu.sh/wa1xD/7fa10c0381.gif" ; //
	public static final String WATERBALL_FAIL = "https://puu.sh/vQ3Ut/1f2dd70303.gif"; //
	public static final String WATERBALL_AVATAR = "https://puu.sh/w95zI/ac684165a8.gif" ; //
	public static final String WATERBALL_AVATAR2 = "https://puu.sh/w96NI/ca6e730406.gif" ; //
	public static final String WATERBALL_HIT = "https://puu.sh/w93Nv/af31208b6a.gif"; //
	public static final String WATERBALL_MERCURY = "https://puu.sh/w956x/9d0d157234.gif" ; //
	public static final String WATERBALL_TORTANK = "https://puu.sh/w95vf/363c2d336b.gif"; //
	public static final String WATERBALL_MASAMUNE = "https://puu.sh/w95KC/04848eaee2.gif" ;
	public static final String WATERBALL_JINBEI = "https://puu.sh/w96bm/9074f50658.gif" ; //
	public static final String WATERBALL_JUVIA = "https://puu.sh/w93Gz/62712ce14e.gif" ; //
	public static final String WATERBALL_OP = "https://puu.sh/w956j/15bdaaace6.gif"; //
	public static final String WATERBALL_REQUIEM = "https://puu.sh/w94gs/3158d439f0.gif" ; //
	public static final String WATERBALL_NOTHING = "https://puu.sh/wa3KU/f4c44c88f9.gif" ; //
 
	
	
	public static final Color Water  = new Color(0, 136, 204);
	private static Random rand = new Random();
	
	@Override
	public String getName() {
		return "waterball";
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
		//Member Toraniso = e.getGuild().getMemberById("123860660487454720"); // a utiliser plus tard, là j'ai pas envie
		
		if(arguments.startsWith("@"))
		{
			arguments = arguments.substring(1);
		}
		List<Member> targetList = e.getGuild().getMembersByEffectiveName(arguments, true);
		// --
		
		int random = CmdWaterball.rand.nextInt(46);
		
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Water);
		
		if(targetList.size()>=1)
		{
			target = targetList.get(0);
			caster = e.getMember();
		}
		
		if(target != null)
		{
			msg = "<@"+target.getUser().getId()+"> a pris "+random+" de dégats d' :droplet:";
			if(target.getEffectiveName().equals(caster.getEffectiveName()))
			{
				eb.setTitle(e.getAuthor().getName()+" is casting WATERBALL on himself...", null);
				eb.setImage(WATERBALL_SELF);
			}
			else
			{
				eb.setTitle(e.getMember().getEffectiveName()+" is casting WATERBALL on "+target.getEffectiveName()+"...", null);
				if (random == 0)
				{
					eb.setImage(WATERBALL_NULL);
					msg = "<@"+caster.getUser().getId()+"> fait une danse de l'eau inutile";
				}
				else if (random == 45)
				{
					eb.setImage(WATERBALL_REQUIEM);
					msg = "<@"+target.getUser().getId()+"> est purifié par l':droplet:";
				}
				else if (random < 5)
				{
					eb.setImage(WATERBALL_FAIL);
					msg = "<@"+caster.getUser().getId()+">, a noyé <@"+target.getUser().getId()+"> parce qu'il ne connait pas de sort, "+random+" de dommage";
				}
				else if (random < 8)
				{
					eb.setImage(WATERBALL_AVATAR);
				}
				else if (random < 10)
				{
					eb.setImage(WATERBALL_AVATAR2);
				}
				else if (random < 15)
				{
					eb.setImage(WATERBALL_HIT);
				}
				else if (random < 20)
				{
					eb.setImage(WATERBALL_MERCURY);
				}
				else if (random < 25)
				{
					eb.setImage(WATERBALL_TORTANK);
					msg = "<@"+target.getUser().getId()+"> a perdu "+random+" PV ";
				}
				else if (random < 30)
				{
					eb.setImage(WATERBALL_MASAMUNE);
				}
				else if (random < 35)
				{
					eb.setImage(WATERBALL_JINBEI);
				}
				else if(random > 40)
				{
					eb.setImage(WATERBALL_OP);
					msg = "<@"+caster.getUser().getId()+"> crache sur <@"+target.getUser().getId()+"> pour "+random+" dégats";
				}
				else
				{
					eb.setImage(WATERBALL_JUVIA);
					msg = "<@"+target.getUser().getId()+"> prend "+random+" dégats par le pouvoir de l'amour";
				}
			}
			eb.appendDescription(msg);
			e.getChannel().sendMessage(eb.build()).queue();
		}
		else
		{
			//msg = "Prend ça dans ta gueule <@"+Toraniso.getUser().getId()+">";
			eb.setImage(WATERBALL_NOTHING);
			//eb.appendDescription(msg);
			e.getChannel().sendMessage(eb.build()).queue();
		}
	}
}
