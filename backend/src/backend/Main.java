package backend;

import com.sun.net.httpserver.*;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);

            // Wrap routes in CORS handler
            server.createContext("/add", cors(new AddApplication()));
            server.createContext("/list", cors(new ListApplications()));
            server.createContext("/update", cors(new UpdateStatus()));

            server.setExecutor(null);
            server.start();

            System.out.println("🔥 Backend running: http://localhost:5000");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // FULL CORS HERE ONLY
    private static HttpHandler cors(HttpHandler next) {
        return exchange -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            next.handle(exchange);
        };
    }
}
