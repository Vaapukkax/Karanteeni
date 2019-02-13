package net.karanteeni.core.information.text;

public enum MessageTitle {
	GAME_TITLE("§5< §a%s §5>§a "),
    BARS_NORMAL("§6§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡§6[§e %1$s §6]§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡§r\n%2$s§6\n§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡§6[§e %1$s §6]§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡"),
    BARS_GAME("§5§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡§5[§a %1$s §5]§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡§r\n%2$s§5\n§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡§5[§a %1$s §5]§m≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡"),
    KARANTEENI_LOGO("§0§l███\n"+
			"§0§l██§8§l███§0§l██§e§l███§0§l██§8§l███§0§l██\n" +
			"§0§l█§e§l█§0§l███§e§l███████§0§l███§e§l█§0§l█\n" +
			"§0§l█§e§l█████████████§0§l█\n" +
			"§0§l█§e§l████§4§l█§e§l███§4§l█§e§l████§0§l█\n" +
			"§0§l█§e§l███§4§l█§e§l█████§4§l█§e§l███§0§l█\n" +
			"§0§l█§e§l███§4§l█§e§l███████§4§l█§e§l███§0§l█\n" +
			"§0§l█§e§l███§4§l█§e§l███████§4§l█§e§l███§0§l█\n" +
			"§0§l█§e§l███§4§l█§e§l██§4§l███§e§l██§4§l█§e§l███§0§l█\n" +
			"§0§l█§e§l█████§4§l██§e§l███§4§l██§e§l█████§0§l█\n" +
			"§0§l█§e§l████§4§l████§e§l█§4§l████§e§l████§0§l█\n" +
			"§0§l█§e§l███§4§l█§e§l██§4§l█§e§l███§4§l█§e§l██§4§l█§e§l███§0§l█\n" +
			"§0§l█§e§l█§4§l█§e§l███§4§l█§e§l█§4§l█§e§l█§4§l█§e§l███§4§l█§e§l█§0§l█\n" +
			"§0§l█§e§l█§4§l█§e§l████§4§l███§e§l████§4§l█§e§l█§0§l█\n" +
			"§0§l█§e§l██████§4§l█§e§l█§4§l█§e§l██████§0§l█\n" +
			"§0§l█§e§l████§4§l█§e§l███§4§l█§e§l████§0§l█\n" +
			"§0§l█§e§l██§4§l██§e§l█████§4§l██§e§l██§0§l█\n" +
			"§0§l█§e§l█████████████§0§l█\n" +
			"§0§l█§e§l█§0§l███§e§l███████§0§l███§e§l█§0§l█\n" +
			"§0§l██§8§l███§l§0§l██§e§l███§0§l██§8§l███§0§l██\n" +
			"§0§l███\n\n%s");
	
	
	MessageTitle(String text)
	{
		this.prefix = text;
	}
	
	private String prefix;
	
	@Override
	public String toString()
	{
		return prefix;
	}
	
	public String getText(String title, String message)
	{
		return String.format(prefix, title, message);
	}
	
	public String getText(String title)
	{
		return String.format(prefix, title);
	}
}
