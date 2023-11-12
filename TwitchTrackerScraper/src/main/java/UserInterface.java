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
    
        //Database size
        
        System.out.println("The database is currently populated by " +sizeChecker+" streamers \n");
    
        // Menu Options
        System.out.println("1. Scrape HTML for Streamers");
        System.out.println("2. Extract Data to Excel");
        System.out.println("3. Update Database");
        System.out.println("4. Search Users");
        System.out.println("5. Search Streams");
    
        // Bottom Border
        System.out.println(new String(new char[50]).replace("\0", "-"));
        System.out.print("Enter your choice: ");
    }

    public static void displayDatabaseMenu() {
        System.out.println("\n--- JPQL Search Operations ---");
        System.out.println("1. Search by Name URL");
        System.out.println("2. Return all streamers");
        System.out.println("3. Search by Followers");
        System.out.println("4. Return to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void displayStreamerData(Streamer streamer){
        System.out.println("NameUrl: " + streamer.getNameUrl());
        System.out.println("Followers: " + streamer.getFollowers());
        System.out.println("Average Viewers: " + streamer.getAverageViewers());
        System.out.println("Peak Viewers: " + streamer.getPeakViewers());
        System.out.println("Hours Watched: " + streamer.getHoursWatched());
        System.out.println("Followers Per Hour: " + streamer.getFollowersPerHour());
        System.out.println("Minutes Streamed: " + streamer.getMinutesStreamed());
        System.out.println("Last scraped: " + streamer.getLastScraped());

        List<Streams> streams = streamer.getStreams();
        for(Streams stream :streams){
            System.out.println("title: " + stream.getTitle() +
            ", date: " + stream.getDate() +
            ", averageViews: " + stream.getAverageViews() +
            ", maxViews: " + stream.getMaxViews() +
            ", followers: " + stream.getFollowers() +
            ", durationMinutes: " + stream.getDurationMinutes());
        }
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
