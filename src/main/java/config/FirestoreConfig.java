package config;


import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class FirestoreConfig {

    @PostConstruct
    public void init() {
        // JSON credentials env variable se load ho jayenge
        System.out.println("Firestore initialized...");
    }

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}