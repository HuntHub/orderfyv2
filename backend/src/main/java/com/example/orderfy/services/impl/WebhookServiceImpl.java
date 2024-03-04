package com.example.orderfy.services.impl;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import lombok.SneakyThrows;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
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

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void handleWebhook(String payload) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(payload);

        String bodyString = rootNode.path("body").asText();

        JsonNode bodyNode = objectMapper.readTree(bodyString);

        JsonNode customerNode = bodyNode.path("data").path("object").path("payment");
        String customerId = customerNode.path("customer_id").asText();
        String merchantId = bodyNode.path("merchant_id").asText();
        String eventId = bodyNode.path("event_id").asText();

        System.out.println("Customer ID: " + customerId);
        System.out.println("Merchant ID: " + merchantId);

        String squareApiToken = getSecret(merchantId);

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

                JsonNode processNode = responseNode.path("customer");
                String givenName = processNode.path("given_name").asText("");
                String familyName = processNode.path("family_name").asText("");
                String email = processNode.path("email_address").asText("");
                String fullName = givenName + " " + (!familyName.isEmpty() ? familyName.charAt(0) : "");
                System.out.println("Received customer data: " + fullName);

                String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
                String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);

                AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .withRegion(Regions.US_EAST_1)
                        .build();

                Map<String, AttributeValue> item = new HashMap<>();
                item.put("merchant_Id", new AttributeValue().withS(merchantId));
                item.put("customer_Id", new AttributeValue().withS(customerId));
                item.put("email", new AttributeValue().withS(email));
                item.put("full_Name", new AttributeValue().withS(fullName));
                item.put("order_Status", new AttributeValue().withS("NEW"));
                item.put("order_Id", new AttributeValue().withS(eventId));

                PutItemRequest request = new PutItemRequest()
                        .withTableName("orderfy_test_table")
                        .withItem(item);

                PutItemResult result = client.putItem(request);

            } else {
                System.out.println("Customer data not found");
            }
        } else {
            System.out.println("Failed to retrieve customer data from Square API");
        }
    }

    @SneakyThrows
    public String getSecret(String merchantId) throws ResourceNotFoundException {

        String secretName = "merchant-" + merchantId + "-api-token";
        Region region = Region.US_EAST_1;

        String accessKeyId = System.getenv("AWS_ACCESS_KEY_ID");
        String secretAccessKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);


        SecretsManagerClient client = SecretsManagerClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse = null;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
            System.out.println(getSecretValueResponse);
        } catch (ResourceNotFoundException e) {
            System.err.println("Secret not found: " + e.getMessage());
        } catch (SdkClientException e) {
            System.err.println("An error occurred while communicating with AWS Secrets Manager: " + e.getMessage());
        }
        if (getSecretValueResponse != null) {
            String secretString = getSecretValueResponse.secretString();
            JsonNode jsonNode = objectMapper.readTree(secretString);
            String secretValue = jsonNode.get(merchantId).asText();
            System.out.println(secretValue);
            return secretValue;
        } else {
            throw new IllegalStateException("Failed to retrieve secret for merchant ID: " + merchantId);
        }
    }
}
