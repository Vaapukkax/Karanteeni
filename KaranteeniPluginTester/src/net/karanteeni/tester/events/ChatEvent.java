package net.karanteeni.tester.events;

import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.particle.ParticleShape;
import net.karanteeni.core.timers.KaranteeniTimer;
import net.karanteeni.tester.TesterMain;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.PacketPlayInFlying.PacketPlayInLook;
import net.minecraft.server.v1_13_R2.PacketPlayInFlying.PacketPlayInPosition;
import net.minecraft.server.v1_13_R2.PacketPlayInFlying.PacketPlayInPositionLook;
import net.minecraft.server.v1_13_R2.PacketPlayOutBlockChange;
import net.minecraft.server.v1_13_R2.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_13_R2.PacketPlayOutUpdateTime;

public class ChatEvent implements Listener{
	KaranteeniPlugin plugin = TesterMain.getPlugin(TesterMain.class);
	private ParticleShape pshape;
	public ChatEvent()
	{
		TesterMain.getTranslator().registerTranslation(plugin, "chat-message", "Chat message has been sent!");
		TesterMain.getTranslator().registerTranslation(plugin, "actionbar-message", "Actionbar has been sent!");
	}
	
	
	@EventHandler
	private void onLeave(PlayerQuitEvent event) {
		//removePlayer(event.getPlayer());
	}
	
	
	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		//injectPlayer(event.getPlayer());
	}
	
	
	private void removePlayer(Player player) {
		Channel channel = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
			return null;
		});
	}
	
	
	private void injectPlayer(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler(){
			
			
			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
				if(!((packet instanceof PacketPlayInPosition) || (packet instanceof PacketPlayInLook) || (packet instanceof PacketPlayInPositionLook))) {
					if(packet instanceof PacketPlayOutBlockChange) {
						PacketPlayOutBlockChange p = (PacketPlayOutBlockChange)packet;
						Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + p.toString() + " eet");
					} else if(packet instanceof PacketPlayOutTileEntityData) {
						PacketPlayOutTileEntityData p = (PacketPlayOutTileEntityData)packet;
						Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.BLUE + p.toString() + " eeee");
					}
					
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ" + ChatColor.RED + packet.toString());
				}
				super.channelRead(channelHandlerContext, packet);
			}
			
			
			@Override
			public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
				if(!(packet instanceof PacketPlayOutUpdateTime)) {
					Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "PACKET WRITE" + ChatColor.GREEN + packet.toString());
				}
				super.write(channelHandlerContext, packet, channelPromise);
			}
		};
		
		ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
	}
	
	
	
	
	
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		/*event.setCancelled(true);
		
		String msg = event.getPlayer().getDisplayName() + " > " + event.getMessage() + " ";
		
		List<Player> players = new ArrayList<Player>();
		for(Player player : Bukkit.getOnlinePlayers())
			players.add(player);*/
		
		/* L�HET� CHATVIESTI */
		//TesterMain.getMessager().sendMessage(players, Sounds.PLING_HIGH.get(), TesterMain.getTranslator().getTranslation(plugin, event.getPlayer(), "chat-message"));
		
		/* L�HET� TITLE + ACTIONBAR */
		/*for(Player player : players)
		{
			TesterMain.getMessager().sendTitle(0.1f, 0.1f, 1.2f, player, "�6"+msg, "�6"+msg, Sounds.NONE.get());
			TesterMain.getMessager().sendActionBar(player, Sounds.NONE.get(), "�6"+TesterMain.getTranslator().getTranslation(plugin, event.getPlayer(), "actionbar-message"));
		}*/
		
		/* LUO BOSSBAR */
		/*BossBar bar = Bukkit.createBossBar(msg, BarColor.YELLOW, BarStyle.SOLID);
		
		List<String> texts = new ArrayList<String>();
		texts.add(msg);
		
		for(int i = 0; i < msg.length(); i++)
		{
			char c = texts.get(i).charAt(0);
			String text = texts.get(i).substring(1);
			texts.add(text+c);
		}*/
		
		/* L�HET� BOSSBAR */
		//TesterMain.getMessager().sendBossbar(players, Sounds.NONE.get(), 5f, 3, true, bar, texts);
		
		/* LUO PARTICLESHAPE KUUTION MUODOSSA */
		/*if(pshape == null)
		{
			UndirectedAdjacencyListGraph<Point3D> shape = new UndirectedAdjacencyListGraph<Point3D>();
			Point3D top = new Point3D(0,2,0);
			Point3D bottom = new Point3D(0,-2,0);
			
			shape.insertVertex(top);
			shape.insertVertex(bottom);
			
			shape.insertVertex(new Point3D(-1,0,0));
			shape.insertVertex(new Point3D(-0.5,0,Math.sqrt(3)/2));
			shape.insertVertex(new Point3D(0.5,0,Math.sqrt(3)/2));
			shape.insertVertex(new Point3D(1,0,0));
			shape.insertVertex(new Point3D(0.5,0,-Math.sqrt(3)/2));
			shape.insertVertex(new Point3D(-0.5,0,-Math.sqrt(3)/2));
			
			shape.insertEqualsUndirectedEdge(top, new Point3D(-1,0,0));
			shape.insertEqualsUndirectedEdge(top, new Point3D(-0.5,0,Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(top, new Point3D(0.5,0,Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(top, new Point3D(1,0,0));
			shape.insertEqualsUndirectedEdge(top, new Point3D(0.5,0,-Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(top, new Point3D(-0.5,0,-Math.sqrt(3)/2));
			
			shape.insertEqualsUndirectedEdge(bottom, new Point3D(-1,0,0));
			shape.insertEqualsUndirectedEdge(bottom, new Point3D(-0.5,0,Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(bottom, new Point3D(0.5,0,Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(bottom, new Point3D(1,0,0));
			shape.insertEqualsUndirectedEdge(bottom, new Point3D(0.5,0,-Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(bottom, new Point3D(-0.5,0,-Math.sqrt(3)/2));
			
			shape.insertEqualsUndirectedEdge(new Point3D(-1,0,0), new Point3D(-0.5,0,Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,0,Math.sqrt(3)/2), new Point3D(0.5,0,Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,0,Math.sqrt(3)/2), new Point3D(1,0,0));
			shape.insertEqualsUndirectedEdge(new Point3D(1,0,0), new Point3D(0.5,0,-Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,0,-Math.sqrt(3)/2), new Point3D(-0.5,0,-Math.sqrt(3)/2));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,0,-Math.sqrt(3)/2), new Point3D(-1,0,0));
			pshape = new ParticleShape(shape, event.getPlayer().getLocation());
			pshape.setRotation(0, 0, 0, 4);
			Point3D loc = new Point3D(event.getPlayer().getLocation().getX(),event.getPlayer().getLocation().getY(),event.getPlayer().getLocation().getZ());

			pshape.startAnimation(plugin, 
					new ParticlePlayer(), pshape.getLocation().toVector(), new Vector(0,0,180), ParticleShape.ANIMATION.LINEAR, 10000, (short)2);
		}
		else
		{
			pshape.stopAnimation();
			pshape = null;
		}*/
		
		/*KaranteeniPerms perms = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		//Group playerGroup = perms.getPlayerModel().getLocalGroup(event.getPlayer());
		//String prefix = playerGroup.getPrefix(event.getPlayer(), false);
		
		PermissionPlayer pp = perms.getPlayerModel().getPermissionPlayer(event.getPlayer().getUniqueId());
		String prefix = pp.getPrefix(event.getPlayer(), DATA_TYPE.GROUP_AND_PLAYER, false);
		event.setFormat(prefix + "%s" + pp.getSuffix(DATA_TYPE.GROUP_AND_PLAYER) + "%s");*/
		
		/*String[] parts = event.getMessage().split(" ");
		if(parts.length == 3)
		{
			int timerCount = 1;
			int runTimes = 1;
			boolean exception = false;
			
			try
			{
				timerCount = Integer.parseInt(parts[0]);
				runTimes = Integer.parseInt(parts[1]);
				exception = Boolean.parseBoolean(parts[2]);
			} catch(Exception e)
			{
				
			}
			
			testTimer(timerCount, runTimes, exception);
		}*/
	}
	
	private void testTimer(int timerCount, int runTimes, boolean exceptionTimer)
	{
		if(!exceptionTimer)
		for(int i = 0; i < timerCount; ++i)
		{
			if(i == 0)
				KaranteeniPlugin.getTimerHandler().registerTimer(new TimerTester(true, true, runTimes), 1);
			else if(i+1 == timerCount)
				KaranteeniPlugin.getTimerHandler().registerTimer(new TimerTester(true, true, runTimes), 1);
			else
				KaranteeniPlugin.getTimerHandler().registerTimer(new TimerTester(false, false, runTimes), 1);
		}
		else
		for(int i = 0; i < timerCount; ++i)
		{
			if(i == 0)
				KaranteeniPlugin.getTimerHandler().registerTimer(new TimerTesterException(true, true, runTimes), 1);
			else if(i+1 == timerCount)
				KaranteeniPlugin.getTimerHandler().registerTimer(new TimerTesterException(true, true, runTimes), 1);
			else
				KaranteeniPlugin.getTimerHandler().registerTimer(new TimerTesterException(false, false, runTimes), 1);
		}
	}
	
	private void testTimerException(int runtimes)
	{
		
	}
	
	class TimerTester implements KaranteeniTimer
	{
		private double val;
		private int runTimes = 0;
		private boolean printStart = false;
		private boolean printStop = false;
		private long startTime;
		private boolean runOnce = false;
		
		public TimerTester (boolean printStart, boolean printStop, int runTimes)
		{ 
			this.printStart = printStart;
			this.printStop = printStop; 
			this.runTimes = runTimes;
		}
		
		@Override
		public void runTimer() 
		{
			if(printStart)
			{
				Bukkit.broadcastMessage("Timer started at:" + System.currentTimeMillis());
				this.printStart = false;
			}
			
			if(!runOnce)
			{
				this.startTime = System.currentTimeMillis();
				this.runOnce = true;
			}
			
			while(runTimes-- > 0)
			{
				Random rnd = new Random();
				val = rnd.nextDouble(); //Generate a random double value
				//--runTimes;
			}	
			
				//Unregister timer to prevent infinite loop
				if(runTimes <= 0)
					KaranteeniPlugin.getTimerHandler().unregisterTimer(this);
		}

		@Override
		public void timerStopped() 
		{
			//Print time when the timer was stopped
			if(printStop)
			{
				Bukkit.broadcastMessage("Result: " + (System.currentTimeMillis() - this.startTime));
			}
		}

		@Override
		public void timerWait() { }
	}
	
	class TimerTesterException implements KaranteeniTimer
	{
		private double val;
		private int runTimes = 0;
		private boolean printStart = false;
		private boolean printStop = false;
		private long startTime;
		private boolean runOnce = false;
		
		public TimerTesterException (boolean printStart, boolean printStop, int runTimes)
		{ 
			this.printStart = printStart;
			this.printStop = printStop; 
			this.runTimes = runTimes;
		}
		
		@Override
		public void runTimer() 
		{
			if(printStart)
			{
				Bukkit.broadcastMessage("Timer started at:" + System.currentTimeMillis());
				this.printStart = false;
			}
			
			if(!runOnce)
			{
				this.startTime = System.currentTimeMillis();
				this.runOnce = true;
			}
			
			Random rnd = new Random();
			val = rnd.nextDouble(); //Generate a random double value
			--runTimes;
			
			//Unregister timer to prevent infinite loop
			if(runTimes == 0)
				KaranteeniPlugin.getTimerHandler().unregisterTimer(this);
			
			int i = 100/0;
		}

		@Override
		public void timerStopped() 
		{
			//Print time when the timer was stopped
			if(printStop)
			{
				Bukkit.broadcastMessage("Result: " + (System.currentTimeMillis() - this.startTime));
			}
		}

		@Override
		public void timerWait() { }
	}
	
	
	
	private class ParticlePlayer implements ParticleShape.Animatable 
	{
		@Override
		public void animationStopped(Location arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void playCornerParticle(Location arg0) {
			//Draw the flame particle to corners
			arg0.getWorld().spawnParticle(Particle.TOTEM, arg0, 1, 0d, 0d, 0d, 0d);
		}

		@Override
		public void playEdgeParticle(Location arg0) {
			//Draw the flame particle to edges
			arg0.getWorld().spawnParticle(Particle.FLAME, arg0, 1, 0d, 0d, 0d, 0d);
		}	
	}
}
