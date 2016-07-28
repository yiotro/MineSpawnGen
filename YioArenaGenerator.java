import net.morbz.minecraft.blocks.IBlock;
import net.morbz.minecraft.blocks.SimpleBlock;
import net.morbz.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ivan on 22.08.2015.
 */
public class YioArenaGenerator extends YioMapGenerator{

    int heightMap[][];
    int heightMapSize;
    int mountainHeight;
    boolean insideCrater[][], sandMap[][];
    IBlock GRASS_BLOCK = SimpleBlock.GRASS;
    IBlock SAND_BLOCK = SimpleBlock.END_STONE;
    IBlock TREE_BLOCK = SimpleBlock.HARDENED_CLAY;
    IBlock LEAF_BLOCK = SimpleBlock.MELON_BLOCK;
    IBlock STONE_BLOCK = SimpleBlock.COBBLESTONE;
    IBlock SNOW_BLOCK = SimpleBlock.SNOW;
    ArrayList<IBlock> oreBlocks;
    int INSIDE_CRATER_GROUND_LEVEL = Parameters.GROUND_LEVEL;
    int INSIDE_CRATER_MAX_CAP = Parameters.GROUND_MAX_CAP;

    public YioArenaGenerator(World world) {
        super(world);
        oreBlocks = new ArrayList<IBlock>();
        oreBlocks.add(SimpleBlock.COAL_ORE);
        oreBlocks.add(SimpleBlock.DIAMOND_ORE);
        oreBlocks.add(SimpleBlock.EMERALD_ORE);
        oreBlocks.add(SimpleBlock.GOLD_ORE);
        oreBlocks.add(SimpleBlock.IRON_ORE);
        oreBlocks.add(SimpleBlock.LAPIS_ORE);
        oreBlocks.add(SimpleBlock.REDSTONE_ORE);
    }

    void initRandom(FirstInterface firstInterface) {
        random = new Random();
        try {
            random = new Random(Long.valueOf(firstInterface.seedField.getText()));
        } catch (Exception e) {}
    }

    @Override
    public void generate() {
        YioSys.say("Started generating world...");
        INSIDE_CRATER_GROUND_LEVEL = Parameters.GROUND_LEVEL;
        INSIDE_CRATER_MAX_CAP = Parameters.GROUND_MAX_CAP;
        clearArea(0, 0, 1, 200, 200, 50);
        setMountainHeight(Parameters.MOUNTAIN_HEIGHT);
        createAllCacheMaps();

        makeCraterInHeightMap();
        trimHeightMapToMinHeight(INSIDE_CRATER_GROUND_LEVEL);
        randomizeLandscapeInsideCrater();

        corruptHeightMap();
        smoothHeightMap();
        smoothMountains();
        removeAllLonelyBlocksInsideCrater();
        cutOffHighBlocksInsideCrater();
        smoothGroundUnderWater();

        coverInSandEverythingNearWater();
        importHeightMapToWorld();
        coverEverythingInWaterUnderSomeLevel(INSIDE_CRATER_GROUND_LEVEL - 1);
        plantGrassAllOverCrater();
        plantTreesAllOverMap(Parameters.HOW_MANY_TREES);
        makeBackgroundMountains();
        YioSys.say("Ended generating world.");
    }

