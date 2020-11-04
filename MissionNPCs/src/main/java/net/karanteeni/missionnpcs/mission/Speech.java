package net.karanteeni.missionnpcs.mission;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import lombok.NonNull;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.npc.NPC;
import net.karanteeni.missionnpcs.MissionNPCs;

public class Speech {
	private ArrayList<String> text;
	
	/**
	 * Returns the speech text
	 * @return text of the speech
	 */
	public List<String> getSpeech() {
		return new ArrayList<String>(text);
	}
	
	
	/**
	 * Appends text to the speech
	 * @param text text to add
	 */
	public void addText(@NonNull final String text) {
		if(this.text == null)
			this.text = new ArrayList<String>();
		this.text.add(text);
	}
	
	
	/**
	 * Removes the text from the current index of the speech list
	 * @param index index to remove the speech from
	 * @return index which is removed
	 */
	public String removeText(int index) {
		if(text.size() < index && index >= 0) {
			return this.text.remove(index);
		}
		return null;
	}
	
	
	/**
	 * Counts the read time for a piece of text
	 * @param text 
	 * @return reading time in MS
	 */
	private int countDelay(@NonNull final String text) {
		String[] parts = text.split(" ");
		return (text.length() / (text.length() - parts.length)/parts.length) / 180 * 60 * 1000 + 500;
	}
	
	
	/**
	 * Makes the given entity speak the speech
	 * @param entity
	 */
	public void speak(@NonNull final NPC npc, @NonNull final Player receiver, Consumer<Player> speechFinisher) {
		int delay = 0;
		Iterator<String> iter = this.text.iterator();
		while(iter.hasNext()) {
			String text = iter.next();

			Bukkit.getScheduler().scheduleSyncDelayedTask(MissionNPCs.getPlugin(MissionNPCs.class),
				new Runnable() {
					@Override
					public void run() {
						/*entity.getWorld().playSound(
								entity.getLocation(),
								iter.hasNext() ? Sound.ENTITY_VILLAGER_AMBIENT : Sound.ENTITY_VILLAGER_TRADE,
								SoundCategory.PLAYERS, 1, 1);*/
						
						if(receiver.isOnline()) {
							SpeechContext context = new SpeechContext(npc, text, receiver);
							npc.getDefaultSpeechController().speak(context);
							
						}

						if(!iter.hasNext() && speechFinisher != null) {
							speechFinisher.accept(receiver);
						}
					}
				}, delay);

			delay += this.countDelay(text);
		}
	}
}
