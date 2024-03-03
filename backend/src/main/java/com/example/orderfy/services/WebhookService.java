package com.example.orderfy.services;

import com.example.orderfy.dtos.WebhookPayloadDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

public interface WebhookService {
    void handleWebhook(String payload) throws JsonProcessingException;
}
