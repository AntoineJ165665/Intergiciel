package org.isihop.fr.shellClient;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientConsDbService {

    private final String url = "jdbc:postgresql://localhost:5432/PGDB";
    private final String user = "Antoine";
    private final String password = "data";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ClientConsDbService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @KafkaListener(topics = "techout", groupId = "client-cons-db")
    public void handleTechnicalMessage(String message) {
        JSONObject json = new JSONObject(message);
        String type = json.getString("type");

        switch (type) {
            case "connect":
                insertOrUpdateClient(json.getString("client"));
                break;
            case "disconnect":
                disconnectClient(json.getString("client"));
                break;
            case "getConnectedClients":
                List<String> clients = getClientsConnectés();
                JSONObject response = new JSONObject();
                response.put("type", "connectedClients");
                response.put("client", json.getString("client"));
                response.put("clients", clients);
                kafkaTemplate.send("techin", response.toString());
                break;
        }
    }

    @KafkaListener(topics = "out", groupId = "client-cons-db")
    public void archiveRawMessage(String message) {
        JSONObject json = new JSONObject(message);
        String sender = json.getString("sender");
        String recipient = json.getString("recipient");
        String msg = json.getString("message");

        archiveMessageBrut(sender, recipient, msg);
    }

    @KafkaListener(topics = "in", groupId = "client-cons-db")
    public void archiveTranslatedMessage(String message) {
        JSONObject json = new JSONObject(message);
        String sender = json.getString("sender");
        String recipient = json.getString("recipient");
        String msg = json.getString("message");

        archiveMessageTraduit(sender, recipient, msg);
    }

    private void insertOrUpdateClient(String nom) {
        String sql = """
            INSERT INTO clientConnected (nom, status)
            VALUES (?, 'connecté')
            ON CONFLICT (nom)
            DO UPDATE SET status = 'connecté'
        """;

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void disconnectClient(String nom) {
        String sql = "UPDATE clientConnected SET status = 'déconnecté' WHERE nom = ?";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> getClientsConnectés() {
        List<String> clients = new ArrayList<>();
        String sql = "SELECT nom FROM clientConnected WHERE status = 'connecté'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clients.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    private void archiveMessageBrut(String sender, String recipient, String message) {
        String sql = "INSERT INTO messageBrute (sender, recipient, message) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void archiveMessageTraduit(String sender, String recipient, String message) {
        String sql = "INSERT INTO messageTraduit (sender, recipient, message) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, recipient);
            stmt.setString(3, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}