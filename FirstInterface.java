import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class FirstInterface extends JFrame {

    private static final long serialVersionUID = 1L;
    JTextArea outputArea;
    JTextField folderPathField, mapSizeField, riverCountField, treesCountField, mountainHeightField;
    JTextField groundLevelField, groundMaxCapField, hillsCountField, grassDensityField, minHillField, maxHillField;
    JTextField bckMountainsCountField, seedField, wallAngleField;
    JCheckBox saveToFolderCheckbox, riversIntersectCheckbox;
    int frameWidth, frameHeight;
    Font font;
    int currentParamX, currentParamY;

    private FirstInterface(){
        super("Minecraft map generator by yiotro");

        frameWidth = 500;
        frameHeight = 600;

        currentParamX = 10;
        currentParamY = 90;

        Parameters.loadPreferences();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        MyButtonListener myButtonListener = new MyButtonListener(this);
        CheckBoxListener checkBoxListener = new CheckBoxListener(this);

        font = new Font("Segoe UI", Font.PLAIN, 14);
        setFont(font);

        setLayout(null);

        saveToFolderCheckbox = new JCheckBox("Копировать мир в папку", true);
        saveToFolderCheckbox.setBounds(10, 10, 200, 30);
        add(saveToFolderCheckbox);
        saveToFolderCheckbox.setActionCommand("saveToFolder");
        saveToFolderCheckbox.addItemListener(checkBoxListener);

        riversIntersectCheckbox = new JCheckBox("Реки могут пересекаться", Parameters.RIVERS_CAN_INTERSECT);
        riversIntersectCheckbox.setBounds(10, frameHeight - 250, 240, 30);
        add(riversIntersectCheckbox);

        JLabel seedLabel = new JLabel("Сид");
        seedLabel.setBounds(250, 10, 50, 30);
        seedLabel.setFont(font);
        add(seedLabel);

        seedField = new JTextField((new Random()).nextInt(999999999) + "");
        seedField.setFont(font);
        seedField.setBounds(285, 10, 195, 30);
        add(seedField);

        folderPathField = new JTextField(Parameters.FOLDER);
        folderPathField.setFont(font);
        folderPathField.setBounds(10, 50, 470, 30);
        add(folderPathField);

        outputArea = new JTextArea("Program successfully started\n");
        outputArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        JScrollPane scrollPane2 = new JScrollPane(outputArea);
        scrollPane2.setBounds(10, frameHeight - 220, 470, 150);
        add(scrollPane2);

        JButton generateButton = new JButton("Сгенерировать мир");
        generateButton.setActionCommand("generate");
        generateButton.setBounds(281, frameHeight - 65, 200, 30);
        generateButton.addActionListener(myButtonListener);
        generateButton.setFont(font);
        add(generateButton);

        mapSizeField = new JTextField(Parameters.MAP_SIZE + "");
        addParameterFieldAndLabel(mapSizeField, "Размер карты");

        riverCountField = new JTextField(Parameters.HOW_MANY_RIVERS + "");
        addParameterFieldAndLabel(riverCountField, "Количество рек");

        treesCountField = new JTextField(Parameters.HOW_MANY_TREES + "");
        addParameterFieldAndLabel(treesCountField, "Макс. кол-во деревьев");

        mountainHeightField = new JTextField(Parameters.MOUNTAIN_HEIGHT + "");
        addParameterFieldAndLabel(mountainHeightField, "Высота гор");

        groundLevelField = new JTextField(Parameters.GROUND_LEVEL + "");
        addParameterFieldAndLabel(groundLevelField, "Уровень земли");

        groundMaxCapField = new JTextField(Parameters.GROUND_MAX_CAP + "");
        addParameterFieldAndLabel(groundMaxCapField, "Высота срезания холмов");

        hillsCountField = new JTextField(Parameters.HOW_MANY_HILLS + "");
        addParameterFieldAndLabel(hillsCountField, "Количество холмов");

        grassDensityField = new JTextField(Parameters.GRASS_DENSITY + "");
        addParameterFieldAndLabel(grassDensityField, "Плотность травы");

        minHillField = new JTextField(Parameters.MIN_HILL_HEIGHT + "");
        addParameterFieldAndLabel(minHillField, "Мин. размер холма");

        maxHillField = new JTextField(Parameters.MAX_HILL_HEIGHT + "");
        addParameterFieldAndLabel(maxHillField, "Макс. размер холма");

        bckMountainsCountField = new JTextField(Parameters.HOW_MANY_BACKGROUND_MOUNTAINS + "");
        addParameterFieldAndLabel(bckMountainsCountField, "Количество фоновых гор");

        wallAngleField = new JTextField(Parameters.WALL_ANGLE + "");
        addParameterFieldAndLabel(wallAngleField, "Наклон стен (0.01 - 0.2)");

        setSize(frameWidth, frameHeight);
        setVisible(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }

    void addParameterFieldAndLabel(JTextField field, String description) {
        field.setFont(font);
        field.setBounds(currentParamX, currentParamY, 50, 30);
        add(field);

        JLabel label = new JLabel(description);
        label.setFont(font);
        label.setBounds(currentParamX + 60, currentParamY, 180, 30);
        add(label);

        currentParamX += 240;
        if (currentParamX > 300) {
            currentParamX = 10;
            currentParamY += 40;
        }
    }

    public static void main(String args[]){
        JFrame f = new FirstInterface();
        f.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent ev){
                System.exit(0);
            }
        });
    }
}

class CheckBoxListener implements ItemListener {

    private final FirstInterface firstInterface;

    CheckBoxListener(FirstInterface firstInterface) {
        this.firstInterface = firstInterface;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        JCheckBox checkBox = ((JCheckBox)e.getItem());
        String actionCommand = checkBox.getActionCommand();
        if (actionCommand.equals("saveToFolder")) {

        }
    }
}

class MyButtonListener implements ActionListener  {

    private final FirstInterface firstInterface;

    MyButtonListener(FirstInterface firstInterface) {
        this.firstInterface = firstInterface;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("generate")) {
            YioSys yioSys = new YioSys();
            yioSys.setFirstInterface(firstInterface);
            yioSys.main(null);
        }
    }
}