package org.jim.bukkit.audit.util;

import org.bukkit.Chunk;

public class ChunkCount implements Comparable<ChunkCount> {

    private Integer count = 0;
    private Chunk chunk;

    public ChunkCount(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public int compareTo(ChunkCount o) {
        return o.count - count;
    }

    public void add() {
        count++;
    }

    public Integer getCount() {
        return count;
    }

    public Chunk getChunk() {
        return chunk;
    }

}
