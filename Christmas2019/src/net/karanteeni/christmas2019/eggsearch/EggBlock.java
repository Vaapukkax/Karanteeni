package net.karanteeni.christmas2019.eggsearch;

import java.util.Random;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.karanteeni.christmas2019.Christmas;
import net.karanteeni.core.block.executable.ActionBlock;
import net.karanteeni.core.database.QueryState;
import net.karanteeni.core.information.text.Prefix;
import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.TileEntitySkull;

public class EggBlock extends ActionBlock implements 
	ActionBlock.Events.PlayerInteractEvent, 
	ActionBlock.Events.BlockPlaceEvent, 
	ActionBlock.Events.BlockFromToEvent, 
	ActionBlock.Events.BlockFormEvent,
	ActionBlock.Events.BlockMultiPlaceEvent {
	private int pointCount;
	private static Random r = new Random();
	private boolean eggHidden = true;
	
	public EggBlock(Block block) throws IllegalArgumentException {
		super(block);
	}
	
	
	public EggBlock(Block block, UUID uuid) throws IllegalArgumentException {
		super(block, uuid);
	}


	@Override
	public void playerInteractEvent(PlayerInteractEvent event) {
		if(!eggHidden && Christmas.getInstance().getGameState().isGameOngoing()) {
			Christmas.getInstance().getGameState().eggBroken(event.getPlayer().getUniqueId(), this);
			hide();
			playFindEffect(event.getPlayer());
		} else if (event.getAction() == Action.LEFT_CLICK_BLOCK && Christmas.getInstance().getGameState().isEditOngoing()) {
			// only OP can break eggs
			if(!event.getPlayer().isOp())
				return;
			
			if(destroy() != QueryState.REMOVAL_SUCCESSFUL) {
				event.getPlayer().sendMessage(Prefix.NEGATIVE + "Epäonnistuttiin munan poistossa, kokeile uudelleen");
				BlockState state = getBlock().getState();
				state.setType(Material.OBSIDIAN);
				state.update(true, false);
			} else {
				Christmas.getInstance().getGameState().unregisterEgg(getUUID());
				event.getPlayer().sendMessage(Prefix.NEGATIVE + "Tuhottiin muna");
			}
		}
	}


	@Override
	public void onLoad() {
		Christmas.getInstance().getGameState().registerEgg(this);
	}
	
	
	private void playFindEffect(Player player) {
		Location middle = getBlock().getLocation();
		middle.add(0.5, 0.5, 0.5);
		Random r = new Random();
		for(int i = 0; i < 10; ++i) {			
			Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255-r.nextInt(100), 255-r.nextInt(100), 255-r.nextInt(100)), 3.2f);
			middle.getWorld().spawnParticle(Particle.REDSTONE, middle, 1, 0.3, 0.3, 0.3, dustOptions);
		}
		middle.getWorld().playSound(middle, Sound.ENTITY_CHICKEN_EGG, SoundCategory.PLAYERS, 1, 1.4f);
	}
	
	
	public boolean isBroken() {
		return eggHidden;
	}
	
	
	private void hide() {
		BlockState state = getBlock().getState();
		state.setType(Material.AIR);
		state.update(true, false);
		eggHidden = true;
	}
	
	
	@Override
	public QueryState destroy() {
		QueryState result = super.destroy();
		
		if(result == QueryState.REMOVAL_SUCCESSFUL) {
			BlockState state = getBlock().getState();
			state.setType(Material.AIR);
			state.update(true, false);
		}
		
		return result;
	}
	
	
	public void generateEgg() {
		double chance = r.nextDouble(); // 0 1 2
		eggHidden = false;
		
		if(chance < 0.50) {
			pointCount = 1;
			createSkullBlock("0e0e78c9-3756-4204-b4c4-06d9c5bc9d03", getBlock(), "e2d317e1a1283ab2f6474f3a7e18259e60a4791b613319efeda7ebdab89934");
		} else if(chance < 0.90) {
			pointCount = 2;
			createSkullBlock("b3543f53-12cd-4ac4-a863-87d52dea06fd", getBlock(), "5ffed7ac15d67ec5a4cb92367f9d75432eb16105aff2c291c88946c7478fcb5");
		} else {
			pointCount = 3;
			createSkullBlock("67e8c714-b4f1-4feb-8034-4a693723fdae", getBlock(), "7be7545297dfd6266bbaa2051825e8879cbfa42c7e7e24e50796f27ca6a18");
		}
	}
	
	
	private void createSkullBlock(String uuid, Block block, String textureValue) {
        block.setType(Material.PLAYER_HEAD);

        Rotatable dir = (Rotatable)block.getBlockData();
        Random r = new Random();
        BlockFace[] faces = new BlockFace[] {
        		BlockFace.EAST,
        		BlockFace.EAST_NORTH_EAST,
        		BlockFace.EAST_SOUTH_EAST,
        		BlockFace.NORTH,
        		BlockFace.NORTH_EAST,
        		BlockFace.NORTH_NORTH_EAST,
        		BlockFace.NORTH_NORTH_WEST,
        		BlockFace.NORTH_WEST,
        		BlockFace.SOUTH,
        		BlockFace.SOUTH_EAST,
        		BlockFace.SOUTH_SOUTH_EAST,
        		BlockFace.SOUTH_SOUTH_WEST,
        		BlockFace.SOUTH_WEST,
        		BlockFace.WEST,
        		BlockFace.WEST_NORTH_WEST,
        		BlockFace.WEST_SOUTH_WEST
        };
        dir.setRotation(faces[r.nextInt(faces.length)]);
        block.setBlockData(dir);
        
        GameProfile profile = new GameProfile(UUID.fromString(uuid), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", "http://textures.minecraft.net/texture/" + textureValue).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        TileEntitySkull skullTile = (TileEntitySkull)((CraftWorld)block.getWorld()).getHandle().getTileEntity(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        skullTile.setGameProfile(profile);
        
        block.getState().update(true);
    }
	
	
	public int getPoints() {
		return pointCount;
	}
	
	
	public void setEditMode(boolean editing) {
		if(editing) {
			BlockState state = this.getBlock().getState();
			state.setType(Material.OBSIDIAN);
			state.update(true, false);
		} else {
			BlockState state = this.getBlock().getState();
			state.setType(Material.AIR);
			state.update(true, false);
		}
	}


	@Override
	public void blockPlaceEvent(BlockPlaceEvent event) {
		event.setCancelled(true);
		event.getPlayer().sendMessage(Prefix.NEGATIVE + "Muna on piilotettu tähän");
	}


	@Override
	public void blockFormEvent(BlockFormEvent event) {
		event.setCancelled(true);
	}


	@Override
	public void blockFromToEvent(BlockFromToEvent event) {
		event.setCancelled(true);
	}


	@Override
	public void blockMultiPlaceEvent(BlockMultiPlaceEvent event) {
		event.setCancelled(true);
		event.getPlayer().sendMessage(Prefix.NEGATIVE + "Muna on piilotettu tähän");
	}
}
