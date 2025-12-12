package com.estimate.infrastructure.auth.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Configuration
@Profile("gcp")
public class FirebaseConfig {
    
    @Value("${app.gcp.project-id}")
    private String projectId;
    
    @Value("${app.gcp.firebase.credentials-path:}")
    private String credentialsPath;
    
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options;
            
            if (credentialsPath != null && !credentialsPath.isEmpty()) {
                log.info("Initializing Firebase with credentials from: {}", credentialsPath);
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream(credentialsPath)
                );
                options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(projectId)
                        .build();
            } else {
                log.info("Initializing Firebase with default credentials for project: {}", projectId);
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.getApplicationDefault())
                        .setProjectId(projectId)
                        .build();
            }
            
            return FirebaseApp.initializeApp(options);
        }
        
        return FirebaseApp.getInstance();
    }
    
    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
