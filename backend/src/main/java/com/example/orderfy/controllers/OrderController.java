package com.example.orderfy.controllers;

import com.example.orderfy.dtos.WebhookPayloadDto;
import com.example.orderfy.services.WebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final WebhookService webhookService;

    @PostMapping("/create")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload) throws JsonProcessingException {
        webhookService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }
}
