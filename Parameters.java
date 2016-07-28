import java.util.prefs.Preferences;

/**
 * Created by ivan on 23.08.2015.
 */
public class Parameters {

    public static int MAP_SIZE = 200;
    public static int HOW_MANY_RIVERS = 2;
    public static int HOW_MANY_TREES = 40;
    public static int MOUNTAIN_HEIGHT = 40;
    public static String FOLDER;
    public static int GROUND_LEVEL = 5;
    public static int GROUND_MAX_CAP = 15;
    public static int HOW_MANY_HILLS = 10;
    public static double GRASS_DENSITY = 0.15;
    public static int MIN_HILL_HEIGHT = 5;
    public static int MAX_HILL_HEIGHT = 10;
    public static int HOW_MANY_BACKGROUND_MOUNTAINS = 30;
    public static boolean RIVERS_CAN_INTERSECT = false;
    public static double WALL_ANGLE = 0.15;

    public static void setParametersByUI(FirstInterface firstInterface) {
        if (firstInterface == null) {
            FOLDER = "C:\\Games\\Minecraft 1.8.3\\saves";
            return;
        }

        try {
            MAP_SIZE = Integer.valueOf(firstInterface.mapSizeField.getText());
        } catch (Exception e) {}

        try {
            HOW_MANY_RIVERS = Integer.valueOf(firstInterface.riverCountField.getText());
        } catch (Exception e) {}

        try {
            HOW_MANY_TREES = Integer.valueOf(firstInterface.treesCountField.getText());
        } catch (Exception e) {}

        try {
            MOUNTAIN_HEIGHT = Integer.valueOf(firstInterface.mountainHeightField.getText());
        } catch (Exception e) {}

        try {
            GROUND_LEVEL = Integer.valueOf(firstInterface.groundLevelField.getText());
        } catch (Exception e) {}

        try {
            GROUND_MAX_CAP = Integer.valueOf(firstInterface.groundMaxCapField.getText());
        } catch (Exception e) {}

        try {
            HOW_MANY_HILLS = Integer.valueOf(firstInterface.hillsCountField.getText());
        } catch (Exception e) {}

        try {
            GRASS_DENSITY = Double.valueOf(firstInterface.grassDensityField.getText());
        } catch (Exception e) {}

        try {
            MIN_HILL_HEIGHT = Integer.valueOf(firstInterface.minHillField.getText());
        } catch (Exception e) {}

        try {
            MAX_HILL_HEIGHT = Integer.valueOf(firstInterface.maxHillField.getText());
        } catch (Exception e) {}

        try {
            HOW_MANY_BACKGROUND_MOUNTAINS = Integer.valueOf(firstInterface.bckMountainsCountField.getText());
        } catch (Exception e) {}

        try {
            RIVERS_CAN_INTERSECT = firstInterface.riversIntersectCheckbox.isSelected();
        } catch (Exception e) {}

        try {
            WALL_ANGLE = Double.valueOf(firstInterface.wallAngleField.getText());
        } catch (Exception e) {}

        try {
            String str = firstInterface.folderPathField.getText();
            if (str.length() > 10) FOLDER = str;
        } catch (Exception e) {}
    }

    public static void savePreferences() {
        if (YioSys.firstInterface == null) return;
        Preferences prefs = Preferences.userRoot();
        prefs.put("folder", FOLDER);
    }

    public static void loadPreferences() {
        Preferences prefs = Preferences.userRoot();
        FOLDER = prefs.get("folder", "C:\\Games\\Minecraft 1.8.3\\saves");
    }
}
