package sorryplspls.EchoAFK.service;

import org.bukkit.Location;

public class RegionService {

    private Location corner1;
    private Location corner2;

    /**
     * Initialize the AFK region corners.
     */

    public void setRegionCorners(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    /**
     * Checks if the given location is inside the cuboid defined by corner1 and corner2.
     */

    public boolean isInRegion(Location loc) {
        if (corner1 == null || corner2 == null) return false;
        if (!loc.getWorld().equals(corner1.getWorld())) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        int xMin = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int yMin = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int zMin = Math.min(corner1.getBlockZ(), corner2.getBlockZ());

        int xMax = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int yMax = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int zMax = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        return x >= xMin && x <= xMax
                && y >= yMin && y <= yMax
                && z >= zMin && z <= zMax;
    }
}
