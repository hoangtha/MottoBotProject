package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdMotto implements Commande {
	private static Random rand = new Random();
	
	private ArrayList<String> nsfwGuilds;
	
	public CmdMotto() {
		this.nsfwGuilds = new ArrayList<String>();
		this.nsfwGuilds.add("269163044427268096"); // Freaking Potatoes
	}
	
	@Override
	public String getName() {
		return "motto";
	}

	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public void run(MottoBot bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());

		e.getChannel().sendTyping();
		
		boolean nsfwGuild = this.nsfwGuilds.contains(e.getGuild().getId());
		
		int selector = CmdMotto.rand.nextInt(3);
		String url = "";
		int nbRecherche = 0;
		Document doc;
		String imageUrl = "";
		 
		while(nbRecherche<3)
		{
			switch(selector)
			{
				case 0: // Yande.re
					if(nsfwGuild) {
						if (arguments!=null && !arguments.isEmpty()) {
							url = "https://yande.re/post?tags=order:random+" + arguments;
						}
						else {
							url = "https://yande.re/post?tags=order:random+" + MottoBot.DEFAULT_SEARCH;
						}
					}
					else {
						url = "https://yande.re/post?tags=order:random+rating:safe";
						if (arguments!=null && !arguments.isEmpty())
							url += "+" + arguments;
					}
					break;
				case 1: // Konachan
					if(nsfwGuild) {
						if (arguments!=null && !arguments.isEmpty()) {
							url = "http://konachan.com/post?tags=order:random+" + arguments;
						} 
						else {
							url = "http://konachan.com/post?tags=order:random+" + MottoBot.DEFAULT_SEARCH;
						}
					}
					else {
						url = "http://konachan.net/post?tags=order:random";
						if (arguments!=null && !arguments.isEmpty())
							url += "+" + arguments;
					}
					break;
				case 2: // Sankaku(si nsfw) ou Yande.re
					if(nsfwGuild) {
						if (arguments!=null && !arguments.isEmpty()) {
							url = "https://chan.sankakucomplex.com/?tags=order:random+" + arguments;
						} 
						else {
							url = "https://chan.sankakucomplex.com/?tags=order:random+" + MottoBot.DEFAULT_SEARCH;
						}
					}
					else {
						url = "https://yande.re/post?tags=order:random+rating:safe";
						if (arguments!=null && !arguments.isEmpty())
							url += "+" + arguments;
					}
					break;
				default:
					break;
			}
			
			try
			{
				doc = Jsoup.connect(url).get();
				if (selector != 2)
				{
					imageUrl = doc.select("span[class=plid]").stream().findAny().map(docs -> docs.html())
							.orElse(null).substring(4);
				} else
				{
					imageUrl = "https://chan.sankakucomplex.com"
							+ doc.select("span[class=thumb blacklisted]").stream().findAny()
									.map(docs -> docs.html()).orElse(null).substring(9, 27).replace("\"", "");
				}

				doc = Jsoup.connect(imageUrl).get();
				imageUrl = doc.select("img[id=image]").stream().findFirst().map(docs -> docs.attr("src").trim())
						.orElse(null);
			} 
			catch (IOException | NullPointerException err)
			{
				doc = null;
				imageUrl = null;
			} 

			if (imageUrl != null)
			{
				if (selector == 0)
				{
					e.getChannel().sendMessage(imageUrl).queue();
				} 
				else
				{
					if (imageUrl.startsWith("//"))
					{
						e.getChannel().sendMessage("https:" + imageUrl).queue();
					} 
					else
					{
						e.getChannel().sendMessage(imageUrl).queue();
					}

				}
				System.out.println(e.getAuthor().getName() + " " + arguments +" : " + imageUrl);
				break;
			} 
			else
			{
				System.out.println("Erreur recherche sur " + selector);
				selector = (selector + 1)%3;
				nbRecherche++;
			}
			
			if(nbRecherche==3)
			{
				e.getChannel().sendMessage("ouin ouin, marche pas...").queue();
			}
		}
	}
}
