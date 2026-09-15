package com.riskservice.controller;

import com.taskmanager.model.Task;
import com.taskmanager.risk.model.RiskDecision;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/risk")
public class RiskController {

    @PostMapping("/evaluate")
    public RiskDecision evaluate(@RequestBody Task task) {

        if ("CANCELLED".equals(task.getStatus())) {

            return new RiskDecision(
                    task.getId(),
                    "HIGH_RISK"
            );
        }

        return new RiskDecision(
                task.getId(),
                "NORMAL"
        );
    }
}
