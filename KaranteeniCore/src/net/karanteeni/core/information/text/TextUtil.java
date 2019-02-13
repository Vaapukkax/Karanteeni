package net.karanteeni.core.information.text;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
	
	private static final String bad[] = {" vitu"," vittu", "kusee", " kusta", "kuseta",
			"perkele","paska","saatana","jumalauta","tippavaara",
			"kulli","kyrpä","pillu","perse","helvetti", "mulkku",
			"pippeli","kyrv","raisk","penis","ime muna",
			"ime mun muna","fuck","shit","kikkeli","tissi",
			"huora","nussi","pimppi","natsi","neekeri","nigg",
			"butthole","pyllyreikä","nekru","porno","jomejone70",
			"masuli","satan","helevetti","runkku","runkka","vetää käte",
			"jonttucraft","pelitcraft","motimaa","sienimaailma","seikkailumaa","kusipää","sperma","helveti",
			"dick", "helvete"};
	
	private static final String taiv[] = {"ataan","tti","tta","ttä","ata","aus","aan","ua","en","in","ti","er","ja","än","ä","a","n","s","o",""};
	private static final char sensor[] = {'*','#','?','&','$','^','½','~','%'};
	private static final String dash[] = {" ","-",". "," .","--","- "," -"};	
	private static final char skip[] = {'Ａ','ａ','Ｅ','ｅ','Ｉ','ｉ','Ｖ','ｖ','Ｕ','ｕ','Ｌ','ｌ','Ｋ','ｋ','Ｔ','ｔ','Ｓ','ｓ','Ｎ','ｎ','Ｍ','ｊ','Ｐ','ｐ','Ｙ','ｙ','Ｈ','ｈ'};
	private static final char skipReplacement[] = {'A','a','E','e','I','i','V','v','U','u','L','l','K','k','T','t','S','s','N','n','M','j','P','p','Y','y','H','h'};
	private static String msg3 = "";
	
	
	/**
	 * Makes text useable in json form
	 * @param msg message to be translated
	 * @return
	 */
	public static String formatJSON(String msg)
	{
		return msg.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	/**
	 * Turns all &k to §k in the string
	 * @param msg
	 * @return
	 */
	public static String formatMagic(String msg)
	{
		return msg.replace("&k", "§k");
	}
	
	/**
	 * Enables all format from text
	 * @param msg
	 * @return
	 */
	public static String formatFormat(String msg)
	{
		msg = msg.replace("&o", "§o");
		msg = msg.replace("&n", "§n");
		msg = msg.replace("&m", "§m");
		msg = msg.replace("&l", "§l");
		return msg.replace("&r", "§r");
	}
	
	/**
	 * Enables all colors from text
	 * @param msg
	 * @return
	 */
	public static String formatColor(String msg)
	{
		msg = msg.replace("&0", "§0");
		msg = msg.replace("&1", "§1");
		msg = msg.replace("&2", "§2");
		msg = msg.replace("&3", "§3");
		msg = msg.replace("&4", "§4");
		msg = msg.replace("&5", "§5");
		msg = msg.replace("&6", "§6");
		msg = msg.replace("&7", "§7");
		msg = msg.replace("&8", "§8");
		msg = msg.replace("&9", "§9");
		msg = msg.replace("&a", "§a");
		msg = msg.replace("&e", "§e");
		msg = msg.replace("&c", "§c");
		msg = msg.replace("&b", "§b");
		return msg.replace("&o", "§o");
	}
	
	/**
	 * Poistaa kaikki linkit tekstistä
	 * @param msg viesti josta poistetaan linkit
	 * @return viesti josta on poistettu linkit
	 */
	public static String parseLinks(String msg)
	{
		String msg2 = msg.replace(',', '.');
		
		// Pattern for recognizing a URL, based off RFC 3986
		final Pattern urlPattern = Pattern.compile(
		        "(http(s)?:\\/\\/)?([\\w-]+\\.)+[\\w-]+(\\/[\\w- ;,.\\/?%&=]*)?",
		        Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = urlPattern.matcher(msg2);
		while (matcher.find()) {
		    int matchStart = matcher.start();
		    int matchEnd = matcher.end();
		    if(matchStart < 0)
		    {
		    	matchStart = 0;
		    }
		  
		    ///Bukkit.getServer().broadcastMessage(""+matchStart + " " + matchEnd);
		    
		    String sub = msg2.substring(matchStart, matchEnd);
		    //Bukkit.getServer().broadcastMessage(sub);
		    
		    if(sub.length() > 7 && !sub.equalsIgnoreCase("karanteeni.net"))
		    {
	    		msg2 = msg2.replace(sub, "§8>§c§mLinkki§8<§r");
		    }
		    
		    //Bukkit.getServer().broadcastMessage(msg);
		}
		if (msg2.length() > 300)
		{
			msg2 = "§8>§c§mLinkki§8<§r";
		}
		
		//Jos viesti ei ole alkuperäinen eli on muokattu
		if(!msg2.equals(msg.replace(',', '.')))
		{
			return msg2;
		}
		
		return msg;
	}
	
	/**
	 * Sensuroi viestin
	 * @param msg
	 * @return
	 */
	public static String censor(String msg)
	{
		String original = msg;
		String msg2 = " " + msg.toLowerCase() + " ";
		msg3 = msg2;
		msg = " " + msg + " ";
		//msg = msg2;
		
		boolean contains = false;

		String bad2[] = new String[bad.length];
		
		msg3 = msg3.replace('!', 'i');
		msg3 = msg3.replace('1', 'i');
		msg3 = msg3.replace('3', 'e');
		msg3 = msg3.replace('4', 'a');
		msg3 = msg3.replace('5', 's');
		msg3 = msg3.replace('7', 't');
		msg3 = msg3.replace('$', 's');
		
		//Sensuroidaan oudot merkit pois
		for(int i = 0; i < skip.length; i++) {
			msg3 = msg3.replace(skip[i], skipReplacement[i]);
		}
		
		//Käydään kaikki taivutusmuodot läpi
		for(int m = 0; m <= taiv.length; m++)
		{
			//Tehdään lista sanoista taivutusmuotojen kanssa
			if(m == taiv.length)
			{
				bad2 = bad;
			}
			else
			{
				for(int j = 0; j < bad.length; j++)
				{
					bad2[j] = bad[j] + taiv[m];
				}
			}
				
			//Käydään kaikki sanat läpi
			for(String cen : bad2)
			{
				//Onko viestissä rumaa sanaa?
				if(msg3.contains(cen))
				{
					//String censor = "";
					//Jos eka merkki on välilyönti, älä korvaa
					if(cen.charAt(0) == ' ')
					{
						cen = cen.substring(1);
					}
					msg = replaceWord(msg, cen);
					contains = true;
				}
			}
			
			//Kaikki välit
			for(int i = 0; i < dash.length; i++)
			{
				String newbad[] = new String[bad2.length];
				
				//Kaikki sanat
				for(int j = 0; j < bad2.length; j++)
				{
					String dashed = "";
					//Sanan pituus
					for(int k = 0; k < bad2[j].length(); k++)
					{
						//Ei viimeinen/viimeinen
						if(k < bad2[j].length()-1 )
						{
							dashed = dashed + bad2[j].charAt(k) + " ";
						}
						else
						{
							dashed = dashed + bad2[j].charAt(k);
						}
					}
					newbad[j] = dashed;
				}
				
				for(String cen : newbad)
				{
					if(msg3.contains(cen))
					{
						msg = replaceWord(msg, cen);
						contains = true;
					}
				}
			}
			
			//Käydään sanat läpi johon on laitettu välilyönti väliin vain
			for(String d : dash)
			{
				String splitted[] = msg3.split(d);
				
				for(int j = 0; j < splitted.length-1; ++j)
				{
					//Käydään 3 sanaa läpi
					for(int n = 0; n < 5; n++)
					{
						String b = "";
						String real = "";
						
						//Käydään n sanan yhdistelmä läpi
						for(int i = n; i >= 0; --i)
						{
							if(j+i < splitted.length)
							{
								if(i != n)
								{
									b = splitted[j+i] + b;
									real = splitted[j+i] + d + real;
								}
								else
								{
									b = splitted[j+i];
									real = splitted[j+i];
								}
							}
						}
						//Bukkit.getServer().broadcastMessage(b);
						//Bukkit.getServer().broadcastMessage(real);
						
						//
						/// KORJAA VÄLIT ESIM -- !
						//
						
						//Käydään kaikki sensuroitavat läpi
						for(String cen : bad2)
						{
							//Onko eka merkki välilyönti?
							if(cen.charAt(0) != ' ')
							{
								//Sisältääkö tätä rumaa sanmaa
								if(cen.equals(b))
								{
									contains = true;
									msg = replaceWord(msg, real);
								}
							}
							else
							{
								//Sisältääkö tätä rumaa sanmaa
								if(cen.equals(' '+b))
								{
									contains = true;
									msg = replaceWord(msg, real);
								}
							}
						}
					}
				}
			}
		}
		
		//Bukkit.broadcastMessage(msg3);
		//Bukkit.broadcastMessage(msg);
		if(contains)
		{
			return msg.substring(1,msg.length()-1);
			//return msg;
		}
		else
		{
			//msg = msg2.substring(1,msg2.length()-1);
			return original;
		}
	}
	
	/**
	 * Returns a random alphabetical string
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length, boolean allowCase)
	{
		String str = "";
		char[] allowed = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		Random random = new Random();
		
		if(allowCase)
		{
			for(int i = 0; i <= length; ++i)
				str = str + allowed[random.nextInt(allowed.length-1)];
		}
		else
		{
			for(int i = 0; i <= length; ++i)
			{
				if(random.nextBoolean())
					str = str + allowed[random.nextInt(allowed.length-1)];
				else
					str = str + Character.toUpperCase(allowed[random.nextInt(allowed.length-1)]);
			}
		}
		
		return str;
	}
	
	/**
	 * Returns a double value from string
	 * @param parse
	 * @return Double.NaN if error parsing
	 */
	public static double parseDouble(final String parse)
	{
		try
		{
			return Double.parseDouble(parse);
		}
		catch(Exception e)
		{
			return Double.NaN;
		}
	}
	
	/**
	 * Returns an Integer from string.
	 * @param parse
	 * @return Integer.MIN_VALUE if error parsing
	 */
	public static int parseInteger(final String parse)
	{
		try
		{
			return Integer.parseInt(parse);
		}
		catch(Exception e)
		{
			return Integer.MIN_VALUE;
		}
	}
	
	/**
	 * Korvaa sanan tekstistä sensuroinnilla
	 * @param fromString teksti josta korvataan
	 * @param smallString teksti pienellä
	 * @param toReplace teksti joka korvataan
	 * @return smallString josta on korvattuna tekstit
	 */
	private static String replaceWord(String fromString, String toReplace)
	{
		//Luodaan sumennettu sama
		String censor = "";
		for(int i = 0; i < toReplace.length(); i++)
		{
			censor = censor + sensor[new Random().nextInt(sensor.length)];
		}
		
		/* EDITOITU */
		//Korvataan kaikki sanat niin kauan kuin niitä on
		while(msg3.indexOf(toReplace) != -1)
		{
			int index = msg3.indexOf(toReplace);
			msg3 = msg3.substring(0, index) + censor + msg3.substring(index + toReplace.length());
			
			if(fromString.length() > index+toReplace.length())
				fromString = fromString.substring(0, index) + censor + fromString.substring(index + toReplace.length());
			else
				fromString = fromString.substring(0, index) + censor;
		}
		
		return fromString;
	}
}
