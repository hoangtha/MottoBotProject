package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.managers.AudioManager;

public class Main implements EventListener, ConnectionListener
{
	// https://discordapp.com/oauth2/authorize?client_id=282539502818426892&scope=bot&permissions=-1

	private JDA jda;

	private boolean stop = false;

	private List<Message> msgTab;
	
	private List<Message> temp;

	private static final String DEFAULT = "nico_robin";

	Main(String token)
	{

		try
		{
			jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
			// aws = new AudioWebSocket()
		} catch (LoginException e)
		{
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		} catch (RateLimitedException e)
		{
			e.printStackTrace();
		}
		jda.addEventListener(this);
		System.out.println("Connecte avec: " + jda.getSelfUser().getName());
		int i;
		System.out.println("Le bot est autorisé sur " + (i = jda.getGuilds().size()) + " serveur" + (i > 1 ? "s" : ""));
		jda.getPresence().setGame(Game.of("=motto"));
		msgTab = new ArrayList<Message>();

		while (!stop)
		{
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);
			String cmd = scanner.next();
			if (cmd.equalsIgnoreCase("stop"))
			{
				jda.shutdown(true);
				stop = true;
			}
		}

	}

	public static void main(String[] args)
	{
		new Main("MjgyNTM5NTAyODE4NDI2ODky.C4n-8g.3NGwGhWK8xeugEk2swSe57CxUPo");
	}

	@Override
	public void onEvent(Event event)
	{
		if (event instanceof MessageReceivedEvent)
		{
			MessageReceivedEvent e = (MessageReceivedEvent) event;
			if (e.getMessage().getContent().startsWith("=motto"))
			{

				msgTab.add(e.getMessage());

				e.getChannel().sendTyping();
				Random rand = new Random();

				int selector = rand.nextInt(3);
				String url = "";
				int ok = 0;

				String strGetted = e.getMessage().getContent();
				String[] arg;
				arg = strGetted.split(" ");

				if (arg.length > 1)
				{
					ok = 1;
				} 
				else if (arg[0].equals("=motto"))
				{
					ok = 2;
				}
				int nbRecherche = 0;
				
				while(nbRecherche<3 && ok != 0)
				{
					if (ok != 0)
					{
						if (selector == 0)
						{
							if (ok == 1)
							{
								url = "https://yande.re/post?tags=order:random+" + strGetted.substring(7);
							} else if (ok == 2)
							{
								url = "https://yande.re/post?tags=order:random+" + DEFAULT;
							}
	
						} else if (selector == 1)
						{
							if (ok == 1)
							{
								url = "http://konachan.com/post?tags=order:random+" + strGetted.substring(7);
							} else if (ok == 2)
							{
								url = "http://konachan.com/post?tags=order:random+" + DEFAULT;
							}
						} else if (selector == 2)
						{
	
							if (ok == 1)
							{
								url = "https://chan.sankakucomplex.com/?tags=order:random+" + strGetted.substring(7)
										+ "&commit=Search";
							} else if (ok == 2)
							{
								url = "https://chan.sankakucomplex.com/?tags=order:random+" + DEFAULT + "&commit=Search";
							}
	
						}
	
						Document doc;
						String imageUrl = "";
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
	
						} catch (IOException e1)
						{
	
							doc = null;
							imageUrl = null;
						} catch (NullPointerException e2)
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
							System.out.println(e.getAuthor().getName() + " : " + imageUrl);
							break;
						} else
						{
							//e.getChannel().sendMessage("ouin ouin, marche pas...").queue();
							selector = (selector + 1)%3;
							nbRecherche++;
						}
					}
					if(nbRecherche==3)
					{
						e.getChannel().sendMessage("ouin ouin, marche pas...").queue();
					}
				
				}

			}

			if (e.getMessage().getContent().equalsIgnoreCase("ping"))
			{
				msgTab.add(e.getMessage());
				e.getChannel().sendTyping();
				e.getChannel().sendMessage("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va.")
						.queue();

			}

			if (e.getMessage().getContent().equalsIgnoreCase("=mottohelp"))
			{
				msgTab.add(e.getMessage());
				e.getChannel().sendTyping();
				e.getChannel()
						.sendMessage(
								"```=motto [tag]: ??(default tag : nico_robin) \n=mottoclear : Supprime tout les motto de tout les channels (X)\n=mottohelp Affiche les commandes disponible```")
						.queue();

			}

			if (e.getMessage().getContent().equalsIgnoreCase("=mottoclear"))
			{
				temp = new ArrayList<Message>();
				int nbMessageInitial = 0;
				msgTab.add(e.getMessage());
				while (msgTab.size() != 0)
				{
					Message messageToDelete = msgTab.get(msgTab.size() - 1);
					if(messageToDelete.getChannel().equals(e.getMessage().getChannel()))
					{
						messageToDelete.deleteMessage().queue();
						nbMessageInitial++;
					}
					else
					{
						temp.add(messageToDelete);
					}
					msgTab.remove(msgTab.size() - 1); 
					
					
				}
				msgTab = temp;
				

				if (nbMessageInitial > 1)
				{
					e.getChannel().sendMessage(nbMessageInitial + " messages effacés.").queue();
				} else
				{
					e.getChannel().sendMessage("Pas de messages à effacer...").queue();
				}

			}

			if (e.getMessage().getContent().startsWith("http")
					|| e.getMessage().getContent().equals("ouin ouin, marche pas...")
					|| e.getMessage().getContent()
							.equals("dé-aisse-elle mec, mais t'as cru que j'allais t'envoyer pong ? pédé va."))
			{
				if (e.getMessage().getAuthor().getName().equals("Motto bot"))
				{
					msgTab.add(e.getMessage());
				}
			}

			// Fonction qui ne sert à rien
			if (e.getMessage().getContent().equalsIgnoreCase("=mottovoice"))
			{
				AudioManager manager = jda.getGuilds().get(0).getAudioManager();
				e.getChannel().sendMessage("J'arrive dans la taverne");
				manager.openAudioConnection(e.getAuthor().getJDA().getVoiceChannelByName("Taverne", true).get(0));

			}

			// Fonction qui ne sert à rien
			if (e.getMessage().getContent().equalsIgnoreCase("=mottoleave"))
			{
				e.getChannel().sendMessage("byebye!");
				AudioManager manager = jda.getGuilds().get(0).getAudioManager();
				manager.closeAudioConnection();
			}
		}
	}

	@Override
	public void onPing(long arg0)
	{
		
	}

	@Override
	public void onStatusChange(ConnectionStatus arg0)
	{

	}

	@Override
	public void onUserSpeaking(User arg0, boolean arg1)
	{
		
	}
}
