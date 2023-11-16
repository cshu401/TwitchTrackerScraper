import Domain.Streamer;
import Utils.StreamerTools;

import java.util.List;

public class UserInterface {

    
    public static void displayMenu() {
        // Top Border
        System.out.println("░▀█▀░█░█░▀█▀░▀█▀░█▀▀░█░█░░░▀█▀░█▀▄░█▀█░█▀▀░█░█░█▀▀░█▀▄░░░█▀▀░█▀▀░█▀▄░█▀█░█▀█░█▀▀░█▀▄");
        System.out.println("░░█░░█▄█░░█░░░█░░█░░░█▀█░░░░█░░█▀▄░█▀█░█░░░█▀▄░█▀▀░█▀▄░░░▀▀█░█░░░█▀▄░█▀█░█▀▀░█▀▀░█▀▄");
        System.out.println("░░▀░░▀░▀░▀▀▀░░▀░░▀▀▀░▀░▀░░░░▀░░▀░▀░▀░▀░▀▀▀░▀░▀░▀▀▀░▀░▀░░░▀▀▀░▀▀▀░▀░▀░▀░▀░▀░░░▀▀▀░▀░▀");
        System.out.println(new String(new char[50]).replace("\0", "-"));
        System.out.println(new String(new char[50]).replace("\0", "-"));
        System.out.println("Loading.....");

        int sizeChecker = StreamerTools.getAllStreamers().size();

        // Database size
        System.out.println("The database is currently populated by " + sizeChecker + " streamers \n");

        // Menu Options
        System.out.println("1. Scrape HTML for Streamers");
        System.out.println("2. Add Domain.Streamer to Database");
        System.out.println("3. Delete Domain.Streamer from Database");
        System.out.println("4. Update all streamers");
        System.out.println("5. Export Database to Excel");
        System.out.println("6. Return all Streamers");
        System.out.println("7. Search Domain.Streamer by URL");
        //System.out.println("8. Search Domain.Streams");
        System.out.println("9. Add Streamers/ Update existing streamer");
        System.out.println("10. Delete Streamers");
        System.out.println("20. Exit");

        // Bottom Border
        System.out.println(new String(new char[50]).replace("\0", "-"));
        System.out.print("Enter your choice: ");
    }


    public static String promptStringInput(String message) {
        System.out.print(message + ": ");
        Main.scanner.nextLine(); // Consume any leftover newline character
        return Main.scanner.nextLine(); // Wait for and return the user's input
    }

    public static void printStreamersPerLine(List<Streamer> streamers, int wordsPerLine) {
        int count = 0;
        for (Streamer streamer : streamers) {
            System.out.print(streamer.getNameUrl() + ", ");
            count++;
    
            if (count % wordsPerLine == 0) {
                System.out.println();
            }
        }
    
        if (count % wordsPerLine != 0) {
            System.out.println();
        }
    }

    /**
     * Prints the percentage of completion to the console.
     *
     * @param current the current number of items processed.
     * @param total the total number of items to process.
     */
    public static int getPercentage(int current, int total) {
        int percentageDone = current * 100 / total;
        return percentageDone;
    }

    /**
     * Prints a loading bar with the current percentage of completion to the console.
     *
     * @param current the current number of items processed.
     * @param total the total number of items to process.
     * @param barLength the length of the loading bar.
     */
    public static void printLoadingBar(int current, int total, int barLength) {
        int progress = (int) ((current / (double) total) * barLength);
        StringBuilder bar = new StringBuilder("[");
    
        for (int i = 0; i < barLength; i++) {
            if (i < progress) {
                bar.append("=");
            } else {
                bar.append(" ");
            }
        }
        bar.append("]");
    
        double percentageDone = (current / (double) total) * 100;
        System.out.printf("\r%s %d%% complete", bar, (int) percentageDone);
        System.out.println("");
    
        // If you're at the end, print a newline character to move to the next line
        if (current == total) {
            System.out.println();
        }
    }

}
