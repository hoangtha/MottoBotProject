package commandes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import main.MottoBot;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdMotto implements Commande {
	private static Random rand = new Random();
	
	private ArrayList<String> robinArmy;
	
	public CmdMotto() {
		this.robinArmy = new ArrayList<String>();
		this.robinArmy.add("269163044427268096"); // Freaking Potatoes
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

		e.getChannel().sendTyping().queue();
		
		boolean isFromRobinArmy = this.robinArmy.contains(e.getGuild().getId()); // sale
		
		int selector = CmdMotto.rand.nextInt(3);
		String url = "";
		int nbRecherche = 0;
		Document doc;
		String imageUrl = "";
		
		while(nbRecherche<3)
		{
			if(arguments.toLowerCase().contains("ademage"))
			{
				if(e.getAuthor().getId().equals("259789587432341506")) {
					selector = 2;
					url = "https://chan.sankakucomplex.com/?tags=order:random+nico_robin+solo+-rating:explicit";
				}
				else {
					e.getChannel().sendMessage("no").queue();
					return;
				}
			}
			else {
				switch(selector)
				{
					case 0: // Yande.re
						url = "https://yande.re/post?tags=order:random";
						break;
					case 1: // Konachan			
						url = "http://konachan.com/post?tags=order:random";
						break;
					case 2: // chan.sankaku
						url = "https://chan.sankakucomplex.com/?tags=order:random";
						break;
					default:
						break;
				}
				if(isFromRobinArmy && arguments=="")
				{
					url += "+" + MottoBot.DEFAULT_SEARCH;
				}
				if(!e.getChannel().getName().toLowerCase().contains("nsfw")) 
				{
					url += "+rating:safe-rating:e";
				}
				url += "+" + arguments;
			}
			
			try
			{
				doc = Jsoup.connect(url).get();
				
				if (selector != 2)
				{
					imageUrl = doc.select("span[class=plid]").stream().findAny().map(docs -> docs.html())
							.orElse(null).substring(4);
				} 
				else
				{
					Elements elems = doc.select("span[class=thumb blacklisted] > a");
					if(elems.size()>0) {
						int selectedA = CmdMotto.rand.nextInt(elems.size());
						imageUrl = "https://chan.sankakucomplex.com"
								+ elems.get(selectedA).attr("href");
					}
					else {
						doc = null;
						imageUrl = null;
					}
				}
				
				if(imageUrl!=null) {
					doc = Jsoup.connect(imageUrl).get();
					imageUrl = doc.select("img[id=image]").stream().findFirst().map(docs -> docs.attr("src").trim())
							.orElse(null);
				}
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
				e.getChannel().sendMessage("ouin ouin, marche pas... <@"+e.getAuthor().getId()+">").queue();
			}
		}
	}
}
