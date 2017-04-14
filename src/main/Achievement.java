package main;

public enum Achievement {
	ACHV_DUMMY("Dummy", "dummyicon", 1);
	
	public final String name;
	public final String iconURL;
	public final int rarity;
	
	private Achievement(String name, String iconURL, int rarity) {
		this.name = name;
		this.iconURL = iconURL;
		this.rarity = rarity;
	}
}
