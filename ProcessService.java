package com.bajaj.qualifier.service;

import com.bajaj.qualifier.model.InitRequest;
import com.bajaj.qualifier.model.InitResponse;
import com.bajaj.qualifier.model.SubmitRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

@Service
public class ProcessService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String START_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    public void executeQualifierProcess() {
        System.out.println("Starting process...");
        
        // 1. Send initial POST request
        InitRequest initRequest = new InitRequest("John Doe", "REG12347", "john@example.com");
        
        try {
            System.out.println("Sending request to: " + START_URL);
            InitResponse initResponse = restTemplate.postForObject(START_URL, initRequest, InitResponse.class);
            
            if (initResponse != null) {
                System.out.println("Received Webhook: " + initResponse.getWebhook());
                System.out.println("Received Access Token: " + (initResponse.getAccessToken() != null ? "Yes" : "No"));
                
                // 2. Solve SQL based on last two digits of regNo
                // REG12347 -> 47 -> Odd -> Question 1
                String solution = solveQuestion1();
                System.out.println("SQL Solution prepared: " + solution);
                
                // 3. Submit solution
                submitSolution(initResponse.getWebhook(), initResponse.getAccessToken(), solution);
            } else {
                System.err.println("Failed to get valid response from start URL.");
            }
        } catch (Exception e) {
            System.err.println("Error during process execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String solveQuestion1() {
        // SQL Solution for Question 4 (highest salaried employee excluding 1st of month)
        // Columns: DEPARTMENT_NAME, SALARY, EMPLOYEE_NAME, AGE
        return "SELECT d.NAME AS DEPARTMENT_NAME, " +
               "x.SALARY AS SALARY, " +
               "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, " +
               "e.AGE AS AGE " +
               "FROM DEPARTMENT d " +
               "JOIN EMPLOYEE e ON d.ID = e.DEPARTMENT_ID " +
               "JOIN (" +
               "    SELECT EMP_ID, SUM(AMOUNT) AS SALARY " +
               "    FROM PAYMENT " +
               "    WHERE DAY(PAYMENT_DATE) != 1 " +
               "    GROUP BY EMP_ID" +
               ") x ON e.ID = x.EMP_ID " +
               "WHERE x.SALARY = (" +
               "    SELECT MAX(x2.SALARY) " +
               "    FROM EMPLOYEE e2 " +
               "    JOIN (" +
               "        SELECT EMP_ID, SUM(AMOUNT) AS SALARY " +
               "        FROM PAYMENT " +
               "        WHERE DAY(PAYMENT_DATE) != 1 " +
               "        GROUP BY EMP_ID" +
               "    ) x2 ON e2.ID = x2.EMP_ID " +
               "    WHERE e2.DEPARTMENT_ID = d.ID" +
               ")";
    }

    private void submitSolution(String webhookUrl, String accessToken, String query) {
        System.out.println("Submitting solution to: " + webhookUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.set("Content-Type", "application/json");

        SubmitRequest submitRequest = new SubmitRequest();
        submitRequest.setQuery(query);
        HttpEntity<SubmitRequest> entity = new HttpEntity<>(submitRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(webhookUrl, HttpMethod.POST, entity, String.class);
            System.out.println("Submission Response Code: " + response.getStatusCode());
            System.out.println("Submission Response Body: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error during submission: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
