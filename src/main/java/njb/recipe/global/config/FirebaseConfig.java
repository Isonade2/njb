package njb.recipe.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Load environment variables from .env file
            Dotenv dotenv = Dotenv.configure().directory("./").load();

            // Retrieve and validate environment variables
            String privateKey = dotenv.get("FIREBASE_PRIVATE_KEY");
            if (privateKey != null) {
                privateKey = privateKey.replace("\\n", "\n");
            } else {
                log.error("❌ FIREBASE_PRIVATE_KEY is not set in .env file");
                return;
            }

            String clientEmail = dotenv.get("FIREBASE_CLIENT_EMAIL");
            if (clientEmail == null) {
                log.error("❌ FIREBASE_CLIENT_EMAIL is not set in .env file");
                return;
            }

            String projectId = dotenv.get("FIREBASE_PROJECT_ID");
            if (projectId == null) {
                log.error("❌ FIREBASE_PROJECT_ID is not set in .env file");
                return;
            }

            // Create JSON formatted service account key
            String serviceAccountJson = String.format(
                "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"%s\",\n" +
                "  \"private_key_id\": \"%s\",\n" +
                "  \"private_key\": \"%s\",\n" +
                "  \"client_email\": \"%s\",\n" +
                "  \"client_id\": \"%s\",\n" +
                "  \"auth_uri\": \"%s\",\n" +
                "  \"token_uri\": \"%s\",\n" +
                "  \"auth_provider_x509_cert_url\": \"%s\",\n" +
                "  \"client_x509_cert_url\": \"%s\"\n" +
                "}",
                projectId,
                dotenv.get("FIREBASE_PRIVATE_KEY_ID"),
                privateKey,
                clientEmail,
                dotenv.get("FIREBASE_CLIENT_ID"),
                dotenv.get("FIREBASE_AUTH_URI"),
                dotenv.get("FIREBASE_TOKEN_URI"),
                dotenv.get("FIREBASE_AUTH_PROVIDER_X509_CERT_URL"),
                dotenv.get("FIREBASE_CLIENT_X509_CERT_URL")
            );

            // Set FirebaseOptions
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes())))
                .setProjectId(projectId)
                .build();

            // Initialize FirebaseApp
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase SDK initialized successfully");
            } else {
                log.info("ℹ️ Firebase SDK already initialized");
            }
        } catch (IOException e) {
            log.error("❌ Firebase SDK initialization failed: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Unexpected error during Firebase SDK initialization: {}", e.getMessage(), e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
