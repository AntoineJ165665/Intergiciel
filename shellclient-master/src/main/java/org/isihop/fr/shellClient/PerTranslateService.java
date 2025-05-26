package org.isihop.fr.shellClient;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import java.net.http.*;
import java.net.URI;

@Service
public class PerTranslateService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public PerTranslateService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "out", groupId = "per-translate")
    public void handleOutgoingMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String msg = json.getString("message");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:5000/translate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "q": "%s",
                            "source": "fr",
                            "target": "en",
                            "format": "text"
                        }
                    """.formatted(msg)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject translation = new JSONObject(response.body());

            JSONObject result = new JSONObject();
            result.put("sender", json.getString("sender"));
            result.put("recipient", json.getString("recipient"));
            result.put("message", translation.getString("translatedText"));

            kafkaTemplate.send("in", result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}