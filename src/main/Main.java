package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.LoginException;

import commandes.CmdClear;
import commandes.CmdHelp;
import commandes.CmdLeave;
import commandes.CmdMotto;
import commandes.CmdPing;
import commandes.CmdVoice;
import commandes.Commande;
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

public class Main implements EventListener, ConnectionListener
{
	// https://discordapp.com/oauth2/authorize?client_id=282539502818426892&scope=bot&permissions=-1

	private JDA jda;

	private List<Message> msgTab;
	
	private List<Commande> commandesValides;

	public static final String DEFAULT_SEARCH = "nico_robin";

	Main(String token)
	{	
		this.msgTab = new ArrayList<Message>();
		this.commandesValides = new ArrayList<Commande>();
		
		try {
			this.jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
			// aws = new AudioWebSocket()
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Connecté avec: " + this.jda.getSelfUser().getName());
		int nbServeurs = this.jda.getGuilds().size();
		System.out.println("Le bot est autorisé sur " + nbServeurs + " serveur" + (nbServeurs > 1 ? "s" : ""));
		this.jda.getPresence().setGame(Game.of("=motto"));
	}

	public static void main(String[] args)
	{
		Main m = new Main("MjgyNTM5NTAyODE4NDI2ODky.C4n-8g.3NGwGhWK8xeugEk2swSe57CxUPo");
		m.registerCommands();
		m.jda.addEventListener(m);
		m.run();
	}
	
    private void registerCommands() {/*
    	final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    	
    	try {
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getAllClasses()) {
				
				Class<?> clazz = info.load();
				Class<?> interfaces[] = clazz.getInterfaces();
				for(Class<?> c:interfaces) {
					if(c.equals(Commande.class))
					{
						try {
							Commande nouvelleCommande = (Commande) clazz.newInstance();
							Optional<Commande> commandeExistante = this.commandesValides.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(nouvelleCommande.getName())).findAny();
			                if (!commandeExistante.isPresent()) {
			                    this.commandesValides.add(nouvelleCommande);
			                    System.out.println("Commande enregistrée: "+ nouvelleCommande.getName());
			                } else {
			                	System.out.println("Enregistrement de deux commandes portant le même nom: " + commandeExistante.get().getName());
			                	System.out.println("Existante: " + commandeExistante.get().getClass().getName());
			                	System.out.println("Nouvelle: " + nouvelleCommande.getClass().getName());
			                }
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    	this.commandesValides.add(new CmdClear());
    	this.commandesValides.add(new CmdMotto());
    	this.commandesValides.add(new CmdLeave());
    	this.commandesValides.add(new CmdPing());
    	this.commandesValides.add(new CmdHelp());
    	this.commandesValides.add(new CmdVoice());
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
				this.jda.shutdown(true);
				stop = true;
			}
			else if (cmd.equalsIgnoreCase("regCmd"))
			{
				System.out.println("Recherche de commandes");
				this.registerCommands();
				System.out.println("Recherche terminée");
			}
		}
		scanner.close();
	}

	@Override
	public void onEvent(Event event)
	{
		if (event instanceof MessageReceivedEvent)
		{
			MessageReceivedEvent e = (MessageReceivedEvent) event;
			
			if (e.getMessage().getAuthor().equals(this.jda.getSelfUser()))
			{
				this.msgTab.add(e.getMessage());
				return;
			}
			if (e.getMessage().getAuthor().isBot())
			{
				return;
			}
			
			Pattern commandPattern = Pattern.compile("^=([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = commandPattern.matcher(e.getMessage().getContent());
	        if (matcher.matches()) {
	        	// Potentielle commande
	        	String commande = matcher.group(1).toLowerCase();
	        	String arguments = matcher.group(2).isEmpty() ? "" : matcher.group(2);
	        	this.lireCommande(e, commande, arguments);
	        }
			else {
				// Message lambda
			}
		}
	}

	private void lireCommande(MessageReceivedEvent e, String cmdString, String arguments) {
		boolean result = false;
		
        Optional<Commande> commande = this.commandesValides.stream()
                .filter(com -> com.getName().equalsIgnoreCase(cmdString) || (com.getAliases() != null && com.getAliases().contains(cmdString)))
                .findAny();
        if (commande.isPresent()) {
        	// La commande existe
            result = commande.get().run(this, e, arguments);
        } else {
        	// Commande inconnue
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

	public void addMsg(Message message) {
		this.msgTab.add(message);
	}

	public JDA getJda() {
		return this.jda;
	}

	public List<Message> getMsgTab() {
		return this.msgTab;
	}
	
	public void setMsgTab(List<Message> msgTab) {
		this.msgTab = msgTab;
	}
}