    void plantOresIntoMountains() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j=0; j<heightMapSize; j++) {
                if (isGoodForPlantingOre(i, j) && random.nextDouble() < 0.2) {
                    IBlock oreBlock = getRandomOreBlock();
                    plantOreToPlace(i, j, oreBlock);
                    if (isGoodForPlantingOre(i+1, j)) plantOreToPlace(i+1, j, oreBlock);
                    if (isGoodForPlantingOre(i, j+1)) plantOreToPlace(i, j+1, oreBlock);
                    if (isGoodForPlantingOre(i-1, j)) plantOreToPlace(i-1, j, oreBlock);
                    if (isGoodForPlantingOre(i, j-1)) plantOreToPlace(i, j-1, oreBlock);
                }
            }
        }
    }

    void plantOreToPlace(int i, int j, IBlock oreBlock) {
        int h = 1 + random.nextInt(3);
        for (int z = heightMap[i][j] - 1; z >= heightMap[i][j] - h; z--) {
            setWorldBlock(i, j, z, oreBlock);
        }
    }

    boolean isGoodForPlantingOre(int i, int j) {
        if (!isCoorInsideMap(i, j)) return false;
        if (insideCrater[i][j]) return false;
        int min = getMinAdjacent(i, j);
        if (min < heightMap[i][j] - 3) return true;
        return false;
    }

    int getMinAdjacent(int i, int j) {
        int min = heightMap[i][j];
        for (int k=0; k<4; k++) {
            int adj = getAdjacentHeightMap(i, j, k);
            if (adj < min) min = adj;
        }
        return min;
    }

    int getMaxAdjacent(int i, int j) {
        int max = heightMap[i][j];
        for (int k=0; k<4; k++) {
            int adj = getAdjacentHeightMap(i, j, k);
            if (adj > max) max = adj;
        }
        return max;
    }

    IBlock getRandomOreBlock() {
        return oreBlocks.get(random.nextInt(oreBlocks.size()));
    }

    void importHeightMapToWorld() {
        for (int i=1; i<heightMapSize-1; i++) {
            for (int j=1; j<heightMapSize-1; j++) {
                int adjMin = getMinAdjacent(i, j);
                if (adjMin >= heightMap[i][j]) adjMin = heightMap[i][j] - 1;
                if (!insideCrater[i][j] && heightMap[i][j] - adjMin > 3) adjMin = heightMap[i][j] - 3 - random.nextInt(2);
                IBlock block = GRASS_BLOCK;
                if (sandMap[i][j]) block = SAND_BLOCK;
                makeSingleVerticalColumn(i, j, 0, adjMin, STONE_BLOCK);
                makeSingleVerticalColumn(i, j, adjMin, heightMap[i][j] - adjMin, block);
            }
        }
    }

    void plantGrassAllOverCrater() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (insideCrater[i][j] && !sandMap[i][j] && heightMap[i][j] >= INSIDE_CRATER_GROUND_LEVEL && random.nextDouble() < Parameters.GRASS_DENSITY) {
                    setWorldBlock(i, j, heightMap[i][j], SimpleBlock.YELLOW_FLOWER);
                }
            }
        }
    }

    void trimHeightMapToMinHeight(int min) {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (heightMap[i][j] < min) heightMap[i][j] = min;
            }
        }
    }

    boolean isNearWater(int x, int y) {
        for (int i=x-3; i<=x+3; i++) {
            for (int j=y-3; j<=y+3; j++) {
                if (isCoorInsideMap(i, j) && insideCrater[i][j] && heightMap[i][j] < INSIDE_CRATER_GROUND_LEVEL && distance2D(x, y, i, j) < 2.6) {
                    return true;
                }
            }
        }
        return false;
    }

    void coverInSandEverythingNearWater() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (isNearWater(i, j)) {
                    if (heightMap[i][j] >= INSIDE_CRATER_GROUND_LEVEL + 2) continue;
                    if (!insideCrater[i][j]) continue;
                    if (heightMap[i][j] > INSIDE_CRATER_GROUND_LEVEL + 4) continue;
                    sandMap[i][j] = true;
                }
            }
        }
    }

    void makePit(int cx, int cy, int radius) {
        for (int x=cx-radius-1; x<=cx+radius+1; x++) {
            for (int y=cy-radius-1; y<=cy+radius+1; y++) {
                if (!isCoorInsideMap(x, y)) continue;
                double d = distance2D(cx, cy, x, y);
                if (d <= radius + 0.2 && insideCrater[x][y]) {
                    d /= radius;
                    double h = d * d;
                    h *= INSIDE_CRATER_GROUND_LEVEL;
                    lowerToLevel(x, y, (int)h);
                }
            }
        }
    }

    void coverEverythingInWaterUnderSomeLevel(int level) {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (heightMap[i][j] <= level && insideCrater[i][j])
                    makeSingleVerticalColumn(i, j, heightMap[i][j], level - heightMap[i][j] + 1, SimpleBlock.WATER);
            }
        }
    }

    double getRandomAngle() {
        return 2d * Math.PI * random.nextDouble();
    }

    double getCraterRadius() {
        return 0.8 * 0.5 * heightMapSize;
    }

    void makeRandomRiverInsideCrater(double startingAngle, int radius, double turnRate) {
        boolean waterMap[][] = null;
        if (!Parameters.RIVERS_CAN_INTERSECT) {
            waterMap = new boolean[heightMapSize][heightMapSize];
            for (int i = 0; i < heightMapSize; i++) {
                for (int j = 0; j < heightMapSize; j++) {
                    if (insideCrater[i][j] && heightMap[i][j] < INSIDE_CRATER_GROUND_LEVEL - 1) waterMap[i][j] = true;
                }
            }
        }

        double angle = startingAngle;
        double craterRadius = getCraterRadius();
        double currentX = heightMapSize/2 + craterRadius * Math.cos(angle);
        double currentY = heightMapSize/2 + craterRadius * Math.sin(angle);
        double step = 2.5;
        angle += Math.PI;
        angle += random.nextDouble() * 0.5 * Math.PI - 0.25 * Math.PI;
        double angleChangeDelta = 0;
        int radiusDelta = 0;
        startingAngle = angle;
        while (true) {
            if (random.nextDouble() > 0.5) radiusDelta = random.nextInt(3) - 1;
            if (radius + radiusDelta < 2) radiusDelta = 2 - radius;
            makePit((int)currentX, (int)currentY, radius + radiusDelta);
            currentX += step * Math.cos(angle);
            currentY += step * Math.sin(angle);
            angleChangeDelta += turnRate * (0.05 * random.nextDouble() - 0.025);
            if (angleChangeDelta > 0.15) angleChangeDelta = 0.15;
            if (angleChangeDelta < -0.15) angleChangeDelta = -0.15;
            if (Math.abs(startingAngle - angle) > 1.5) {
                angleChangeDelta = -0.5 * angleChangeDelta;
            }
            angle += angleChangeDelta;
            if (distance2D(heightMapSize/2, heightMapSize/2, currentX, currentY) > craterRadius) break;
            if (!Parameters.RIVERS_CAN_INTERSECT && waterMap[(int)currentX][(int)currentY]) break;
        }
    }

    void plantTreesAllOverMap(int howManyTrees) {
        boolean plantable[][] = new boolean[heightMapSize][heightMapSize];
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                plantable[i][j] = true;
                if (isNearWater(i, j)) plantable[i][j] = false;
                if (getMaxAdjacent(i, j) - getMinAdjacent(i, j) > 1) plantable[i][j] = false;
            }
        }
        int radius = 5;
        for (int k=0; k<howManyTrees; k++) {
            int i = random.nextInt(heightMapSize);
            int j = random.nextInt(heightMapSize);
            if (plantable[i][j]) {
                if (insideCrater[i][j] && random.nextDouble() < 0.2) makeGiantTree(i, j, heightMap[i][j]);
                else makeTree(i, j, heightMap[i][j]);
                for (int x = i-radius; x <= i+radius; x++) {
                    for (int y = j-radius; y <= j+radius; y++) {
                        if (isCoorInsideMap(x, y) && distance2D(i, j, x, y) <= radius) plantable[x][y] = false;
                    }
                }
            }
        }
    }

    void spawnManyTestTrees() {
        for (int i=-100; i<-15; i += 40) {
            for (int j=-100; j<-15; j += 40) {
                makeGiantTree(i, j, 0);
            }
        }
    }

    void makeGiantTree(int x, int y, int z) {
        double height = random.nextDouble() * 10 + 20;
        double stemRadius = 0.8 + 0.4 * random.nextDouble();
        double rh = height * (0.2 + 0.2 * random.nextDouble());
        double rx = x + 6 * random.nextDouble() - 3;
        double ry = y + 6 * random.nextDouble() - 3;
        int howManyBranches = random.nextInt(3) + 22;
        makeStick(x, y, z, rx, ry, z + rh, stemRadius + 0.2, TREE_BLOCK);
        makeStick(rx, ry, z + rh, rx = rx + 4 * random.nextDouble() - 2, ry = ry + 4 * random.nextDouble() - 2, z + height - 5, stemRadius, TREE_BLOCK);
        for (int i=0; i<howManyBranches; i++) {
            double a = getRandomAngle();
            double h1 = height * (0.4 + 0.2 * random.nextDouble());
            double h2 = height * (0.8 + 0.3 * random.nextDouble());
            double s = random.nextDouble() * 5 + 8;
            makeTreeBranch(x, y, h1, x + s * Math.cos(a), y + s * Math.sin(a), h2, 3.4 + random.nextDouble() * 0.5);
        }
        makeSphere(rx, ry, z + height - 5, 5.3 + 2 * random.nextDouble(), 0.2, LEAF_BLOCK);
    }

    void makeTree(int x, int y, int z) {
        double height = random.nextDouble() * 12 + 7;
        double stemRadius = 0.8 + 0.4 * random.nextDouble();
        double rh = height * (0.4 + 0.2 * random.nextDouble());
        double rx = x + 4 * random.nextDouble() - 2;
        double ry = y + 4 * random.nextDouble() - 2;
        int howManyBranches = random.nextInt(5) + 2;
        if (random.nextDouble() < 0.7) { // small tree
            howManyBranches = 0;
            height = 7 + random.nextInt(4);
            stemRadius = 0.5;
        }
        makeStick(x, y, z, rx, ry, z + rh, stemRadius, TREE_BLOCK);
        makeStick(rx, ry, z + rh, rx = rx + 4 * random.nextDouble() - 2, ry = ry + 4 * random.nextDouble() - 2, z + height, stemRadius, TREE_BLOCK);
        for (int i=0; i<howManyBranches; i++) {
            double a = getRandomAngle();
            double h1 = height * (0.2 + 0.5 * random.nextDouble());
            double h2 = h1 + height * (0.2 + 0.2 * random.nextDouble());
            double s = random.nextDouble() * 2 + 3;
            makeTreeBranch(x, y, h1, x + s * Math.cos(a), y + s * Math.sin(a), h2, 0.8 + random.nextDouble() * 0.5);
        }
        makeSphere(rx, ry, z + height, 2.9 + 2 * random.nextDouble(), 0.7, LEAF_BLOCK);
    }

    void makeStick(double x1, double y1, double z1, double x2, double y2, double z2, double radius, IBlock block) {
        int n = (int)(distance3D(x1, y1, z1, x2, y2, z2) / 0.2);
        double dx = (x2 - x1) / n;
        double dy = (y2 - y1) / n;
        double dz = (z2 - z1) / n;
        double x = x1, y = y1, z = z1;
        for (int k=0; k<n; k++) {
            makeSphere(x, y, z, radius, 1, block);
            x += dx;
            y += dy;
            z += dz;
        }
    }

    void makeTreeBranch(double x1, double y1, double z1, double x2, double y2, double z2, double radius) {
        makeStick(x1, y1, z1, x2, y2, z2, 0.3, TREE_BLOCK);
        makeSphere(x2, y2, z2, radius, 0.1, LEAF_BLOCK);
    }

    void makeSphere(double cx, double cy, double cz, double radius, double verticalFactor, IBlock block) {
        int lastX = 0, lastY = 0, lastZ = 0, currX, currY, currZ;
        for (double x = cx - radius; x <= cx + radius; x += 0.1) {
            for (double y = cy - radius; y <= cy + radius; y += 0.1) {
                for (double z = cz - radius; z <= cz + radius; z += 0.1) {
                    if (Math.sqrt((cx-x)*(cx-x) + (cy-y)*(cy-y) + (cz-z)*(cz-z)/verticalFactor) <= radius) {
                        currX = (int)x;
                        currY = (int)y;
                        currZ = (int)z;
                        if (currX == lastX && currY == lastY && currZ == lastZ) continue;
                        setWorldBlock(currX, currY, currZ, block);
                        lastX = currX;
                        lastY = currY;
                        lastZ = currZ;
                    }
                }
            }
        }
    }

    void makeHillRoad(double startingAngle, int radius, double turnRate) {
        double angle = startingAngle;
        double craterRadius = getCraterRadius();
        double currentX = heightMapSize/2 + craterRadius * Math.cos(angle);
        double currentY = heightMapSize/2 + craterRadius * Math.sin(angle);
        double step = 2.5;
        angle += Math.PI;
        double angleChangeDelta = 0;
        int h = random.nextInt(4);
        while (true) {
            makeHill((int) currentX, (int) currentY, h + random.nextInt(2), radius);
            currentX += step * Math.cos(angle);
            currentY += step * Math.sin(angle);
            angleChangeDelta += turnRate * (0.05 * random.nextDouble() - 0.025);
            if (angleChangeDelta > 0.15) angleChangeDelta = 0.15;
            if (angleChangeDelta < -0.15) angleChangeDelta = -0.15;
            angle += angleChangeDelta;
            if (distance2D(heightMapSize/2, heightMapSize/2, currentX, currentY) > craterRadius) break;
        }
    }

    void makeRandomizedHill(int x, int y) {
        int size = random.nextInt(Parameters.MAX_HILL_HEIGHT - Parameters.MIN_HILL_HEIGHT + 1) + Parameters.MIN_HILL_HEIGHT;
        int radius = 2 * size;
        for (int i=x-radius-1; i<=x+radius+1; i++) {
            for (int j=y-radius-1; j<=y+radius+1; j++) {
                if (!isCoorInsideMap(i, j)) continue;
                double d = distance2D(x, y, i, j);
                if (d <= radius && isNearWater(i, j)) {
                    return;
                }
            }
        }
        int corruption[][] = new int[heightMapSize][heightMapSize];
        for (int k=0; k<size; k++) {
            radius = 2 * (size - k);
            int currX = x + random.nextInt(2 * (k + 1)) - k;
            int currY = y + random.nextInt(2 * (k + 1)) - k;
            for (int i=currX-radius-1; i<=currX+radius+1; i++) {
                for (int j=currY-radius-1; j<=currY+radius+1; j++) {
                    if (!isCoorInsideMap(i, j)) continue;
                    double d = distance2D(currX, currY, i, j);
                    if (d <= radius) corruption[i][j] += 1;
                }
            }
        }
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                raiseToLevel(i, j, heightMap[i][j] + corruption[i][j]);
            }
        }
    }

    void cutOffHighBlocksInsideCrater() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (insideCrater[i][j] && heightMap[i][j] > INSIDE_CRATER_MAX_CAP) heightMap[i][j] = INSIDE_CRATER_MAX_CAP;
            }
        }
    }

    void makeHill(int x, int y, int height, int radius) {
        for (int i=x-radius-1; i<=x+radius+1; i++) {
            for (int j=y-radius-1; j<=y+radius+1; j++) {
                if (!isCoorInsideMap(i, j)) continue;
                double d = distance2D(x, y, i, j);
                d /= radius;
                d *= 0.5 * Math.PI;
                double h = Math.cos(d);
                h *= height;
                h -= 0.01;
                raiseToLevel(i, j, INSIDE_CRATER_GROUND_LEVEL + (int)h + 1);
            }
        }
    }

    void raiseToLevel(int x, int y, int level) {
        if (heightMap[x][y] > level) return;
        heightMap[x][y] = level;
    }

    void lowerToLevel(int x, int y, int level) {
        if (heightMap[x][y] < level) return;
        heightMap[x][y] = level;
    }

    void makeBackgroundMountain(int x, int y) {
        int size = 50;
        boolean m[][] = new boolean[size][size];
        boolean canExpand[][] = new boolean[size][size];
        m[size/2][size/2] = true;
        int startingHeight = mountainHeight + 20 + random.nextInt(40);
        for (int h = startingHeight; h >= mountainHeight; h--) {
            for (int i=0; i<size; i++) {
                for (int j=0; j<size; j++) {
                    canExpand[i][j] = false;
                    if (m[i][j]) {
                        IBlock block = GRASS_BLOCK;
                        double snowProbability = (double)(h - mountainHeight - 35) / 10d;
                        if (random.nextDouble() < snowProbability) block = SNOW_BLOCK;
                        setWorldBlock(x - size/2 + i, y - size/2 + j, h, block);
                        canExpand[i][j] = true;
                    }
                }
            }
            for (int i=0; i<size; i++) {
                for (int j=0; j<size; j++) {
                    if (m[i][j] && canExpand[i][j]) {
                        if (random.nextDouble() < 0.25 && j < size - 1) m[i][j+1] = true;
                        if (random.nextDouble() < 0.25 && i < size - 1) m[i+1][j] = true;
                        if (random.nextDouble() < 0.25 && j > 0) m[i][j-1] = true;
                        if (random.nextDouble() < 0.25 && i > 0) m[i-1][j] = true;
                    }
                }
            }
        }
    }

    void makeBackgroundMountains() {
        for (int i=0; i<Parameters.HOW_MANY_BACKGROUND_MOUNTAINS; i++) {
            double a = getRandomAngle();
            double delta = 10;
            double x = heightMapSize / 2;
            double y = heightMapSize / 2;
            while (x > -10 && y > -10 && x < heightMapSize + 10 && y < heightMapSize + 10) {
                x += delta * Math.cos(a);
                y += delta * Math.sin(a);
            }
            makeBackgroundMountain((int)x, (int)y);
        }
    }

    void throwPancakesOnFields() {
        int delta[][] = new int[heightMapSize][heightMapSize];
        int n = (int)(0.005 * heightMapSize * heightMapSize);
        int radius = 10;
        for (int k=0; k<n; k++) {
            double a = getRandomAngle();
            double d = random.nextDouble() * getCraterRadius();
            int cx = (int)(heightMapSize / 2 + d * Math.cos(a));
            int cy = (int)(heightMapSize / 2 + d * Math.sin(a));
            boolean raise;
            if (random.nextDouble() < 0.1) raise = false;
            else raise = true;
            for (int i=cx-radius; i<=cx+radius; i++) {
                for (int j=cy-radius; j<=cy+radius; j++) {
                    if (distance2D(cx, cy, i, j) < radius && insideCrater[i][j]) {
                        if (raise) delta[i][j] += 1;
                        else delta[i][j] -= 1;
                    }
                }
            }
        }

        for (int i = 0; i < heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (delta[i][j] > 2) delta[i][j] = 2;
                if (delta[i][j] < -1) delta[i][j] = -1;
                heightMap[i][j] += delta[i][j];
            }
        }
    }

    void randomizeLandscapeInsideCrater() {
        throwPancakesOnFields();

        for (int i=0; i<Parameters.HOW_MANY_HILLS; i++) {
            makeHillRoad(getRandomAngle(), 6 + random.nextInt(5), 1.3);
        }

        // rivers
        double angle;
        for (int i=0; i<Parameters.HOW_MANY_RIVERS; i++) {
            angle = getRandomAngle();
            makeRandomRiverInsideCrater(angle, 2 + random.nextInt(2), 1 + 0.2 * random.nextDouble());
        }

        for (int i=0; i<Parameters.HOW_MANY_HILLS; i++) {
            double a = getRandomAngle();
            double d = random.nextDouble() * getCraterRadius();
            makeRandomizedHill(heightMapSize / 2 + (int)(d * Math.cos(a)), heightMapSize / 2 + (int)(d * Math.sin(a)));
        }
    }

    void templateForLoop() {
        for (int i = 0; i < heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {

            }
        }
    }

    void smoothMountains() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (!insideCrater[i][j]) {
                    int c = 0, max = 0;
                    for (int k=0; k<4; k++)
                        if (getAdjacentHeightMap(i, j, k) >= heightMap[i][j]) {
                            c++;
                            if (getAdjacentHeightMap(i, j, k) > max) max = getAdjacentHeightMap(i, j, k);
                        }
                    int delta = max - heightMap[i][j];
                    if (delta > 3) delta = 3;
                    if (c < 2) raiseToLevel(i, j, heightMap[i][j] + delta);
                }
            }
        }
    }

    void smoothGroundUnderWater() {
        for (int i = 0; i < heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (insideCrater[i][j] && heightMap[i][j] < GROUND_LEVEL) {
                    int sameLevelPlusOne = 0;
                    int muchHigher = 0;
                    int value = heightMap[i][j];
                    for (int k=0; k<4; k++) {
                        int adj = getAdjacentHeightMap(i, j, k);
                        if (adj == value + 1) sameLevelPlusOne++;
                        else if (adj > value + 1) muchHigher++;
                    }
                    if (muchHigher >= 1 && sameLevelPlusOne >= 2) heightMap[i][j] += 1;
                }
            }
        }
    }

    void smoothHeightMap() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j=0; j<heightMapSize; j++) {
                int value = heightMap[i][j];
                int max = getMaxAdjacent(i, j);
                int min = getMinAdjacent(i, j);
                if (max > value + 2 && min < value - 2) continue;
                if (max > value + 2) {
                    heightMap[i][j] += 1;
                }
                if (min < value - 2) {
                    heightMap[i][j] -= 1;
                }
             }
        }
    }

    boolean isCoorInsideMap(int i, int j) {
        if (i < 0 || j < 0 || i >= heightMapSize || j >= heightMapSize) return false;
        return true;
    }

    int getHeightMapValue(int i, int j) {
        if (!isCoorInsideMap(i, j)) return 0;
        return heightMap[i][j];
    }

    int getAdjacentHeightMap(int i, int j, int dir) {
        switch (dir) {
            default:
            case 0: return getHeightMapValue(i, j+1);
            case 1: return getHeightMapValue(i+1, j);
            case 2: return getHeightMapValue(i, j-1);
            case 3: return getHeightMapValue(i-1, j);
        }
    }

    void corruptHeightMap() {
        int corruption[][] = new int[heightMapSize][heightMapSize];
        for (int i=0; i<heightMapSize; i++) {
            for (int j=0; j<heightMapSize; j++) {
                if (!insideCrater[i][j]) corruption[i][j] = random.nextInt(4) - 1;
//                else {
//                    if (random.nextDouble() < 0.2) corruption[i][j] = 1;
//                    else corruption[i][j] = 0;
//                }
                heightMap[i][j] += corruption[i][j];
            }
        }
    }

    void removeAllLonelyBlocksInsideCrater() {
        for (int i=0; i<heightMapSize; i++) {
            for (int j = 0; j < heightMapSize; j++) {
                if (insideCrater[i][j]) {
                    boolean alone = true;
                    for (int k=0; k<4; k++) {
                        if (getAdjacentHeightMap(i, j, k) >= heightMap[i][j]) {
                            alone = false;
                            break;
                        }
                    }
                    if (alone) {
                        heightMap[i][j] -= 1;
                    }
                }
            }
        }
    }

    void createAllCacheMaps() {
        heightMap = new int[heightMapSize][heightMapSize];
        insideCrater = new boolean[heightMapSize][heightMapSize];
        sandMap = new boolean[heightMapSize][heightMapSize];
    }

    void makeCraterInHeightMap() {
        double maxDistance = heightMapSize / 2;
        for (int i=0; i<heightMapSize; i++) {
            for (int j=0; j<heightMapSize; j++) {
                double d = distance2D(heightMapSize / 2, heightMapSize / 2, i, j);
                double a = angle2D(i, j, heightMapSize / 2, heightMapSize / 2);
                while (a > 0.5 * Math.PI) a -= 0.5 * Math.PI;
                while (a < 0) a += 0.5 * Math.PI;
                double multiplier = 1 - 0.15 * Math.sin(2 * a);
                d /= maxDistance;
                d *= multiplier;
                double f = function(d, 0.7, 0.7 + Parameters.WALL_ANGLE);
                int currentHeight = (int)(f * mountainHeight);
                heightMap[i][j] = currentHeight;
                if (currentHeight == 0) insideCrater[i][j] = true;
            }
        }
    }

    void makeSingleVerticalColumn(int x, int y, int z, int height, IBlock block) {
        for (int k=z; k<z+height; k++) {
            setWorldBlock(x, y, k, block);
        }
    }

    double function(double x, double a, double b) {
        if (x < a) return 0;
        if (x > b) return 1;
        return (x - a) / (b - a);
    }

    public void setMountainHeight(int mountainHeight) {
        this.mountainHeight = mountainHeight;
    }

    public void setHeightMapSize(int heightMapSize) {
        this.heightMapSize = heightMapSize;
    }
}
