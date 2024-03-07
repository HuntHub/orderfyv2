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
public class OrderController {

    private final WebhookService webhookService;
    private final OrderService orderService;

    public OrderController(WebhookService webhookService, OrderService orderService) {
        this.webhookService = webhookService;
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> handleWebhook(@RequestBody String payload) throws JsonProcessingException {
        webhookService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public List<OrderResponseDto> getAllOrders(@PathVariable String id) throws JsonProcessingException {
        return orderService.getAllOrders(id);
    }

    @PatchMapping("{id}/update")
    public void updateOrderStatus(@PathVariable String id) {
        orderService.updateOrderStatus(id);
    }
}
