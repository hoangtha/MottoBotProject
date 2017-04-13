package main;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class MemberStatistics implements Serializable {
	private static final long serialVersionUID = 42L;
	public int messages;
	public int commandes;
	
	public Duration tempsEnLigne;
	public Duration tempsEnVocal;
	public Instant debutEnLigne;
	public Instant debutVocal;
	public boolean enLigne;
	public boolean enVocal;
	
	public MemberStatistics() {
		this.messages = 0;
		this.commandes = 0;
		this.tempsEnLigne = Duration.ZERO;
		this.tempsEnVocal = Duration.ZERO;
		this.debutEnLigne = null;
		this.debutVocal = null;
		this.enLigne = false;
		this.enVocal = false;
	}
}
