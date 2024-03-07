package com.example.orderfy.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderResponseDto {
    private String orderId;
    private String merchantId;
    private String email;
    private String fullName;
    private String orderStatus;
}
