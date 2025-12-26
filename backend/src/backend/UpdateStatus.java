package backend;

import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;

public class UpdateStatus implements HttpHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {

        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            ex.sendResponseHeaders(200, -1);
            return;
        }

        if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
            ex.sendResponseHeaders(405, -1);
            return;
        }

        // Read body
        String body = new String(ex.getRequestBody().readAllBytes());
        System.out.println("Updating row: " + body);

        int id = getInt(body, "id");
        String newStatus = getString(body, "status");

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE applications SET status=? WHERE id=?"
            );
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            ps.executeUpdate();

            ps.close();
            con.close();

            System.out.println("✔ Status updated for ID: " + id);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String resp = "{\"success\":true}";
        ex.sendResponseHeaders(200, resp.length());
        ex.getResponseBody().write(resp.getBytes());
        ex.close();
    }

    private int getInt(String body, String key) {
        try {
            return Integer.parseInt(body.split("\"" + key + "\":")[1].split(",|}")[0].replace("\"", "").trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private String getString(String body, String key) {
        try {
            return body.split("\"" + key + "\":\"")[1].split("\"")[0];
        } catch (Exception e) {
            return "";
        }
    }
}
