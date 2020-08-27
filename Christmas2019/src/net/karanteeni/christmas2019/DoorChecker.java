package net.karanteeni.christmas2019;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import net.karanteeni.core.timers.KaranteeniTimer;

public class DoorChecker implements KaranteeniTimer {
	private HashMap<Long, Doors> doors = new HashMap<Long, Doors>();
	
	
	public DoorChecker() {
		World world = Bukkit.getWorld("Survival");
		if(world == null)
			return;
		doors.put(getDate(1), new Doors(false, Arrays.asList(new Location(world, 7842, 64, -7322))));
		doors.put(getDate(2), new Doors(false, Arrays.asList(new Location(world, 7784, 64, -7248), new Location(world, 7783, 64, -7245))));
		doors.put(getDate(3), new Doors(true, Arrays.asList(new Location(world, 7823, 66, -7352))));
		doors.put(getDate(4), new Doors(false, Arrays.asList(new Location(world, 7927, 64, -7291))));
		doors.put(getDate(5), new Doors(false, Arrays.asList(new Location(world, 7812, 65, -7328), new Location(world, 7811, 65, -7328))));
		doors.put(getDate(6), new Doors(false, Arrays.asList(new Location(world, 7929, 65, -7260))));
		doors.put(getDate(7), new Doors(false, Arrays.asList(new Location(world, 7869,64, -7346))));
		doors.put(getDate(8), new Doors(false, Arrays.asList(new Location(world, 7790, 64, -7340), new Location(world, 7791, 64, -7340))));
		doors.put(getDate(9), new Doors(false, Arrays.asList(new Location(world, 7880, 65, -7371), new Location(world, 7881, 65, -7371))));
		doors.put(getDate(10), new Doors(true, Arrays.asList(new Location(world, 7818, 64, -7300))));
		doors.put(getDate(11), new Doors(false, Arrays.asList(new Location(world, 7764, 64, -7269))));
		doors.put(getDate(12), new Doors(false, Arrays.asList(new Location(world, 7851, 64, -7335))));
		doors.put(getDate(13), new Doors(true, Arrays.asList(new Location(world, 7880, 64, -7285), new Location(world, 7677, 64, -7288), new Location(world, 7677, 64, -7294), new Location(world, 7677, 64, -7299), new Location(world, 7880, 64, -7303), new Location(world, 7885, 64, -7303), new Location(world, 7892, 64, -7303), new Location(world, 7895, 64, -7300), new Location(world, 7895, 64, -7294), new Location(world, 7895, 64, -7288), new Location(world, 7892, 64, -7285), new Location(world, 7886, 64, -7285))));
		doors.put(getDate(14), new Doors(false, Arrays.asList(new Location(world, 7782, 65, -7257), new Location(world, 7783, 65, -7257))));
		doors.put(getDate(15), new Doors(true, Arrays.asList(new Location(world, 7909, 64, -7332))));
		doors.put(getDate(16), new Doors(false, Arrays.asList(new Location(world, 7889, 65, -7314), new Location(world, 7890, 65, -7314))));
		doors.put(getDate(17), new Doors(false, Arrays.asList(new Location(world, 7842, 64, -7335))));
		doors.put(getDate(18), new Doors(false, Arrays.asList(new Location(world, 7816, 68, -7272))));
		doors.put(getDate(19), new Doors(true, Arrays.asList(new Location(world, 7894, 62, -7230))));
		doors.put(getDate(20), new Doors(false, Arrays.asList(new Location(world, 7799, 70, -7213))));
		doors.put(getDate(21), new Doors(false, Arrays.asList(new Location(world, 7760, 72, -7325), new Location(world, 7761, 72, -7325))));
		doors.put(getDate(22), new Doors(false, Arrays.asList(new Location(world, 7897, 64, -7347))));
		doors.put(getDate(23), new Doors(true, Arrays.asList(new Location(world, 7917, 67, -7376))));
	}
	
	
	public long getDate(int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String dateStr = String.format("%s-12-2019 00:00:00", day);
		
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date.getTime();
	}
		
	
	@Override
	public void runTimer() {
		List<Long> toRemove = null;
		
		for(Entry<Long, Doors> entry : doors.entrySet()) {
			// we can open the door
			if(entry.getKey() <= System.currentTimeMillis()) {
				if(toRemove == null)
					toRemove = new ArrayList<Long>();
				entry.getValue().open();
				toRemove.add(entry.getKey());
			}
		}
		
		if(toRemove != null)
		for(Long key : toRemove)
			doors.remove(key);
	}

	
	@Override
	public void timerStopped() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void timerWait() {
		// TODO Auto-generated method stub
		
	}
}
