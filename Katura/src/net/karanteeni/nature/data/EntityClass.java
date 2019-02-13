package net.karanteeni.nature.data;

public enum EntityClass {
	NOT_VANILLA("NOT_VANILLA"),
	NPC("NPC"),
	VANILLA("VANILLA"),
	KEY("Katura");
	
	
	private String tag;
	EntityClass(String tag)
	{
		this.tag = tag;
	}
	
	@Override
	public String toString()
	{
		return tag;
	}
}
