package net.karanteeni.nature.treemanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class TreeGenerator {
	private HashMap<TreeType, TreeStructureData> treeGenMaps = new HashMap<TreeType, TreeStructureData>();
	Random r = new Random();
	
	public TreeGenerator() {
		treeGenMaps.put(TreeType.BIG_TREE, new TreeStructureData());
		treeGenMaps.put(TreeType.TREE, new TreeStructureData());
	}
	
	
	public List<BlockState> generateTree(TreeType species, Location location) {
		TreeStructureData data = treeGenMaps.get(species);
		LinkedList<BlockState> blocks = new LinkedList<BlockState>();
		if(data == null) return blocks;
		
		// generate root node
		TreeNode root = new TreeNode(0, 0, 0);
		
		// create min - max amount of trunk
		createTrunk(root, data, r.nextInt(data.maxLength() - data.minLength()) + data.minLength());
		
		return root.getBlocks(location);
	}
	
	
	private void createTrunk(TreeNode previousNode, TreeStructureData data, int depth) {
		for(int i = 0; i < 1; ++i) {
			// create a branch
			TreeNode newNode = new TreeNode(0, 
					previousNode.y + 1, 
					0);
			previousNode.connectNode(newNode);
			
			// continue trunk generation
			if(depth > 1) {
				createTrunk(newNode, data, depth - 1);
			}
			
			// generate branches
			if(depth % 2 == 0)
			if(r.nextDouble() * 100 < data.branchChance()) {
				int branchCount = r.nextInt(data.getBranchCount()) + 1;
				Bukkit.broadcastMessage("Create " + branchCount + " branches");
				BlockFace dir = BlockFace.NORTH;
				double random = r.nextDouble();
				if(random < 25)
					dir = BlockFace.SOUTH;
				else if(random < 50)
					dir = BlockFace.WEST;
				else if(random < 75)
					dir = BlockFace.EAST;
				
				if(depth > 1)
				for(int j = 0; j < branchCount; ++j) {
					createBranch(newNode, data, depth - 1, dir);
				}
			}
		}
	}
	
	
	private void createBranch(TreeNode previousNode, 
			TreeStructureData data, 
			int depth,  
			BlockFace direction) {
		for(int i = 0; i < r.nextInt(2) + 1; ++i) {
			TreeNode newBranch = null;
			Bukkit.broadcastMessage("Generate branch!");
			
			if(direction == BlockFace.EAST) {
				newBranch = new TreeNode(previousNode.x + r.nextInt(depth) - depth/2 + 3, previousNode.y + r.nextDouble() * 1.4 - 0.7, previousNode.z + r.nextDouble() * 1.4 - 0.7);
			} else if(direction == BlockFace.WEST) {
				newBranch = new TreeNode(previousNode.x - r.nextInt(depth) + depth/2 - 3, previousNode.y + r.nextDouble() * 1.4 - 0.7, previousNode.z  + r.nextDouble() * 1.4 - 0.7); 
			} else if(direction == BlockFace.SOUTH) {
				newBranch = new TreeNode(previousNode.x  + r.nextDouble() * 1.4 - 0.7, previousNode.y + r.nextDouble() * 1.4 - 0.7, previousNode.z + r.nextInt(depth) - depth/2 + 3);
			} else {
				newBranch = new TreeNode(previousNode.x + r.nextDouble() * 1.4 - 0.7, previousNode.y + r.nextDouble() * 1.4 - 0.7, previousNode.z - r.nextInt(depth) + depth/2 - 3);
			}
			
			previousNode.connectNode(newBranch);
			if(depth > 2) {
				createBranch(newBranch, data, depth - 1, direction);
			}
		}		
	}
	
	
	/*private void createAvoid(TreeNode previousNode, 
			TreeStructureData data, 
			int depth,  
			BlockFace direction) {
		TreeNode branchEnd = new TreeNode(0,0,0);
		
		
		return createAvoid(branchEnd, data, depth, direction);
	}*/
	
	
	private class TreeNode {
		private double x;
		private double y;
		private double z;
		private LinkedList<TreeNode> nextNodes = new LinkedList<TreeNode>();
		
		public TreeNode(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		
		public void connectNodes(List<TreeNode> nodes) {
			nextNodes.addAll(nextNodes);
		}
		
		
		public void connectNode(TreeNode node) {
			nextNodes.add(node);
		}
		
		
		/**
		 * Connects two tree nodes and returns the blocks between them
		 * @param node
		 * @return
		 */
		public List<BlockState> getBlocks(Location location) {
			List<BlockState> blocks = new LinkedList<BlockState>();
			location.add(x, y + 2, z);
			getBlocks(location, blocks);
			return blocks;
		}
		
		
		private void getBlocks(Location location, List<BlockState> states) {
			// get blocks to all of the next nodes
			if(nextNodes.isEmpty()) {
				return;
			}
			
			for(TreeNode node : nextNodes) {
				Bukkit.broadcastMessage(node.toString());
				Location temp = new Location(location.getWorld(), location.getX() + node.x - x, location.getY() + node.y - y, location.getZ() + node.z - z);
				
				// if the next node is in the same position don't do anything to prevent null pointer exception
				if((int)node.x != x || (int)node.y != y || (int)node.z != z) {
					BlockIterator iter = new BlockIterator(location.getWorld(), 
							location.toVector(), 
							new Vector(node.x,node.y,node.z), 
							0, 
							(int) Math.floor(location.distance(temp)));
					
					while(iter.hasNext()) {
						Block block = iter.next();
						if(block != null) {
							BlockState state = block.getState();
							state.setType(Material.OAK_LOG);
							states.add(state);							
						}
					}
				}
				
				if(node == this) {
					Bukkit.broadcastMessage("Inner loop created preventing crash!");
					continue;
				}
				node.getBlocks(temp, states);
			}
		}
		
		
		@Override
		public String toString() {
			return "X: " + x + "Y: " + y + "Z: " + z;
		}
	}
}
