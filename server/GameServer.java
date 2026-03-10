import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class GameServer {

    static class FrameHandler implements HttpHandler {
        private GameEngine game;

        public FrameHandler(GameEngine engine) {
            this.game = engine;
        }

        private boolean getQueryParam(String query, String key) {
            if (query == null) return false;
            String[] params = query.split("&");
            for (String param : params) {
                String[] pair = param.split("=");
                if (pair.length == 2 && pair[0].equals(key)) {
                    return Boolean.parseBoolean(pair[1]);
                }
            }
            return false;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            String query = t.getRequestURI().getQuery();

            boolean w = getQueryParam(query, "w");
            boolean a = getQueryParam(query, "a");
            boolean s = getQueryParam(query, "s");
            boolean d = getQueryParam(query, "d");

            String response = game.tick(w, a, s, d);

            byte[] responseBytes = response.getBytes("UTF-8");
            t.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            t.sendResponseHeaders(200, responseBytes.length);
            
            OutputStream os = t.getResponseBody();
            os.write(responseBytes); // Fixed: this must be responseBytes, not response.getBytes()
            os.close();
        }
    } 

    static class MapHandler implements HttpHandler {
        private GameEngine game;

        public MapHandler(GameEngine engine) {
            this.game = engine;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            
            // Gets the JSON map string and converts to network bytes
            byte[] responseBytes = game.getMapJSON().getBytes("UTF-8");
            
            t.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            t.sendResponseHeaders(200, responseBytes.length);
            
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    } 

    public static void main(String args[]) throws Exception {
        
        GameEngine game = new GameEngine();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/frame", new FrameHandler(game));
        server.createContext("/map", new MapHandler(game));

        server.setExecutor(null);
        server.start();

        System.out.println("🚀 3D Engine Server is running!");
        System.out.println("Go to your web browser and open: http://localhost:8080/frame");
    }
}