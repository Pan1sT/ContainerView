package me.pan1st.containerview.profile;

import org.bukkit.block.BlockState;

import java.util.*;

public class PlayerProfile {

    private UUID playerUUID;
    private List<List<BlockState>> containerPages;
    private int currentPage;


    public PlayerProfile(UUID uuid, List<BlockState> containerList){
        this.playerUUID = uuid;
        this.currentPage = 0;
        this.containerPages = partitionEntities(containerList, 16);

    }

    public UUID getUUID(){
        return this.playerUUID;
    }

    public int getPage(){
        return this.currentPage;
    }

    public void setPage(int page){
        this.currentPage = page;
    }

    public List<BlockState> getContainersByPage(int page){
        if (page < 0 || page > this.containerPages.size()) return null;
        return this.containerPages.get(page);
    }

    public int getContainerspPageSize(){
        return this.containerPages.size();
    }

    public void removeSpecificBlockState(BlockState target) {
        Iterator<List<BlockState>> outerIterator = containerPages.iterator();

        while (outerIterator.hasNext()) {
            List<BlockState> page = outerIterator.next();
            Iterator<BlockState> innerIterator = page.iterator();

            while (innerIterator.hasNext()) {
                BlockState currentBlock = innerIterator.next();
                if (currentBlock.getLocation() == target.getLocation()) {
                    innerIterator.remove();
                }
            }

            if (page.isEmpty()) {
                outerIterator.remove();
            }
        }
    }
    private List<List<BlockState>> partitionEntities(List<BlockState> entities, final int partitionSize) {
        if (entities == null) {
            throw new NullPointerException("The entity list cannot be null");
        }

        if (partitionSize <= 0) {
            throw new IllegalArgumentException("The partition size should be positive");
        }

        int totalEntities = entities.size();
        int partitionsCount = (totalEntities + partitionSize - 1) / partitionSize;
        List<List<BlockState>> partitions = new ArrayList<>(partitionsCount);

        for (int i = 0; i < totalEntities; i += partitionSize) {
            final int finalI = i;
            partitions.add(new AbstractList<BlockState>() {
                @Override
                public BlockState get(int index) {
                    return entities.get(finalI + index);
                }

                @Override
                public int size() {
                    return Math.min(partitionSize, totalEntities - finalI);
                }
            });
        }

        return partitions;
    }

}
