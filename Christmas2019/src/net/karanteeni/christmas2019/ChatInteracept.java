package net.karanteeni.christmas2019;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.karanteeni.core.players.KPlayer;
import net.karanteeni.karanteeniperms.KaranteeniPerms;
import net.karanteeni.karanteeniperms.groups.player.PermissionPlayer;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.minecraft.server.v1_14_R1.NBTTagList;
import net.minecraft.server.v1_14_R1.NBTTagString;

public class ChatInteracept implements Listener {
	Christmas plugin = null;
	private HashMap<UUID, Integer> playerMissions = new HashMap<UUID, Integer>();
	private HashMap<Integer, String> answers = new HashMap<Integer, String>();
	private HashMap<Integer, String> missionTexts = new HashMap<Integer, String>();
	private HashMap<Integer, ItemStack> rewards = new HashMap<Integer, ItemStack>();
	private String prefix = "§2>§4>§2> ";
	
	public ChatInteracept(Christmas plugin) {
		this.plugin = plugin;
		
		answers.put(1, "sika");
		answers.put(2, "Hannu ja Kerttu");
		answers.put(3, "Helikopterilla");
		answers.put(4, "Puuroa");
		answers.put(5, "luumusta");
		answers.put(6, "karhu");
		answers.put(7, "Niko Lentäjän Poika");
		answers.put(8, "ruotsista");
		answers.put(9, "kaalilaatikko");
		answers.put(10, "manteli");
		answers.put(11, "korvatunturilta");
		answers.put(12, "mistelinoksan");
		answers.put(13, "1800");
		answers.put(14, "30");
		answers.put(15, "19");
		answers.put(16, "toiveunta");
		answers.put(17, "pippurista");
		answers.put(18, "seimikätkyessään");
		answers.put(19, "4");
		answers.put(20, "pehmeistä");
		answers.put(21, "kaiku");
		answers.put(22, "taikatalvi");
		answers.put(23, "3");
		answers.put(24, "punaisissa");
		
		missionTexts.put(1, "Mitä eläintä on yleensä joulupöydässä? Vinkki: Juice Leskisen kappale");
		missionTexts.put(2, "Mihin lastensatuun liittyy piparkakkutalossa asuva noita?");
		missionTexts.put(3, "Jos jouluna ei ole lunta, millä pukin pitäisi tulla?");
		missionTexts.put(4, "Joulu on taas, joulu on taas, mutta mitä on kattiloissa?");
		missionTexts.put(5, "Mistä tehtyä rahkaa syödään jouluna?");
		missionTexts.put(6, "Mikä on suomen kansalliseläin?");
		missionTexts.put(7, "Mikä jouluelokuva kertoo porosta, joka osaa lentää, ja hänen isänsä on yksi Joulupukin poroista?");
		missionTexts.put(8, "Mistä maasta glögi tuli alunperin suomeen?");
		missionTexts.put(9, "Mikä ei kuulu perinteiseen joulupöytään: maksalaatikko, riisipuuro, kaalilaatikko, vai porkkanalaatikko?");
		missionTexts.put(10, "Mitä piilotetaan joulupuuroon, jonka löytäjälle annetaan yleensä palkinto");
		missionTexts.put(11, "Mistä joulupukki on kotoisin?");
		missionTexts.put(12, "Minkä alla suudellaan?");
		missionTexts.put(13, "Millä luvulla lucia-perinne tuli suomeen? (vain vuosiluku)");
		missionTexts.put(14, "Kuinka monesti Gävlen olkipukki on poltettu ennen vuotta 2019?");
		missionTexts.put(15, "Kuinka monta lumiukkoa joulumaassa on?");
		missionTexts.put(16, "Joulumaa on muutakin kuin pelkkää...?");
		missionTexts.put(17, "Mistä piparkakku on saanut nimensä?");
		missionTexts.put(18, "Mihin sanaan päättyy Jouluyö, juhlayön ensimmäinen säkeistö");
		missionTexts.put(19, "Mikä on adventtien määrä jouluna?");
		missionTexts.put(20, "Minkälaisista paketeista lapset eivät yleensä tykkää?");
		missionTexts.put(21, "Kuka asuu talossa 18?");
		missionTexts.put(22, "Missä muumikirjassa poltetaan keskitalven kokkoa?");
		missionTexts.put(23, "Kuinka monta nappia päähahmon rakentamalla lumiukolla on animaatioelokuvassa, jonka tunnuslaulu on “Walking in The Air”?");
		missionTexts.put(24, "Minkä värisissä kirjekuorissa postikortit lähetetään nykyään?");
		
		
		rewards.put(1, createSkull("§6§nLuukku 1", "c8e07022-dbe3-459a-9431-b45d2cd3c152", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWM4NDc1Y2NiMTc3MTAwNmEzMTcyN2E4OWNkMTU3NTU5MjQ4YmQ1M2RmMTE0ZDE1NDhhODdkNDAzN2VjIn19fQ==", false));
		rewards.put(2, createSkull("§6§nLuukku 2", "7a419c21-9074-43b7-a443-df76fd56a2c6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNjNTJlYWU3NDdjYWQ1YjRmZDE5YjFhMjNiMzlhMzM2YjYyZWQ0MjI3OTdhNjIyZDA0NWY0M2U1ZDM4In19fQ", false));
		rewards.put(3, createSkull("§6§nLuukku 3", "5d0e580a-0235-4484-91f6-7a27ddd7b5dc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDYwZDA0ZDI5MzY3ZGYwYzFjYWMxZGE0MDQxMDQyMjM4ODU2ZGY1MzMyOWE1MzVhMWYwOGRiZTk2ZWY0YTQ4NSJ9fX0", false));
		rewards.put(4, createSkull("§6§nLuukku 4", "d04be752-824a-45dd-ac8b-976681fe9390", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmM5OTRlMTQyZDc5ZjU2NzI3MjU0YWU0NDQ5ZDJlMzgzODAzM2IwOTEyNWJmYmU3NDNlNDZhYTdhNTE3MjlkZCJ9fX0", false));
		rewards.put(5, createSkull("§6§nLuukku 5", "18344e15-ce8e-40f3-9172-a25e85629694", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjEyYTNlZmM5NmJiN2EwYjI5MWYzNzM3MTlhZDJlNTZlYWQ0ZTg5NWVmOWNmNmRhNmQ3YWNhOWVlZmFkNzNjZCJ9fX0", false));
		rewards.put(6, createSkull("§6§nLuukku 6", "82f8a252-128a-45ec-8354-2ee281dd8188", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlmMjM0OTcyOWE3ZWM4ZDRiMTQ3OGFkZmU1Y2E4YWY5NjQ3OWU5ODNmYmFkMjM4Y2NiZDgxNDA5YjRlZCJ9fX0", false));
		rewards.put(7, createSkull("§6§nLuukku 7", "88267e1e-010f-453d-8edb-b91089977c3f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA1OThiMmFlYmY4MWMxMzcwNGQ5YWEwNTA2YmE4NWI2NjBhYTg0ZTA0Zjk2N2U2MzFiYTYxOWNlZjUwY2Y3YyJ9fX0", false));
		rewards.put(8, createSkull("§6§nLuukku 8", "010dc21d-cafa-4cc0-bcbc-3115acd9c139", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjM2OGE2OWM5NGI0NWRkMGE0MzVkZTIxN2MyOWNkYmQ0MzNjN2I0NDczOTFmYWEzM2MyNDFkYzA4MjcxIn19fQ==", false));
		rewards.put(9, createSkull("§6§nLuukku 9", "673db4c6-b7ea-421e-ae35-d7ab65e8b35e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZkZmQxZjc1MzhjMDQwMjU4YmU3YTkxNDQ2ZGE4OWVkODQ1Y2M1ZWY3MjhlYjVlNjkwNTQzMzc4ZmNmNCJ9fX0=", false));
		rewards.put(10, createSkull("§6§nLuukku 10", "73d2ae21-b59a-4a3d-9e36-86ed8a91f545", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM1NTNhY2UzMmNmYTEyZjkxOTEzMjMyODQ3NDY3YmViNTZkNjQ1ZjFjOTZjNDE0NWU3OTlkMGZiOTM3YTMwIn19fQ==", false));
		rewards.put(11, createSkull("§6§nLuukku 11", "33a84c61-263c-4689-a62c-3b8044e1ff4d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19", false));
		rewards.put(12, createSkull("§6§nLuukku 12", "190750ad-0d37-47ba-bda5-a4c66be62a69", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI0NjI3MWZmNDFhZWMzZmQ4NGU2NGU0NWVkNjlhOGM1MzViMTRhZDRjMTMzYWZiMWE1YjM2ZTQzNjcxYjkifX19", false));
		rewards.put(13, createSkull("§6§nLuukku 13", "4b617d70-ccc2-4147-9340-75d3e0fb4f9c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y1YzM5YTQ4MTFiYTE3MTNmMTcwMGExNmRkOGI4MGFkMmI1ZTlhNjE0NGMzNWY0OWRhYzBhN2FiNzMzMmU0YiJ9fX0", false));
		rewards.put(14, createSkull("§6§nLuukku 14", "dc9014cd-9974-459c-9863-2e013f46b679", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2E0OTg2ZmRmMDRjYzg2ZDhhZmYyMDM3YTZiNGRmNTczY2RjMWU3MDhkMDU3OTBjNzY3MjhmYWVmNzk2ZjMzYSJ9fX0=", false));
		rewards.put(15, createSkull("§6§nLuukku 15", "ad04b162-ae44-4e40-8c5b-9271cb3a40d6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2JiMzExZjNiYTFjMDdjM2QxMTQ3Y2QyMTBkODFmZTExZmQ4YWU5ZTNkYjIxMmEwZmE3NDg5NDZjMzYzMyJ9fX0=", false));
		rewards.put(16, createSkull("§6§nLuukku 16", "ba439110-08e8-478f-a560-62db623c11d7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdjN2IyYzFmMjhkMWE0YzRlOGU0MjZjOTE3ZjQ4NGIxM2NmODAyMzdiZGFhMDc3YWJiNDFkNGM0MTBhYjU4NCJ9fX0", false));
		rewards.put(17, createSkull("§6§nLuukku 17", "420c164d-cb22-437f-ba48-4ed895d6a43f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBlYzA2YjFkZTg2M2RhMWZlY2IxOWM4ZTYxNzQ3NTBhYjI1NmJiMDM0OTlmNDA0NmIyZDc1M2IyZjQyN2M3YiJ9fX0=", false));
		rewards.put(18, createSkull("§6§nLuukku 18", "e2a0667e-40d7-49ea-8400-8a6ca8e55354", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDQxMWZjM2YyZGU1YjFlYjliODdlOTk3OTA5MTk5M2MzNDkwNTAyZWJhNzI2NWJlZDkzZDhiMWVkZjJjZmEzNyJ9fX0", false));
		rewards.put(19, createSkull("§6§nLuukku 19", "f515010e-830c-412a-9d60-2d22cd2fb217", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkZGMzZmM5OGI5MjQ2NWY2MGI1YzgzZTc0YWJmZWI1YzNiMWQ5NDQ1YjkyNDIwNmZlMmMzYTNkMzFlYWI4NSJ9fX0", false));
		rewards.put(20, createSkull("§6§nLuukku 20", "be6b6cbc-223a-4c98-b205-b00b7c545579", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRkNjYzMTM2Y2FmYTExODA2ZmRiY2E2YjU5NmFmZDg1MTY2YjRlYzAyMTQyYzhkNWFjODk0MWQ4OWFiNyJ9fX0=", false));
		rewards.put(21, createSkull("§6§nLuukku 21", "a9a4bdfa-310e-4478-9199-8810b8ef3f5d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWEyZDIyOGEzNGI5M2M1MWZiMzc0ZmJiOTU0M2RhYmI0MGI5OTY5ZTEwMWIyNjdmOGE2YTM2YmE3MDk2YzVkOSJ9fX0=", false));
		rewards.put(22, createSkull("§6§nLuukku 22", "c65047c2-45b8-424a-8461-7a28ed109686", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQ3ZTJlNWQ1NWI2ZDA0OTQzNTE5YmVkMjU1N2M2MzI5ZTMzYjYwYjkwOWRlZTg5MjNjZDg4YjExNTIxMCJ9fX0=", false));
		rewards.put(23, createSkull("§6§nLuukku 23", "bb5f4180-98bf-49c6-a252-b499e56b7f26", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjczYjg3NjliNDYxZTg2NWNiODVkMGUwNWEzNDhjYzQxMmVjNzAxYjhmY2E5OWRkNWQ0NjRjOWUyN2Y5YjQ0MCJ9fX0=", false));
		rewards.put(24, createSkull("§6§nLuukku 24", "f4b89f66-750d-4ffb-8002-90a097de4b9b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGExNTkyMzZkNzUxMmJkYjQzMjZhMjRlMTQ1MDIxNjdiNzZiY2Q4NWMwNDE5MzFjMjE5NDIwMWIxN2Y1ZTcifX19=", false));
	}
	
	
	public UUID getUuid(String name) {
		try {
			if(plugin.getConfig().isSet("names."+name))
	        	return UUID.fromString(plugin.getConfig().getString("names."+name));	
		} catch (Exception e) {
		}
        
		
		String url = "https://api.mojang.com/users/profiles/minecraft/"+name;
		UUID uuid = null;
        try {
            @SuppressWarnings("deprecation")
            String UUIDJson = IOUtils.toString(new URL(url));           
            if(!UUIDJson.isEmpty()) {
            	JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
            	uuid = UUID.fromString(UUIDObject.get("id").toString());            	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        // save the name to config so there wont be any requests later
        plugin.getConfig().set("names."+name, uuid.toString());
        
        return uuid;
    }
	
	
	@SuppressWarnings("deprecation")
	public ItemStack createSkull(String text, String name, String modelData, boolean url) {
		//UUID uuid = getUuid(name);
		UUID uuid = null;
		try {			
			uuid = UUID.fromString(name);
		} catch(Exception e) {
		}
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = null;
		if(url) {
			encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", modelData).getBytes());
		
			profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
			Field profileField = null;
			
			try {
				profileField = meta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(meta, profile);
			} catch (Exception e) {
				if(uuid != null)
					meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));
				else
					meta.setOwner(name);
			}
		}
		
		meta.setDisplayName(text);
		skull.setItemMeta(meta);
		
		if(!url) {
			skull = setSkullOwner(skull, name, modelData);
		}
		
		return skull;
	}
	
	
	
	ItemStack setSkullOwner(ItemStack itemStack, String id, String textureValue) {
        net.minecraft.server.v1_14_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", new NBTTagString(id));
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", new NBTTagString(textureValue));
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }
	
	
	
	public void startPlayer(UUID uuid, int mission) {
		Player player = Bukkit.getPlayer(uuid);
		if(!plugin.getConfig().isSet("players.completed."+player.getUniqueId())) {
			plugin.getConfig().set("players.completed."+player.getUniqueId(), new ArrayList<Integer>());
			plugin.saveConfig();
		}
		
		List<Integer> completed = plugin.getConfig().getIntegerList("players.completed."+player.getUniqueId());
		if(completed.contains(mission)) {
			if(!player.isOp()) {
				player.sendMessage(prefix + "Luukkusi on tyhjä, olet avannut sen jo!");
				return;				
			} else {
				player.sendMessage(prefix + "Olet avannut jo luukun, mutta ylläpitolaisena luukuistasi tulee mystisesti loputtomasti karkkia");
			}
		}
		
		playerMissions.put(uuid, mission);
		player.sendMessage(prefix + "Kirjoita vastaus chattiin tai kirjoita §clopeta§2 lopettaaksesi vastauksen teko\n" + prefix + "Ethän spoilaa toisille pelaajille vastauksia :3!");
		player.sendMessage(prefix + missionTexts.get(mission));
		player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1f, 1.8f);
	}
	
	
	public void stopPlayer(UUID uuid) {
		playerMissions.remove(uuid);
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerChat(AsyncPlayerChatEvent event) {
		// verify the player is playing the quessing game
		if(!playerMissions.containsKey(event.getPlayer().getUniqueId()))
			return;
		
		event.setCancelled(true);
		String msg = event.getMessage().toLowerCase();
		if(msg.equals("lopeta")) {
			stopPlayer(event.getPlayer().getUniqueId());
			return;
		}
		
		// tarkista onko pelaajan antama vastaus oikein
		int mission = playerMissions.get(event.getPlayer().getUniqueId());
		String answer = answers.get(mission);
		if(msg.equalsIgnoreCase(answer)) {
			correctAnswer(event.getPlayer());
		} else {
			wrongAnswer(event.getPlayer());
		}
	}
	
	
	public void correctAnswer(Player player) {
		KPlayer kp = KPlayer.getKPlayer(player.getUniqueId());
		if(!plugin.getConfig().isSet("players.completed."+player.getUniqueId())) {
			plugin.getConfig().set("players.completed."+player.getUniqueId(), new ArrayList<Integer>());
			plugin.saveConfig();
		}
		
		List<Integer> completed = plugin.getConfig().getIntegerList("players.completed."+player.getUniqueId().toString());
		int missionNum = playerMissions.get(player.getUniqueId());
		completed.add(missionNum);
		
		// give reward to player
		ItemStack item = rewards.get(missionNum);
		HashMap<Integer, ItemStack> items = player.getInventory().addItem(item);
		if(!items.isEmpty()) {
			Bukkit.getScheduler().runTask(plugin, new Runnable() {
				@Override
				public void run() {
					kp.dropItemsAtPlayer(new ArrayList<ItemStack>(items.values()));
					
				}
			});
		}
		
		player.sendMessage(prefix + "Vastaus oli oikein!");
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 10);
		
		// check if the player has answered all the questions
		if(completed.size() <= 24) {
			plugin.getConfig().set("players.completed."+player.getUniqueId().toString(), completed);
			plugin.saveConfig();
		}
		
		if(completed.size() == 24)
			answeredAll(player);
		
		
		stopPlayer(player.getUniqueId());
	}
	
	
	public void answeredAll(Player player) {
		KaranteeniPerms plugin = KaranteeniPerms.getPlugin(KaranteeniPerms.class);
		PermissionPlayer pp = plugin.getPlayerModel().getPermissionPlayer(player.getUniqueId());
		pp.addPermission("karpet.blocks."+Material.GREEN_GLAZED_TERRACOTTA.name().toLowerCase());
		player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 100000, 0.5f);
		player.sendMessage("§d>§5>§d> Olet avannut kaikki 24 luukkua! Palkinnoksi saat palikan §e"+Material.GREEN_GLAZED_TERRACOTTA.name().replace("_", " ").toLowerCase() + "§d taikamattoosi!");
	}
	
	
	public void wrongAnswer(Player player) {
		player.sendMessage(prefix + "Vastasit valitettavasti väärin. Yritä uudelleen!");
	}
	
	
	@EventHandler
	public void playerQuit(PlayerQuitEvent event) {
		stopPlayer(event.getPlayer().getUniqueId());
	}
}
