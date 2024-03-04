package com.example.orderfy.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderRequestDto {
    private String merchantId;
    private String orderId;
}
