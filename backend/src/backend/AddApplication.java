package backend;

import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.util.HashMap;

public class AddApplication implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {

        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        String body = new String(ex.getRequestBody().readAllBytes());
        System.out.println("Saving: " + body);

        HashMap<String, String> map = parse(body);

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO applications (email, company, role, type, status, applied_date, interview_date) VALUES (?,?,?,?,?,?,?)"
            );

            ps.setString(1, map.get("email"));
            ps.setString(2, map.get("company"));
            ps.setString(3, map.get("role"));
            ps.setString(4, map.get("type"));
            ps.setString(5, map.get("status"));
            ps.setString(6, map.get("applied_date"));
            ps.setString(7, map.get("interview_date"));

            ps.executeUpdate();

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String resp = "{\"success\":true}";
        ex.sendResponseHeaders(200, resp.length());
        ex.getResponseBody().write(resp.getBytes());
        ex.close();
    }

    private HashMap<String, String> parse(String json) {
        HashMap<String, String> map = new HashMap<>();

        json = json.trim().replace("{", "").replace("}", "");
        String[] parts = json.split(",");

        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0].replace("\"", "").trim(),
                        kv[1].replace("\"", "").trim());
            }
        }

        return map;
    }
}
