package main;

import java.util.Random;
import java.util.function.Consumer;

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
	private MottoBot bot;
	
	public RandomEvent(Guild g, MottoBot bot) {
		this.eventMessage = null;
		this.guild = g;
		this.finished = false;
		this.bot = bot;
		this.RNG = new Random();
	}

	public boolean hasEnded() {
		return this.finished;
	}

	public void run() {
		this.guild.getTextChannels().get(this.RNG.nextInt(this.guild.getTextChannels().size())).sendMessage("Event !").queue(new Consumer<Message>() {
			
			@Override
			public void accept(Message t) {
				RandomEvent.this.eventMessage = t;
			}
		});
	}
	/*
    public void onMessageDelete(MessageDeleteEvent e) {
    	
    }
    
    public void onMessageBulkDelete(MessageBulkDeleteEvent e) {
    	
    }
    */
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
    	if(e.getMessageId()==this.eventMessage.getId()) {
	    	this.eventMessage.editMessage("Event terminé !").queue();
	    	this.end();
    	}
    }
    /*
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
    	
    }
    
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent e) {
    	
    }
    */

	private void end() {
    	this.bot.getJda().removeEventListener(this);
    	this.finished = true;
	}
}
