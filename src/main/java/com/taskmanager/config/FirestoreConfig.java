package com.taskmanager.config;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

/*
@Configuration
public class FirestoreConfig {
//used to setup the connection with firestore and also create the bean .which use by the controller and service class.
    @PostConstruct
    public void init() {
        // JSON credentials env variable se load ho jayenge
      //  System.out.println("Firestore initialized...");------------when actual gcp pe atch krna tb hoga init ka usage//
    }
    @Bean
    public Firestore getFirestore() {
        return FirestoreOptions.getDefaultInstance().toBuilder().setProjectId("demo-project").build().getService();
    }
}
*/
