package main;

import java.util.Hashtable;

import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TallyCounter extends ListenerAdapter {
	Hashtable<String, UserStatistics> statistics;
	
}
