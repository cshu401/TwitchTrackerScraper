import java.net.*;
import java.net.http.*;
import java.net.http.HttpResponse.*;

public class TwitchApi {

    public static void main(String[] args) {
        // Replace with your own credentials and access token
        String clientId = "4nzxk3i6n5ugt2bvftjw5xzjikky1r";
        String accessToken = "rtwjtj8by2xuon44atnfd33rjdsf69";
        
        // Create an HttpClient
        HttpClient client = HttpClient.newHttpClient();
        
        // Create a HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.twitch.tv/helix/streams"))
            .header("Client-Id", clientId)
            .header("Authorization", "Bearer " + accessToken)
            .build();
        
        // Send the HttpRequest and get the response
        client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println)
            .join();
    }
}
