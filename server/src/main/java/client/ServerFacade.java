
package client;

import com.google.gson.Gson;
import java.net.http.HttpClient;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }
}
