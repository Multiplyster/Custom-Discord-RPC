package app;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import net.arikia.dev.drpc.DiscordRPC;

public class Main {
    public static Logger LOGGER;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER = Logger.getLogger(Main.class.getName());

        LOGGER.info("Initializing...");

        DataManager.init();
        DiscordStatus.init();

        // Wait for handlers to finish setting up
        LOGGER.info("Awaiting Discord Rich Presence Handlers");
        while(!DiscordStatus.ready) {
            DiscordRPC.discordRunCallbacks();
            Thread.sleep(5);
        }

        DiscordStatus.start();

        Scanner scan = new Scanner(System.in);
        System.out.print("Press Enter to Close!");
        while(!scan.hasNextLine()) {
            Thread.sleep(250);
        }

        System.out.println();
        LOGGER.info("Shutting Down...");
        DiscordStatus.stop();
        LOGGER.info("Goodbye!");
    }
}
