package backend;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.URI;
import java.sql.*;
import java.util.*;

public class ListApplications implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI uri = exchange.getRequestURI();
        String query = uri.getQuery();

        if (query == null || !query.contains("email=")) {
            send(exchange, "[]");
            return;
        }

        String email = query.split("email=")[1];

        System.out.println("Loading for: " + email);

        List<String> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM applications WHERE email=? ORDER BY id DESC"
            );
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String json =
                    "{"
                    + "\"id\":" + rs.getInt("id") + ","
                    + "\"company\":\"" + rs.getString("company") + "\","
                    + "\"role\":\"" + rs.getString("role") + "\","
                    + "\"type\":\"" + rs.getString("type") + "\","
                    + "\"status\":\"" + rs.getString("status") + "\","
                    + "\"applied_date\":\"" + rs.getString("applied_date") + "\","
                    + "\"interview_date\":\"" + rs.getString("interview_date") + "\""
                    + "}";

                list.add(json);
            }

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        send(exchange, "[" + String.join(",", list) + "]");
    }

    private void send(HttpExchange ex, String resp) throws IOException {
        ex.sendResponseHeaders(200, resp.length());
        ex.getResponseBody().write(resp.getBytes());
        ex.close();
    }
}
