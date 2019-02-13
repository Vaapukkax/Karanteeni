package net.karanteeni.core.information.text;

public enum Prefix {
	PLUSPOSITIVE("§2>§a>§2>§a "),
	POSITIVE("§a>§2>§a> "),
	NEUTRAL("§6>§0>§6> "),
	NEGATIVE("§c>§0>§c> "),
	PLUSNEGATIVE("§4>§c>§4>§c "),
	GAME("§5>§2>§5>§a "),
	ERROR("§4>§c>§4>§4 ");
	
	
	Prefix(String text)
	{
		this.prefix = text;
	}
	
	private String prefix;
	
	@Override
	public String toString()
	{
		return prefix;
	}
}
