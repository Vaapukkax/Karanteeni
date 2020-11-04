package net.karanteeni.core.skull;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagString;

public class SkullFactory {
	
	/**
	 * Creates a player skull item from url
	 * @param itemName
	 * @param uuid
	 * @param url
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static ItemStack createSkullItemFromURL(String itemName, UUID uuid, String url) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		byte[] encodedData = null;
		encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", url).getBytes());
	
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		Field profileField = null;
		
		try {
			profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, profile);
		} catch (Exception e) {
			if(uuid != null)
				meta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(uuid));
			else if (itemName != null)
				meta.setOwner(itemName);
		}
		
		if(itemName != null) {			
			meta.setDisplayName(itemName);
			skull.setItemMeta(meta);
		}
		
		return skull;
	}
	
	
	/**
	 * Create skull item using model data
	 * @param text
	 * @param name
	 * @param modelData
	 * @param url
	 * @return
	 */
	public static ItemStack createSkullItemFromModelData(String itemName, UUID uuid, String modelData) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		
		if(itemName != null) {			
			SkullMeta meta = (SkullMeta) skull.getItemMeta();
			meta.setDisplayName(itemName);
			skull.setItemMeta(meta);
		}
		
		skull = setSkullOwner(skull, uuid, modelData);
		
		return skull;
	}
	
	
	private static ItemStack setSkullOwner(ItemStack itemStack, UUID id, String textureValue) {
        net.minecraft.server.v1_16_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

        NBTTagCompound compound = nmsStack.getTag();
        if (compound == null) {
            compound = new NBTTagCompound();
            nmsStack.setTag(compound);
            compound = nmsStack.getTag();
        }

        NBTTagCompound skullOwner = new NBTTagCompound();
        skullOwner.set("Id", NBTTagString.a(id.toString())); // a = new
        NBTTagCompound properties = new NBTTagCompound();
        NBTTagList textures = new NBTTagList();
        NBTTagCompound value = new NBTTagCompound();
        value.set("Value", NBTTagString.a(textureValue)); // a = new
        textures.add(value);
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);

        compound.set("SkullOwner", skullOwner);
        nmsStack.setTag(compound);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }
}
