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

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
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
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.Message.MentionType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MottoBot extends ListenerAdapter
{
	// https://discordapp.com/oauth2/authorize?client_id=282539502818426892&scope=bot&permissions=-1
	private Pattern commandPattern = Pattern.compile("^=([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
	private Pattern wordPattern = Pattern.compile("([a-zA-ZàèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇßØøÅåÆæœ'`:@#-]{4,})");
	
	private JDA jda;

	private List<Message> msgTab;
	
	private List<Commande> commandesValides;

	public static final String DEFAULT_SEARCH = "nico_robin";

	public static final String VERSION = "46";
	
    private final AudioPlayerManager playerManager;

	private final Map<Long, GuildMusicManager> musicManagers;
	
	private AudioManagerMotto properAudioManager;
	
	private TallyCounter tallyCounter;
	
	private Instant startTime;

	private boolean stop;

	private String token;

	private final Random rand = new Random();

	public MottoBot(String token)
	{
		this.token = token;
		this.msgTab = new ArrayList<Message>();
		this.commandesValides = new ArrayList<Commande>();
		this.startTime = Instant.now();
		
		try {
			this.jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).setAudioSendFactory(new NativeAudioSendFactory()).buildBlocking();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Connecté avec: " + this.jda.getSelfUser().getName());
		int nbServeurs = this.jda.getGuilds().size();
		System.out.println("Le bot est autorisé sur " + nbServeurs + " serveur" + (nbServeurs > 1 ? "s" : ""));
		for(Guild g:this.jda.getGuilds()) {
			System.out.println("\t"+g.getName()+" - "+g.getId());
		}
		this.jda.getPresence().setGame(Game.playing("=motto"));
		
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
		this.jda.shutdown();
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
		
		Matcher matcher = this.commandPattern.matcher(event.getMessage().getContentRaw());
        if (matcher.matches()) {
        	// Potentielle commande
        	String commande = matcher.group(1).toLowerCase();
        	String arguments = matcher.group(2).isEmpty() ? "" : matcher.group(2);
        	this.lireCommande(event, commande, arguments);
        }
		else {
			// Message lambda
			String msgStripped = event.getMessage().getContentStripped().toLowerCase();
	    	
			if(event.getMessage().isMentioned(this.jda.getSelfUser(), MentionType.USER)
	    			|| event.getMessage().getContentRaw().toLowerCase().contains(this.jda.getSelfUser().getName().toLowerCase())) {
				event.getChannel().sendTyping().queue();
				Random rand = new Random();
				int randomizedMsgIndex = rand.nextInt(12);
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
				case 4 :
					msg = "J'ai rien à te dire, boloss";
					break;
				case 5 :
					msg = "tu peux arreter ça stttttttttttttttppppppppppppppppppp? merci, enculé(e)";
					break;
				case 6 :
					msg ="La pieuvre mimétique ou pieuvre-mime (Thaumoctopus mimicus) est une espèce de pieuvres, capable d'imiter d'autres animaux marins. C'est la seule espèce du genre Thaumoctopus.La pieuvre mimétique vit dans les mers tropicales d'Asie du Sud-Est. Elle ne fut découverte officiellement qu'en 1998, au large des côtes de Sulawesi. Cette pieuvre est capable d'imiter l'apparence(cmb) et les mouvements de plus de quinze espèces différentes, dont les serpents de mer, la rascasse volante, les poissons plats, les poissons-grenouilles, les ophiures, les crabes géants, les coquillages, les raies, les anémones de mer, les ademages, les méduses et les crevettes mantes. Elle accomplit cette prouesse en se contorsionnant — corps et bras — et en changeant de couleur. Source Wikipédia";
					break;
				case 7 :
					msg = "OKLM les batards?";
					break;
				case 8 :
					msg = "https://puu.sh/vQV22/5b109f8574.jpg";
					break;
				case 9 :
					msg = "Blargh";
					break;
				case 10 :
					msg = "Ademage est une légende";
					break;
				case 11 :
					msg = "Kestuveutoi?";
					break;
				default:
					msg = "???????";
					break;
				}
				event.getChannel().sendMessage(msg).queue();
			}
			else if(msgStripped.length()<60 && (msgStripped.contains("c'est pas faux") || msgStripped.contains("c pas faux"))) {
	    		event.getChannel().getHistoryBefore(event.getMessage(), 3).queue(h -> {laBotteSecrete(event, h);});
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
	
	private void laBotteSecrete(MessageReceivedEvent event, MessageHistory history) {
    	ArrayList<String> wordsList = new ArrayList<String>();
    	
    	for(Message m : history.getRetrievedHistory()) {
    		if(!m.getAuthor().equals(event.getAuthor()) && m.getContentStripped().length()>20) {
    	    	Matcher matcher = this.wordPattern.matcher(m.getContentStripped());
    	        while (matcher.find()) {
    	        	if(!(matcher.group(1).startsWith(":")||matcher.group(1).startsWith("@")||matcher.group(1).startsWith("#")))
    	        		wordsList.add(matcher.group(1));
    	        }
    		}
    		if(!wordsList.isEmpty()) {
    			break;
    		}
    	}
    	if(wordsList.isEmpty())
    		return;
        
    	String word = wordsList.get(this.rand.nextInt(wordsList.size()));
    	
    	if(this.rand.nextBoolean())
			event.getChannel().sendMessage("Sans rire <@"+event.getAuthor().getId()+">, tu sais pas ce que ça veut dire \"" + word + "\" ?").queue();
		else
			event.getChannel().sendMessage("C'est \"" + word + "\" que t'as pas compris <@"+event.getAuthor().getId()+"> ?").queue();
    }
}
