package com.example.orderfy.services.impl;

import com.example.orderfy.dtos.OrderResponseDto;
import com.example.orderfy.entities.Order;
import com.example.orderfy.mappers.OrderMapper;
import com.example.orderfy.services.OrderService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final DynamoDbClient dynamoDbClient;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(DynamoDbClient dynamoDbClient, OrderMapper orderMapper) {
        this.dynamoDbClient = dynamoDbClient;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponseDto> getAllOrders(String id) {
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#merchant_Id", "merchant_Id");

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":merchantIdVal", AttributeValue.builder().s(id).build());
        expressionAttributeValues.put(":orderStatusVal", AttributeValue.builder().s("NEW").build());

        String tableName = "orderfy_test_table";
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("#merchant_Id = :merchantIdVal")
                .filterExpression("order_Status = :orderStatusVal")
                .expressionAttributeNames(expressionAttributeNames)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        List<Order> orders = new ArrayList<>();
        QueryResponse response = dynamoDbClient.query(queryRequest);
        for (Map<String, AttributeValue> item : response.items()) {
            System.out.println(item.get("order_Id").s());
            Order order = new Order();
            order.setOrderId(item.get("order_Id").s());
            order.setMerchantId(item.get("merchant_Id").s());
            order.setEmail(item.get("email").s());
            order.setFullName(item.get("full_Name").s());
            order.setOrderStatus(item.get("order_Status").s());

            orders.add(order);
        }

        System.out.println(orders);

        return orderMapper.entitiesToDtos(orders);
    }

    @Override
    public void updateOrderStatus(String id) {
        String newStatus = "FULFILLED";
        HashMap<String, AttributeValueUpdate> updateValues = new HashMap<>();
        updateValues.put("order_Status",
                AttributeValueUpdate.builder()
                        .value(AttributeValue.builder().s(newStatus).build())
                        .action(AttributeAction.PUT)
                        .build());

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName("orderfy_test_table")
                .key(Collections.singletonMap("order_Id", AttributeValue.builder().s(id).build()))
                .attributeUpdates(updateValues)
                .build();

        dynamoDbClient.updateItem(updateItemRequest);
    }
}