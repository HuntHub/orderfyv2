package com.example.orderfy.services.impl;

import com.example.orderfy.dtos.WebhookPayloadDto;
import com.example.orderfy.dtos.CustomerApiResponseDto;
import com.example.orderfy.services.WebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Override
    public void handleWebhook(String payload) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(payload);

        // Assuming the entire JSON you provided is the value of "body"
        String bodyString = rootNode.path("body").asText();

        // Parse the string inside "body" to a JsonNode
        JsonNode bodyNode = objectMapper.readTree(bodyString);

        // Now, navigate to the "customer_id" inside the nested structure
        JsonNode customerNode = bodyNode.path("data").path("object").path("payment");
        String customerId = customerNode.path("customer_id").asText();

        System.out.println("Customer ID: " + customerId);

        //String squareApiToken = fetchSquareApiToken(merchantId);
        String squareApiToken = "EAAAEP55lWQwSc-rSQS41rzwSKKCZIyJ2I1GitMw49ZA0jvNc6wbsX5eHq8luQi0";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(squareApiToken);

        String squareApiUrl = "https://connect.squareupsandbox.com/v2/customers/" + customerId;

        System.out.println("Making customer API call");
        ResponseEntity<String> response = restTemplate.exchange(
                squareApiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        System.out.println("API call complete");
        System.out.println("Response: " + response);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            if (responseBody != null) {
                JsonNode responseNode = objectMapper.readTree(responseBody);
                System.out.println("Response node: " + responseNode);

                // Assuming the structure of the response JSON, adjust the path as necessary
                JsonNode processNode = responseNode.path("customer"); // Adjust the path based on actual response structure
                String givenName = processNode.path("given_name").asText("");
                String familyName = processNode.path("family_name").asText("");
                String fullName = givenName + " " + (!familyName.isEmpty() ? familyName.charAt(0) : "");
                System.out.println("Received customer data: " + fullName);

            } else {
                System.out.println("Customer data not found");
            }
        } else {
            System.out.println("Failed to retrieve customer data from Square API");
            // Handle API error response
        }
    }
    private String fetchSquareApiToken(String merchantId) {
        // Call an external service or AWS Secrets Manager to fetch the Square API token based on the merchant ID
        // For simplicity, let's assume a hardcoded value here
        return "YOUR_SQUARE_API_TOKEN";
    }
}
