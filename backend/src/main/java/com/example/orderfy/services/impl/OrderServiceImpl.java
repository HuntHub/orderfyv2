package com.example.orderfy.services.impl;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.example.orderfy.dtos.OrderRequestDto;
import com.example.orderfy.dtos.OrderResponseDto;
import com.example.orderfy.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    public List<OrderResponseDto> getAllOrders(OrderRequestDto orderRequestDto) {
        return null;
    }
}
