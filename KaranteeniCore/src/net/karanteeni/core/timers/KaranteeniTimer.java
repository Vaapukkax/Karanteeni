package net.karanteeni.core.timers;

public interface KaranteeniTimer {
	/* Laukeaa joka hetki kun on annettu initialisoinnissa */
	public abstract void runTimer();
	
	/* Laukeaa kun ajastin pysäytetään */
	public void timerStopped();
	
	/* Laukeaa joka tick kun odotetaan oikeaa timerin laukeamista */
	public void timerWait();
}