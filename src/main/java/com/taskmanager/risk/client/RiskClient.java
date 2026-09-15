package com.taskmanager.risk.client;

import com.taskmanager.model.Task;
import com.taskmanager.risk.model.RiskDecision;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RiskClient {

    private final RestClient restClient;

    public RiskClient(RestClient.Builder builder) {

        this.restClient = builder
                .baseUrl("http://risk-service:8082")
                .build();
    }

    public RiskDecision evaluate(Task task) {

        return restClient.post()
                .uri("/risk/evaluate")
                .body(task)
                .retrieve()
                .body(RiskDecision.class);
    }
}
