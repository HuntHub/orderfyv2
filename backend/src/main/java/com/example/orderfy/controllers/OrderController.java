package com.example.orderfy.controllers;

import com.example.orderfy.dtos.OrderRequestDto;
import com.example.orderfy.dtos.OrderResponseDto;
import com.example.orderfy.dtos.WebhookPayloadDto;
import com.example.orderfy.services.OrderService;
import com.example.orderfy.services.WebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final WebhookService webhookService;
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload) throws JsonProcessingException {
        webhookService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public List<OrderResponseDto> getAllOrders(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
        return orderService.getAllOrders(orderRequestDto);
    }
}
