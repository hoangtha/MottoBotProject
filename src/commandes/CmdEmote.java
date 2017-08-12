package commandes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import main.MottoBot;
import main.MottoEmote;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdEmote implements Commande {
	private static final Pattern PATTERN = Pattern.compile("^([^\\s]+)[\\s]*(.*)", Pattern.CASE_INSENSITIVE);
	
	private Hashtable<String, ArrayList<MottoEmote>> emotes;
	
	public CmdEmote() {
		this.emotes = new Hashtable<String, ArrayList<MottoEmote>>();
		try (Stream<Path> paths = Files.walk(Paths.get("emotes"))) {
		    paths.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().startsWith("emotes_")).forEach(path -> this.loadGuildEmotes(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IMPOSSIBLE D'INITIALISER LES EMOTES ! D:");
		}
	}
	
	private void loadGuildEmotes(Path path) {
		String gID = path.getFileName().toString().replaceAll("emotes_", "").replaceAll(".txt", "");
		
		ArrayList<MottoEmote> gEmotes = new ArrayList<MottoEmote>();

		try {
			List<String> lines = Files.readAllLines(path);
			for(String line : lines) {
				MottoEmote e = new MottoEmote(line);
				if(e.name!=null) {
					gEmotes.add(e);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		this.emotes.putIfAbsent(gID, gEmotes);
	}

	@Override
	public String getName() {
		return "emote";
	}

	@Override
	public List<String> getAliases() {
		List<String> alias = new ArrayList<String>();
		alias.add("e");
		alias.add("emo");
		alias.add("emoji");
		return alias;
	}

	@Override
	public synchronized void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		if(e.getChannelType()==ChannelType.TEXT) {
			String request;
			String params;
			String url;
			String name;
			String result = null;
			Matcher matcherRequest = PATTERN.matcher(arguments);
	        if (matcherRequest.matches()) {
	        	request = matcherRequest.group(1);
	        	params = matcherRequest.group(2).isEmpty() ? null : matcherRequest.group(2);
				switch(request) {
					case "add":
						if(params!=null) {
							Matcher matcherParams = PATTERN.matcher(params);
							if (matcherParams.matches()) {
								name = matcherParams.group(1);
					        	url = matcherParams.group(2).isEmpty() ? null : matcherParams.group(2);
								if(url==null)
									result = "Vous devez preciser un nom et une url pour l'emote. \n Ex: =emote add wink hxxps://media.giphy.com/media/7FTH5DhX8IHbG/giphy.gif";
								else
									result = this.addEmote(e.getGuild(), e.getMember(), name, url);
							}
							else {
								result = "Vous devez preciser un nom et une url pour l'emote. \n Ex: =emote add wink hxxps://media.giphy.com/media/7FTH5DhX8IHbG/giphy.gif";
							}
						}
						else {
							result = "Vous devez preciser un nom et une url pour l'emote. \n Ex: =emote add wink hxxps://media.giphy.com/media/7FTH5DhX8IHbG/giphy.gif";
						}
						break;
					case "remove":
						if(params!=null) {
							Matcher matcherParams = PATTERN.matcher(params);
							if (matcherParams.matches()) {
								name = matcherParams.group(1);
								result = this.removeEmote(e.getGuild(), e.getMember(), name);
							}
							else {
								result = "Vous devez preciser le nom de l'emote à supprimer. \n Ex: =emote remove wink";
							}
						}
						else {
							result = "Vous devez preciser le nom de l'emote à supprimer. \n Ex: =emote remove wink";
						}
						break;
					case "list":
						if(params!=null) {
							Matcher matcherParams = PATTERN.matcher(params);
							if (matcherParams.matches()) {
								name = matcherParams.group(1);
								result = this.searchEmotes(e.getGuild(), name);
							}
							else {
								result = this.listEmotes(e.getGuild());
							}
						}
						else {
							result = this.listEmotes(e.getGuild());
						}
						break;
					case "help":
						result = getHelp();
						break;
					default:
						ArrayList<MottoEmote> list = this.emotes.getOrDefault(e.getGuild().getId(), new ArrayList<MottoEmote>());
						MottoEmote emote = new MottoEmote(request, e.getAuthor().getId());
						if(list.contains(emote)) {
							EmbedBuilder eb = new EmbedBuilder();
							eb.setImage(list.get(list.indexOf(emote)).url);
							e.getChannel().sendMessage(eb.build()).queue();
						}
						break;
				}
			}
	        else {
	        	result = "Pas comme ça ! =emote help";
	        }
	        if(result!=null) {
	        	e.getChannel().sendMessage(result).queue();
	        }
		}
	}

	private synchronized String listEmotes(Guild guild) {
		String gID = guild.getId();
		ArrayList<MottoEmote> list = this.emotes.getOrDefault(gID, new ArrayList<MottoEmote>());
		if(list.isEmpty())
			return "Il n'y a aucune emote sur ce serveur.";
		
		String result = "Il y a "+list.size()+" emotes sur ce serveur.\n";
		Iterator<MottoEmote> it = list.iterator();
		
		result += it.next().name;
		while(it.hasNext()) {
			result += ", ";
			result += it.next().name;
		}
		result += ".";
		
		return result;
	}

	private synchronized String searchEmotes(Guild guild, String search) {
		String gID = guild.getId();
		ArrayList<MottoEmote> list = this.emotes.getOrDefault(gID, new ArrayList<MottoEmote>());
		Iterator<MottoEmote> it = list.stream().filter(e->e.name.toLowerCase().contains(search.toLowerCase())).iterator();
		if(!it.hasNext())
			return "Aucune emote ne correspond à cette recherche.";
		
		String result = "Emotes trouvées avec \""+search+"\": \n";
				
		result += it.next().name;
		while(it.hasNext()) {
			result += ", ";
			result += it.next().name;
		}
		result += ".";
		
		return result;
	}

	private synchronized String removeEmote(Guild guild, Member member, String name) {
		String gID = guild.getId();
		String uID = member.getUser().getId();
		ArrayList<MottoEmote> list = this.emotes.getOrDefault(gID, new ArrayList<MottoEmote>());
		
		MottoEmote removedEmote = new MottoEmote(name, uID);
		if(!list.contains(removedEmote))
			return "Cette emote n'existe pas.";
		if(!(list.get(list.indexOf(removedEmote)).authorID==removedEmote.authorID || member.hasPermission(Permission.ADMINISTRATOR)))
			return "Vous ne pouvez pas supprimer cette emote.";
		
		list.remove(removedEmote);
		this.emotes.putIfAbsent(gID, list);
		this.saveEmotes(guild, true);
		
		return "Emote \""+name+"\" supprimée";
	}

	private synchronized String addEmote(Guild guild, Member member, String name, String url) {
		String gID = guild.getId();
		String uID = member.getUser().getId();
		ArrayList<MottoEmote> list = this.emotes.getOrDefault(gID, new ArrayList<MottoEmote>());
		
		MottoEmote addedEmote = new MottoEmote(name, url, uID);
		if(list.contains(addedEmote))
			return "Une emote portant ce nom existe déjà.";
		
		list.add(addedEmote);
		this.emotes.putIfAbsent(gID, list);
		this.saveEmotes(guild, false);
		
		return "Emote \""+name+"\" ajoutée";
	}
	
	private static String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("```");
		sb.append("=e nomemote\n");
		sb.append("\tAffiche l'emote nomemote, les noms sont insensibles à la casse\n\n");
		sb.append("=e add nomemote url\n");
		sb.append("\tCrée une nouvelle emote nomemote qui affichera url\n\n");
		sb.append("=e remove nomemote\n");
		sb.append("\tSupprime l'emote nomemote, l'emote doit être la votre\n\n");
		sb.append("=e list\n");
		sb.append("\tAffiche la liste des emotes disponibles\n\n");
		sb.append("=e list mot\n");
		sb.append("\tAffiche la liste des emotes dont le nom contient \"mot\"\n\n");
		sb.append("```");
		
		return sb.toString();
	}
	
	private void saveEmotes(Guild guild, boolean remove) {
		String gID = guild.getId();
		Path path = Paths.get("emotes","emotes_"+gID+".txt");
		
		List<String> lines = new ArrayList<String>();
		
		for(MottoEmote e : this.emotes.getOrDefault(guild.getId(), new ArrayList<MottoEmote>()))
		{
			lines.add(e.toString());
		}
		
		try {
			if(remove)
				Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			else
				Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}