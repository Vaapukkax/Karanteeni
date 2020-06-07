package net.karanteeni.christmas2019.skinkisa;

import net.karanteeni.core.KaranteeniPlugin;

public class SkinkisaManager {
	private SkinkisaState state;
	
	
	public SkinkisaManager() {
		state = new SkinkisaState();
	}
	
	
	public void registerCommand(KaranteeniPlugin plugin) {
		
	}
	
	
	public SkinkisaState getGameState() {
		return state;
	}
}
