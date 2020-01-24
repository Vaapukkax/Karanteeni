package net.karanteeni.core.data;


/**
 * ChannelKey is intended to ease the management of channels in BungeeCord channel management.
 * This channelkey will format the channel and subchannel to the correct format given valid arguments.
 * @author Nuubles
 *
 */
public abstract class ChannelKey {
	private final String channel;
	private final String subChannel;
	
	public ChannelKey(String channel) {
		this.channel = channel.toLowerCase();
		this.subChannel = null;
	}
	
	
	public ChannelKey(String channel, String subChannel) {
		this.channel = channel;
		this.subChannel = subChannel.toLowerCase();
	}
	
	
	/**
	 * Returns this channels key
	 * @return key of this channel
	 */
	public String getChannel() {
		return this.channel;
	}
	
	
	/**
	 * Returns the subChannel key
	 * @return subchannel key if set
	 */
	public String getSubChannel() {
		return this.subChannel;
	}
	
	
	/**
	 * Checks if this channel key is fully formatted
	 * @return true if both channel and subchannel have been set. Otherwise false and you should use formatChannel etc.
	 */
	public boolean isFull() {
		return subChannel != null;
	}
	
	
	/**
	 * Returns a formatted channel key with channel
	 * @param subChannel subchannel this will be using
	 * @return the channel formatted from channel and subchannel divided by :
	 */
	public String formatChannel(String subChannel) {
		return channel + ":" + subChannel.replace(':', '-').toLowerCase();
	}
	
	
	/**
	 * Returns the formatted channel in either
	 * Channel or Channel:SubChannel
	 */
	public String toString() {
		if(subChannel != null)
			return channel + ":" + subChannel;
		return channel;
	}
}
