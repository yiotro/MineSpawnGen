import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.blocks.SimpleBlock;
import net.morbz.minecraft.world.World;

import java.util.Random;

/**
 * Created by ivan on 22.08.2015.
 */
public abstract class YioMapGenerator {

    World world;
    Random random;
    public static final int NORTH = 0;
    public static final int EAST = 1;
    public static final int SOUTH = 2;
    public static final int WEST = 3;
    int GROUND_LEVEL = 20;

    public YioMapGenerator(World world) {
        this.world = world;
        random = new Random();
    }

    public abstract void generate();

    static double angle2D(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }

    void clearArea(int x, int y, int z, int xSize, int ySize, int zSize) {
        for (int i=x; i<x+xSize; i++) {
            for (int j=y; j<y+ySize; j++) {
                for (int k=z; k<z+zSize; k++) {
                    setWorldBlock(i, j, k, SimpleBlock.AIR);
                }
                setWorldBlock(i, j, 0, SimpleBlock.SOUL_SAND);
            }
        }
    }

    int rotateDirectionToTheRight(int direction) {
        direction++;
        if (direction > 3) direction = 0;
        return direction;
    }

    int rotateDirectionToTheLeft(int direction) {
        direction--;
        if (direction < 0) direction = 3;
        return direction;
    }

    int getRandomDirection() {
        return random.nextInt(4);
    }

    public static double distance2D(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    public static double distance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
    }

    void setWorldBlock(int x, int y, int z, IBlock block) {
        world.setBlock(x, z + GROUND_LEVEL, y, block);
    }
}
