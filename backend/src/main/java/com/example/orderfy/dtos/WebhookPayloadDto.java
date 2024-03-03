package com.example.orderfy.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class WebhookPayloadDto {

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("data")
    private Data data;

    @JsonProperty("customer_id")
    private String customerId;

    // Getters and setters
    // Constructors as needed

}
