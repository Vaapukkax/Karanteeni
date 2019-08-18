package net.karanteeni.karanteenials.teleport;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitTask;

import net.karanteeni.core.information.Teleporter;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteenials.Karanteenials;

public class TeleportRequest {
	private static final NamespacedKey key = 
			new NamespacedKey(Karanteenials.getPlugin(Karanteenials.class), "teleport-request");
	private KPlayer 			asker;
	private KPlayer 			receiver;
	private TeleportType 		type;
	private Location 			location;
	private Consumer<Player> 	override 	= null;
	
	private BiConsumer<Player,Player> tptimeout 	= null;
	private BiConsumer<Player,Player> accepted 		= null; //asker receiver
	private BiConsumer<Player,Player> denied 		= null;
	
	private BukkitTask timeout = null;
	
	/**
	 * Initializes a teleport request and automatically starts the countdown to remove
	 * itself. Handles all the data and etc. itself
	 * @param asker Sender of the tp request
	 * @param receiver Receiver of the tp request
	 * @param type Type of the teleport
	 */
	public TeleportRequest(KPlayer asker, KPlayer receiver, TeleportType type) {
		this.asker = asker;
		this.receiver = receiver;
		//We're overriding a previous teleport request
		if(override != null && receiver.dataExists(key)) {
			override.accept(receiver.getPlayer());
			TeleportRequest request = ((TeleportRequest)receiver.getData(key));
			//Send the timeout message to previous teleport requester
			if(request != null)
			{
				if(request.tptimeout != null && request.asker.getPlayer().isOnline())
					request.tptimeout.accept(request.asker.getPlayer(), null);
				request.clearMemory();
			}
		}
			
		receiver.setCacheData(key, this);
		
		//Set the teleport location if type is here
		if(type == TeleportType.HERE)
			this.location = asker.getPlayer().getLocation();
		this.type = type;
		
		//Create a timer to remove this request after 1200 ticks (1 minute)
		timeout = Bukkit.getScheduler().runTaskLater(Karanteenials.getPlugin(Karanteenials.class), 
			new Runnable() {
				@Override
				public void run() {
					//Run the events
					if(tptimeout != null)
						tptimeout.accept(asker.getPlayer(), receiver.getPlayer());
					
					//Remove this data from kplayer
					receiver.removeData(key);
					
				}}, 1200); // 1 minute timeout to teleport request
	}
	
	/**
	 * Clears the object everywhere to be taken care of the trashman
	 */
	public void clearMemory() {
		if(receiver.dataExists(key)) {
			TeleportRequest request = ((TeleportRequest)receiver.getData(key));
			if(request != null)
			{
				request.timeout.cancel();
				receiver.removeData(key);
			}
		}
	}
	
	/**
	 * Accepts this teleport request, teleports players and removed
	 * @param safe is the teleport safe
	 * @param safeFail function to be called if safe teleport fails or either of players is offline. 
	 * First argument is the sender of request and latter is receiver of the teleport request
	 */
	public void acceptRequest(boolean safe, BiConsumer<Player, Player> safeFail)
	{
		Teleporter teleporter = new Teleporter(this.getTeleportLocation());
		
		if(!asker.getPlayer().isOnline() || !receiver.getPlayer().isOnline()) {
			safeFail.accept(asker.getPlayer(), receiver.getPlayer());
			return;
		}
		
		if(type == TeleportType.HERE) {
			net.karanteeni.karanteenials.functionality.Back back = 
					new net.karanteeni.karanteenials.functionality.Back(this.receiver.getPlayer());
			Location oldLoc = receiver.getPlayer().getLocation();
			
			boolean successful = teleporter.teleport(receiver.getPlayer(), safe, false, true, TeleportCause.COMMAND) != null;
			
			if(!successful) { //if teleport was not successful, cancel the code
				safeFail.accept(asker.getPlayer(), receiver.getPlayer());
				return;
			}
			
			//Run the accepted commands
			if(accepted != null)
				accepted.accept(asker.getPlayer(), receiver.getPlayer());
			
			back.setBackLocation(oldLoc); //This this location to be the previous location
		} else {
			net.karanteeni.karanteenials.functionality.Back back = 
					new net.karanteeni.karanteenials.functionality.Back(this.receiver.getPlayer());
			Location oldLoc = asker.getPlayer().getLocation();
			
			boolean successful = teleporter.teleport(asker.getPlayer(), safe, false, true, TeleportCause.COMMAND) != null;
			
			if(!successful) {
				safeFail.accept(asker.getPlayer(), receiver.getPlayer());
				return;
			}
			
			//Run the accepted commands
			if(accepted != null)
				accepted.accept(asker.getPlayer(), receiver.getPlayer());
			
			back.setBackLocation(oldLoc); //This this location to be the previous location
		}
		
		clearMemory(); //Remove the pointers to this object
	}
	
	/**
	 * Declines this teleport request and removes from memory
	 */
	public void declineRequest() 
	{
		if(denied != null)
			denied.accept(asker.getPlayer(), receiver.getPlayer());
		
		clearMemory();
	}
	
	/**
	 * The given consumer is run when an existing teleport request is overwritten from
	 * given player.
	 * The given parameter is the receiver of this teleport request
	 * @param consumer
	 */
	public void setOverwritten(Consumer<Player> consumer)
	{ this.override = consumer; }
	
	/**
	 * The given consumer is run when the timer runs out.
	 * The given parameter is the sender of this teleport request
	 * @param consumer
	 */
	public void setTimeoutMethod(BiConsumer<Player,Player> consumer)
	{ this.tptimeout = consumer; }
	
	/**
	 * The given consumer will be fired when the request has been accepted
	 * @param consumer
	 */
	public void setAcceptedMethod(BiConsumer<Player,Player> consumer) 
	{ this.accepted = consumer; }
	
	/**
	 * The given consumer will be fired when the request has been denied
	 * @param consumer
	 */
	public void setDeniedMethod(BiConsumer<Player,Player> consumer) 
	{ this.denied = consumer; }
	
	/**
	 * Returns the type of this teleport request
	 * @return type of this teleport request
	 */
	public TeleportType getTeleportType()
	{ return this.type; }
	
	/**
	 * Returns the location where this teleport request points to
	 * @return destination of teleport request
	 */
	public Location getTeleportLocation() {
		if(this.location == null)
		{
			if(receiver.getPlayer().isOnline() && asker.getPlayer().isOnline())
				return receiver.getPlayer().getLocation();
			else
				return null;
		}
		return this.location.clone();
	}
	
	/**
	 * Types for the teleport
	 * @author Nuubles
	 */
	public static enum TeleportType {
		THERE, HERE
	}
}
