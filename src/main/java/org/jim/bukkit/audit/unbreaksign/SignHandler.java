package org.jim.bukkit.audit.unbreaksign;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.jim.bukkit.audit.util.Lang;

public class SignHandler {

    private static final BlockFace[] FACES = {BlockFace.NORTH, BlockFace.SOUTH,
            BlockFace.EAST, BlockFace.WEST};
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
        if (Lang.isSign(up.getType()) && matchPattern(up)) {
            isProtected = true;
            return;
        }
        // wall sign
        for (BlockFace face : FACES) {
            Block near = block.getRelative(face);
            if (Lang.isWallSign(near.getType())) {
                Sign s = (Sign) near.getState();
                if (matchPattern(s) && ((org.bukkit.block.data.type.WallSign) s.getBlockData())
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
        return isSign(block) && matchPattern((Sign) block.getState());
    }

    protected static boolean matchPattern(Sign sign) {
        return SignUnbreakingListener.matchPattern(sign.getLine(0));
    }

    public static boolean isSign(Block block) {
        return block != null && (Lang.isSign(block.getType()) || Lang.isWallSign(block.getType()));
    }
}
