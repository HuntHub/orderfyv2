package com.example.orderfy.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerApiResponseDto {
    private String givenName;
    private String familyName;
}
