package com.example.orderfy.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order {
        private String orderId;
        private String merchantId;
        private String email;
        private String fullName;
        private String orderStatus;
    }
