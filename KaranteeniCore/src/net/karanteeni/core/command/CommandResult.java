package net.karanteeni.core.command;

import net.karanteeni.core.information.sounds.SoundType;
import net.karanteeni.core.information.sounds.Sounds;

public class CommandResult {
	public static final CommandResult INVALID_ARGUMENTS = new CommandResult(null, ResultType.INVALID_ARGUMENTS, Sounds.NO.get(), 		DisplayFormat.MESSAGE);
	public static final CommandResult NO_PERMISSION 	= new CommandResult(null, ResultType.NO_PERMISSION, 	Sounds.NO.get(), 		DisplayFormat.MESSAGE);
	public static final CommandResult OTHER 			= new CommandResult(null, ResultType.OTHER, 			Sounds.NO.get(), 		DisplayFormat.MESSAGE);
	public static final CommandResult SUCCESS 			= new CommandResult(null, ResultType.SUCCESS, 			Sounds.SETTINGS.get(), 	DisplayFormat.MESSAGE);
	public static final CommandResult ERROR 			= new CommandResult(null, ResultType.ERROR, 			Sounds.ERROR.get(), 	DisplayFormat.MESSAGE);
	public static final CommandResult NOT_FOR_CONSOLE	= new CommandResult(null, ResultType.NOT_FOR_CONSOLE, 	Sounds.ERROR.get(), 	DisplayFormat.MESSAGE);
	public static final CommandResult ONLY_CONSOLE		= new CommandResult(null, ResultType.ONLY_CONSOLE, 		Sounds.ERROR.get(), 	DisplayFormat.MESSAGE);
	public static final CommandResult ASYNC_CALLBACK	= new CommandResult(null, ResultType.ASYNC_CALLBACK, 	Sounds.NONE.get(), 		DisplayFormat.NONE);
	
	private final String commandResult; // message to be sent to the player
	private final ResultType type; // type of the result
	private final SoundType sound; // sound to be played with the message
	private final DisplayFormat format; // in which way is the message sent to the player
	
	public CommandResult(String message, ResultType resultType) {
		this.commandResult = message;
		this.sound = Sounds.NONE.get();
		this.type = resultType;
		this.format = DisplayFormat.MESSAGE;
	}
	
	
	public CommandResult(String message, ResultType resultType, SoundType sound) {
		this.commandResult = message;
		this.sound = sound;
		this.type = resultType;
		this.format = DisplayFormat.MESSAGE;
	}
	
	
	public CommandResult(ResultType resultType) {
		this.commandResult = null;
		this.sound = Sounds.NONE.get();
		this.type = resultType;
		this.format = DisplayFormat.NONE;
	}
	
	
	public CommandResult(ResultType resultType, SoundType sound) {
		this.commandResult = null;
		this.sound = sound;
		this.type = resultType;
		this.format = DisplayFormat.NONE;
	}

	
	public CommandResult(String message, ResultType resultType, DisplayFormat display) {
		this.commandResult = message;
		this.sound = Sounds.NONE.get();
		this.type = resultType;
		this.format = display;
	}
	
	
	public CommandResult(String message, ResultType resultType, SoundType sound, DisplayFormat display) {
		this.commandResult = message;
		this.sound = sound;
		this.type = resultType;
		this.format = display;
	}
	
	
	/**
	 * Returns the message set to this command result
	 * @return command result message to be sent to the player
	 */
	public String getResultMessage() {
		return this.commandResult;
	}
	
	
	/**
	 * Returns the type of this result
	 * @return
	 */
	public ResultType getResultType() {
		return this.type;
	}
	
	
	/**
	 * Returns the message this commandresult has
	 * @return the message to be displayed
	 */
	public String getMessage() {
		return this.commandResult;
	}
	
	
	/**
	 * Returns the sound to be played with this command result
	 * @return sound to be played
	 */
	public SoundType getSound() {
		return this.sound;
	}
	
	
	/**
	 * Get the way in which this message is supposed to be shown to the player
	 * @return the way this message is sent to the player
	 */
	public DisplayFormat getDisplayFormat() {
		return this.format;
	}
	
	
	/**
	 * Is the resultType of this CommandResult same as the other object
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		else if(obj instanceof ResultType) return ((ResultType)obj) == type;
		else if(obj instanceof CommandResult) return ((CommandResult)obj).type == type;
		return false;
	}
	
	
	/**
	 * The type of message to be sent
	 * @author Nuubles
	 *
	 */
	public static enum ResultType {
		INVALID_ARGUMENTS,
		NO_PERMISSION,
		OTHER,
		SUCCESS,
		ERROR,
		NOT_FOR_CONSOLE,
		ONLY_CONSOLE,
		ASYNC_CALLBACK;
	}
	
	
	/**
	 * The type of message to be sent
	 * @author Nuubles
	 *
	 */
	public static enum DisplayFormat {
		ACTIONBAR,
		BOSSBAR,
		TITLE,
		SUBTITLE,
		NONE,
		MESSAGE;
	}
}
