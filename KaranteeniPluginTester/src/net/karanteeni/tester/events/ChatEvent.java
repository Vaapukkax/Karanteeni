package net.karanteeni.tester.events;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.Vector;

import net.karanteeni.core.KaranteeniPlugin;
import net.karanteeni.core.data.math.Point3D;
import net.karanteeni.core.data.structures.UndirectedAdjacencyListGraph;
import net.karanteeni.core.particle.ParticleShape;
import net.karanteeni.tester.TesterMain;

public class ChatEvent implements Listener{
	KaranteeniPlugin plugin = TesterMain.getPlugin(TesterMain.class);
	private ParticleShape pshape;
	public ChatEvent()
	{
		TesterMain.getTranslator().registerTranslation(plugin, "chat-message", "Chat message has been sent!");
		TesterMain.getTranslator().registerTranslation(plugin, "actionbar-message", "Actionbar has been sent!");
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		/*event.setCancelled(true);
		
		String msg = event.getPlayer().getDisplayName() + " > " + event.getMessage() + " ";
		
		List<Player> players = new ArrayList<Player>();
		for(Player player : Bukkit.getOnlinePlayers())
			players.add(player);*/
		
		/* LÄHETÄ CHATVIESTI */
		//TesterMain.getMessager().sendMessage(players, Sounds.PLING_HIGH.get(), TesterMain.getTranslator().getTranslation(plugin, event.getPlayer(), "chat-message"));
		
		/* LÄHETÄ TITLE + ACTIONBAR */
		/*for(Player player : players)
		{
			TesterMain.getMessager().sendTitle(0.1f, 0.1f, 1.2f, player, "§6"+msg, "§6"+msg, Sounds.NONE.get());
			TesterMain.getMessager().sendActionBar(player, Sounds.NONE.get(), "§6"+TesterMain.getTranslator().getTranslation(plugin, event.getPlayer(), "actionbar-message"));
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
		
		/* LÄHETÄ BOSSBAR */
		//TesterMain.getMessager().sendBossbar(players, Sounds.NONE.get(), 5f, 3, true, bar, texts);
		
		/* LUO PARTICLESHAPE KUUTION MUODOSSA */
		if(pshape == null)
		{
			UndirectedAdjacencyListGraph<Point3D> shape = new UndirectedAdjacencyListGraph<Point3D>();
			shape.insertVertex(new Point3D(-0.5,-0.5,-0.5));
			shape.insertVertex(new Point3D(0.5,-0.5,-0.5));
			shape.insertVertex(new Point3D(-0.5,0.5,-0.5));
			shape.insertVertex(new Point3D(-0.5,-0.5,0.5));
			shape.insertVertex(new Point3D(-0.5,0.5,0.5));
			shape.insertVertex(new Point3D(0.5,-0.5,0.5));
			shape.insertVertex(new Point3D(0.5,0.5,-0.5));
			shape.insertVertex(new Point3D(0.5,0.5,0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,-0.5,-0.5), new Point3D(0.5,-0.5,-0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,-0.5,-0.5), new Point3D(-0.5,-0.5,0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,-0.5,-0.5), new Point3D(-0.5,0.5,-0.5));
			
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,-0.5,0.5), new Point3D(0.5,-0.5,-0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,-0.5,0.5), new Point3D(-0.5,-0.5,0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,-0.5,0.5), new Point3D(0.5,0.5,0.5));
			
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,0.5,0.5), new Point3D(-0.5,-0.5,0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,0.5,0.5), new Point3D(-0.5,0.5,-0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(-0.5,0.5,0.5), new Point3D(0.5,0.5,0.5));
			
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,0.5,-0.5), new Point3D(0.5,-0.5,-0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,0.5,-0.5), new Point3D(-0.5,0.5,-0.5));
			shape.insertEqualsUndirectedEdge(new Point3D(0.5,0.5,-0.5), new Point3D(0.5,0.5,0.5));
			pshape = new ParticleShape(shape, event.getPlayer().getLocation());
			pshape.setRotation(45, 35.3, 30, 2);
			//pshape.setRotation(0, 30, 0, 2);
			/*Point3D loc = new Point3D(event.getPlayer().getLocation().getX(),event.getPlayer().getLocation().getY(),event.getPlayer().getLocation().getZ());
			pshape.startAnimation(
					null, 
					new ParticlePlayer(), 
					loc, 
					new Vector(1,1,1), 
					ParticleShape.ANIMATION.LINEAR, 
					5000l);*/
			pshape.show(TesterMain.getPlugin(TesterMain.class), new ParticlePlayer());
		}
		else
		{
			pshape.hide();
			pshape = null;
		}
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
			arg0.getWorld().spawnParticle(Particle.FLAME, arg0, 1, 0d, 0d, 0d, 0d);
		}

		@Override
		public void playEdgeParticle(Location arg0) {
			//Draw the flame particle to edges
			arg0.getWorld().spawnParticle(Particle.FLAME, arg0, 1, 0d, 0d, 0d, 0d);
		}	
	}
}
