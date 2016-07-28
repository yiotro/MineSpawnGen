import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.blocks.SimpleBlock;
import net.morbz.minecraft.world.World;

/**
 * Created by ivan on 21.08.2015.
 */
public class YioCastleGenerator extends YioMapGenerator {

    public YioCastleGenerator(World world) {
        super(world);
    }

    public void generate() {
        YioSys.say(" - Started generating - ");

        for (int i=0; i<5; i++) {
            int x1 = random.nextInt(50);
            int y1 = random.nextInt(50);
            int x2 = random.nextInt(50);
            int y2 = random.nextInt(50);
            int r = random.nextInt(5) + 5;
            int h = random.nextInt(5) + 5;
        }

        showAllSimpleBlocks();

//        world.setBlock(50, 51, 50, DoorBlock.makeLower(DoorBlock.DoorMaterial.OAK, Facing4State.EAST, false));
//        world.setBlock(50, 52, 50, DoorBlock.makeUpper(DoorBlock.DoorMaterial.OAK, DoorBlock.HingeSide.LEFT));
        YioSys.say(" - Ended generating - ");
    }

    void makeCone(int cx, int cy, int z, int radius, int height, IBlock block) {
        double angle = angle2D(0, 0, radius, height);
        double distance = distance2D(0, 0, radius, height);
        int currentRadius, currentRX, currentRY, lastH = -1;
        for (double currentPos = 0; currentPos <= distance; currentPos += 0.2) {
            currentRX = (int)(currentPos * Math.cos(angle));
            currentRY = (int)(currentPos * Math.sin(angle));
            if (currentRY == lastH) continue;
            currentRadius = radius - currentRX - 1;
            makePancake(cx, cy, z + currentRY, currentRadius, block);
            lastH = currentRY;
        }
    }

    void makePancake(int cx, int cy, int z, int radius, IBlock block) {
        for (int i=cx-radius-1; i<=cx+radius+1; i++) {
            for (int j=cy-radius-1; j<=cy+radius+1; j++) {
                if (distance2D(cx, cy, i, j) <= radius + 0.2) {
                    setWorldBlock(i, j, z, block);
                }
            }
        }
    }

    void makeCircularWall(int cx, int cy, int z, int radius, int height, int segments, IBlock block) {
        double angleDelta = (2d * Math.PI) / (double)segments;
        int x1, y1, x2, y2;
        for (double currentAngle = 0; currentAngle <= 2d * Math.PI - angleDelta; currentAngle += angleDelta) {
            x1 = cx + (int)(radius * Math.cos(currentAngle));
            y1 = cy + (int)(radius * Math.sin(currentAngle));
            x2 = cx + (int)(radius * Math.cos(currentAngle + angleDelta));
            y2 = cy + (int)(radius * Math.sin(currentAngle + angleDelta));
            makeWall(x1, y1, z, x2, y2, height, block);
        }
    }

    void makeWall(int x1, int y1, int z, int x2, int y2, int height, IBlock block) {
        double angle = angle2D(x1, y1, x2, y2);
        double distance = distance2D(x1, y1, x2, y2);
        double delta = 0.1;
        int lastX = x1 - 1, lastY = y1 - 1;
        int currentX, currentY;
        for (double currentPos = 0; currentPos <= distance; currentPos += delta) {
            currentX = x1 + (int)(currentPos * Math.cos(angle));
            currentY = y1 + (int)(currentPos * Math.sin(angle));
            if (currentX == lastX && currentY == lastY) continue;
            makeSingleVerticalColumn(currentX, currentY, z, height, block);
            lastX = currentX;
            lastY = currentY;
        }
    }

    void makeSingleStairs(int startX, int startY, int startZ, int height, int direction, IBlock block) { // stairs with width = 1
        int x = startX;
        int y = startY;
        for (int i=0; i<height; i++) {
            makeSingleVerticalColumn(x, y, startZ, i+1, block);
            switch (direction) {
                case NORTH: y++; break;
                case EAST: x++; break;
                case SOUTH: y--; break;
                case WEST: x--; break;
            }
        }
    }

    void makeStairs(int startX, int startY, int startZ, int width, int height, int direction, IBlock block) {
        int perpendicularDirection;
        if (width > 0) perpendicularDirection = rotateDirectionToTheRight(direction);
        else perpendicularDirection = rotateDirectionToTheLeft(direction);
        int x = startX;
        int y = startY;
        for (int i=0; i<width; i++) {
            makeSingleStairs(x, y, startZ, height, direction, block);
            switch (perpendicularDirection) {
                case NORTH: y++; break;
                case EAST: x++; break;
                case SOUTH: y--; break;
                case WEST: x--; break;
            }
        }
    }

    void makeSemiSphere(int cx, int cy, int cz, int radius, IBlock block) {
        for (int i=cx-radius-1; i<=cx+radius+1; i++) {
            for (int j=cy-radius-1; j<=cy+radius+1; j++) {
                for (int k=cz; k<=cz+radius+1; k++) {
                    if (distance3D(cx, cy, cz, i, j, k) <= radius + 0.2) {
                        setWorldBlock(i, j, k, block);
                    }
                }
            }
        }
    }

    void makeSphere(int cx, int cy, int cz, int radius, IBlock block) {
        for (int i=cx-radius-1; i<=cx+radius+1; i++) {
            for (int j=cy-radius-1; j<=cy+radius+1; j++) {
                for (int k=cz-radius-1; k<=cz+radius+1; k++) {
                    if (distance3D(cx, cy, cz, i, j, k) <= radius + 0.2) {
                        setWorldBlock(i, j, k, block);
                    }
                }
            }
        }
    }

    void makeBrick(int x, int y, int z, int xSize, int ySize, int zSize, IBlock block) {
        for (int i=x; i<x+xSize; i++) {
            for (int j=y; j<y+ySize; j++) {
                for (int k=z; k<z+zSize; k++) {
                    setWorldBlock(i, j, k, block);
                }
            }
        }
    }

    void makeCircularPillar(int cx, int cy, int startZ, int height, int radius, IBlock block) {
        for (int z=startZ; z<startZ + height; z++) {
            for (int x=cx-radius-1; x<=cx+radius+1; x++) {
                for (int y=cy-radius-1; y<=cy+radius+1; y++) {
                    if (distance2D(cx, cy, x, y) <= radius + 0.2) {
                        setWorldBlock(x, y, z, block);
                    }
                }
            }
        }
    }

    void makeSingleVerticalColumn(int x, int y, int z, int height, IBlock block) {
        for (int k=z; k<z+height; k++) {
            setWorldBlock(x, y, k, block);
        }
    }

    void showAllSimpleBlocks() {
        int x = 2;
        int y = 2;
        for (IBlock block : SimpleBlock.values()) {
            setWorldBlock(x, y, 1, block);
            x++;
            if (x > 11) {
                x = 2;
                y++;
            }
        }
    }


}
