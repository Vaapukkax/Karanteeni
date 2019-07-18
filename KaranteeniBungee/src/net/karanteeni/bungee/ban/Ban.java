package net.karanteeni.bungee.ban;

import java.util.UUID;

public class Ban {
	private UUID uuid;
	private long banTime;
	private long banLength;
	private String reason;
	private boolean alreadyExecuted;
	
	/**
	 * Constructs a new ban executor
	 * @param uuid uuid of banned player
	 * @param banTime time of ban
	 * @param banLength length of ban
	 * @param reason reason for ban
	 */
	public Ban(UUID uuid, long banTime, long banLength, String reason) {
		this.uuid = uuid;
		this.banLength = banLength;
		this.banTime = banTime;
		this.reason = reason;
		this.alreadyExecuted = false;
	}
	
	
	/**
	 * Private constructor which allows to set the ban execution state
	 * @param uuid uuid of banned player
	 * @param banTime time of ban
	 * @param banLength length of ban
	 * @param reason reason for ban
	 * @param executed has the ban already been executed
	 */
	private Ban(UUID uuid, long banTime, long banLength, String reason, boolean executed) {
		this.uuid = uuid;
		this.banLength = banLength;
		this.banTime = banTime;
		this.reason = reason;
		this.alreadyExecuted = executed;
	}
	
	
	/**
	 * Loads a possible existing ban with the given uuid
	 * @param uuid uuid of the player whose ban is being checked
	 * @return the ban loaded
	 */
	public static Ban loadBan(UUID uuid) {
		return null;
		// TODO
	}
	
	
	/**
	 * Loads a possible existing ban with the given name
	 * @param name name of the player whose ban is being checked
	 * @return the ban loaded
	 */
	public static Ban loadBan(String name) {
		return null;
		// TODO
	}
	
	
	/**
	 * Get ban reason
	 * @return ban reason
	 */
	public String getReason() {
		return this.reason;
	}
	
	
	
	
	
	/**
	 * Executes the ban. Returns true on successful ban and false if ban already exists
	 * @return was the player banned
	 */
	public boolean execute() {
		if(alreadyExecuted) return true;
		alreadyExecuted = true;
		return alreadyExecuted;
	}
}
