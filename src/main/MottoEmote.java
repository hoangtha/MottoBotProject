package main;

public class MottoEmote {
	public final String name;
	public final String url;
	public final String authorID;
	
	public MottoEmote(String input) {
		String split[] = input.split("\t");
		if(split.length!=3) {
			this.name = null;
			this.url = null;
			this.authorID = null;
		}
		else {
			this.name = split[0];
			this.url = split[1];
			this.authorID = split[2];
		}
	}
	
	public MottoEmote(String name, String authorID) {
		this.name = name;
		this.url = null;
		this.authorID = authorID;
	}

	public MottoEmote(String name, String url, String authorID) {
		this.name = name;
		this.url = url;
		this.authorID = authorID;
	}

	public MottoEmote() {
		this.name = null;
		this.url = null;
		this.authorID = null;
	}

	@Override
	public String toString() {
		return this.name + "\t" + this.url + "\t" + this.authorID;
	}
	
	@Override
	public int hashCode() {
		return this.name.toLowerCase().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MottoEmote other = (MottoEmote) obj;
		if (this.name == null && other.name != null)
			return false;
		if (!this.name.toLowerCase().equals(other.name.toLowerCase()))
			return false;
		return true;
	}
}
