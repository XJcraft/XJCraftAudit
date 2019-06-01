package org.jim.bukkit.audit.unbreaksign;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

public class SignHandler {

	private static final BlockFace[] FACES = { BlockFace.NORTH, BlockFace.SOUTH,
			BlockFace.EAST, BlockFace.WEST };
	private boolean isProtected = false;

	SignHandler(Block block) {
		findSign(block);
	}

	private void findSign(Block block) {
		// self
		if (matchPattern(block)) {
			isProtected = true;
			return;
		}
		// sign post
		Block up = block.getRelative(BlockFace.UP);
		if (Material.LEGACY_SIGN == up.getType() && matchPattern(up)) {
			isProtected = true;
			return;
		}
		// wall sign
		for (BlockFace face : FACES) {
			Block near = block.getRelative(face);
			if (Material.LEGACY_WALL_SIGN == near.getType()) {
				Sign s = (Sign) near.getState();
				if (matchPattern(s) && ((org.bukkit.material.Sign) s.getData())
						.getFacing() == face) {
					isProtected = true;
					return;
				}
			}
		}
	}

	public boolean isProtected() {
		return isProtected;
	}

	protected static boolean matchPattern(Block block) {
		return isSign(block) ? matchPattern((Sign) block.getState()) : false;
	}

	protected static boolean matchPattern(Sign sign) {
		return SignUnbreakingListener.matchPattern(sign.getLine(0));
	}

	public static boolean isSign(Block block) {
		return block != null && (Material.LEGACY_WALL_SIGN == block.getType()
				|| Material.LEGACY_SIGN == block.getType());
	}
}
