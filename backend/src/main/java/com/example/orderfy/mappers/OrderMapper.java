package com.example.orderfy.mappers;

import com.example.orderfy.dtos.OrderRequestDto;
import com.example.orderfy.dtos.OrderResponseDto;
import com.example.orderfy.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    @Mapping (source = "merchantId", target = "merchantId")
    @Mapping (source = "email", target = "email")
    @Mapping (source = "fullName", target = "fullName")
    @Mapping (source = "orderStatus", target = "orderStatus")
    @Mapping (source = "orderId", target = "orderId")
    OrderResponseDto entityToDto(Order order);

    List<OrderResponseDto> entitiesToDtos(List<Order> orders);
}
