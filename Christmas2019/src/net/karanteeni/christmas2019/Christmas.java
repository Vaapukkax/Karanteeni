package net.karanteeni.christmas2019;

import net.karanteeni.christmas2019.eggsearch.ArenaCreate;
import net.karanteeni.christmas2019.eggsearch.ArenaModify;
import net.karanteeni.christmas2019.eggsearch.ArenaModifyListener;
import net.karanteeni.christmas2019.eggsearch.DeleteArenaData;
import net.karanteeni.christmas2019.eggsearch.EggArenaStart;
import net.karanteeni.christmas2019.eggsearch.EggArenaStop;
import net.karanteeni.christmas2019.eggsearch.EggBlock;
import net.karanteeni.christmas2019.eggsearch.FinishArenaCreate;
import net.karanteeni.christmas2019.eggsearch.GameState;
import net.karanteeni.christmas2019.eggsearch.PlayerQuit;
import net.karanteeni.christmas2019.skinkisa.SkinkisaManager;
import net.karanteeni.christmas2019.snowfigth.StartSnowFight;
import net.karanteeni.christmas2019.worldguard.WorldGuardManager;
import net.karanteeni.core.KaranteeniPlugin;

public class Christmas extends KaranteeniPlugin {
	private ChatInteracept chat = null;
	WorldGuardManager wgm = null;
	//public HashMap<Integer, ChristmasButton> buttons = new HashMap<Integer, ChristmasButton>();
	private GameState eggGameState;
	private SkinkisaManager skinKisaManager;
	
	public Christmas() {
		super(true);
		//getActionBlockManager().registerClass(ChristmasButton.class);
		getActionBlockManager().registerClass(EggBlock.class);
		eggGameState = new GameState();
	}

	
	@Override
	public void onLoad() {
		wgm = new WorldGuardManager();
		wgm.registerFlags();
	}
	
	
	@Override
	public void onEnable() {
		if(wgm != null && this.getServer().getPluginManager().getPlugin("WorldGuard") != null && 
				this.getServer().getPluginManager().getPlugin("WorldGuard").isEnabled()) {
			wgm.register();	
		} else {
			wgm = null;
		}
		
		// register arena modify events
		getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
		getServer().getPluginManager().registerEvents(new ArenaModifyListener(), this);
		// register arena modify command
		ArenaCreate chainer = new ArenaCreate(this);
		ArenaModify modifier = new ArenaModify();
		chainer.addComponent("modify", modifier);
		DeleteArenaData dad = new DeleteArenaData();
		chainer.addComponent("clear-all-data", dad);
		EggArenaStart startComp = new EggArenaStart();
		chainer.addComponent("start-match", startComp);
		EggArenaStop stopComp = new EggArenaStop();
		chainer.addComponent("stop-match", stopComp);
		FinishArenaCreate fac = new FinishArenaCreate();
		chainer.addComponent("finish-modify", fac);
		chainer.register();
		
		(new StartSnowFight(this)).register();
		
		skinKisaManager = new SkinkisaManager();
		skinKisaManager.registerCommand(this);
		// register events
		/*chat = new ChatInteracept(this);
		getServer().getPluginManager().registerEvents(chat, this);
		
		// register commands
		(new CreateNappi(this)).register();
		(new DeleteNappi(this)).register();
		(new Luukut(this)).register();
		(new StartSnowFight(this)).register();
		
		// register timer to check on doors
		getTimerHandler().registerTimer(new DoorChecker(), 500);
		
		// register events
		getServer().getPluginManager().registerEvents(new LanterBreakEvent(this), this);
		
		// register commands
		CountLanternCMD lcmd = new CountLanternCMD(this, "countlanterns", "a", "a", "a", Arrays.asList());
		lcmd.setPermission("karanteenials.player.gamemode.spectator.self");
		lcmd.register();*/
	}
	
	
	public GameState getGameState() {
		return eggGameState;
	}
	
	
	public SkinkisaManager getSkinkisaManager() {
		return skinKisaManager;
	}
	
	
	public static Christmas getInstance() {
		return Christmas.getPlugin(Christmas.class);
	}
	
	
	/**
	 * Returns the worldguard manager
	 * @return
	 */
	public WorldGuardManager getWorldGuard() {
		return wgm;
	}
	
	
	public ChatInteracept getChatManager() {
		return chat;
	}
	
	
	@Override
	public void onDisable() {
		
	}
}
