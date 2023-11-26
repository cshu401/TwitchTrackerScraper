package main;

import java.util.List;
import java.util.Scanner;

public class Main {

    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
    
        System.out.println("  _______       _ _       _       _    _ _______ __  __ _         _____                                \n" +
                " |__   __|     (_) |     | |     | |  | |__   __|  \\/  | |       / ____|                               \n" +
                "    | |_      ___| |_ ___| |__   | |__| |  | |  | \\  / | |      | (___   ___ _ __ __ _ _ __   ___ _ __ \n" +
                "    | \\ \\ /\\ / / | __/ __| '_ \\  |  __  |  | |  | |\\/| | |       \\___ \\ / __| '__/ _` | '_ \\ / _ \\ '__|\n" +
                "    | |\\ V  V /| | || (__| | | | | |  | |  | |  | |  | | |____   ____) | (__| | | (_| | |_) |  __/ |   \n" +
                "    |_| \\_/\\_/ |_|\\__\\___|_| |_| |_|  |_|  |_|  |_|  |_|______| |_____/ \\___|_|  \\__,_| .__/ \\___|_|   \n" +
                "                                                                                      | |              \n" +
                "                                                                                      |_|              \n" +
                "\n");
        System.out.println("Enter the path of the Twitch HTML file:");
        String filePath = scanner.nextLine();
    
        try {
            List<String> usernames = TwitchHTMLParser.getUsernames(filePath);
            if (usernames != null && !usernames.isEmpty()) {
                StringBuilder jsonOutput = new StringBuilder("[");
                for (int i = 0; i < usernames.size(); i++) {
                    jsonOutput.append("\"").append(usernames.get(i)).append("\"");
                    if (i < usernames.size() - 1) {
                        jsonOutput.append(", ");
                    }
                }
                jsonOutput.append("]");
                System.out.println("Extracted Usernames Copy from Here:");
                System.out.println(jsonOutput.toString());
            } else {
                System.out.println("No usernames found or an error occurred.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }

        System.out.println("Press Enter to exit...");
        scanner.nextLine();
    
        scanner.close();
    }
    
}
