package main;

import java.util.ArrayList;
import java.util.List;
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

	public static final String DEFAULT_SEARCH = "nico_robin";

	Main(String token)
	{	
		this.msgTab = new ArrayList<Message>();
		
		try {
			this.jda = new JDABuilder(AccountType.BOT).setToken(token).setBulkDeleteSplittingEnabled(false).buildBlocking();
			// aws = new AudioWebSocket()
		} catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException e) {
			e.printStackTrace();
		}
		
		this.jda.addEventListener(this);
		System.out.println("Connecté avec: " + this.jda.getSelfUser().getName());
		int nbServeurs = this.jda.getGuilds().size();
		System.out.println("Le bot est autorisé sur " + nbServeurs + " serveur" + (nbServeurs > 1 ? "s" : ""));
		this.jda.getPresence().setGame(Game.of("=motto"));
	}

	public static void main(String[] args)
	{
		Main m = new Main("MjgyNTM5NTAyODE4NDI2ODky.C4n-8g.3NGwGhWK8xeugEk2swSe57CxUPo");
		m.run();
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
		}
		scanner.close();
	}

	@Override
	public void onEvent(Event event)
	{
		if (event instanceof MessageReceivedEvent)
		{
			MessageReceivedEvent e = (MessageReceivedEvent) event;
			
			if (e.getMessage().getAuthor().getName().equals("Motto bot"))
			{
				this.msgTab.add(e.getMessage());
				return;
			}
			
			Pattern commandPattern = Pattern.compile("^=([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = commandPattern.matcher(e.getMessage().getContent());
	        if (matcher.matches()) {
	        	// Potentielle commande
	        	String commande = matcher.group(1).toLowerCase();
	        	String[] arguments = matcher.group(2).isEmpty() ? new String[0] : matcher.group(2).split(" ");
	        	this.lireCommande(e, commande, arguments);
	        }
			else {
				// Message lambda
			}
		}
	}

	private void lireCommande(MessageReceivedEvent e, String commande, String[] arguments) {
		boolean result = false;
		switch(commande)
		{
			case "motto":
				result = new CmdMotto().run(this, e, arguments);
				break;
			case "mottohelp":
				result = new CmdHelp().run(this, e, arguments);
				break;
			case "mottovoice":
				result = new CmdVoice().run(this, e, arguments);
				break;
			case "mottoleave":
				result = new CmdLeave().run(this, e, arguments);
				break;
			case "mottoclear":
				result = new CmdClear().run(this, e, arguments);
				break;
			case "ping":
				result = new CmdPing().run(this, e, arguments);
				break;
			default:
				// Commande non reconnue
				break;
		}
		if(!result)
			System.out.println("Commande non reconnue ou problème d'execution.");
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
