import net.morbz.minecraft.blocks.Material;
import net.morbz.minecraft.level.FlatGenerator;
import net.morbz.minecraft.level.GameType;
import net.morbz.minecraft.level.IGenerator;
import net.morbz.minecraft.level.Level;
import net.morbz.minecraft.world.DefaultLayers;
import net.morbz.minecraft.world.World;

import javax.swing.*;

/**
 * Created by ivan on 21.08.2015.
 */
public class YioSys {

    static World world;

    static FirstInterface firstInterface;

    public static void main(String[] args) {
        Parameters.setParametersByUI(firstInterface);
        Parameters.savePreferences();
        DefaultLayers layers = new DefaultLayers();
        layers.setLayer(0, Material.BEDROCK);
        layers.setLayers(1, 20, Material.IRON_BLOCK);

        IGenerator generator = new FlatGenerator(layers);

        Level level = new Level("Yiotro world", generator);
        level.setGameType(GameType.CREATIVE);
        level.setSpawnPoint(35, 35, 100);
//        level.setSpawnPoint(-5, 25, -5);

        world = new World(level, layers);

//        YioCastleGenerator yioCastleGenerator = new YioCastleGenerator(world);
//        yioCastleGenerator.generate();
//
        YioArenaGenerator yioArenaGenerator = new YioArenaGenerator(world);
        yioArenaGenerator.initRandom(firstInterface);
        yioArenaGenerator.setHeightMapSize(Parameters.MAP_SIZE);
        yioArenaGenerator.generate();

        if (needToSaveToFolder()) {
            DeleteDirectory.deleteFolder(System.getProperty("user.dir") + "\\worlds");
            DeleteDirectory.deleteFolder(Parameters.FOLDER + "\\Yiotro world");
        }

        try {
            world.save();
            say("Saved world");
        } catch (Exception e) {
            say("Got ERROR when saving world: " + e.toString());
        }

        if (needToSaveToFolder()) CopyDirectory.copyWorldToSavesFolder();

        say("Finished.");
    }

    static boolean needToSaveToFolder() {
        if (firstInterface == null) return true;
        return firstInterface.saveToFolderCheckbox.isSelected();
    }

    public void setFirstInterface(FirstInterface firstInterface) {
        this.firstInterface = firstInterface;
    }

    public static void say(String message) {
        System.out.println(message);
        if (firstInterface != null) {
            String text = firstInterface.outputArea.getText();
            String newText = text + message + "\n";
            firstInterface.outputArea.setText(newText);
            firstInterface.outputArea.update(firstInterface.outputArea.getGraphics());
        }
    }

    public static void whisper(String message) {
        System.out.print(message);
    }
}
