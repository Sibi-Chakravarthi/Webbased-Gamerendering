import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class GameServer {

    static class FrameHandler implements HttpHandler{

        private Raycaster engine;

        public FrameHandler(Raycaster engine){

            this.engine = engine;

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
    public void handle(HttpExchange t) throws IOException{

        t.getResponseHeaders().add("Access-Control-Allow-Origin","*");

        String query = t.getRequestURI().getQuery();

        boolean w = getQueryParam(query, "w");
        boolean a = getQueryParam(query, "a");
        boolean s = getQueryParam(query, "s");
        boolean d = getQueryParam(query, "d");

        engine.player.move(w, a, s, d, engine.worldMap);

        int[][] frameData = engine.castRays();
        String response = engine.toJSON(frameData);

        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();

        os.write(response.getBytes());
        os.close();

        }

    }

    public static void main(String args[]) throws Exception{
        
        Raycaster engine = new Raycaster();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);

        server.createContext("/frame",new FrameHandler(engine));

        server.setExecutor(null);
        server.start();

        System.out.println("🚀 3D Engine Server is running!");
        System.out.println("Go to your web browser and open: http://localhost:8080/frame");
    }
}
