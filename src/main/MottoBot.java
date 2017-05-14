package main;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import audio.AudioManagerMotto;
import audio.GuildMusicManager;
import commandes.CmdStats;
import commandes.Commande;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MottoBot extends ListenerAdapter
{
	// https://discordapp.com/oauth2/authorize?client_id=282539502818426892&scope=bot&permissions=-1
	private Pattern commandPattern = Pattern.compile("^=([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
	
	private JDA jda;

	private List<Message> msgTab;
	
	private List<Commande> commandesValides;

	public static final String DEFAULT_SEARCH = "nico_robin";

	public static final String VERSION = "42.0";
	
    private final AudioPlayerManager playerManager;

	private final Map<Long, GuildMusicManager> musicManagers;
	
	private AudioManagerMotto properAudioManager;
	
	private TallyCounter tallyCounter;
	
	private Instant startTime;

	private boolean stop;

	private String token;

	public MottoBot(String token)
	{
		this.token = token;
		this.msgTab = new ArrayList<Message>();
		this.commandesValides = new ArrayList<Commande>();
		this.startTime = Instant.now();
		
		try {
			this.jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Connecté avec: " + this.jda.getSelfUser().getName());
		int nbServeurs = this.jda.getGuilds().size();
		System.out.println("Le bot est autorisé sur " + nbServeurs + " serveur" + (nbServeurs > 1 ? "s" : ""));
		for(Guild g:this.jda.getGuilds()) {
			System.out.println("\t"+g.getName());
		}
		this.jda.getPresence().setGame(Game.of("=motto"));
		
		this.musicManagers = new HashMap<>();
		this.tallyCounter = new TallyCounter(this, "./userProgress.ser");

		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(this.playerManager);
		AudioSourceManagers.registerLocalSource(this.playerManager); 
		this.properAudioManager = new AudioManagerMotto(); 
	}

	public static void main(String[] args) throws InterruptedException
	{
		if(args.length<1) {
			System.out.println("Il faut un token pour lancer le bot !");
			System.exit(1);
		}
		if(args.length>1) {
			int sec;
			try {
				sec = Integer.parseInt(args[1]);
			} catch (Exception e) {
				sec = 5;
				e.printStackTrace();
			}

			System.out.println("Lancement dans " + sec + " secondes.");
			
			try {
				Thread.sleep(1000*sec);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("Lancement !");
		}
		MottoBot m = new MottoBot(args[0]);
		m.registerCommands();
		m.jda.addEventListener(m);
		m.getTallyCounter().statsInit();
		m.jda.addEventListener(m.getTallyCounter());
		m.run();
	}

	private void registerCommands() {
    	new Reflections("commandes").getSubTypesOf(Commande.class).forEach(clazz -> {
            try {
                Commande nouvelleCommande = clazz.newInstance();
                Optional<Commande> commandeExistante = this.commandesValides.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(clazz.getName())).findAny();
                if (!commandeExistante.isPresent()) {
                	this.commandesValides.add(nouvelleCommande);
                    System.out.println("Commande enregistrée: "+ nouvelleCommande.getName());
                } else {
                	System.out.println("Enregistrement de deux commandes portant le même nom: " + commandeExistante.get().getName());
                	System.out.println("Existante: " + commandeExistante.get().getClass().getName());
                	System.out.println("Nouvelle: " + nouvelleCommande.getClass().getName());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	private void run() {
		this.stop = false;

		while (!this.stop)
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Arrêt.");
		
		this.tallyCounter.saveToFile();
		this.jda.shutdown(true);
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getAuthor().equals(this.jda.getSelfUser()))
		{
			this.msgTab.add(event.getMessage());
			return;
		}
		else if (event.getMessage().getAuthor().isBot())
		{
			return;
		}
		
		Matcher matcher = this.commandPattern.matcher(event.getMessage().getContent());
        if (matcher.matches()) {
        	// Potentielle commande
        	String commande = matcher.group(1).toLowerCase();
        	String arguments = matcher.group(2).isEmpty() ? "" : matcher.group(2);
        	this.lireCommande(event, commande, arguments);
        }
		else {
			// Message lambda
			if (event.getMessage().getContent().toLowerCase().contains("motto bot"))
			{
				event.getChannel().sendTyping().queue();
				Random rand = new Random();
				int randomizedMsgIndex = rand.nextInt(4);
				String msg = "";
				switch (randomizedMsgIndex)
				{
				case 0 :
					msg = "vous voulez quoi les pédé ?";
					break;
				case 1 :
					msg = "waa, m'appelle pas comme ça, t'es fou";
					break;
				case 2 :
					msg = "je suis pas venu ici pour souffir, ok ?";
					break;
				case 3 :
					msg = "koi?";
					break;
				default:
					msg = "???????";
					break;
				}
				event.getChannel().sendMessage(msg).queue();
			}
		}
	}
	
	private void lireCommande(MessageReceivedEvent e, String cmdString, String arguments) {
        Optional<Commande> commande = this.commandesValides.stream()
                .filter(com -> com.getName().equalsIgnoreCase(cmdString) || (com.getAliases() != null && com.getAliases().contains(cmdString)))
                .findAny();
        if (commande.isPresent()) {
        	// La commande existe
        	if(e.getChannelType()==ChannelType.TEXT) {
        		this.tallyCounter.onCommandUse(e, commande.get(), arguments);
        	}
        	commande.get().run(this, e, arguments);
        } else {
        	// Commande inconnue
        }
	}

	public void addMsg(Message message) {
		this.msgTab.add(message);
	}

	public JDA getJda() {
		return this.jda;
	}
    
    public TallyCounter getTallyCounter() {
		return this.tallyCounter;
	}

	public List<Message> getMsgTab() {
		return this.msgTab;
	}
	
	public void setMsgTab(List<Message> msgTab) {
		this.msgTab = msgTab;
	}
	
	public AudioPlayerManager getPlayerManager() {
		return this.playerManager;
	}
	
	public Map<Long, GuildMusicManager> getMusicManagers() {
		return this.musicManagers;
	}
	
	public AudioManagerMotto getProperAudioManager() {
		return this.properAudioManager;
	}

	public List<String> admins() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("262610896545644554"); // Momojean
		list.add("269162033855856650"); // Wylentar
		return list;
	}

	public String getUptime() {
		Duration d = Duration.between(this.startTime, Instant.now());
		String uptime = CmdStats.formatDuration(d);
		return uptime;
	}

	public String getToken() {
		return this.token;
	}
}
