package com.example.orderfy.services;

import com.example.orderfy.dtos.OrderRequestDto;
import com.example.orderfy.dtos.OrderResponseDto;

import java.util.List;

public interface OrderService {
    List<OrderResponseDto> getAllOrders(OrderRequestDto orderRequestDto);
}
