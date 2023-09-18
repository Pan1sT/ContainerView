package me.pan1st.containerview.util;

import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@DefaultQualifier(NonNull.class)
public final class Containers {

    private Containers() {}

    public static CompletableFuture<List<BlockState>> getNearbyContainersAsync(Location playerLoc, int blockRadius, Material... materials) {
        int chunkSpan = (int) Math.ceil(blockRadius / 16.0);

        int minChunkX = ((playerLoc.getBlockX() - blockRadius) >> 4) - chunkSpan;
        int maxChunkX = ((playerLoc.getBlockX() + blockRadius) >> 4) + chunkSpan;
        int minChunkZ = ((playerLoc.getBlockZ() - blockRadius) >> 4) - chunkSpan;
        int maxChunkZ = ((playerLoc.getBlockZ() + blockRadius) >> 4) + chunkSpan;

        Set<Material> containerMaterials = new HashSet<>(Arrays.asList(materials));

        ConcurrentLinkedQueue<BlockState> foundTileEntities = new ConcurrentLinkedQueue<>();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                futures.add(PaperLib.getChunkAtAsync(playerLoc.getWorld(), chunkX, chunkZ).thenAccept(loadedChunk -> {
                    Arrays.stream(loadedChunk.getTileEntities())
                            .filter(tileEntity -> containerMaterials.contains(tileEntity.getType()))
                            .filter(tileEntity -> isWithinBoundingBox(playerLoc, tileEntity.getLocation(), blockRadius))
                            .forEach(foundTileEntities::add);
                }));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<BlockState> sortedTileEntities = new ArrayList<>(foundTileEntities);
                    sortedTileEntities.sort(Comparator.comparing(tileEntity -> tileEntity.getLocation().distanceSquared(playerLoc)));
                    return sortedTileEntities;
                });
    }

    private static boolean isWithinBoundingBox(Location center, Location check, int radius) {
        return check.getX() >= center.getX() - radius && check.getX() <= center.getX() + radius &&
                check.getY() >= center.getY() - radius && check.getY() <= center.getY() + radius &&
                check.getZ() >= center.getZ() - radius && check.getZ() <= center.getZ() + radius;
    }
}
