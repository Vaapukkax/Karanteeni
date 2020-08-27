package net.karanteeni.nature.treemanager;

import org.bukkit.Material;

public class TreeStructureData {
	private short minLength = 5; // min length of tree
	private short maxLength = 10; // max length of tree
	private byte splitChance = 10; // chance to split in section
	private byte continueChance = 10; // chance to continue in section
	private byte branchChance = 80; // chance to generate branches at section
	private boolean canAvoid = true; // can the tree avoid structures
	private Material leafType = Material.OAK_LEAVES; // type of leaves
	private Material branchType = Material.OAK_LOG; // type of logs
	private byte minBranchCount = 1;
	private byte maxBranchCount = 4;
	
	
	public TreeStructureData() {
		
	}
	
	
	public TreeStructureData(short minLength, 
			short maxLength, 
			byte splitChance, 
			byte continueChance, 
			byte branchChance, 
			boolean canAvoid, 
			Material leafType, 
			Material branchType) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.splitChance = splitChance;
		this.continueChance = continueChance;
		this.branchChance = branchChance;
		this.canAvoid = canAvoid;
		this.leafType = leafType;
		this.branchType = branchType;
	}
	
	
	public short minLength() {
		return minLength;
	}
	
	public short maxLength() {
		return maxLength;
	}
	
	
	public enum SECTION_TYPE {
		SPLIT,
		CONTINUE,
		BRANCH
	}
	
	
	public int getBranchCount() {
		return maxBranchCount;
	}
	
	public int branchChance() {
		return this.branchChance;
	}
}
