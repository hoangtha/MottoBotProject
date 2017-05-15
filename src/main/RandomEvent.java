package main;

import java.util.Random;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class RandomEvent extends ListenerAdapter {
	private Message eventMessage;
	private boolean finished;
	private Guild guild;
	private Random RNG;
	
	public RandomEvent(Guild g) {
		this.eventMessage = null;
		this.guild = g;
		this.finished = false;
		this.RNG = new Random();
	}

	public boolean hasEnded() {
		return this.finished;
	}

	public void run() {
		this.guild.getTextChannels().get(this.RNG.nextInt(this.guild.getTextChannels().size())).sendMessage("Event !");
		
	}
	/*
    public void onMessageDelete(MessageDeleteEvent e) {
    	
    }
    
    public void onMessageBulkDelete(MessageBulkDeleteEvent e) {
    	
    }
    */
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
    	this.eventMessage.editMessage("Event terminé !");
    	this.finished = true;
    }
    /*
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
    	
    }
    
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent e) {
    	
    }
    */
}
