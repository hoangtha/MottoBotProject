package commandes;

import java.util.ArrayList;
//import java.util.Hashtable;
import java.util.List;
//import java.util.function.Consumer;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

//import java.awt.Color;
import main.MottoBot;
//import main.TallyCounter;
//import main.UserProgress;
//import net.dv8tion.jda.core.entities.ChannelType;
//import net.dv8tion.jda.core.entities.Guild;
//import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.core.managers.GuildController;
//import net.dv8tion.jda.core.requests.restaction.order.RoleOrderAction;

public class CmdRank implements Commande {
	//private static final Pattern PATTERN = Pattern.compile("^[^\\s]+ \"(.+)\" (#([0123456789ABCDEF]{1}[0123456789ABCDEF]{1}[0123456789ABCDEF]{1}$|[0123456789ABCDEF]{1}[0123456789ABCDEF]{1}[0123456789ABCDEF]{1}[0123456789ABCDEF]{1}$|[0123456789ABCDEF]{2}[0123456789ABCDEF]{2}[0123456789ABCDEF]{2}$|[0123456789ABCDEF]{2}[0123456789ABCDEF]{2}[0123456789ABCDEF]{2}[0123456789ABCDEF]{2}$))", Pattern.CASE_INSENSITIVE); // option "titre" #couleur
	
	@Override
	public String getName() {
		return "mottorank";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("mrank");
		return alias;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		/*if(e.getChannelType()!=ChannelType.PRIVATE) {
			TallyCounter tc = bot.getTallyCounter();
			String guildId = e.getGuild().getId();
			String userId = e.getAuthor().getId();
			String name = e.getAuthor().getName();
			String discriminator = e.getAuthor().getDiscriminator();
			String userName = name + "#" + discriminator;
			
			if(arguments!=null && arguments.isEmpty()==false) {
				String firstWord = arguments.split("\\s+")[0];
				Hashtable<String, UserProgress> guildStats = tc.getProgress().getOrDefault(guildId, new Hashtable<String, UserProgress>());
				UserProgress up = guildStats.getOrDefault(userName, new UserProgress(guildId, userId, name, discriminator));
				Guild guild = bot.getJda().getGuildById(guildId);
				GuildController guildController = guild.getController();
				
				Matcher matcher = PATTERN.matcher(arguments);
				switch(firstWord) {
					case "set":
						if(up.canRequestTitleChange) {
							if(matcher.matches()) {
								if(matcher.group(1).length()>24) {
									e.getChannel().sendMessage("Titre trop long, 24 caractères max.").queue();
									return;
								}
								String title = matcher.group(1);
								Role existe = null;
								for(Role r : guild.getRoles()) {
									if (r.getName().toLowerCase().equals(title.toLowerCase())) {
										existe = r;
									}
								}
								if(existe!=null)
								{
									e.getChannel().sendMessage("Ce titre existe déjà, trouves en un autre.").queue();
									return;
								}
								try {
									javafx.scene.paint.Color.web(matcher.group(2));
								}
								catch (IllegalArgumentException | NullPointerException ex) {
									e.getChannel().sendMessage("Couleur invalide").queue();
									return;
								}
								javafx.scene.paint.Color cjfx = javafx.scene.paint.Color.web(matcher.group(2));
								Color c = new Color((float)cjfx.getRed(), (float)cjfx.getGreen(), (float)cjfx.getBlue(), (float)cjfx.getOpacity());
								guildController.createRole().setColor(c).setName(title).setMentionable(true).queue(new Consumer<Role>() {

									@Override
									public void accept(Role t) {
										RoleOrderAction roa = guildController.modifyRolePositions().selectPosition(t);
										System.out.println("Position actuelle: "+roa.getSelectedPosition());
										System.out.println("Position bot: "+guild.getSelfMember().getRoles().get(0).getPosition());
										System.out.println("Position visée: "+(guild.getSelfMember().getRoles().get(0).getPosition()-1));
										roa.moveTo(guild.getMemberById(up.userId).getRoles().get(0).getPosition()).queue();
										if(up.title!=null) {
											Role d = null;
											for(Role r : guild.getRoles()) {
												if (r.getName().toLowerCase().equals(up.title.toLowerCase())) {
													d = r;
												}
											}
											if (d!=null) {
												d.delete().queue();
											}
											if(d==null) {
												System.out.println("Je devais supprimer un rang mais je l'ai pas trouvé");
											}
										}
										up.title = title;
										up.titleColor = c;
										up.titleOn = true;
										up.canRequestTitleChange = false;
										guildController.addRolesToMember(e.getMember(), t).queue();
									}
								});
							}
							else {
								e.getChannel().sendMessage("Pour changer de titre, utilisez =mottorank set \"Votre Rang\" #COULEURHEXA\n\tExemple: =mottorank set \"Gros débile\" #AA6622\nPour activer ou désactiver votre rang personnalisé, utiliser =mottorank on/off").queue();
							}
						}
						else {
							e.getChannel().sendMessage("Vous n'avez pas la possibilité de changer de titre pour l'instant, attendez votre prochain prestige !").queue();
						}
						break;
					case "on":
						if(up.title!=null) {
							Role t = null;
							for(Role r : guild.getRoles()) {
								if (r.getName().toLowerCase().equals(up.title.toLowerCase())) {
									t = r;
								}
							}
							if (t!=null) {
								up.titleOn = true;
								guildController.addRolesToMember(e.getMember(), t).queue();
							}
							else {
								e.getChannel().sendMessage("Pas trouvé D:").queue();
							}
						}
						else {
							e.getChannel().sendMessage("T'as pas de titre, sac.").queue();
						}
						break;
					case "off":
						if(up.title!=null) {
							Role t = null;
							for(Role r : guild.getRoles()) {
								if (r.getName().toLowerCase().equals(up.title.toLowerCase())) {
									t = r;
								}
							}
							if (t!=null) {
								up.titleOn = false;
								guildController.removeRolesFromMember(e.getMember(), t).queue();
							}
							else {
								e.getChannel().sendMessage("Pas trouvé D:").queue();
							}
						}
						else {
							e.getChannel().sendMessage("T'as pas de titre, sac.").queue();
						}
						break;
					default:
						e.getChannel().sendMessage("Pour changer de titre, utilisez =mottorank set \"Votre Rang\" #COULEURHEXA\n\tExemple: =mottorank set \"Gros débile\" #AA6622\nPour activer ou désactiver votre rang personnalisé, utiliser =mottorank on/off").queue();
						break;
				}
			}
	        else {
	        	e.getChannel().sendMessage("Pour changer de titre, utilisez =mottorank set \"Votre Rang\" #COULEURHEXA\n\tExemple: =mottorank set \"Gros débile\" #AA6622\nPour activer ou désactiver votre rang personnalisé, utiliser =mottorank on/off").queue();
	        }
		}*/
		e.getChannel().sendMessage("Pas pour l'instant. :/");
	}
}