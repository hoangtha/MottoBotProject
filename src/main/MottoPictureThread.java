package main;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import main.MottoBot;
import main.MottoPictureThread;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MottoPictureThread implements Runnable {
	private static final int YANDERE = 0;
	private static final int KONACHAN = 1;
	private static final int SANKAKU = 2;
	private static final int MAX_TRIES = 3; // Nombre maximal de recherches
	private static final int NB_SEL = 3; // Nombre de sites sur lesquels on peut rechercher
	public static final Color EXPLICIT = new Color(255, 20, 147);
	public static final Color SAFE = new Color(50, 205, 50);//vert, je prend le bleu pour la water
	public static final Color QUESTIONABLE = new Color(255, 165, 0);
	
	private Random rand;
	
	public ArrayList<String> robinArmy;

	private String arguments;

	private MessageReceivedEvent e;
	
	public MottoPictureThread(MessageReceivedEvent e, String arguments, ArrayList<String> robinArmy) {
		this.rand = new Random();
		this.e = e;
		this.arguments = arguments;
		this.robinArmy = robinArmy; 
	}
		
	@Override
	public void run() {
		this.e.getChannel().sendTyping().queue();
		
		boolean isFromRobinArmy = this.robinArmy.contains(this.e.getGuild().getId()); // sale
		boolean channelIsNSFW = this.e.getChannel().getName().toLowerCase().contains("nsfw");
		
		int selector = this.rand.nextInt(3);
		int nbRecherche = 0;
		Document doc;
		String searchUrl = null;
		String pageUrl = null;
		String imageUrl = null;
		
		while(nbRecherche<MAX_TRIES)
		{
			if(this.arguments.toLowerCase().contains("ademage"))
			{
				if(this.e.getAuthor().getId().equals("259789587432341506")) {
					selector = SANKAKU;
					nbRecherche = MAX_TRIES-1;
					searchUrl = "https://chan.sankakucomplex.com/?tags=order:random+nico_robin+solo+-rating:explicit";
				}
				else {
					this.e.getChannel().sendMessage("no").queue();
					return;
				}
			}
			else {
				switch(selector)
				{
					case YANDERE: // Yande.re
						searchUrl = "https://yande.re/post?tags=order:random";
						break;
					case KONACHAN: // Konachan			
						searchUrl = "http://konachan.com/post?tags=order:random";
						break;
					case SANKAKU: // chan.sankaku
						searchUrl = "https://chan.sankakucomplex.com/?tags=order:random";
						break;
					default:
						break;
				}
				
				if(this.arguments==null || this.arguments=="") { // Pas d'arguments
					if(isFromRobinArmy) // Tag par défaut
					{
						searchUrl += "+" + MottoBot.DEFAULT_SEARCH;
					}
				}
				else {
					searchUrl += "+" + this.arguments;
				}
				
				if(channelIsNSFW==false)  // Si le salon n'est pas NSFW, restreindre le contenu
				{
					searchUrl += "+rating:safe-rating:e";
				}
			}
			
			try
			{
				doc = Jsoup.connect(searchUrl).get();
				
				if (selector==SANKAKU)
				{
					Elements elems = doc.select("span[class=thumb blacklisted] > a");
					if(elems.size()>0) {
						int selectedA = this.rand.nextInt(elems.size());
						pageUrl = "https://chan.sankakucomplex.com"
								+ elems.get(selectedA).attr("href");
					}
					else {
						pageUrl = null;
					}
				} 
				else
				{
					pageUrl = doc.select("span[class=plid]").stream().findAny().map(docs -> docs.html())
							.orElse(null).substring(4);
				}
				
				if(pageUrl!=null) {
					doc = Jsoup.connect(pageUrl).get();
					
					imageUrl = doc.select("img[id=image]").stream().findFirst().map(docs -> docs.attr("src").trim())
							.orElse(null);
				}
				else {
					doc = null;
					imageUrl = null;
				}
			}
			catch (IOException | NullPointerException err)
			{
				doc = null;
				pageUrl = null;
				imageUrl = null;
			} 

			if (imageUrl != null)
			{
				EmbedBuilder eb = new EmbedBuilder();
				
				String title = this.arguments;
				if(this.arguments==null || this.arguments=="") {
					title = "Motto !";
				}
				
				if(channelIsNSFW) {
					eb.setTitle(title, pageUrl);
					eb.setColor(EXPLICIT);
				}
				else {
					eb.setTitle(title, null);
					eb.setColor(SAFE);
				}
				
				eb.setDescription("Demandé par " + this.e.getMember().getEffectiveName());
				
				if (selector==YANDERE)
				{
					eb.setImage(imageUrl);
				} 
				else
				{
					if (imageUrl.startsWith("//"))
					{
						eb.setImage("https:" + imageUrl);
					} 
					else
					{
						eb.setImage(imageUrl);
					}
				}
				
				this.e.getChannel().sendMessage(eb.build()).queue();
				System.out.println(this.e.getAuthor().getName() + " " + this.arguments +" : " + imageUrl);
				break;
			}
			else
			{
				System.out.println("Erreur recherche sur " + selector);
				selector = (selector + 1)%NB_SEL;
				nbRecherche++;
			}
			
			if(nbRecherche>=MAX_TRIES)
			{
				this.messageErreur();
			}
		}
	}

	private void messageErreur() {
		this.e.getChannel().sendMessage("ouin ouin, marche pas... <@"+this.e.getAuthor().getId()+">").queue();
	}
}
