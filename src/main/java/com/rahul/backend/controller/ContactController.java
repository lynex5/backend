package com.rahul.backend.controller;

import com.rahul.backend.entity.Contact;
import com.rahul.backend.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactRepository repo;

    @Value("${discord.webhook.url}")
    private String discordWebhookUrl;

    public ContactController(ContactRepository repo) { this.repo = repo; }
    
    @GetMapping
    public List<Contact> get() { return repo.findAll(); }
    
    @PutMapping
    public Contact update(@RequestBody Contact contact) {
        contact.setId(1L);
        return repo.save(contact);
    }

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        try {
            if (discordWebhookUrl == null || discordWebhookUrl.trim().isEmpty()) {
                System.err.println("ERROR: discordWebhookUrl is empty. Ensure DISCORD_WEBHOOK_URL is set in environment variables.");
                return ResponseEntity.internalServerError().body(Map.of("success", false, "error", "Discord Webhook URL is not configured"));
            }

            Map<String, Object> discordPayload = new HashMap<>();
            discordPayload.put("content", "🚀 **New Contact Form Submission!**");

            Map<String, Object> embed = new HashMap<>();
            embed.put("color", 3447003);
            embed.put("timestamp", java.time.Instant.now().toString());

            String name = payload.get("name") != null && !payload.get("name").trim().isEmpty() ? payload.get("name") : "Unknown";
            String email = payload.get("email") != null && !payload.get("email").trim().isEmpty() ? payload.get("email") : "Unknown";
            String message = payload.get("message") != null && !payload.get("message").trim().isEmpty() ? payload.get("message") : "No message provided";

            List<Map<String, Object>> fields = List.of(
                    Map.of("name", "Name", "value", name, "inline", true),
                    Map.of("name", "Email", "value", email, "inline", true),
                    Map.of("name", "Message", "value", message)
            );
            embed.put("fields", fields);
            discordPayload.put("embeds", List.of(embed));

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Portfolio-Backend/1.0");

            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity = 
                new org.springframework.http.HttpEntity<>(discordPayload, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(discordWebhookUrl, requestEntity, String.class);

            return ResponseEntity.ok(Map.of("success", true, "message", "Message sent successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false, 
                "error", "Failed to send message: " + e.getMessage()
            ));
        }
    }
}
