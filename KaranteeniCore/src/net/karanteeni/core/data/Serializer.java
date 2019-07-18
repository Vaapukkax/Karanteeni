package net.karanteeni.core.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class Serializer {
	
	/**
	 * Encodes a given inventory to Base64
	 * @param inventory inventory to encode
	 * @return encoded string
	 */
	public static String toBase64(Inventory inventory) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			
			// write the size of the inventory
			//dataOutput.writeInt(inventory.getSize());
			
			// Save every item in the inventory
			for(ItemStack item : inventory.getContents()) {
				dataOutput.writeObject(item);
			}
			
			// serialize array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch(Exception e) {
			throw new IllegalStateException("Unable to convert inventory to base64", e);
		}
	}
	
	
	/**
	 * Converts a base64 encoded string to inventory
	 * @param data inventory string to convert back
	 * @return the inventory converted
	 * @throws IOException class type decode failed
	 */
	public static Inventory inventoryFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, InventoryType.PLAYER); //(null, dataInput.readInt());
    
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
	}
}
