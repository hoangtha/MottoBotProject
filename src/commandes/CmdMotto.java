package commandes;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import main.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CmdMotto implements Commande {
	private static Random rand = new Random();
	
	@Override
	public String getName() {
		return "motto";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean run(Main bot, MessageReceivedEvent e, String arguments) {
		bot.addMsg(e.getMessage());

		e.getChannel().sendTyping();
		
		int selector = CmdMotto.rand.nextInt(3);
		String url = "";
		int nbRecherche = 0;
		Document doc;
		String imageUrl = "";
		 
		while(nbRecherche<3)
		{
			switch(selector)
			{
				case 0:
					if (!arguments.equals(""))
					{
						url = "https://yande.re/post?tags=order:random+" + arguments;
					} else
					{
						url = "https://yande.re/post?tags=order:random+" + Main.DEFAULT_SEARCH;
					}
					break;

				
				case 1:
					if (!arguments.equals(""))
					{
						url = "http://konachan.com/post?tags=order:random+" + arguments;
					} else
					{
						url = "http://konachan.com/post?tags=order:random+" + Main.DEFAULT_SEARCH;
					}
					break;
				case 2:
					if (!arguments.equals(""))
					{
						url = "https://chan.sankakucomplex.com/?tags=order:random+" + arguments
								+ "&commit=Search";
					} else
					{
						url = "https://chan.sankakucomplex.com/?tags=order:random+" + Main.DEFAULT_SEARCH + "&commit=Search";
					}
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

			} catch (IOException | NullPointerException e1)
			{
				doc = null;
				imageUrl = null;
			} 

			if (imageUrl != null)
			{
				if (selector == 0)
				{
					e.getChannel().sendMessage(imageUrl).queue();
				} else
				{
					if (imageUrl.startsWith("//"))
					{
						e.getChannel().sendMessage("https:" + imageUrl).queue();
					} else
					{
						e.getChannel().sendMessage(imageUrl).queue();
					}

				}
				System.out.println(e.getAuthor().getName() + " " +arguments +" : " + imageUrl);
				break;
			} else
			{
				selector = (selector + 1)%3;
				nbRecherche++;
			}
			
			if(nbRecherche==3)
			{
				e.getChannel().sendMessage("ouin ouin, marche pas...").queue();
			}
		}
		return true;
	}

}
