package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DataManager {
    private static final String DATA_FILE_PATH = "lib/PERSISTENT_DATA.txt";
    private static final DataKey[] DATA_KEYS = DataKey.values();
    private static final String DATA_SERPARATOR = ": ";
    private static HashMap<DataKey, String> dataMap = new HashMap<DataKey, String>();

    public static void init() {
        Main.LOGGER.info("Validating Data File Integrity...");

        if(validateDataFileIntegrity()) {
            Main.LOGGER.info("Data File Integrity Check Passed");
        } else {
            createDataFile(); // Ensure dataFile is deleted
            Scanner scan = new Scanner(System.in);

            // File check passed unsuccessfully, manual data input required
            Main.LOGGER.info("Data File Integrity Check Failed, Manual Input Required!\n");
            for(int i = 0; i < DATA_KEYS.length; i++) {
                System.out.print("Enter " + DATA_KEYS[i].getName() + ": ");
                DataManager.setData(i, scan.nextLine().trim());
            }

            System.out.println();
            scan.close();
        }

        // All persistent data checked, load data into ram
        Main.LOGGER.info("Loading Data into Memory");
        loadDataToMap();

        Main.LOGGER.info("All Data Loaded!");
    }

    /**
     * <p>Checks if the file exists, if the file is not empty, and if every key has a value</p>
     * <p>If the file is not present or is empty, a new file will be created</p>
     * 
     * @return True if the file existed and was not empty
     */
    private static boolean validateDataFileIntegrity() {
        File dataFile = new File(DATA_FILE_PATH);
        
        // File exists
        if(!dataFile.exists() || dataFile.length() == 0) {
            return false;
        }

        // File's lines are good
        try {
            List<String> lines = Files.readAllLines(dataFile.toPath());
            if(lines.size() < DATA_KEYS.length || lines.size() > DATA_KEYS.length)
                return false;

            // Each data key has a value
            Scanner scan = new Scanner(dataFile);
            for(int i = 0; scan.hasNextLine(); i++) {
                if(scan.nextLine().equals(DATA_KEYS[i] + DATA_SERPARATOR)) { // Just data key and serparator
                    return false;
                }
            }
            scan.close();
        } catch(IOException e) {
            e.printStackTrace();
            Main.LOGGER.severe("Failed to Read Data File, Exiting!");
            System.exit(1);
        }

        return true;
    }

    private static void createDataFile() {
        File dataFile = new File(DATA_FILE_PATH);
        
        try {
            FileOutputStream os = new FileOutputStream(dataFile, false);

            // Write data keys to file
            for (DataKey key : DATA_KEYS) {
                os.write((key + DATA_SERPARATOR + "\n").getBytes());
            }

            os.close();
        } catch(IOException e) {
            e.printStackTrace();
            Main.LOGGER.severe("Failed to Write to Data File, Exiting!");
            System.exit(1);
        }
    }

    public static void loadDataToMap() {
        List<String> lines = null;

        try {
            lines = Files.readAllLines(new File(DATA_FILE_PATH).toPath());
        } catch(IOException e){
            e.printStackTrace();
            Main.LOGGER.severe("Failed to Read Data File, Exiting!");
            System.exit(1);
        }

        for(String line : lines) {
            String[] pair = line.split(DATA_SERPARATOR);

            dataMap.put(DataKey.valueOf(pair[0]), pair[1]);
        }
    }

    public static void setData(int index, String value) {
        File dataFile = new File(DATA_FILE_PATH);

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile.toPath());
            lines.set(index, DATA_KEYS[index] + DATA_SERPARATOR + value);
            Files.write(dataFile.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
            Main.LOGGER.severe("Failed to Read/Write to Data File, Exiting!");
            System.exit(1);
        }

        DataManager.loadDataToMap();
    }

    public static void setData(DataKey dataKey, String value) throws IOException {
        for(int i = 0; i < DATA_KEYS.length; i++) {
            if(DATA_KEYS[i] == dataKey) {
                setData(i, value);
                return;
            }
        }
    }

    public static HashMap<DataKey, String> getDataMap() {
        return dataMap;
    }
}

enum DataKey {
    Application_ID("Application ID"),
    Millis_Since_Start("Time Since Start (Millis)"),
    First_Lines("First Lines (use '|' as separator)"),
    Second_Lines("Second Lines (use '|' as separator)"),
    Line_Switch_Times_Millis("Line Switch Times (Millis, use '|' as separator)"),
    Party_Members("Party Members (use '|' as separator)"),
    Party_Max("Max Party Size"),
    Party_Switch_Times_Millis("Party Switch Times (Millis, use '|' as separator)"),
    Display_Image_Keys("Display Image Keys (use '|' as separator)"),
    Image_Hover_Details("Image Hover Details (use '|' as separator)"),
    Image_Switch_Times_Millis("Image Switch Times (Millis, use '|' as separator)");

    private String name;
    private DataKey(String fullName) {
        name = fullName;
    }

    public String getName() {
        return name;
    }
}