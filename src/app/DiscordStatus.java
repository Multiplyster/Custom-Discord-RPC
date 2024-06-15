package app;

import java.io.IOException;
import java.util.Scanner;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class DiscordStatus {

    private static final String DATA_SEPARATOR = "\\|"; // '|' means 'or' in regex, therefore we must use \|, but since \ is an escape code, we must use \\|

    private static boolean enabled;
    private static String[] firstLines, secondLines, imageKeys, imageHoverDetails;
    private static int[] lineTimeDurations, imageTimeDurations, partyTimeDurations, partyMembers;
    private static int currLineIndex, currImageIndex, currPartyIndex, maxParty;
    private static long startTime, lastLineUpdate, lastImageUpdate, lastPartyUpdate;

    public static boolean ready;
    
    public static void init() throws IOException {
        Main.LOGGER.info("Transforming Line Data to Arrays...");
        Scanner scan = new Scanner(System.in);

        // Convert lines into arrays
        for(boolean areArraysSameSize = false; !areArraysSameSize;) {
            firstLines = DataManager.getDataMap().get(DataKey.First_Lines).split(DATA_SEPARATOR);
            secondLines = DataManager.getDataMap().get(DataKey.Second_Lines).split(DATA_SEPARATOR);
        
            // Input loop for data verification of line switching times
            for(boolean areLineSwitchTimesValid = false; !areLineSwitchTimesValid;) {
                try {
                    // Will throw an exception if array contains non-numbers
                    lineTimeDurations = stringArrToIntArr(DataManager.getDataMap().get(DataKey.Line_Switch_Times_Millis).split(DATA_SEPARATOR));
                    areLineSwitchTimesValid = true;
                } catch(NumberFormatException e) {
                    System.out.println();
                    Main.LOGGER.severe("Exception Found Parsing Line Switch Times to Integer Array!");
                    Main.LOGGER.warning("Manual Input Required!");

                    System.out.print("\nEnter " + DataKey.Line_Switch_Times_Millis.getName() + ": ");
                    DataManager.setData(DataKey.Line_Switch_Times_Millis, scan.nextLine());
                }
            }

            // Check if first line arr and second line arr are same size
            if(firstLines.length != secondLines.length || firstLines.length != lineTimeDurations.length) {
                System.out.println();
                Main.LOGGER.warning("First Lines, Second Lines, and Line Switch Times Must be the Same Length!");
                Main.LOGGER.info("Manual Input Required!");

                System.out.print("\nEnter " + DataKey.First_Lines.getName() + ": ");
                DataManager.setData(DataKey.First_Lines, scan.nextLine());

                System.out.print("Enter " + DataKey.Second_Lines.getName() + ": ");
                DataManager.setData(DataKey.Second_Lines, scan.nextLine());

                System.out.print("Enter " + DataKey.Line_Switch_Times_Millis.getName() + ": ");
                DataManager.setData(DataKey.Line_Switch_Times_Millis, scan.nextLine());
            } else {
                areArraysSameSize = true;
                break;
            }
        }

        // If line arrays are empty, add an empty item
        if(firstLines.length == 0) {
            firstLines = new String[] {""};
            secondLines = new String[] {""};
            lineTimeDurations = new int[] {0};
        }

        Main.LOGGER.info("Transforming Party Data to Arrays...");
        for(boolean areArraysSameSize = false; !areArraysSameSize;) {
            for(boolean arePartyMembersValid = false; !arePartyMembersValid;) {
                try {
                    // Will throw an exception if array contains non-numbers
                    partyMembers = stringArrToIntArr(DataManager.getDataMap().get(DataKey.Party_Members).split(DATA_SEPARATOR));
                    arePartyMembersValid = true;
                } catch(NumberFormatException e) {
                    System.out.println();
                    Main.LOGGER.severe("Exception Found Parsing Party Members to Integer Array!");
                    Main.LOGGER.warning("Manual Data Input Necessary");

                    System.out.println("\nEnter " + DataKey.Party_Members.getName() + ": ");
                    DataManager.setData(DataKey.Party_Members, scan.nextLine());
                }
            }

            maxParty = Integer.parseInt(DataManager.getDataMap().get(DataKey.Party_Max));

            for(boolean arePartySwitchTimesValid = false; !arePartySwitchTimesValid;) {
                try {
                    // Will throw an exception if array contains non-numbers
                    partyTimeDurations = stringArrToIntArr(DataManager.getDataMap().get(DataKey.Party_Switch_Times_Millis).split(DATA_SEPARATOR));
                    arePartySwitchTimesValid = true;
                } catch(NumberFormatException e) {
                    System.out.println();
                    Main.LOGGER.severe("Exception Found Parsing Party Switch Times to Integer Array!");
                    Main.LOGGER.warning("Manual Data Input Necessary");

                    System.out.println("\nEnter " + DataKey.Party_Switch_Times_Millis.getName() + ": ");
                    DataManager.setData(DataKey.Party_Switch_Times_Millis, scan.nextLine());
                }
            }

            if(partyMembers.length != partyTimeDurations.length) {
                System.out.println();
                Main.LOGGER.warning("Party Members and Party Time Durations Must be the Same Length!");
                Main.LOGGER.info("Manual Data Input Necessary");

                System.out.println("\nEnter " + DataKey.Party_Members.getName() + ": ");
                DataManager.setData(DataKey.Party_Members, scan.nextLine());

                System.out.println("Enter " + DataKey.Party_Switch_Times_Millis.getName() + ": ");
                DataManager.setData(DataKey.Party_Switch_Times_Millis, scan.nextLine());
            } else {
                areArraysSameSize = true;
                break;
            }
        }

        // If image arrays are empty, add an empty item
        if(partyMembers.length == 0) {
            partyMembers = new int[] {0};
            partyTimeDurations = new int[] {0};
        }

        Main.LOGGER.info("Transforming Image Key Data to Arrays...");
        // Convert image keys into arrays
        for(boolean areArraysSameSize = false; !areArraysSameSize;){
            imageKeys = DataManager.getDataMap().get(DataKey.Display_Image_Keys).split(DATA_SEPARATOR);
            imageHoverDetails = DataManager.getDataMap().get(DataKey.Image_Hover_Details).split(DATA_SEPARATOR);

            // Input loop for data verification of image switch times
            for(boolean areImageSwitchTimesValid = false; !areImageSwitchTimesValid;) {
                try {
                    // Will throw an exception if array contains non-numbers
                    imageTimeDurations = stringArrToIntArr(DataManager.getDataMap().get(DataKey.Image_Switch_Times_Millis).split(DATA_SEPARATOR));
                    areImageSwitchTimesValid = true;
                } catch(NumberFormatException e) {
                    System.out.println();
                    Main.LOGGER.severe("Exception Found Parsing Image Switch Times to Integer Array!");
                    Main.LOGGER.warning("Manual Data Input Necessary");

                    System.out.print("\nEnter " + DataKey.Image_Switch_Times_Millis.getName() + ": ");
                    DataManager.setData(DataKey.Image_Switch_Times_Millis, scan.nextLine());
                }
            }

            // Check if first line arr and second line arr are same size
            if(imageKeys.length != imageHoverDetails.length || imageKeys.length != imageTimeDurations.length) {
                System.out.println();
                Main.LOGGER.warning("Image Keys, Image Hover Details, and Image Switch Times Must be the Same Length!");
                Main.LOGGER.info("Manual Input Required!");

                System.out.print("\nEnter " + DataKey.Display_Image_Keys.getName() + ": ");
                DataManager.setData(DataKey.Display_Image_Keys, scan.nextLine());

                System.out.print("Enter " + DataKey.Image_Hover_Details.getName() + ": ");
                DataManager.setData(DataKey.Image_Hover_Details, scan.nextLine());

                System.out.print("Enter " + DataKey.Image_Switch_Times_Millis.getName() + ": ");
                DataManager.setData(DataKey.Image_Switch_Times_Millis, scan.nextLine());
            } else {
                areArraysSameSize = true;
                break;
            }
        }

        // If image arrays are empty, add an empty item
        if(imageKeys.length == 0) {
            imageKeys = new String[] {""};
            imageHoverDetails = new String[] {""};
            imageTimeDurations = new int[] {0};
        }

        Main.LOGGER.info("Creating Discord Handlers...");
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("\nUser connected: " + user.username);
            ready = true;
        }).build();

        // Discord application initialization
        Main.LOGGER.info("Initializing Application...");
        String appID = DataManager.getDataMap().get(DataKey.Application_ID);
        DiscordRPC.discordInitialize(appID, handlers, false);
        DiscordRPC.discordRegister(appID, "");

        // Discord presence initialization stuff
        Main.LOGGER.info("Initializing Discord Rich Presence...");
        DiscordRichPresence.Builder p = new DiscordRichPresence.Builder(secondLines[0]);
        // Set beginning lines, image, and start time stamp
        try{
            startTime = System.currentTimeMillis() - Long.parseLong(DataManager.getDataMap().get(DataKey.Millis_Since_Start)) - 18000;
        } catch(NumberFormatException e) {
            Main.LOGGER.warning("Exception Found Parsing Millis Since Start to Long, Defaulting to System Time");
            startTime = System.currentTimeMillis() - 18000;
        }
    
        p.setStartTimestamps(startTime);
        p.setDetails(firstLines[0]);
        p.setBigImage(imageKeys[0], imageHoverDetails[0]);
        p.setParty("", partyMembers[0], maxParty);

        DiscordRPC.discordUpdatePresence(p.build()); 

        Main.LOGGER.info("Discord Rich Presence Initialized!");
    }

    public static void start() {
        enabled = true;
        int lineArrayLen = firstLines.length;
        int imageArrayLen = imageKeys.length;
        int partyArryLen = partyMembers.length;

        new Thread(() -> {
            while(enabled) {
                DiscordRichPresence.Builder p = new DiscordRichPresence.Builder("");
                boolean hasPresenceChanged = false;

                // Increment line index
                if(lineArrayLen > 0 && lineTimeDurations[currLineIndex] > 0 && System.currentTimeMillis() - lastLineUpdate >= lineTimeDurations[currLineIndex]) {
                    currLineIndex++;
                    lastLineUpdate = System.currentTimeMillis();
                    hasPresenceChanged = true;

                    if(currLineIndex >= lineArrayLen)
                        currLineIndex = 0;
                }

                // Increment image index
                if(imageArrayLen > 0 && imageTimeDurations[currImageIndex] > 0 && System.currentTimeMillis() - lastImageUpdate >= imageTimeDurations[currImageIndex]) {
                    currImageIndex++;
                    lastImageUpdate = System.currentTimeMillis();
                    hasPresenceChanged = true;

                    if(currImageIndex >= imageArrayLen)
                        currImageIndex = 0;
                }

                // Increment party index
                if(partyArryLen > 0 && partyTimeDurations[currImageIndex] > 0 && System.currentTimeMillis() - lastPartyUpdate >= imageTimeDurations[currPartyIndex]) {
                    currPartyIndex++;
                    lastPartyUpdate = System.currentTimeMillis();
                    hasPresenceChanged = true;

                    if(currImageIndex >= partyArryLen)
                        currPartyIndex = 0;
                }

                // Update presence if presence has changed
                if(hasPresenceChanged)
                    updatePresence();
                
                DiscordRPC.discordRunCallbacks();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Discord Callbacks").start();
    }

    public static void stop() {
        Main.LOGGER.info("Shutting Down Discord RPC...");
        enabled = false; // Stop callback thread

        DiscordRPC.discordShutdown();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Main.LOGGER.warning("Thread Interupted During Discord Shutdown");
        }
    }

    /**
     * Updates the discord presence according to the current line and current image
     */
    private static void updatePresence() {
        DiscordRichPresence.Builder p = new DiscordRichPresence.Builder(secondLines[currLineIndex]);
        p.setDetails(firstLines[currLineIndex]);
        p.setBigImage(imageKeys[currImageIndex], imageHoverDetails[currImageIndex]);
        p.setStartTimestamps(startTime);
        p.setParty("Deez Party", partyMembers[currPartyIndex], maxParty);

        DiscordRPC.discordUpdatePresence(p.build());
    }

    private static int[] stringArrToIntArr(String[] arr) {
        int[] out = new int[arr.length];

        for(int i = 0; i < arr.length; i++) {
            out[i] = Integer.parseInt(arr[i]);
        }

        return out;
    }
}