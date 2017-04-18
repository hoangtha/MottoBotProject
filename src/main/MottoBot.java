package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import audio.AudioManagerMotto;
import commandes.Commande;
import manager.GuildMusicManager;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MottoBot extends ListenerAdapter
{
	// https://discordapp.com/oauth2/authorize?client_id=282539502818426892&scope=bot&permissions=-1
	private JDA jda;

	private List<Message> msgTab;
	
	private List<Commande> commandesValides;

	// que robin + d'autre random féminin
	public static final String DEFAULT_SEARCH = "nico_robin -monkey_d_luffy -sanji -usopp -tony_tony_chopper";
	
    private final AudioPlayerManager playerManager;

	private final Map<Long, GuildMusicManager> musicManagers;
	
	private AudioManagerMotto properAudioManager;
	
	private TallyCounter tallyCounter;

	public MottoBot(String token)
	{
		this.msgTab = new ArrayList<Message>();
		this.commandesValides = new ArrayList<Commande>();
		
		try {
			this.jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Connecté avec: " + this.jda.getSelfUser().getName());
		int nbServeurs = this.jda.getGuilds().size();
		System.out.println("Le bot est autorisé sur " + nbServeurs + " serveur" + (nbServeurs > 1 ? "s" : ""));
		this.jda.getPresence().setGame(Game.of("=motto"));
		
		this.musicManagers = new HashMap<>();
		this.tallyCounter = new TallyCounter(this, "./userProgress.ser");

		this.playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(this.playerManager);
		AudioSourceManagers.registerLocalSource(this.playerManager); 
		this.properAudioManager = new AudioManagerMotto(); 
	}

	public static void main(String[] args)
	{
		if(args.length<1) {
			System.out.println("Il faut un token pour lancer le bot !");
			System.exit(1);
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
	
	private void run() {
		boolean stop = false;
		Scanner scanner = new Scanner(System.in);
		while (!stop)
		{
			String cmd = scanner.next();
			if (cmd.equalsIgnoreCase("stop"))
			{
				System.out.println("Arrêt demandé");
				this.tallyCounter.saveToFile();
				this.jda.shutdown(true);
				stop = true;
			}
			else if (cmd.equalsIgnoreCase("regCmd"))
			{
				System.out.println("Recherche de commandes");
				this.registerCommands();
				System.out.println("Recherche terminée");
			}
			else if (cmd.equalsIgnoreCase("forceSave"))
			{
				if(this.tallyCounter.saveToFile()) {
					System.out.println("Sauvegarde terminée.");
				} 
				else {
					System.out.println("Erreur de sauvegarde.");
				}
			}
			else if (cmd.equalsIgnoreCase("checkLevelUp"))
			{
				System.out.println("Vérification des level up...");
				this.tallyCounter.checkLevelUpForEveryone();
				System.out.println("Vérification des level up terminée!");
			}
		}
		scanner.close();
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
		
		Pattern commandPattern = Pattern.compile("^=([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = commandPattern.matcher(event.getMessage().getContent());
        if (matcher.matches()) {
        	// Potentielle commande
        	String commande = matcher.group(1).toLowerCase();
        	String arguments = matcher.group(2).isEmpty() ? "" : matcher.group(2);
        	this.lireCommande(event, commande, arguments);
        }
		else {
			// Message lambda
		}
	}
	
	private void lireCommande(MessageReceivedEvent e, String cmdString, String arguments) {
        Optional<Commande> commande = this.commandesValides.stream()
                .filter(com -> com.getName().equalsIgnoreCase(cmdString) || (com.getAliases() != null && com.getAliases().contains(cmdString)))
                .findAny();
        if (commande.isPresent()) {
        	// La commande existe
        	this.tallyCounter.onCommandUse(e, commande.get());
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
}
