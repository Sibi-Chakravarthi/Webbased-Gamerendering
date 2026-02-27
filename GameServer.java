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

    @Override
    public void handle(HttpExchange t) throws IOException{

        t.getResponseHeaders().add("Access-Control-Allow-Origin","*");

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
