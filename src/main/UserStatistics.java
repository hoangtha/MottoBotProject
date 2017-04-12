package main;

import java.time.Duration;

public class UserStatistics {
	public int messages;
	public int commandes;
	public int reactions;
	
	public Duration tempsEnLigne;
	public Duration tempsEnVocal;
	
	public UserStatistics() {
		this.messages = 0;
		this.commandes = 0;
		this.reactions = 0;
		this.tempsEnLigne = Duration.ZERO;
		this.tempsEnVocal = Duration.ZERO;
	}
}
